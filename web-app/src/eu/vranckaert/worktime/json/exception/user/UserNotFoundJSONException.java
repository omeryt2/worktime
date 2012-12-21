package eu.vranckaert.worktime.json.exception.user;

import eu.vranckaert.worktime.json.base.exception.WorkTimeJSONException;

public class UserNotFoundJSONException extends WorkTimeJSONException {
	private String userMail;

	public UserNotFoundJSONException(String requestUrl, String email) {
		super(requestUrl);
		this.userMail = email;
	}

	public String getUserMail() {
		return userMail;
	}

	public void setUserMail(String userMail) {
		this.userMail = userMail;
	}

}
