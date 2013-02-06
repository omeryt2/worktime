package eu.vranckaert.worktime.json.exception.sync;

import eu.vranckaert.worktime.json.base.exception.WorkTimeJSONException;

public class CorruptDataJSONException extends WorkTimeJSONException {

	public CorruptDataJSONException() {}
	
	public CorruptDataJSONException(String requestUrl) {
		super(requestUrl);
	}

}
