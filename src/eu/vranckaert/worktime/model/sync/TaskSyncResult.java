package eu.vranckaert.worktime.model.sync;

import eu.vranckaert.worktime.model.Task;

public class TaskSyncResult {
	private Task task;	
	private Task syncedTask;
	private EntitySyncResolution resolution;
	
	public TaskSyncResult() {}
	
	public TaskSyncResult(Task origianlTask) {
		try {
			this.task = (Task) origianlTask.clone();
		} catch (CloneNotSupportedException e) {
			this.task = null;
		}
	}

	public Task getTask() {
		return task;
	}
	
	public void setTask(Task task) {
		try {
			this.task = (Task) task.clone();
		} catch (CloneNotSupportedException e) {
			this.task = null;
		}
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
