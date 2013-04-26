package eu.vranckaert.worktime.model;

import java.util.Calendar;
import java.util.Date;

import com.google.appengine.api.datastore.Key;
import com.google.code.twig.annotation.Entity;
import com.google.code.twig.annotation.GaeKey;
import com.google.code.twig.annotation.Parent;

@Entity(kind="session")
public class Session {
	@GaeKey private Key key;
	@Parent private User user;
	private String sessionKey;
	private Date creationDate;
	private int timesUsed;
	private Date lastTimeUsed;
	private Platform platform;
	
	public Session() {}
	
	public Session(String sessionKey, User user, Platform platform) {
		this.creationDate = new Date();
		this.sessionKey = sessionKey;
		this.user = user;
		this.timesUsed = 1;
		this.lastTimeUsed = new Date();
		this.platform = platform;
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
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

	public Date getLastTimeUsed() {
		return lastTimeUsed;
	}

	public void setLastTimeUsed(Date lastTimeUsed) {
		this.lastTimeUsed = lastTimeUsed;
	}

	public Platform getPlatform() {
		if (platform == null) {
			platform = Platform.OTHER;
		}
		return platform;
	}

	public void setPlatform(Platform platform) {
		if (platform == null) {
			platform = Platform.OTHER;
		}
		this.platform = platform;
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
	
	public enum Platform {
		WEB, OTHER;
	}

	public boolean isExpired() {
		switch (getPlatform()) {
		case WEB:
			Date now = new Date();
			Calendar expirationDate = Calendar.getInstance();
			if (lastTimeUsed != null)
				expirationDate.setTime(lastTimeUsed);
			else
				expirationDate.setTime(creationDate);
			expirationDate.add(Calendar.HOUR_OF_DAY, 24);
			if (expirationDate.getTime().before(now)) {
				return true;
			}
			break;
		}
		
		return false;
	}
}
