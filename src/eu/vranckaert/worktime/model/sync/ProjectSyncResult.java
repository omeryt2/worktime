package eu.vranckaert.worktime.model.sync;

import eu.vranckaert.worktime.model.Project;

public class ProjectSyncResult {
	private Project project;
	private Project syncedProject;
	private EntitySyncResolution resolution;
	
	public ProjectSyncResult() {}
	
	public ProjectSyncResult(Project originalProject) {
		this.project = (Project) originalProject.clone();
	}
	
	public Project getProject() {
		return project;
	}
	
	public void setProject(Project project) {
		this.project = (Project) project.clone();
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
