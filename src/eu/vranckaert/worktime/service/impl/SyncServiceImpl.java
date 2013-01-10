package eu.vranckaert.worktime.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.google.appengine.api.datastore.Transaction;
import com.google.code.twig.ObjectDatastore;
import com.google.inject.Inject;
import com.google.inject.Provider;

import eu.vranckaert.worktime.dao.ProjectDao;
import eu.vranckaert.worktime.dao.SyncHistoryDao;
import eu.vranckaert.worktime.dao.TaskDao;
import eu.vranckaert.worktime.dao.TimeRegistrationDao;
import eu.vranckaert.worktime.exception.CorruptDataException;
import eu.vranckaert.worktime.exception.SynchronisationLockedException;
import eu.vranckaert.worktime.exception.SyncronisationFailedException;
import eu.vranckaert.worktime.model.Project;
import eu.vranckaert.worktime.model.Task;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.model.User;
import eu.vranckaert.worktime.model.sync.EntitySyncResolution;
import eu.vranckaert.worktime.model.sync.EntitySyncResult;
import eu.vranckaert.worktime.model.sync.ProjectSyncResult;
import eu.vranckaert.worktime.model.sync.SyncConflictConfiguration;
import eu.vranckaert.worktime.model.sync.SyncHistory;
import eu.vranckaert.worktime.model.sync.SyncResult;
import eu.vranckaert.worktime.model.sync.TaskSyncResult;
import eu.vranckaert.worktime.model.sync.TimeRegistrationSyncResult;
import eu.vranckaert.worktime.security.service.UserService;
import eu.vranckaert.worktime.security.utils.KeyGenerator;
import eu.vranckaert.worktime.service.SyncService;

public class SyncServiceImpl implements SyncService {
	private static final Logger log = Logger.getLogger(SyncService.class.getName());

	@Inject private UserService userService;
	
	@Inject private ProjectDao projectDao;
	
	@Inject private TaskDao taskDao;
	
	@Inject private TimeRegistrationDao timeRegistrationDao;
	
	@Inject private SyncHistoryDao syncHistoryDao;
	
	@Inject private Provider<ObjectDatastore> dataStore;
	
	@Override
	public EntitySyncResult sync(String userEmail, SyncConflictConfiguration conflictConfiguration, List<TimeRegistration> timeRegistrations) throws SyncronisationFailedException, SynchronisationLockedException, CorruptDataException {
		User user = userService.findUser(userEmail);
		if (timeRegistrations == null) {
			timeRegistrations = new ArrayList<TimeRegistration>();
		}
		log.info("Starting synchronisation for user " + userEmail + " which conflict configuration set to : " + conflictConfiguration + " (this one will always win!)");
		
		int ongoingTrs = 0;
		for (TimeRegistration timeRegistration : timeRegistrations) {
			if (timeRegistration.getStartTime() == null)
				throw new CorruptDataException();
			if (timeRegistration.getEndTime() == null)
				ongoingTrs++;
			if (ongoingTrs > 1)
				throw new CorruptDataException();
			if (timeRegistration.getTask() == null)
				throw new CorruptDataException();
			if (StringUtils.isBlank(timeRegistration.getTask().getName()))
				throw new CorruptDataException();
			if (timeRegistration.isOngoingTimeRegistration() && timeRegistration.getTask().isFinished())
				throw new CorruptDataException();
			if (timeRegistration.getTask().getProject() == null)
				throw new CorruptDataException();
			if (StringUtils.isBlank(timeRegistration.getTask().getProject().getName())) {
				throw new CorruptDataException();
			}
		}
		
		boolean isFirstTimeSync = !syncHistoryDao.hasSyncHistory(user);
		log.info("Is user " + user.getEmail() + " syncing for the first time? " + (isFirstTimeSync ? "Yes" : "No"));
		
		SyncHistory ongoingSync = syncHistoryDao.getOngoingSyncHistory(user);
		if (ongoingSync != null) {
			log.warning("Another syncronisation is already in progress. Will chekc if the timeout has been reached or not for user " + user.getEmail());
			long syncDuration = new Date().getTime() - ongoingSync.getStartTime().getTime();
			if (syncDuration > 300000) { // A 5 minute timeout
				log.warning("The previous synchronisation is going on for more than five minutes, this is not usual, will mark as timeout and continue with current synchronisation request for user " + user.getEmail());
				ongoingSync.setSyncResult(SyncResult.TIME_OUT);
				ongoingSync.setEndTime(new Date());
				syncHistoryDao.update(ongoingSync);
			} else {
				log.warning("The previous synchronisation is ongoing and did not yet reach it's timeout, this synchronisation will end now for user " + user.getEmail());
				throw new SynchronisationLockedException();
			}
		}
		
		// Create a new sync history object to indicate that the user started syncing!
		log.info("Creating a new synchronisation history object for user " + user.getEmail());
		SyncHistory syncHistory = new SyncHistory();
		syncHistory.setStartTime(new Date());
		syncHistory.setSyncResult(SyncResult.BUSY);
		syncHistory.setUserEmail(user.getEmail());
		syncHistory.setIncomingTimeRegistrations(timeRegistrations.size());
		syncHistoryDao.persist(syncHistory);
		
		// Prepare lists of projects and tasks before syncing...
		List<Project> projects = new ArrayList<Project>();
		List<Task> tasks = new ArrayList<Task>();
		
		log.info("test 123 456");
		
		for (TimeRegistration timeRegistration : timeRegistrations) {
			Task task = timeRegistration.getTask();
			Project project = task.getProject();
			
			project.setUser(user);

			tasks.add(task);
			projects.add(project);
		}
		
		List<ProjectSyncResult> projectResults = new ArrayList<ProjectSyncResult>();
		List<TaskSyncResult> taskResults = new ArrayList<TaskSyncResult>();
		List<TimeRegistrationSyncResult> timeRegistrationResults = new ArrayList<TimeRegistrationSyncResult>();
		
		int projectsSynced = 0;
		int tasksSynced = 0;
		int timeRegistrationsSynced = 0;
		
		Transaction tx = dataStore.get().beginTransaction();
		try {
			// Sync all projects
			log.info("Starting to synchronize projects for user " + user.getEmail());
			for (Project project : projects) {
				ProjectSyncResult result = syncProject(project, user, conflictConfiguration);
				if (result.getResolution() != EntitySyncResolution.NO_ACTION)
					projectsSynced++;
				projectResults.add(result);
			}
			log.info(projectsSynced + " projects have been synced for user " + user.getEmail());
			
			// Sync all tasks
			log.info("Starting to synchronize tasks for user " + user.getEmail());
			for (Task task : tasks) {
				Project projectForTask = projectDao.find(task.getProject().getName(), user);
				TaskSyncResult result = syncTask(task, projectForTask, user, conflictConfiguration);
				if (result.getResolution() != EntitySyncResolution.NO_ACTION)
					tasksSynced++;
				taskResults.add(result);
			}
			log.info(tasksSynced + " tasks have been synced for user " + user.getEmail());
			
			// Sync all time registrations
			log.info("Starting to synchronize time registrations for user " + user.getEmail());
			for (TimeRegistration timeRegistration : timeRegistrations) {
				Task taskForTr = taskDao.find(timeRegistration.getTask().getName(), user);
				TimeRegistrationSyncResult result = syncTimeRegistration(timeRegistration, taskForTr, user, conflictConfiguration);
				if (result.getResolution() != EntitySyncResolution.NO_ACTION)
					timeRegistrationsSynced++;
				timeRegistrationResults.add(result);
			}
			log.info(timeRegistrationsSynced + " time registrations have been synced for user " + user.getEmail());
			
			tx.commit();
		} catch (Exception e) {
			log.info("Exception occured during sycnhronisation for user " + user.getEmail() + ". Exception " + e.getClass().getName() + " message is: " + e.getMessage());
		} finally {
			if (tx.isActive()) {
				tx.rollback();
				
				syncHistory.setEndTime(new Date());
				syncHistory.setSyncResult(SyncResult.FAILURE);
				syncHistoryDao.update(syncHistory);
				
				log.info("Something went wrong during synchronisation, transaction has been rolled back and nothing has been saved!");
				throw new SyncronisationFailedException();
			}
		}
		
		log.info("Marking the synchronisation history successfull for user " + user.getEmail());
		syncHistory.setEndTime(new Date());
		syncHistory.setSyncResult(SyncResult.SUCCESS);
		syncHistory.setSyncedProjects(projectsSynced);
		syncHistory.setSyncedTasks(tasksSynced);
		syncHistory.setSyncedTimeRegistrations(timeRegistrationsSynced);
		syncHistoryDao.update(syncHistory);
		
		EntitySyncResult syncResult = new EntitySyncResult();
		syncResult.setProjectSyncResults(projectResults);
		syncResult.setTaskSyncResults(taskResults);
		syncResult.setTimeRegistrationSyncResults(timeRegistrationResults);
		
		obscureData(syncResult);
		
		log.info("Synchronisation completed for user " + user.getEmail());
		return syncResult;
	}

	private ProjectSyncResult syncProject(Project project, User user, SyncConflictConfiguration conflictConfiguration) {
		ProjectSyncResult result = new ProjectSyncResult(project);
		
		log.info("Starting to synchronize incoming project with name " + project.getName() + " for user " + user.getEmail());
		Project localProject = null;
		if (project.getSyncKey() == null) {
			localProject = projectDao.find(project.getName(), user);
		} else {
			localProject = projectDao.findBySyncKey(project.getSyncKey(), user);
			if (localProject == null) {
				project.setSyncKey(null);
				localProject = projectDao.find(project.getName(), user);
			}
		}
		
		if (localProject == null) { // No matching project is found so persist project
			log.info("No matching project is found for project with name " + project.getName() + " for user " + user.getEmail());
			project.setSyncKey(generateSyncKeyForProject(user));
			projectDao.persist(project);
			
			result.setResolution(EntitySyncResolution.ACCEPTED);
			result.setSyncedProject(project);
		} else { // A matching project is found so compare the contents
			log.info("A mathcing project is found for project with name " + project.getName() + ". The matching project has name " + localProject.getName() + " for user " + user.getEmail());
			if (localProject.getSyncKey() == null) {
				localProject.setSyncKey(generateSyncKeyForProject(user));
			}
			if (project.getSyncKey() == null || !project.getSyncKey().equals(localProject.getSyncKey())) {
				project.setSyncKey(localProject.getSyncKey());
			}
			log.info("Synchronisation key set to " + project.getSyncKey() + " for user " + user.getEmail());
			
			if (!project.equalsContent(localProject)) {
				log.info("Project contents are not equal, will start to merge now for user " + user.getEmail());
				if (localProject.isModifiedAfter(project)) {
					copyProjectContents(localProject, project);
					log.info("The server project is more recent than the incoming one, using the server project for user " + user.getEmail());
				} else if (project.isModifiedAfter(localProject)) {
					copyProjectContents(project, localProject);
					log.info("The incoming project is more recent than the server one, using the incoming project for user " + user.getEmail());
				} else { // Last modification date-time is the same but contents are different so resolve conflict...
					log.info("Both projects are conflicting, will use the conflict configuration to keep either the incoming or the server project for user " + user.getEmail());
					switch (conflictConfiguration) {
					case CLIENT:
						copyProjectContents(project, localProject);
						break;
					case SERVER:
						copyProjectContents(localProject, project);
						break;
					}
				}
				
				result.setResolution(EntitySyncResolution.MERGED);
				result.setSyncedProject(localProject);
			} else {
				result.setResolution(EntitySyncResolution.NO_ACTION);
				result.setSyncedProject(localProject);
			}
			log.info("About to update project " + localProject.getName() + " in database for user " + user.getEmail());
			projectDao.update(localProject);
		}
		return result;
	}
	
	private String generateSyncKeyForProject(User user) {
		String syncKey = KeyGenerator.getNewKey();
		log.info("Generated project sync-key '" + syncKey + "' for user " + user.getEmail());
		while (!projectDao.isUniqueSynKey(syncKey, user)) {
			syncKey = KeyGenerator.getNewKey();
			log.info("Generted sync-key was already in use, generated new sync-key '" + syncKey + "' for user " + user.getEmail());
		}
		return syncKey;
	}
	
	private void copyProjectContents(Project source, Project destination) {
		destination.setName(source.getName());
		destination.setComment(source.getComment());
		destination.setDefaultValue(source.isDefaultValue());
		destination.setFinished(source.isFinished());
		destination.setFlags(source.getFlags());
		destination.setOrder(source.getOrder());
		destination.setLastUpdated(source.getLastUpdated());
	}
	
	private TaskSyncResult syncTask(Task task, Project project, User user, SyncConflictConfiguration conflictConfiguration) {
		TaskSyncResult result = new TaskSyncResult(task);
		
		log.info("Starting to synchronize incoming task with name " + task.getName() + " for user " + user.getEmail());
		Task localTask = null;
		if (task.getSyncKey() == null) {
			localTask = taskDao.find(task.getName(), user);
		} else {
			localTask = taskDao.findBySyncKey(task.getSyncKey(), user);
			if (localTask == null) {
				task.setSyncKey(null);
				localTask = taskDao.find(task.getName(), user);
			}
		}
		
		if (localTask == null) { // No matching task is found so persist project
			log.info("No matching project is found for task with name " + task.getName() + " for user " + user.getEmail());
			task.setProject(project);
			task.setSyncKey(generateSyncKeyForTask(user));
			taskDao.persist(task);
			
			result.setResolution(EntitySyncResolution.ACCEPTED);
			result.setSyncedTask(task);
		} else { // A matching task is found so compare the contents
			log.info("A mathcing task is found for task with name " + task.getName() + ". The matching task has name " + localTask.getName() + " for user " + user.getEmail());
			if (localTask.getSyncKey() == null) {
				localTask.setSyncKey(generateSyncKeyForTask(user));
			}
			if (task.getSyncKey() == null || task.getSyncKey().equals(localTask.getSyncKey())) {
				task.setSyncKey(localTask.getSyncKey());
			}
			log.info("Synchronisation key set to " + task.getSyncKey() + " for user " + user.getEmail());
			
			if (!task.equalsContent(localTask)) {
				log.info("Task contents are not equal, will start to merge now for user " + user.getEmail());
				if (localTask.isModifiedAfter(task)) {
					copyTaskContents(localTask, task, project);
					log.info("The server task is more recent than the incoming one, using the server task for user " + user.getEmail());
				} else if (task.isModifiedAfter(localTask)) {
					copyTaskContents(task, localTask, project);
					log.info("The incoming task is more recent than the server one, using the incoming task for user " + user.getEmail());
				} else { // Last modification date-time is the same but contents are different so resolve conflict...
					log.info("Both tasks are conflicting, will use the conflict configuration to keep either the incoming or the server task for user " + user.getEmail());
					switch (conflictConfiguration) {
					case CLIENT:
						copyTaskContents(task, localTask, project);
						break;
					case SERVER:
						copyTaskContents(localTask, task, project);
						break;
					}
				}
				

				result.setResolution(EntitySyncResolution.MERGED);
				result.setSyncedTask(localTask);
			} else {
				result.setResolution(EntitySyncResolution.NO_ACTION);
				result.setSyncedTask(localTask);
			}
			log.info("About to update task " + localTask.getName() + " in database for user " + user.getEmail());
			taskDao.update(localTask);
		}
		return result;
	}
	
	private String generateSyncKeyForTask(User user) {
		String syncKey = KeyGenerator.getNewKey();
		log.info("Generated project sync-key '" + syncKey + "' for user " + user.getEmail());
		while(!taskDao.isUniqueSynKey(syncKey, user)) {
			syncKey = KeyGenerator.getNewKey();
			log.info("Generted sync-key was already in use, generated new sync-key '" + syncKey + "' for user " + user.getEmail());
		}
		return syncKey;
	}
	
	private void copyTaskContents(Task source, Task destination, Project project) {
		destination.setName(source.getName());
		destination.setComment(source.getComment());
		destination.setFinished(source.isFinished());
		destination.setFlags(source.getFlags());
		destination.setOrder(source.getOrder());
		destination.setLastUpdated(source.getLastUpdated());
		destination.setProject(project);
	}
	
	private TimeRegistrationSyncResult syncTimeRegistration(TimeRegistration timeRegistration, Task task, User user, SyncConflictConfiguration conflictConfiguration) {
		TimeRegistrationSyncResult result = new TimeRegistrationSyncResult(timeRegistration);
		
		log.info("Starting to synchronize incoming time registration for user " + user.getEmail());
		TimeRegistration localTimeRegistration = null;
		if (timeRegistration.getSyncKey() == null) {
			localTimeRegistration = timeRegistrationDao.find(timeRegistration.getStartTime(), timeRegistration.getEndTime(), user);
		} else {
			localTimeRegistration = timeRegistrationDao.findBySyncKey(timeRegistration.getSyncKey(), user);
			if (localTimeRegistration == null) {
				timeRegistration.setSyncKey(null);
				localTimeRegistration = timeRegistrationDao.find(timeRegistration.getStartTime(), timeRegistration.getEndTime(), user);
			}
		}
		
		if (localTimeRegistration == null) { // No matching time registration is found so persist project
			log.info("No matching time registration is found for user " + user.getEmail());
			List<TimeRegistration> interferingTimeRegistrations = timeRegistrationDao.findInterferingTimeRegistrations(timeRegistration, user);
			if (interferingTimeRegistrations.isEmpty()) {
				log.info("No interfering time registrations found, can safely persist time registration for user " + user.getEmail());
				timeRegistration.setTask(task);
				timeRegistration.setSyncKey(generateSyncKeyForTimeRegistration(user));
				timeRegistrationDao.persist(timeRegistration);
				
				result.setResolution(EntitySyncResolution.ACCEPTED);
				result.setSyncedTimeRegistration(timeRegistration);
			} else {
				log.info("Conflicting time registrations have been found, using the conflict configuration to handle this safely for user " + user.getEmail());
				switch (conflictConfiguration) {
					case CLIENT: {
						// Remove all interfering time registrations and persist the incoming time registration
						log.info("Client wins, about to remove all conflicting time registrations from server and persist incoming time registration for user " + user.getEmail());
						for (TimeRegistration interferingTimeRegistration : interferingTimeRegistrations) {
							timeRegistrationDao.remove(interferingTimeRegistration);
						}
						timeRegistration.setTask(task);
						timeRegistration.setSyncKey(generateSyncKeyForTimeRegistration(user));
						timeRegistrationDao.persist(timeRegistration);
						
						result.setResolution(EntitySyncResolution.ACCEPTED);
						result.setSyncedTimeRegistration(timeRegistration);
						
						break;
					}
					case SERVER: {
						// Server wins so incoming time registration is not kept!
						log.info("Server wins, will not remove conflicting time registrations and not persist the incoming one for user " + user.getEmail());
						
						// Check if all the time registrations have a sync-key set, if not update with sync key
						for (TimeRegistration intereferingTimeRegistration : interferingTimeRegistrations) {
							if (StringUtils.isEmpty(intereferingTimeRegistration.getSyncKey())) {
								intereferingTimeRegistration.setSyncKey(generateSyncKeyForTimeRegistration(user));
								timeRegistrationDao.persist(intereferingTimeRegistration);
							}
						}
						
						result.setResolution(EntitySyncResolution.NOT_ACCEPTED);
						result.setSyncedTimeRegistrations(interferingTimeRegistrations);
						
						break;
					}
				}
			}
		} else { // A matching time registration is found so compare the contents
			log.info("A mathcing time registration is found for user " + user.getEmail());
			if (localTimeRegistration.getSyncKey() == null) {
				localTimeRegistration.setSyncKey(generateSyncKeyForTimeRegistration(user));
			}
			if (timeRegistration.getSyncKey() == null || timeRegistration.getSyncKey().equals(localTimeRegistration.getSyncKey())) {
				timeRegistration.setSyncKey(localTimeRegistration.getSyncKey());
			}
			log.info("Synchronisation key set to " + timeRegistration.getSyncKey() + " for user " + user.getEmail());
			
			if (!timeRegistration.equalsContent(localTimeRegistration)) {
				log.info("Time registration contents are not equal, will start to merge now for user " + user.getEmail());
				if (localTimeRegistration.isModifiedAfter(timeRegistration)) {
					copyTimeRegistrationContents(localTimeRegistration, timeRegistration, task);
					log.info("The server time registration is more recent than the incoming one, using the server time registration for user " + user.getEmail());
				} else if (timeRegistration.isModifiedAfter(localTimeRegistration)) {
					copyTimeRegistrationContents(timeRegistration, localTimeRegistration, task);
					log.info("The incoming time registration is more recent than the server one, using the incoming time registration for user " + user.getEmail());
				} else { // Last modification date-time is the same but contents are different so resolve conflict...
					log.info("Both time registrations are conflicting, will use the conflict configuration to keep either the incoming or the server time registration for user " + user.getEmail());
					switch (conflictConfiguration) {
					case CLIENT:
						copyTimeRegistrationContents(timeRegistration, localTimeRegistration, task);
						break;
					case SERVER:
						copyTimeRegistrationContents(timeRegistration, localTimeRegistration, task);
						break;
					}
				}
				
				result.setResolution(EntitySyncResolution.MERGED);
				result.setSyncedTimeRegistration(localTimeRegistration);
			} else {
				result.setResolution(EntitySyncResolution.NO_ACTION);
				result.setSyncedTimeRegistration(localTimeRegistration);
			}
			log.info("About to update time registration in database for user " + user.getEmail());
			timeRegistrationDao.update(localTimeRegistration);
		}
		return result;
	}
	
	private String generateSyncKeyForTimeRegistration(User user) {
		String syncKey = KeyGenerator.getNewKey();
		log.info("Generated project sync-key '" + syncKey + "' for user " + user.getEmail());
		while(!timeRegistrationDao.isUniqueSynKey(syncKey, user)) {
			syncKey = KeyGenerator.getNewKey();
			log.info("Generted sync-key was already in use, generated new sync-key '" + syncKey + "' for user " + user.getEmail());
		}
		return syncKey;
	}
	
	private void copyTimeRegistrationContents(TimeRegistration source, TimeRegistration destination, Task task) {
		destination.setComment(source.getComment());
		destination.setFlags(source.getFlags());
		destination.setTask(task);
		destination.setLastUpdated(source.getLastUpdated());
	}

	@Override
	public List<Project> getSyncedProjects(String userEmail,
			Date lastSuccessfulSyncDate) {
		User user = userService.findUser(userEmail);
		List<Project> projects = null;
		if (lastSuccessfulSyncDate == null) {
			projects = projectDao.findAll(user);
		} else {
			projects = projectDao.findAllModifiedAfter(user, lastSuccessfulSyncDate);
		}
		
		obscureData(projects);
			
		return projects;
	}

	@Override
	public List<Task> getSyncedTasks(String userEmail,
			Date lastSuccessfulSyncDate) {
		User user = userService.findUser(userEmail);
		List<Task> tasks = null;
		if (lastSuccessfulSyncDate == null) {
			tasks = taskDao.findAll(user);
		} else {
			tasks = taskDao.findAllModifiedAfter(user, lastSuccessfulSyncDate);
		}
		
		obscureData(tasks);
			
		return tasks;
	}

	@Override
	public List<TimeRegistration> getSyncedTimeRegistrations(String userEmail,
			Date lastSuccessfulSyncDate) {
		User user = userService.findUser(userEmail);
		List<TimeRegistration> timeRegistrations = null;
		if (lastSuccessfulSyncDate == null) {
			timeRegistrations = timeRegistrationDao.findAll(user);
		} else {
			timeRegistrations = timeRegistrationDao.findAllModifiedAfter(user, lastSuccessfulSyncDate);
		}
		
		obscureData(timeRegistrations);
			
		return timeRegistrations;
	}
	
	private void obscureData(EntitySyncResult syncResult) {
		// Obscure user info...
		for (ProjectSyncResult projectSyncResult : syncResult.getProjectSyncResults()) {
			projectSyncResult.getProject().setUser(null);
			projectSyncResult.getProject().setKey(null);
			projectSyncResult.getSyncedProject().setUser(null);
			projectSyncResult.getSyncedProject().setKey(null);
		}
		for (TaskSyncResult taskSyncResult : syncResult.getTaskSyncResults()) {
			taskSyncResult.getTask().getProject().setUser(null);
			taskSyncResult.getTask().getProject().setKey(null);
			taskSyncResult.getTask().setKey(null);
			taskSyncResult.getSyncedTask().getProject().setUser(null);
			taskSyncResult.getSyncedTask().getProject().setKey(null);
			taskSyncResult.getSyncedTask().setKey(null);
		}
		for (TimeRegistrationSyncResult timeRegistrationResult : syncResult.getTimeRegistrationSyncResults()) {
			timeRegistrationResult.getTimeRegistration().getTask().getProject().setUser(null);
			if (timeRegistrationResult.getSyncedTimeRegistration() != null) {
				timeRegistrationResult.getSyncedTimeRegistration().getTask().getProject().setUser(null);
				timeRegistrationResult.getSyncedTimeRegistration().getTask().getProject().setKey(null);
				timeRegistrationResult.getSyncedTimeRegistration().getTask().setKey(null);
				timeRegistrationResult.getSyncedTimeRegistration().setKey(null);
			}
			if (timeRegistrationResult.getSyncedTimeRegistrations() != null) {
				for (TimeRegistration syncedTimeRegistration : timeRegistrationResult.getSyncedTimeRegistrations()) {
					syncedTimeRegistration.getTask().getProject().setUser(null);
					syncedTimeRegistration.getTask().getProject().setKey(null);
					syncedTimeRegistration.getTask().setKey(null);
					syncedTimeRegistration.setKey(null);
				}
			}
		}
	}
	
	private void obscureData(List objects) {
		// Obscure user info...
		for (Object object : objects) {
			if (object instanceof Project) {
				obscureData((Project) object);
			} else if (object instanceof Task) {
				obscureData((Task) object);
			} else if (object instanceof TimeRegistration) {
				obscureData((TimeRegistration) object);
			}
		}
	}
	
	private void obscureData(TimeRegistration timeRegistration) {
		timeRegistration.getTask().getProject().setUser(null);
		timeRegistration.getTask().getProject().setKey(null);
		timeRegistration.getTask().setKey(null);
		timeRegistration.setKey(null);
	}
	
	private void obscureData(Project project) {
		project.setUser(null);
		project.setKey(null);
	}
	
	private void obscureData(Task task) {
		task.getProject().setUser(null);
		task.getProject().setKey(null);
		task.setKey(null);
	}
}
