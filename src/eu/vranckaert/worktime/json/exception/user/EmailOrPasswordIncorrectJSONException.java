package eu.vranckaert.worktime.json.exception.user;

import eu.vranckaert.worktime.json.base.exception.WorkTimeJSONException;

public class EmailOrPasswordIncorrectJSONException extends WorkTimeJSONException {	
	public EmailOrPasswordIncorrectJSONException(String requestUrl) {
		super(requestUrl);
	}
}
