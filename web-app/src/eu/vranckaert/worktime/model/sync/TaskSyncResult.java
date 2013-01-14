package eu.vranckaert.worktime.model.sync;

import eu.vranckaert.worktime.model.Task;

public class TaskSyncResult {
	private Task task;	
	private Task syncedTask;
	private EntitySyncResolution resolution;
	
	public TaskSyncResult() {}
	
	public TaskSyncResult(Task origianlTask) {
		this.task = (Task) origianlTask.clone();
	}

	public Task getTask() {
		return task;
	}
	
	public void setTask(Task task) {
		this.task = (Task) task.clone();
	}

	public Task getSyncedTask() {
		return syncedTask;
	}

	public void setSyncedTask(Task syncedTask) {
		this.syncedTask = syncedTask;
	}

	public EntitySyncResolution getResolution() {
		return resolution;
	}

	public void setResolution(EntitySyncResolution resolution) {
		this.resolution = resolution;
	}
}
