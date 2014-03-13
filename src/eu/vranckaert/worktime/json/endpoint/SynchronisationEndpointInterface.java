package eu.vranckaert.worktime.json.endpoint;

import eu.vranckaert.worktime.json.request.sync.WorkTimeSyncRequest;
import eu.vranckaert.worktime.json.response.sync.WorkTimeSyncResponse;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.model.sync.EntitySyncResult;

public interface SynchronisationEndpointInterface {
	/**
	 * Registers a new user to the application.
	 * Synchronizes a bunch of incoming {@link TimeRegistration}s to the local
	 * database. 
	 * @param request The request containing the time registrations to be 
	 * synced.
	 * @return Returns an instance of {@link WorkTimeSyncResponse} containing
	 * a the {@link EntitySyncResult} in which is noted for each 
	 * {@link Project}, {@link Task} and {@link TimeRegistration} if it is 
	 * synced or ignored or if replaces other objects... It will also contain a
	 * list of {@link TimeRegistration} containing all time registration on the
	 * server for this user.
	 */
	WorkTimeSyncResponse syncAll(WorkTimeSyncRequest request);
}
