package eu.vranckaert.worktime.json.request.service;

import eu.vranckaert.worktime.json.base.request.AuthenticatedUserRequest;
import eu.vranckaert.worktime.model.ServicePlatform;

public class ServiceCreationRequest extends AuthenticatedUserRequest {
	private String appName;
	private ServicePlatform platform;
	private String contact;
	
	public String getAppName() {
		return appName;
	}
	
	public void setAppName(String appName) {
		this.appName = appName;
	}
	
	public ServicePlatform getPlatform() {
		return platform;
	}
	
	public void setPlatform(ServicePlatform platform) {
		this.platform = platform;
	}
	
	public String getContact() {
		return contact;
	}
	
	public void setContact(String contact) {
		this.contact = contact;
	}
}
