package eu.vranckaert.worktime.json.exception.user;

import eu.vranckaert.worktime.json.base.exception.WorkTimeJSONException;

public class InvalidEmailJSONException extends WorkTimeJSONException {
	private String email;

	public InvalidEmailJSONException(String requestUrl, String email) {
		super(requestUrl);
		this.email = email;
	}

}
