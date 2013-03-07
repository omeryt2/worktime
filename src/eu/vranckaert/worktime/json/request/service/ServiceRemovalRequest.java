package eu.vranckaert.worktime.json.request.service;

import eu.vranckaert.worktime.json.base.request.AuthenticatedUserRequest;

public class ServiceRemovalRequest extends AuthenticatedUserRequest {
	private String serviceKeyForRemoval;

	public String getServiceKeyForRemoval() {
		return serviceKeyForRemoval;
	}

	public void setServiceKeyForRemoval(String serviceKeyForRemoval) {
		this.serviceKeyForRemoval = serviceKeyForRemoval;
	}
}
