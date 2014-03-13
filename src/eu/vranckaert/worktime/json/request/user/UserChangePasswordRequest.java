package eu.vranckaert.worktime.json.request.user;

import eu.vranckaert.worktime.json.base.request.AuthenticatedUserRequest;

public class UserChangePasswordRequest extends AuthenticatedUserRequest {
	private String oldPassword;
	private String newPassword;
	
	public String getOldPassword() {
		return oldPassword;
	}
	
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	
	public String getNewPassword() {
		return newPassword;
	}
	
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
}
