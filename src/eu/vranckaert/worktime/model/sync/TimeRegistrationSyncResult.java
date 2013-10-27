package eu.vranckaert.worktime.model.sync;

import java.util.List;

import eu.vranckaert.worktime.model.Task;
import eu.vranckaert.worktime.model.TimeRegistration;

public class TimeRegistrationSyncResult {
	private TimeRegistration timeRegistration;
	private TimeRegistration syncedTimeRegistration;
	private List<TimeRegistration> syncedTimeRegistrations;
	private EntitySyncResolution resolution;
	
	public TimeRegistrationSyncResult() {}
	
	public TimeRegistrationSyncResult(TimeRegistration originalTimeRegistration) {
		this.timeRegistration = (TimeRegistration) originalTimeRegistration.clone();
	}

	public TimeRegistration getTimeRegistration() {
		return timeRegistration;
	}

	public void setTimeRegistration(TimeRegistration timeRegistration) {
		this.timeRegistration = (TimeRegistration) timeRegistration.clone();
	}

	public TimeRegistration getSyncedTimeRegistration() {
		return syncedTimeRegistration;
	}

	public void setSyncedTimeRegistration(TimeRegistration syncedTimeRegistration) {
		this.syncedTimeRegistration = syncedTimeRegistration;
	}

	public List<TimeRegistration> getSyncedTimeRegistrations() {
		return syncedTimeRegistrations;
	}

	public void setSyncedTimeRegistrations(
			List<TimeRegistration> syncedTimeRegistrations) {
		this.syncedTimeRegistrations = syncedTimeRegistrations;
	}

	public EntitySyncResolution getResolution() {
		return resolution;
	}

	public void setResolution(EntitySyncResolution resolution) {
		this.resolution = resolution;
	}
}
