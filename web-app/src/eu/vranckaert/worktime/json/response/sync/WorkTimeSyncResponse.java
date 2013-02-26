package eu.vranckaert.worktime.json.response.sync;

import java.util.List;
import java.util.Map;

import eu.vranckaert.worktime.json.base.response.WorkTimeResponse;
import eu.vranckaert.worktime.json.exception.sync.CorruptDataJSONException;
import eu.vranckaert.worktime.json.exception.sync.SynchronisationLockedJSONException;
import eu.vranckaert.worktime.json.exception.sync.SyncronisationFailedJSONException;
import eu.vranckaert.worktime.model.Project;
import eu.vranckaert.worktime.model.Task;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.model.sync.EntitySyncResult;

public class WorkTimeSyncResponse extends WorkTimeResponse {
	private SyncronisationFailedJSONException syncronisationFailedJSONException;
	private SynchronisationLockedJSONException synchronisationLockedJSONException;
	private CorruptDataJSONException corruptDataJSONException;
	private EntitySyncResult syncResult;
	private List<Project> projectsSinceLastSync;
	private List<Task> tasksSinceLastSync;
	private List<TimeRegistration> timeRegistrationsSinceLastSync;
	private Map<String, String> syncRemovalMap;

	public SyncronisationFailedJSONException getSyncronisationFailedJSONException() {
		return syncronisationFailedJSONException;
	}

	public void setSyncronisationFailedJSONException(
			SyncronisationFailedJSONException syncronisationFailedJSONException) {
		this.syncronisationFailedJSONException = syncronisationFailedJSONException;
		setResultOk(false);
	}

	public SynchronisationLockedJSONException getSynchronisationLockedJSONException() {
		return synchronisationLockedJSONException;
	}

	public void setSynchronisationLockedJSONException(
			SynchronisationLockedJSONException synchronisationLockedJSONException) {
		this.synchronisationLockedJSONException = synchronisationLockedJSONException;
		setResultOk(false);
	}

	public CorruptDataJSONException getCorruptDataJSONException() {
		return corruptDataJSONException;
	}

	public void setCorruptDataJSONException(
			CorruptDataJSONException corruptDataJSONException) {
		this.corruptDataJSONException = corruptDataJSONException;
		setResultOk(false);
	}

	public EntitySyncResult getSyncResult() {
		return syncResult;
	}

	public void setSyncResult(EntitySyncResult syncResult) {
		this.syncResult = syncResult;
	}

	public List<Project> getProjectsSinceLastSync() {
		return projectsSinceLastSync;
	}

	public void setProjectsSinceLastSync(List<Project> projectsSinceLastSync) {
		this.projectsSinceLastSync = projectsSinceLastSync;
	}

	public List<Task> getTasksSinceLastSync() {
		return tasksSinceLastSync;
	}

	public void setTasksSinceLastSync(List<Task> tasksSinceLastSync) {
		this.tasksSinceLastSync = tasksSinceLastSync;
	}

	public List<TimeRegistration> getTimeRegistrationsSinceLastSync() {
		return timeRegistrationsSinceLastSync;
	}

	public void setTimeRegistrationsSinceLastSync(
			List<TimeRegistration> timeRegistrationsSinceLastSync) {
		this.timeRegistrationsSinceLastSync = timeRegistrationsSinceLastSync;
	}

	public Map<String, String> getSyncRemovalMap() {
		return syncRemovalMap;
	}

	public void setSyncRemovalMap(Map<String, String> syncRemovalMap) {
		this.syncRemovalMap = syncRemovalMap;
	}
}
