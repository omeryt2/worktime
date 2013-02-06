package eu.vranckaert.worktime.security.service;

import eu.vranckaert.worktime.model.ServicePlatform;

public interface ServiceService {
	/**
	 * Checks if a service with a certain key is allowed to access the 
	 * application-data.
	 * @param key The key of the service.
	 * @return True if the service can have access, false otherwise.
	 */
	boolean isServiceAllowed(String key);

	/**
	 * Creates a new service that can access the application.
	 * @param appName The name of the application that needs access.
	 * @param contact The contact details (email or postal address) of a
	 * responsible for the service.
	 * @param platform The platform on which this service will work.
	 * @return The generated service-key.
	 */
	String createService(String appName, String contact, ServicePlatform platform);

	/**
	 * Removes the service with a certain key to no longer have access to the
	 * application.
	 * @param serviceKey The key of the service to remove.
	 */
	void removeService(String serviceKey);
}
