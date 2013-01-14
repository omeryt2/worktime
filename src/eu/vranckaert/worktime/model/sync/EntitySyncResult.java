package eu.vranckaert.worktime.model.sync;

import java.util.List;

public class EntitySyncResult {
	private List<ProjectSyncResult> projectSyncResults;
	private List<TaskSyncResult> taskSyncResults;
	private List<TimeRegistrationSyncResult> timeRegistrationSyncResults;

	public List<ProjectSyncResult> getProjectSyncResults() {
		return projectSyncResults;
	}

	public void setProjectSyncResults(List<ProjectSyncResult> projectSyncResults) {
		this.projectSyncResults = projectSyncResults;
	}

	public List<TaskSyncResult> getTaskSyncResults() {
		return taskSyncResults;
	}

	public void setTaskSyncResults(List<TaskSyncResult> taskSyncResults) {
		this.taskSyncResults = taskSyncResults;
	}

	public List<TimeRegistrationSyncResult> getTimeRegistrationSyncResults() {
		return timeRegistrationSyncResults;
	}

	public void setTimeRegistrationSyncResults(
			List<TimeRegistrationSyncResult> timeRegistrationSyncResults) {
		this.timeRegistrationSyncResults = timeRegistrationSyncResults;
	}
}
