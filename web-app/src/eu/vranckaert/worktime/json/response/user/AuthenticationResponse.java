package eu.vranckaert.worktime.json.response.user;

import javax.xml.bind.annotation.XmlRootElement;

import eu.vranckaert.worktime.json.base.response.WorkTimeResponse;
import eu.vranckaert.worktime.json.exception.FieldRequiredJSONException;
import eu.vranckaert.worktime.json.exception.user.EmailOrPasswordIncorrectJSONException;
import eu.vranckaert.worktime.json.exception.user.InvalidEmailJSONException;
import eu.vranckaert.worktime.json.exception.user.PasswordLengthInvalidJSONException;
import eu.vranckaert.worktime.json.exception.user.RegisterEmailAlreadyInUseJSONException;

@XmlRootElement
public class AuthenticationResponse extends WorkTimeResponse {
	private String sessionKey;
	
	private FieldRequiredJSONException fieldRequiredJSONException;
	private EmailOrPasswordIncorrectJSONException emailOrPasswordIncorrectJSONException;
	private RegisterEmailAlreadyInUseJSONException registerEmailAlreadyInUseJSONException;
	private PasswordLengthInvalidJSONException passwordLengthInvalidJSONException;
	private InvalidEmailJSONException invalidEmailJSONException;

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

	public FieldRequiredJSONException getFieldRequiredJSONException() {
		return fieldRequiredJSONException;
	}

	public void setFieldRequiredJSONException(
			FieldRequiredJSONException fieldRequiredJSONException) {
		this.fieldRequiredJSONException = fieldRequiredJSONException;
		setResultOk(false);
	}

	public EmailOrPasswordIncorrectJSONException getEmailOrPasswordIncorrectJSONException() {
		return emailOrPasswordIncorrectJSONException;
	}

	public void setEmailOrPasswordIncorrectJSONException(
			EmailOrPasswordIncorrectJSONException emailOrPasswordIncorrectJSONException) {
		this.emailOrPasswordIncorrectJSONException = emailOrPasswordIncorrectJSONException;
		setResultOk(false);
	}

	public RegisterEmailAlreadyInUseJSONException getRegisterEmailAlreadyInUseJSONException() {
		return registerEmailAlreadyInUseJSONException;
	}

	public void setRegisterEmailAlreadyInUseJSONException(
			RegisterEmailAlreadyInUseJSONException registerEmailAlreadyInUseJSONException) {
		this.registerEmailAlreadyInUseJSONException = registerEmailAlreadyInUseJSONException;
		setResultOk(false);
	}

	public InvalidEmailJSONException getInvalidEmailJSONException() {
		return invalidEmailJSONException;
	}

	public void setInvalidEmailJSONException(
			InvalidEmailJSONException invalidEmailJSONException) {
		this.invalidEmailJSONException = invalidEmailJSONException;
		setResultOk(false);
	}

	public PasswordLengthInvalidJSONException getPasswordLengthInvalidJSONException() {
		return passwordLengthInvalidJSONException;
	}

	public void setPasswordLengthInvalidJSONException(
			PasswordLengthInvalidJSONException passwordLengthInvalidJSONException) {
		this.passwordLengthInvalidJSONException = passwordLengthInvalidJSONException;
	}
}
