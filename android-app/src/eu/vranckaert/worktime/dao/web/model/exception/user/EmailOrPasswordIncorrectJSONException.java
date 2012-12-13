package eu.vranckaert.worktime.dao.web.model.exception.user;

import eu.vranckaert.worktime.dao.web.model.base.exception.WorkTimeJSONException;

public class EmailOrPasswordIncorrectJSONException extends WorkTimeJSONException {

	public EmailOrPasswordIncorrectJSONException(String requestUrl) {
		super(requestUrl);
	}

}
