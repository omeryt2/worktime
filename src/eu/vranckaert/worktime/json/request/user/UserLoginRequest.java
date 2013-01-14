package eu.vranckaert.worktime.json.request.user;

import eu.vranckaert.worktime.json.base.request.RegisteredServiceRequest;

public class UserLoginRequest extends RegisteredServiceRequest {
	private String email;
	private String password;
	
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
}
