package eu.vranckaert.worktime.json.exception.service;

import eu.vranckaert.worktime.json.base.exception.WorkTimeJSONException;

public class ServiceRemovesItselfJSONException extends WorkTimeJSONException {
	public ServiceRemovesItselfJSONException(String requestUrl) {
		super(requestUrl);
	}
}
