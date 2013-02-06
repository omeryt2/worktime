package eu.vranckaert.worktime.json.exception.security;

import eu.vranckaert.worktime.json.base.exception.WorkTimeJSONException;

public class UserNotLoggedInJSONException extends WorkTimeJSONException {	
	public UserNotLoggedInJSONException() {}
	
	public UserNotLoggedInJSONException(String requestUrl) {
		super(requestUrl);
	}
}
