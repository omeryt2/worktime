package eu.vranckaert.worktime.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.code.twig.annotation.Activate;
import com.google.code.twig.annotation.Child;
import com.google.code.twig.annotation.Entity;
import com.google.code.twig.annotation.Id;

import eu.vranckaert.worktime.model.Session.Platform;

@Entity(kind="user")
public class User implements Cloneable {
	@Id private String email;
	private String passwordHash;
	private String lastName;
	private String firstName;
	private Date registrationDate;
	private Date lastLoginDate;
	private Role role = Role.USER;
	
	private String profileImageUrl;
	
	@Activate @Child private List<Session> sessions;
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPasswordHash() {
		return passwordHash;
	}
	
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public Role getRole() {
		return role;
	}
	
	public void setRole(Role role) {
		this.role = role;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}
	
	public List<Session> getSessions() {
		return sessions;
	}

	public void setSessions(List<Session> sessions) {
		this.sessions = sessions;
	}

	/**
	 * Add a {@link Session} object for this user based on the provided session
	 * key. The session will be persisted when the user is persisted/updated. 
	 * @param sessionKey The session key.
	 */
	public void addSessionKey(String sessionKey, Platform platform) {
		if (sessions == null) {
			sessions = new ArrayList<Session>();
		}
		
		Session session = new Session(sessionKey, this, platform);
		sessions.add(session);
	}

	/**
	 * Removes a {@link Session} object from the list of sessions for this user
	 * (only if a session with this key exists).<br/>
	 * <b>CAUTION</b>: This does not remove the {@link Session} object from the
	 * database.
	 * @param sessionKey The session key.
	 */
	public void removeSessionKey(String sessionKey) {
		Session sessionForRemoval = null;
		if (sessions != null) {
			for (Session session : sessions) {
				if (session.getSessionKey().equals(sessionKey)) {
					sessionForRemoval = session;
					break;
				}
			}
		}
		
		if (sessionForRemoval != null) {
			sessions.remove(sessionForRemoval);
		}
	}
	
	public String getFullName() {
		return firstName + " " + lastName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
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
		User other = (User) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		return true;
	}

	@Override
	public User clone() {
		User user = new User();
		user.setEmail(getEmail());
		user.setFirstName(getFirstName());
		user.setLastName(getLastName());
		user.setLastLoginDate(getLastLoginDate());
		user.setRegistrationDate(getRegistrationDate());
		user.setRole(getRole());
		return user;
	}
	
	public static User getTechnicalUser() {
		User from = new User();
		from.setEmail("no-reply@worktime-web.appspotmail.com");
		from.setFirstName("Work");
		from.setLastName("Time");
		return from;
	}
}
