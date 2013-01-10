package eu.vranckaert.worktime.json.exception.sync;

import eu.vranckaert.worktime.json.base.exception.WorkTimeJSONException;

public class SynchronisationLockedJSONException extends WorkTimeJSONException {

	public SynchronisationLockedJSONException() {}
	
	public SynchronisationLockedJSONException(String requestUrl) {
		super(requestUrl);
	}

}
