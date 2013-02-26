package eu.vranckaert.worktime.json.request.sync;

import java.util.Date;
import java.util.List;
import java.util.Map;

import eu.vranckaert.worktime.json.base.request.AuthenticatedUserRequest;
import eu.vranckaert.worktime.model.Project;
import eu.vranckaert.worktime.model.Task;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.model.sync.SyncConflictConfiguration;

public class WorkTimeSyncRequest extends AuthenticatedUserRequest {
	private Date lastSuccessfulSyncDate;
	private SyncConflictConfiguration conflictConfiguration;
    private List<Project> projects;
    private List<Task> tasks;
    private List<TimeRegistration> timeRegistrations;
    private Map<String, String> syncRemovalMap;

	public Date getLastSuccessfulSyncDate() {
		return lastSuccessfulSyncDate;
	}

	public void setLastSuccessfulSyncDate(Date lastSuccessfulSyncDate) {
		this.lastSuccessfulSyncDate = lastSuccessfulSyncDate;
	}

	public SyncConflictConfiguration getConflictConfiguration() {
		return conflictConfiguration;
	}

	public void setConflictConfiguration(
			SyncConflictConfiguration conflictConfiguration) {
		this.conflictConfiguration = conflictConfiguration;
	}

	public List<Project> getProjects() {
		return projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	public List<TimeRegistration> getTimeRegistrations() {
		return timeRegistrations;
	}

	public void setTimeRegistrations(List<TimeRegistration> timeRegistrations) {
		this.timeRegistrations = timeRegistrations;
	}

	public Map<String, String> getSyncRemovalMap() {
		return syncRemovalMap;
	}

	public void setSyncRemovalMap(Map<String, String> syncRemovalMap) {
		this.syncRemovalMap = syncRemovalMap;
	}
}
