package eu.vranckaert.worktime.json.endpoint;

import eu.vranckaert.worktime.json.exception.service.ServiceRemovesItselfJSONException;
import eu.vranckaert.worktime.json.request.service.ServiceCreationRequest;
import eu.vranckaert.worktime.json.request.service.ServiceRemovalRequest;
import eu.vranckaert.worktime.json.response.service.CreateServiceResponse;
import eu.vranckaert.worktime.json.response.service.RemoveServiceResponse;

public interface ServiceEndpointInterface {
	/**
	 * Creates a new service that has access to the application using the rest
	 * services.
	 * @param request The request.
	 * @return An instance of {link {@link CreateServiceResponse} containing the
	 * newly created service his service-key.
	 */
	CreateServiceResponse createService(ServiceCreationRequest request);
	
	/**
	 * Removes a registered service from the system so it has no longer access
	 * to the application.
	 * @param request
	 * @return An instance of {@link RemoveServiceResponse} that will contain 
	 * the exception {@link ServiceRemovesItselfJSONException} if the service
	 * tries to remove itself. Otherwise it will just be an empty instance.
	 */
	RemoveServiceResponse removeService(ServiceRemovalRequest request);
}
