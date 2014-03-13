package eu.vranckaert.worktime.json.exception.security;

import eu.vranckaert.worktime.json.base.exception.WorkTimeJSONException;
import eu.vranckaert.worktime.model.Role;

public class UserIncorrectRoleException extends WorkTimeJSONException {
	private Role requiredRole;
	
	public UserIncorrectRoleException() {}
	
	public UserIncorrectRoleException(String requestUrl, Role requiredRole) {
		super(requestUrl);
		this.requiredRole = requiredRole;
	}

	public Role getRequiredRole() {
		return requiredRole;
	}

	public void setRequiredRole(Role requiredRole) {
		this.requiredRole = requiredRole;
	}
}
