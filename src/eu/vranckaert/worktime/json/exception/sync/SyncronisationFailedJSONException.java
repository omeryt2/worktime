package eu.vranckaert.worktime.json.exception.sync;

import eu.vranckaert.worktime.json.base.exception.WorkTimeJSONException;

public class SyncronisationFailedJSONException extends WorkTimeJSONException {

	public SyncronisationFailedJSONException() {}
	
	public SyncronisationFailedJSONException(String requestUrl) {
		super(requestUrl);
	}

}
