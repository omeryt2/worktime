package eu.vranckaert.worktime.json.exception.user;

import eu.vranckaert.worktime.json.base.exception.WorkTimeJSONException;

public class PasswordLengthInvalidJSONException extends WorkTimeJSONException {
	public PasswordLengthInvalidJSONException() {}
	
	public PasswordLengthInvalidJSONException(String requestUrl) {
		super(requestUrl);
	}
}
