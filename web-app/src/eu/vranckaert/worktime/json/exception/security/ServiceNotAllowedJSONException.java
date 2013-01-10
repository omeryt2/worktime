package eu.vranckaert.worktime.json.exception.security;

import eu.vranckaert.worktime.json.base.exception.WorkTimeJSONException;

/**
 * The service is not allowed to execute the request.
 * @author dirkvranckaert
 */
public class ServiceNotAllowedJSONException extends WorkTimeJSONException {
	private String serviceKey;
	
	public ServiceNotAllowedJSONException() {}
	
	public ServiceNotAllowedJSONException(String requestUrl, String serviceKey) {
		super(requestUrl);
		this.serviceKey = serviceKey;
	}

	public String getServiceKey() {
		return serviceKey;
	}

	public void setServiceKey(String serviceKey) {
		this.serviceKey = serviceKey;
	}
}
