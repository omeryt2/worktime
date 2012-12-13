package eu.vranckaert.worktime.dao.web.model.exception.security;

import eu.vranckaert.worktime.dao.web.model.base.exception.WorkTimeJSONException;

public class UserNotLoggedInJSONException extends WorkTimeJSONException {

	public UserNotLoggedInJSONException(String requestUrl) {
		super(requestUrl);
	}

}
