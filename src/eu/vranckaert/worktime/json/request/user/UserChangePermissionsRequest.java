package eu.vranckaert.worktime.json.request.user;

import eu.vranckaert.worktime.json.base.request.AuthenticatedUserRequest;
import eu.vranckaert.worktime.model.Role;

public class UserChangePermissionsRequest extends AuthenticatedUserRequest {
	private String userToChange;
	private Role newRole;
	
	public String getUserToChange() {
		return userToChange;
	}
	
	public void setUserToChange(String userToChange) {
		this.userToChange = userToChange;
	}
	
	public Role getNewRole() {
		return newRole;
	}
	
	public void setNewRole(Role newRole) {
		this.newRole = newRole;
	}
}
