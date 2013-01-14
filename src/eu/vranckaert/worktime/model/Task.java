package eu.vranckaert.worktime.model;

import java.util.Date;

import com.google.appengine.api.datastore.Key;
import com.google.code.twig.annotation.Activate;
import com.google.code.twig.annotation.Entity;
import com.google.code.twig.annotation.GaeKey;
import com.google.code.twig.annotation.Index;
import com.google.code.twig.annotation.Parent;

@Entity(kind="task")
public class Task implements Cloneable {
	@GaeKey private Key key;
	@Index private String name;
	private String comment;
	private Integer order;
	private String flags;
	private boolean finished;
	@Activate @Parent private Project project;
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

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
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
		result = prime * result + ((project == null) ? 0 : project.hashCode());
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
		Task other = (Task) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (project == null) {
			if (other.project != null)
				return false;
		} else if (!project.equals(other.project))
			return false;
		return true;
	}

	public boolean equalsContent(Task task) {
		if (!name.equals(task.getName()) 
				|| ((comment == null && task.getComment() != null) || (comment != null && task.getComment() == null) || (comment != null && task.getComment() != null && !comment.equals(task.getComment())))
				|| ((order == null && task.getOrder() != null) || (order != null && task.getOrder() == null) || (order != null && task.getOrder() != null && !order.equals(task.getOrder())))
				|| ((flags == null && task.getFlags() != null) || (flags != null && task.getFlags() == null) || (flags != null && task.getFlags() != null && !flags.equals(task.getFlags())))
				|| !(finished == task.isFinished())
				|| ((project == null && task.getProject() != null) || (project != null && task.getProject() == null) && (project != null && task.getProject() != null && !project.equals(task.getProject())))) {
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
	public Object clone() throws CloneNotSupportedException {
		Task task = new Task();
		task.setName(name);
		task.setComment(comment);
		task.setOrder(order);
		task.setFlags(flags);
		task.setFinished(finished);
		task.setLastUpdated(lastUpdated);
		task.setSyncKey(syncKey);
		task.setProject((Project) project.clone());
		return task;
	}
}
