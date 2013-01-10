package eu.vranckaert.worktime.json.request.sync;

import java.util.Date;
import java.util.List;

import eu.vranckaert.worktime.json.base.request.AuthenticatedUserRequest;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.model.sync.SyncConflictConfiguration;

public class WorkTimeSyncRequest extends AuthenticatedUserRequest {
	private Date lastSuccessfulSyncDate;
	private SyncConflictConfiguration conflictConfiguration;
	private List<TimeRegistration> timeRegistrations;

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

	public List<TimeRegistration> getTimeRegistrations() {
		return timeRegistrations;
	}

	public void setTimeRegistrations(List<TimeRegistration> timeRegistrations) {
		this.timeRegistrations = timeRegistrations;
	}
}
