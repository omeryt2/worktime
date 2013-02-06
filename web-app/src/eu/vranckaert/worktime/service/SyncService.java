package eu.vranckaert.worktime.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import eu.vranckaert.worktime.exception.CorruptDataException;
import eu.vranckaert.worktime.exception.SynchronisationLockedException;
import eu.vranckaert.worktime.exception.SyncronisationFailedException;
import eu.vranckaert.worktime.model.Project;
import eu.vranckaert.worktime.model.Task;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.model.sync.EntitySyncResult;
import eu.vranckaert.worktime.model.sync.SyncConflictConfiguration;

public interface SyncService {
	/**
	 * 
	 * @param userEmail The email of the user-account on which to perform this 
	 * sync.
	 * @param conflictConfiguration The conflict configuration which defines who
	 * (server or client) should win in case of a conflict.
	 * @param incomingProjects A list of {@link Project}s to be synced. The 
	 * projects in this list are checked one-by-one to see if they already 
	 * appear somewhere in once of the incoming time registrations. If so that
	 * project is ignored.
	 * @param incomingTasks A list of {@link Task}s to be synced. The tasks in 
	 * this list are checked one-by-one to see if they already appear somewhere 
	 * in once of the incoming time registrations. If so that task is ignored.
	 * @param incomingTimeRegstrations The list of {@link TimeRegistration}s 
	 * that must be synced with the server.
	 * @param syncRemovalMap A map of String and String where the key is a
	 * syncKey and the value is the name of an entity class (being Project, Task 
	 * or TimeRegistration). All syncKeys found in this map will be searched for
	 * and will be removed if the entity is not modified after the the last
	 * successful sync date that is also provided as input to this method. If 
	 * the entity has been updated afterwards the conflict configuration will be
	 * used to determine who wins, if the entity should be removed (client wins)
	 * or if the entity should remain (server wins). If the entity it needs to 
	 * be synced back to the client.
	 * @param lastSuccessfulSyncDate The last successful synchronization date.
	 * @return An instance of {@link EntitySyncResult} containing three lists:
	 * <br/>
	 * 1. List of incoming projects, what happend with it and the result how it
	 * is stored on the server.<br/>
	 * 2. List of incoming tasks, what happend with it and the result how it is
	 * stored on the server.<br/>
	 * 3. List of incoming time registrations, whap happend with it and the 
	 * result how it is stored on the server or (in case the server won) what
	 * other time registrations are in place on the server.  
	 * @throws SyncronisationFailedException If syncronisation failed this 
	 * exception is thrown meaning that something went wrong on the server.
	 * @throws SynchronisationLockedException This exception means that the user
	 * already started another sync that is currently ongoing. This exception 
	 * will be thrown until the timeout of a sync has been reached (5 minutes).
	 * @throws CorruptDataException If the incoming data is corrupt, this 
	 * exception is thrown. It can mean that a time registration is missing a 
	 * task, a task is missing a project, a task or project name is missing,
	 * multiple ongoing time registrations are passed in or a time registration 
	 * without a start time is passed in. 
	 */
	EntitySyncResult sync(String userEmail, SyncConflictConfiguration conflictConfiguration, List<Project> incomingProjects, List<Task> incomingTasks, List<TimeRegistration> incomingTimeRegstrations, Map<String, String> syncRemovalMap, Date lastSuccessfulSyncDate) throws SyncronisationFailedException, SynchronisationLockedException, CorruptDataException;
	
	/**
	 * Searches for all projects of a user that have been last modified date 
	 * that equal to or after the specified date. If the specified last sync 
	 * date is null all projects are returned.  
	 * @param userEmail The email of the user-account for which to retrieve the
	 * synced projects.
	 * @param lastSuccessfulSyncDate The last time synchronization was 
	 * successful.
	 * @return A list of {@link Project}s that have been modified after the 
	 * provided date, or if the date is null a list of all projects.
	 */
	List<Project> getSyncedProjects(String userEmail, Date lastSuccessfulSyncDate);
	
	/**
	 * Searches for all tasks of a user that have been last modified date that 
	 * equal to or after the specified date. If the specified last sync date is 
	 * null all tasks are returned.  
	 * @param userEmail The email of the user-account for which to retrieve the
	 * synced tasks.
	 * @param lastSuccessfulSyncDate The last time synchronization was 
	 * successful.
	 * @return A list of {@link Task}s that have been modified after the 
	 * provided date, or if the date is null a list of all tasks.
	 */
	List<Task> getSyncedTasks(String userEmail, Date lastSuccessfulSyncDate);
	
	/**
	 * Searches for all time registrations of a user that have been last 
	 * modified date that equal to or after the specified date. If the specified 
	 * last sync date is null all time registrations are returned.  
	 * @param userEmail The email of the user-account for which to retrieve the
	 * synced time registrations.
	 * @param lastSuccessfulSyncDate The last time synchronization was 
	 * successful.
	 * @return A list of {@link TimeRegistration}s that have been modified after 
	 * the provided date, or if the date is null a list of all 
	 * time registrations.
	 */
	List<TimeRegistration> getSyncedTimeRegistrations(String userEmail, Date lastSuccessfulSyncDate);
}
