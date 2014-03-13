package eu.vranckaert.worktime.json.request.user;

import eu.vranckaert.worktime.json.base.request.RegisteredServiceRequest;

public class UserRegistrationRequest extends RegisteredServiceRequest {
	private String email;
	private String password;
	private String lastName;
	private String firstName;
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
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
}
