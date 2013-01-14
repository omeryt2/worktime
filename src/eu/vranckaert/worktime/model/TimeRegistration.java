package eu.vranckaert.worktime.model;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.google.appengine.api.datastore.Key;
import com.google.code.twig.annotation.Activate;
import com.google.code.twig.annotation.Entity;
import com.google.code.twig.annotation.GaeKey;
import com.google.code.twig.annotation.Parent;

@Entity(kind="timeRegistration")
public class TimeRegistration {
	@GaeKey private Key key;
	private Date startTime;
	private Date endTime;
	private String comment;
	private String flags;
	@Activate @Parent private Task task;
	private Date lastUpdated;
	private String syncKey;

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getFlags() {
		return flags;
	}

	public void setFlags(String flags) {
		this.flags = flags;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public String getSyncKey() {
		return syncKey;
	}

	public void setSyncKey(String syncKey) {
		this.syncKey = syncKey;
	}
	
	@JsonIgnore(true)
	public boolean isOngoingTimeRegistration() {
		if (endTime == null) 
			return true; 
		else
			return false;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
		result = prime * result
				+ ((startTime == null) ? 0 : startTime.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimeRegistration other = (TimeRegistration) obj;
		if (endTime == null) {
			if (other.endTime != null)
				return false;
		} else if (!endTime.equals(other.endTime))
			return false;
		if (startTime == null) {
			if (other.startTime != null)
				return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		return true;
	}

	public boolean equalsContent(TimeRegistration timeRegistration) {
		if (((comment == null && timeRegistration.getComment() != null) || (comment != null && timeRegistration.getComment() == null) || (comment != null && timeRegistration.getComment() != null && !comment.equals(timeRegistration.getComment())))
				|| ((flags == null && timeRegistration.getFlags() != null) || (flags != null && timeRegistration.getFlags() == null) || (flags != null && timeRegistration.getFlags() != null && !flags.equals(timeRegistration.getFlags())))
				|| ((task == null && timeRegistration.getTask() != null) || (task != null && timeRegistration.getTask() == null) && (task != null && timeRegistration.getTask() != null && !task.equals(timeRegistration.getTask())))) {
			return false;
		}
		
		return true;
	}
	
	public boolean isModifiedAfter(Date lastModifiedDate) {
		if (lastUpdated == null && lastModifiedDate == null) {
			return false;
		} else if (lastUpdated != null && lastModifiedDate == null) {
			return true;
		} else if (lastUpdated == null && lastModifiedDate != null) {
			return false;
		} else {
			return lastUpdated.after(lastModifiedDate);
		}
	}

	@Override
	public TimeRegistration clone() throws CloneNotSupportedException {
		TimeRegistration timeRegistration = new TimeRegistration();
		timeRegistration.setStartTime(startTime);
		timeRegistration.setEndTime(endTime);
		timeRegistration.setComment(comment);
		timeRegistration.setFlags(flags);
		timeRegistration.setLastUpdated(lastUpdated);
		timeRegistration.setSyncKey(syncKey);
		timeRegistration.setTask((Task) task.clone());
		return timeRegistration;
	}
}
