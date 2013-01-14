/*
 * Copyright 2013 Dirk Vranckaert
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.vranckaert.worktime.service.impl;

import android.content.Context;
import com.google.inject.Inject;
import eu.vranckaert.worktime.dao.*;
import eu.vranckaert.worktime.dao.web.WorkTimeWebDao;
import eu.vranckaert.worktime.dao.web.model.response.sync.EntitySyncResult;
import eu.vranckaert.worktime.dao.web.model.response.sync.ProjectSyncResult;
import eu.vranckaert.worktime.dao.web.model.response.sync.TaskSyncResult;
import eu.vranckaert.worktime.dao.web.model.response.sync.TimeRegistrationSyncResult;
import eu.vranckaert.worktime.exceptions.SDCardUnavailableException;
import eu.vranckaert.worktime.exceptions.backup.BackupException;
import eu.vranckaert.worktime.exceptions.backup.BackupFileCouldNotBeCreated;
import eu.vranckaert.worktime.exceptions.backup.BackupFileCouldNotBeWritten;
import eu.vranckaert.worktime.exceptions.network.NoNetworkConnectionException;
import eu.vranckaert.worktime.exceptions.network.WifiConnectionRequiredException;
import eu.vranckaert.worktime.exceptions.worktime.account.*;
import eu.vranckaert.worktime.exceptions.worktime.sync.CorruptSyncDataException;
import eu.vranckaert.worktime.exceptions.worktime.sync.SyncAlreadyBusyException;
import eu.vranckaert.worktime.exceptions.worktime.sync.SynchronizationFailedException;
import eu.vranckaert.worktime.model.*;
import eu.vranckaert.worktime.service.AccountService;
import eu.vranckaert.worktime.service.BackupService;
import eu.vranckaert.worktime.utils.network.NetworkUtil;
import eu.vranckaert.worktime.utils.preferences.Preferences;
import eu.vranckaert.worktime.web.json.exception.GeneralWebException;

import java.util.*;

/**
 * User: DIRK VRANCKAERT
 * Date: 12/12/12
 * Time: 20:04
 */
public class AccountServiceImpl implements AccountService {
    @Inject
    private WorkTimeWebDao workTimeWebDao;

    @Inject
    private AccountDao accountDao;

    @Inject
    private SyncHistoryDao syncHistoryDao;

    @Inject
    private ProjectDao projectDao;

    @Inject
    private TaskDao taskDao;

    @Inject
    private TimeRegistrationDao timeRegistrationDao;

    @Inject
    private SyncRemovalCacheDao syncRemovalCacheDao;

    @Inject
    private BackupService backupService;

    @Inject
    private Context context;

    @Override
    public boolean isUserLoggedIn() {
        User user = accountDao.getLoggedInUser();
        return user!=null;
    }

    @Override
    public void login(String email, String password) throws GeneralWebException, NoNetworkConnectionException, LoginCredentialsMismatchException {
        String sessionKey = workTimeWebDao.login(email, password);

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setSessionKey(sessionKey);

        accountDao.storeLoggedInUser(user);
    }

    @Override
    public void register(String email, String firstName, String lastName, String password) throws GeneralWebException, NoNetworkConnectionException, RegisterEmailAlreadyInUseException, PasswordLengthValidationException, RegisterFieldRequiredException {
        String sessionKey = workTimeWebDao.register(email, firstName, lastName, password);

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setSessionKey(sessionKey);

        accountDao.storeLoggedInUser(user);
    }

    @Override
    public User loadUserData() throws UserNotLoggedInException, GeneralWebException, NoNetworkConnectionException {
        User user = accountDao.getLoggedInUser();

        User updatedUser = null;
        try {
            updatedUser = workTimeWebDao.loadProfile(user);
        } catch (UserNotLoggedInException e) {
            accountDao.delete(user);
            throw e;
        }

        return updatedUser;
    }

    @Override
    public void sync() throws UserNotLoggedInException, GeneralWebException, NoNetworkConnectionException, WifiConnectionRequiredException, BackupException, SyncAlreadyBusyException, SynchronizationFailedException {
        SyncHistory ongoingSyncHistory = syncHistoryDao.getOngoingSyncHistory();
        if (ongoingSyncHistory != null) {
            long startTime = ongoingSyncHistory.getStarted().getTime();
            if (new Date().getTime() - startTime  > 600000) { // Checks for a 10 minute timeout.
                ongoingSyncHistory.setStatus(SyncHistoryStatus.TIMED_OUT);
                ongoingSyncHistory.setEnded(new Date());
                syncHistoryDao.update(ongoingSyncHistory);
            } else {
                throw new SyncAlreadyBusyException();
            }
        }

        // Save an instance of SyncHistory so we always know that a sync is going on...
        SyncHistory syncHistory = new SyncHistory();
        syncHistoryDao.save(syncHistory);

        try {
            // TODO wifi-check does not work!
            if (Preferences.Account.syncOnWifiOnly(context) && !NetworkUtil.isConnectedToWifi(context)) {
                WifiConnectionRequiredException e = new WifiConnectionRequiredException();
                markSyncAsFailed(e);
                throw e;
            }

            if (Preferences.Account.backupBeforeSync(context)) {
                try {
                    backupService.backup(context);
                } catch (SDCardUnavailableException e) {
                    BackupException backupException = new BackupException(e);
                    markSyncAsFailed(backupException);
                    throw backupException;
                } catch (BackupFileCouldNotBeCreated backupFileCouldNotBeCreated) {
                    markSyncAsFailed(backupFileCouldNotBeCreated);
                    throw backupFileCouldNotBeCreated;
                } catch (BackupFileCouldNotBeWritten backupFileCouldNotBeWritten) {
                    markSyncAsFailed(backupFileCouldNotBeWritten);
                    throw backupFileCouldNotBeWritten;
                }
            }

            // Retrieve the logged in user
            User user = accountDao.getLoggedInUser();

            // Retrieve the conflict configuration
            String conflictConfiguration = Preferences.Account.conflictConfiguration(context);

            // Get the last successful sync date
            Date lastSuccessfulSyncDate = syncHistoryDao.getLastSuccessfulSyncDate();

            // Retrieve all time projects, tasks and registrations to be synced
            // If no sync has been done before all entities will be synced. Otherwise only those that have changed
            // since the last sync.
            List<Project> projects = null;
            List<Task> tasks = null;
            List<TimeRegistration> timeRegistrations = null;
            if (lastSuccessfulSyncDate != null) {
                projects = projectDao.findAllModifiedAfter(lastSuccessfulSyncDate);
                tasks = taskDao.findAllModifiedAfter(lastSuccessfulSyncDate);
                timeRegistrations = timeRegistrationDao.findAllModifiedAfter(lastSuccessfulSyncDate);
            } else {
                projects = projectDao.findAll();
                tasks = taskDao.findAll();
                timeRegistrations = timeRegistrationDao.findAll();
            }

            // Make sure all relations are correctly loaded into memory...
            for (Task task : tasks) {
                projectDao.refresh(task.getProject());
            }
            for (TimeRegistration timeRegistration : timeRegistrations) {
                taskDao.refresh(timeRegistration.getTask());
                projectDao.refresh(timeRegistration.getTask().getProject());
            }

            // Retrieve removed sync-keys
            Map<String, String> syncRemovalMap = syncRemovalCacheDao.findAllSyncKeys();

            List<Object> result;
            try {
                // Execute the sync on the server
                result = workTimeWebDao.sync(user, conflictConfiguration, lastSuccessfulSyncDate, projects, tasks, timeRegistrations, syncRemovalMap);
            } catch (UserNotLoggedInException e) {
                accountDao.delete(user);
                markSyncAsFailed(e);
                throw e;
            } catch (SynchronizationFailedException e) {
                markSyncAsFailed(e);
                throw e;
            } catch (CorruptSyncDataException e) {
                markSyncAsFailed(e);
                throw new RuntimeException("The data of the application seems to be corrupt!", e);
            } catch (SyncAlreadyBusyException e) {
                markSyncAsFailed(e);
                throw e;
            } catch (GeneralWebException e) {
                markSyncAsFailed(e);
                throw e;
            } catch (NoNetworkConnectionException e) {
                markSyncAsFailed(e);
                throw e;
            }

            List<Project> projectsSinceLastSync = (List<Project>) result.get(0);
            List<Task> tasksSinceLastSync = (List<Task>) result.get(1);
            List<TimeRegistration> timeRegistrationsSinceLastSync = (List<TimeRegistration>) result.get(2);
            EntitySyncResult entitySyncResult = (EntitySyncResult) result.get(3);
            Map<String, String> serverSyncRemovalMap = (Map<String, String>) result.get(4);

            applySyncResult(entitySyncResult);
            checkServerEntities(projectsSinceLastSync, tasksSinceLastSync, timeRegistrationsSinceLastSync);
            removeEntities(serverSyncRemovalMap);

            // Clean up the entities that should be removed on the next sync.
            syncRemovalCacheDao.deleteAll();

            syncHistory = syncHistoryDao.getOngoingSyncHistory();
            syncHistory.setEnded(new Date());
            syncHistory.setStatus(SyncHistoryStatus.SUCCESSFUL);
            syncHistoryDao.update(syncHistory);
        } catch (RuntimeException e) {
            markSyncAsFailed(e);
            throw e;
        }
    }

    private void removeEntities(Map<String, String> syncRemovalMap) {
        if (syncRemovalMap == null || syncRemovalMap.size() == 0)
            return;

        List<String> projectSyncKeys = new ArrayList<String>();
        List<String> taskSyncKeys = new ArrayList<String>();
        List<String> timeRegistrationSyncKeys = new ArrayList<String>();

        for (Map.Entry<String, String> entry : syncRemovalMap.entrySet()) {
            String syncKey = entry.getKey();
            String entityName = entry.getValue();

            if (entityName.equals("Project")) {
                projectSyncKeys.add(syncKey);
            } else if (entityName.equals("Task")) {
                taskSyncKeys.add(syncKey);
            } else if (entityName.equals("TimeRegistration")) {
                timeRegistrationSyncKeys.add(syncKey);
            }
        }

        for (String syncKey : timeRegistrationSyncKeys) {
            TimeRegistration entity = timeRegistrationDao.findBySyncKey(syncKey);
            if (entity != null)
                timeRegistrationDao.delete(entity);
        }

        for (String syncKey : taskSyncKeys) {
            Task entity = taskDao.findBySyncKey(syncKey);
            if (entity != null)
                taskDao.delete(entity);
        }

        for (String syncKey : projectSyncKeys) {
            Project entity = projectDao.findBySyncKey(syncKey);
            if (entity != null)
                projectDao.delete(entity);
        }
    }

    /**
     * Apply the sync result as it has been returned from the server. For each entity group it result in which we find
     * the original entity (that has been sent to the server by us), the synced entity (as it is stored on the server)
     * or null and the resolution (if it's accepted, merged, not accepted or no action has been performed.<br/>
     * In case an entity is merged we do a lookup locally based on the original entity and replace it's content with the
     * content of the synced entity.<br/>
     * In case the entity is not either accepted or no action has been performed for the entity we'll check if the
     * sync-key is set or not. If the sync key is not set we set it and update the entity.<br/>
     * In case the entity is not accepted the entity will be removed from the local database. If it's a time
     * registration the list of synced time registrations needs to be checked. If that list is not empty it means the
     * the just removed entity needs to be replaced with that list of time registrations. So each one of them should be
     * persisted.
     * @param entitySyncResult The synchronization result containing every single entity that has been sent to the
     *                         server.
     */
    private void applySyncResult(EntitySyncResult entitySyncResult) {
        for (ProjectSyncResult syncResult : entitySyncResult.getProjectSyncResults()) {
            Project localProject = syncResult.getProject();
            Project syncedProject = syncResult.getSyncedProject();
            switch (syncResult.getResolution()) {
                case MERGED: {
                    // The entity has been merged with an entity on the server, so we will have copy all of the
                    // synced data into the local entity.
                    Project project = projectDao.findByName(localProject.getName());
                    updateProject(syncedProject, project);
                    break;
                }
                case NOT_ACCEPTED: {
                    // The entity has been synced before but is no longer found remotely based on the sync key so we
                    // can safely remove the entity from the database.
                    Project project = projectDao.findByName(localProject.getName());
                    projectDao.delete(project);
                    break;
                }
                default: {
                    // The entity has been either accepted remotely or it already exists on the server and the
                    // contents did not have to be merged. We will only update it's syncKey!
                    Project project = projectDao.findByName(localProject.getName());
                    if (project.getSyncKey() == null) {
                        project.setSyncKey(syncedProject.getSyncKey());
                        projectDao.update(project);
                    }
                    break;
                }
            }
        }
        for (TaskSyncResult syncResult : entitySyncResult.getTaskSyncResults()) {
            Task localTask = syncResult.getTask();
            Task syncedTask = syncResult.getSyncedTask();
            switch (syncResult.getResolution()) {
                case MERGED: {
                    // The entity has been merged with an entity on the server, so we will have copy all of the
                    // synced data into the local entity.
                    Task task = taskDao.findByName(localTask.getName());
                    updateTask(syncedTask, task);
                    break;
                }
                case NOT_ACCEPTED: {
                    // The entity has been synced before but is no longer found remotely based on the sync key so we
                    // can safely remove the entity from the database.
                    Task task = taskDao.findByName(localTask.getName());
                    taskDao.delete(task);
                    break;
                }
                default: {
                    // The entity has been either accepted remotely or it already exists on the server and the
                    // contents did not have to be merged. We will only update it's syncKey!
                    Task task = taskDao.findByName(localTask.getName());
                    if (task.getSyncKey() == null) {
                        task.setSyncKey(syncedTask.getSyncKey());
                        taskDao.update(task);
                    }
                    break;
                }
            }
        }
        for (TimeRegistrationSyncResult syncResult : entitySyncResult.getTimeRegistrationSyncResults()) {
            TimeRegistration localTimeRegistration = syncResult.getTimeRegistration();
            TimeRegistration syncedTimeRegistration = syncResult.getSyncedTimeRegistration();
            List<TimeRegistration> syncedTimeRegistrations = syncResult.getSyncedTimeRegistrations();
            switch (syncResult.getResolution()) {
                case MERGED: {
                    // The entity has been merged with an entity on the server, so we will have copy all of the
                    // synced data into the local entity.
                    TimeRegistration timeRegistration = timeRegistrationDao.findByDates(
                            localTimeRegistration.getStartTime(), localTimeRegistration.getEndTime()
                    );
                    updateTimeRegistration(syncedTimeRegistration, timeRegistration);
                    break;
                }
                case NOT_ACCEPTED: {
                    // The time registration was not accepted remotely because it interfered with other time
                    // registrations so the time registration should be removed and the 'others' should be
                    // persisted. Or the time registration was not accepted because there was not match found with
                    // the sync-key (so it must have been removed on the server) before this sync. We can then
                    // safely remove it here also.
                    TimeRegistration timeRegistration = timeRegistrationDao.findByDates(
                            localTimeRegistration.getStartTime(), localTimeRegistration.getEndTime()
                    );
                    timeRegistrationDao.delete(timeRegistration);
                    if (syncedTimeRegistrations != null && !syncedTimeRegistrations.isEmpty()) {
                        for (TimeRegistration incomingTimeRegistration : syncedTimeRegistrations) {
                            Task task = taskDao.findByName(incomingTimeRegistration.getTask().getName());
                            incomingTimeRegistration.setTask(task);
                            timeRegistrationDao.save(incomingTimeRegistration);
                        }
                    }
                }
                default: {
                    // The entity has been either accepted remotely or it already exists on the server and the
                    // contents did not have to be merged. We will only update it's syncKey!
                    TimeRegistration timeRegistration = timeRegistrationDao.findByDates(
                            localTimeRegistration.getStartTime(), localTimeRegistration.getEndTime()
                    );
                    if (timeRegistration.getSyncKey() == null) {
                        timeRegistration.setSyncKey(syncedTimeRegistration.getSyncKey());
                        timeRegistrationDao.update(timeRegistration);
                    }
                    break;
                }
            }
        }
    }

    /**
     * Check the entities returned by the server and check if everything is already available locally or if any entity
     * should be updated or persisted.
     * @param projects Projects coming from the server.
     * @param tasks Tasks coming from the server.
     * @param timeRegistrations Time registrations coming from the server.
     */
    private void checkServerEntities(List<Project> projects, List<Task> tasks, List<TimeRegistration> timeRegistrations) {
        for (Project project : projects) {
            Project localProject = projectDao.findBySyncKey(project.getSyncKey());
            if (localProject == null) {
                localProject = projectDao.findByName(project.getName());
                if (localProject != null) {
                    updateProject(project, localProject);
                } else {
                    projectDao.save(project);
                }
            } else {
                updateProject(project, localProject);
            }
        }
        for (Task task : tasks) {
            Task localTask = taskDao.findBySyncKey(task.getSyncKey());
            if (localTask == null) {
                localTask = taskDao.findByName(task.getName());
                if (localTask != null) {
                    updateTask(task, localTask);
                } else {
                    updateTask(task, task);
                    taskDao.save(task);
                }
            } else {
                updateTask(task, localTask);
            }
        }
        for (TimeRegistration timeRegistration : timeRegistrations) {
            TimeRegistration localTimeRegistration = timeRegistrationDao.findBySyncKey(timeRegistration.getSyncKey());
            if (localTimeRegistration == null) {
                localTimeRegistration = timeRegistrationDao.findByDates(timeRegistration.getStartTime(), timeRegistration.getEndTime());
                if (localTimeRegistration != null) {
                    updateTimeRegistration(timeRegistration, localTimeRegistration);
                } else {
                    updateTimeRegistration(timeRegistration, timeRegistration);
                    timeRegistrationDao.save(timeRegistration);
                }
            } else {
                updateTimeRegistration(timeRegistration, localTimeRegistration);
            }
        }
    }

    /**
     * Updates the destination project after the contents of the source project have been copied into the destination
     * project. The id is not overwritten!
     * @param source The source project.
     * @param destination The destination project that will be updated.
     */
    private void updateProject(Project source, Project destination) {
        destination.setName(source.getName());
        destination.setComment(source.getComment());
        destination.setDefaultValue(source.isDefaultValue());
        destination.setFinished(source.isFinished());
        destination.setFlags(source.getFlags());
        destination.setOrder(source.getOrder());
        destination.setLastUpdated(source.getLastUpdated());
        destination.setSyncKey(source.getSyncKey());

        if (source.getId() != null)
            projectDao.update(destination);
    }

    /**
     * Updates the destination task after the contents of the source task have been copied into the destination task.
     * The id is not overwritten! The linked project is looked up in the database based on the project of the
     * source-task.
     * @param source The source task.
     * @param destination The destination task that will be updated.
     */
    private void updateTask(Task source, Task destination) {
        destination.setName(source.getName());
        destination.setComment(source.getComment());
        destination.setFinished(source.isFinished());
        destination.setFlags(source.getFlags());
        destination.setOrder(source.getOrder());
        destination.setLastUpdated(source.getLastUpdated());
        destination.setSyncKey(source.getSyncKey());
        Project project = projectDao.findBySyncKey(source.getProject().getSyncKey());
        if (project == null) {
            project = projectDao.findByName(source.getProject().getName());
        }
        destination.setProject(project);
        if (source.getId() != null)
            taskDao.update(destination);
    }

    /**
     * Updates the destination time registration after the contents of the source time registration have been copied
     * into the destination time registration. The id is not overwritten! The linked task is looked up in the database
     * based on the task of the source-time-registration.
     * @param source The source time registration.
     * @param destination The destination time registration that will be updated.
     */
    private void updateTimeRegistration(TimeRegistration source, TimeRegistration destination) {
        destination.setComment(source.getComment());
        destination.setFlags(source.getFlags());
        destination.setLastUpdated(source.getLastUpdated());
        destination.setSyncKey(source.getSyncKey());
        Task task = taskDao.findBySyncKey(source.getTask().getSyncKey());
        if (task == null) {
            task = taskDao.findByName(source.getTask().getName());
        }
        destination.setTask(task);
        if (source.getId() != null)
            timeRegistrationDao.update(destination);
    }

    /**
     * Mark the ongoing {@link SyncHistory} as {@link SyncHistoryStatus#FAILED}. If an exception is provided the simple
     * name of the exception-class will be put in the {@link SyncHistory#failureReason} field.
     * @param e The exception that occurred or null.
     */
    private void markSyncAsFailed(Exception e) {
        SyncHistory syncHistory = syncHistoryDao.getOngoingSyncHistory();
        if (syncHistory.getStatus().equals(SyncHistoryStatus.BUSY)) {
            syncHistory.setStatus(SyncHistoryStatus.FAILED);
            syncHistory.setEnded(new Date());
            if (e != null) {
                syncHistory.setFailureReason(e.getClass().getSimpleName());
            }
            syncHistoryDao.update(syncHistory);
        }
    }

    @Override
    public void logout() {
        clearUserAppData();

        // Clear all the sync keys...
        List<TimeRegistration> timeRegistrations = timeRegistrationDao.findAll();
        for (TimeRegistration timeRegistration : timeRegistrations) {
            timeRegistration.setSyncKey(null);
            timeRegistrationDao.update(timeRegistration);
        }
        List<Project> projects = projectDao.findAll();
        for (Project project : projects) {
            project.setSyncKey(null);
            projectDao.update(project);
        }
        List<Task> tasks = taskDao.findAll();
        for (Task task : tasks) {
            task.setSyncKey(null);
            taskDao.update(task);
        }
    }

    @Override
    public void removeAll() {
        clearUserAppData();
    }

    private void clearUserAppData() {
        // Remove all the caching data...
        syncRemovalCacheDao.deleteAll();

        // Remove all sync history
        syncHistoryDao.deleteAll();

        // Logout the current logged in user
        User user = accountDao.getLoggedInUser();
        accountDao.delete(user);
        workTimeWebDao.logout(user);
    }
}
