package eu.vranckaert.worktime.model.sync;

import eu.vranckaert.worktime.model.Project;

public class ProjectSyncResult {
	private Project project;
	private Project syncedProject;
	private EntitySyncResolution resolution;
	
	public ProjectSyncResult() {}
	
	public ProjectSyncResult(Project originalProject) {
		try {
			this.project = (Project) originalProject.clone();
		} catch (CloneNotSupportedException e) {
			this.project = null;
		}
	}
	
	public Project getProject() {
		return project;
	}
	
	public void setProject(Project project) {
		try {
			this.project = (Project) project.clone();
		} catch (CloneNotSupportedException e) {
			this.project = null;
		}
	}
	
	public Project getSyncedProject() {
		return syncedProject;
	}
	
	public void setSyncedProject(Project syncedProject) {
		this.syncedProject = syncedProject;
	}

	public EntitySyncResolution getResolution() {
		return resolution;
	}

	public void setResolution(EntitySyncResolution resolution) {
		this.resolution = resolution;
	}
}
