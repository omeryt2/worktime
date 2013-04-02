package eu.vranckaert.worktime.json.request.user;

import eu.vranckaert.worktime.json.base.request.RegisteredServiceRequest;

public class ResetPasswordRequest extends RegisteredServiceRequest {
	private String passwordResetKey;
	private String newPassword;
	
	public String getPasswordResetKey() {
		return passwordResetKey;
	}
	
	public void setPasswordResetKey(String passwordResetKey) {
		this.passwordResetKey = passwordResetKey;
	}
	
	public String getNewPassword() {
		return newPassword;
	}
	
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
}
