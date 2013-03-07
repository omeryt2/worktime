package eu.vranckaert.worktime.json.response.user;

import java.util.Date;

import eu.vranckaert.worktime.json.base.response.WorkTimeResponse;
import eu.vranckaert.worktime.model.Role;

public class UserProfileResponse extends WorkTimeResponse {
	private String firstName;
	private String lastName;
	private String email;
	private Date registeredSince;
	private Date loggedInSince;
	private Role role;
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public Date getRegisteredSince() {
		return registeredSince;
	}
	
	public void setRegisteredSince(Date registeredSince) {
		this.registeredSince = registeredSince;
	}
	
	public Date getLoggedInSince() {
		return loggedInSince;
	}
	
	public void setLoggedInSince(Date loggedInSince) {
		this.loggedInSince = loggedInSince;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
}
