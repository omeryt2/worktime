package eu.vranckaert.worktime.model;

import java.util.Date;

import com.vercer.engine.persist.annotation.Key;
import com.vercer.engine.persist.annotation.Parent;

public class Session {
	@Parent private User user;
	private String sessionKey;
	private Date creationDate;
	private int timesUsed;
	
	public Session() {}
	
	public Session(String sessionKey, User user) {
		this.creationDate = new Date();
		this.sessionKey = sessionKey;
		this.user = user;
		this.timesUsed = 1;
	}

	public String getSessionKey() {
		return sessionKey;
	}
	
	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public int getTimesUsed() {
		return timesUsed;
	}
	
	public void setTimesUsed(int timesUsed) {
		this.timesUsed = timesUsed;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((sessionKey == null) ? 0 : sessionKey.hashCode());
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
		Session other = (Session) obj;
		if (sessionKey == null) {
			if (other.sessionKey != null)
				return false;
		} else if (!sessionKey.equals(other.sessionKey))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}
}
