package eu.vranckaert.worktime.json.endpoint.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;

import eu.vranckaert.worktime.exception.CorruptDataException;
import eu.vranckaert.worktime.exception.SynchronisationLockedException;
import eu.vranckaert.worktime.exception.SyncronisationFailedException;
import eu.vranckaert.worktime.json.exception.security.ServiceNotAllowedJSONException;
import eu.vranckaert.worktime.json.exception.security.UserNotLoggedInJSONException;
import eu.vranckaert.worktime.json.exception.sync.CorruptDataJSONException;
import eu.vranckaert.worktime.json.exception.sync.SynchronisationLockedJSONException;
import eu.vranckaert.worktime.json.exception.sync.SyncronisationFailedJSONException;
import eu.vranckaert.worktime.json.request.sync.WorkTimeSyncRequest;
import eu.vranckaert.worktime.json.response.sync.WorkTimeSyncResponse;
import eu.vranckaert.worktime.model.sync.EntitySyncResult;
import eu.vranckaert.worktime.security.exception.ServiceNotAllowedException;
import eu.vranckaert.worktime.security.exception.UserNotLoggedInException;
import eu.vranckaert.worktime.security.service.SecurityChecker;
import eu.vranckaert.worktime.service.SyncService;

@Path("sync")
public class SynchronisationEndpoint {
	@Inject
	private SecurityChecker securityChecker;
	
	@Inject
	private SyncService syncService;
	
	@POST
	@Path("all")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public WorkTimeSyncResponse syncAll(WorkTimeSyncRequest request) {
		WorkTimeSyncResponse response = new WorkTimeSyncResponse();
		
		try {
			securityChecker.checkUserLoggedIn(request);
		} catch (ServiceNotAllowedException e) {
			ServiceNotAllowedJSONException exception = new ServiceNotAllowedJSONException("sync/all", request.getServiceKey());
			response.setServiceNotAllowedException(exception);
			return response;
		} catch (UserNotLoggedInException e) {
			UserNotLoggedInJSONException exception = new UserNotLoggedInJSONException("sync/all");
			response.setUserNotLoggedInException(exception);
			return response;
		}
		
		try {
			EntitySyncResult result = syncService.sync(request.getEmail(), request.getConflictConfiguration(), request.getTimeRegistrations());
			response.setSyncResult(result);
			response.setTimeRegistrationsSinceLastSync(syncService.getSyncedTimeRegistrations(request.getEmail(), request.getLastSuccessfulSyncDate()));
			response.setProjectsSinceLastSync(syncService.getSyncedProjects(request.getEmail(), request.getLastSuccessfulSyncDate()));
			response.setTasksSinceLastSync(syncService.getSyncedTasks(request.getEmail(), request.getLastSuccessfulSyncDate()));
		} catch (SyncronisationFailedException e) {
			SyncronisationFailedJSONException jsonException = new SyncronisationFailedJSONException("sync/all");
			response.setSyncronisationFailedJSONException(jsonException);
		} catch (SynchronisationLockedException e) {
			SynchronisationLockedJSONException jsonException = new SynchronisationLockedJSONException("sync/all");
			response.setSynchronisationLockedJSONException(jsonException);
		} catch (CorruptDataException e) {
			CorruptDataJSONException jsonException = new CorruptDataJSONException("sync/all");
			response.setCorruptDataJSONException(jsonException);
		}
		
		return response;
	}
}
