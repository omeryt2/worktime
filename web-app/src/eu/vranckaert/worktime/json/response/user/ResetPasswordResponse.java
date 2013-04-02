package eu.vranckaert.worktime.json.response.user;

import eu.vranckaert.worktime.json.base.response.WorkTimeResponse;
import eu.vranckaert.worktime.json.exception.FieldRequiredJSONException;
import eu.vranckaert.worktime.json.exception.user.InvalidPasswordResetKeyJSONException;
import eu.vranckaert.worktime.json.exception.user.PasswordLengthInvalidJSONException;
import eu.vranckaert.worktime.json.exception.user.PasswordResetKeyAlreadyUsedJSONException;
import eu.vranckaert.worktime.json.exception.user.PasswordResetKeyExpiredJSONException;
import eu.vranckaert.worktime.security.exception.InvalidPasswordResetKeyException;
import eu.vranckaert.worktime.security.exception.PasswordResetKeyAlreadyUsedException;
import eu.vranckaert.worktime.security.exception.PasswordResetKeyExpiredException;

public class ResetPasswordResponse extends WorkTimeResponse {
	private FieldRequiredJSONException fieldRequiredJSONException;
	private PasswordLengthInvalidJSONException passwordLengthInvalidJSONException;
	private InvalidPasswordResetKeyJSONException invalidPasswordResetKeyJSONException;
	private PasswordResetKeyAlreadyUsedJSONException passwordResetKeyAlreadyUsedJSONException;
	private PasswordResetKeyExpiredJSONException passwordResetKeyExpiredJSONException;
	
	public FieldRequiredJSONException getFieldRequiredJSONException() {
		return fieldRequiredJSONException;
	}
	
	public void setFieldRequiredJSONException(
			FieldRequiredJSONException fieldRequiredJSONException) {
		this.fieldRequiredJSONException = fieldRequiredJSONException;
	}
	
	public PasswordLengthInvalidJSONException getPasswordLengthInvalidJSONException() {
		return passwordLengthInvalidJSONException;
	}
	
	public void setPasswordLengthInvalidJSONException(
			PasswordLengthInvalidJSONException passwordLengthInvalidJSONException) {
		this.passwordLengthInvalidJSONException = passwordLengthInvalidJSONException;
	}

	public InvalidPasswordResetKeyJSONException getInvalidPasswordResetKeyJSONException() {
		return invalidPasswordResetKeyJSONException;
	}

	public void setInvalidPasswordResetKeyJSONException(
			InvalidPasswordResetKeyJSONException invalidPasswordResetKeyJSONException) {
		this.invalidPasswordResetKeyJSONException = invalidPasswordResetKeyJSONException;
	}

	public PasswordResetKeyAlreadyUsedJSONException getPasswordResetKeyAlreadyUsedJSONException() {
		return passwordResetKeyAlreadyUsedJSONException;
	}

	public void setPasswordResetKeyAlreadyUsedJSONException(
			PasswordResetKeyAlreadyUsedJSONException passwordResetKeyAlreadyUsedJSONException) {
		this.passwordResetKeyAlreadyUsedJSONException = passwordResetKeyAlreadyUsedJSONException;
	}

	public PasswordResetKeyExpiredJSONException getPasswordResetKeyExpiredJSONException() {
		return passwordResetKeyExpiredJSONException;
	}

	public void setPasswordResetKeyExpiredJSONException(
			PasswordResetKeyExpiredJSONException passwordResetKeyExpiredJSONException) {
		this.passwordResetKeyExpiredJSONException = passwordResetKeyExpiredJSONException;
	}
}
