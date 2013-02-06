package eu.vranckaert.worktime.model;

import java.util.Date;

import com.google.appengine.api.datastore.Key;
import com.google.code.twig.annotation.Activate;
import com.google.code.twig.annotation.Entity;
import com.google.code.twig.annotation.GaeKey;
import com.google.code.twig.annotation.Index;
import com.google.code.twig.annotation.Parent;

@Entity(kind="project")
public class Project implements Cloneable {
	@GaeKey private Key key;
	@Index private String name;
	private String comment;
	private Integer order;
	private boolean defaultValue;
	private String flags;
	private boolean finished;
	@Activate @Parent private User user;
	@Index private Date lastUpdated;
	@Index private String syncKey;

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public boolean isDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(boolean defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getFlags() {
		return flags;
	}

	public void setFlags(String flags) {
		this.flags = flags;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		Project other = (Project) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	public boolean equalsContent(Project project) {
		if (!name.equals(project.getName()) 
				|| ((comment == null && project.getComment() != null) || (comment != null && project.getComment() == null) || (comment != null && project.getComment() != null && !comment.equals(project.getComment())))
				|| ((order == null && project.getOrder() != null) || (order != null && project.getOrder() == null) || (order != null && project.getOrder() != null && !order.equals(project.getOrder())))
				|| ((flags == null && project.getFlags() != null) || (flags != null && project.getFlags() == null) || (flags != null && project.getFlags() != null && !flags.equals(project.getFlags())))
				|| !(finished == project.isFinished())
				|| !(defaultValue == project.isDefaultValue())) {
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
	public Object clone() {
		Project project = new Project();
		project.setComment(comment);
		project.setDefaultValue(defaultValue);
		project.setFinished(finished);
		project.setFlags(flags);
		project.setLastUpdated(lastUpdated);
		project.setName(name);
		project.setOrder(order);
		project.setSyncKey(syncKey);
		project.setUser(user);
		return project;
	}
}
