package eu.vranckaert.worktime.json.exception.user;

import eu.vranckaert.worktime.json.base.exception.WorkTimeJSONException;

public class RegisterEmailAlreadyInUseJSONException extends WorkTimeJSONException {
	private String email;

	public RegisterEmailAlreadyInUseJSONException(String requestUrl, String email) {
		super(requestUrl);
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
