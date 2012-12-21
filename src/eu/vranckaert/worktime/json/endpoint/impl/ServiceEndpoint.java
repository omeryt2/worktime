package eu.vranckaert.worktime.json.endpoint.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;

import eu.vranckaert.worktime.json.endpoint.ServiceEndpointInterface;
import eu.vranckaert.worktime.json.exception.security.ServiceNotAllowedJSONException;
import eu.vranckaert.worktime.json.exception.security.UserIncorrectRoleException;
import eu.vranckaert.worktime.json.exception.security.UserNotLoggedInJSONException;
import eu.vranckaert.worktime.json.exception.service.ServiceRemovesItselfJSONException;
import eu.vranckaert.worktime.json.request.service.ServiceCreationRequest;
import eu.vranckaert.worktime.json.request.service.ServiceRemovalRequest;
import eu.vranckaert.worktime.json.response.service.CreateServiceResponse;
import eu.vranckaert.worktime.json.response.service.RemoveServiceResponse;
import eu.vranckaert.worktime.model.Role;
import eu.vranckaert.worktime.security.exception.ServiceNotAllowedException;
import eu.vranckaert.worktime.security.exception.UserNotAdminException;
import eu.vranckaert.worktime.security.exception.UserNotLoggedInException;
import eu.vranckaert.worktime.security.service.SecurityChecker;
import eu.vranckaert.worktime.security.service.ServiceService;
import eu.vranckaert.worktime.security.service.impl.SecurityCheckerImpl;

@Path("service")
public class ServiceEndpoint implements ServiceEndpointInterface {
	@Inject
	private ServiceService serviceService;
	
	@Inject
	private SecurityChecker securityChecker;
	
	/**
	 * Creates a new service that has access to the application using the rest
	 * services.
	 * @param request The request.
	 * @return An instance of {link {@link CreateServiceResponse} containing the
	 * newly created service his service-key.
	 */
	@POST
	@Path("createService")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public CreateServiceResponse createService(ServiceCreationRequest request) {
		CreateServiceResponse response = new CreateServiceResponse();
		
		try {
			securityChecker.checkUserIsAdmin(request);
		} catch (ServiceNotAllowedException e) {
			ServiceNotAllowedJSONException exception = new ServiceNotAllowedJSONException("serivce/createService", request.getServiceKey());
			response.setServiceNotAllowedException(exception);
			return response;
		} catch (UserNotLoggedInException e) {
			UserNotLoggedInJSONException exception = new UserNotLoggedInJSONException("serivce/createService");
			response.setUserNotLoggedInException(exception);
			return response;
		} catch (UserNotAdminException e) {
			UserIncorrectRoleException exception = new UserIncorrectRoleException("serivce/createService", Role.ADMIN);
			response.setUserIncorrectRoleException(exception);
			return response;
		}
		
		String serviceKey = serviceService.createService(
				request.getAppName(), 
				request.getContact(), 
				request.getPlatform()
		);
		response.setServiceKey(serviceKey);
		return response;
	}
	
	/**
	 * Removes a registered service from the system so it has no longer access
	 * to the application.
	 * @param request
	 * @return An instance of {@link RemoveServiceResponse} that will contain 
	 * the exception {@link ServiceRemovesItselfJSONException} if the service
	 * tries to remove itself. Otherwise it will just be an empty instance.
	 */
	@POST
	@Path("removeService")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public RemoveServiceResponse removeService(ServiceRemovalRequest request) {
		RemoveServiceResponse response = new RemoveServiceResponse();
		
		try {
			securityChecker.checkUserIsAdmin(request);
		} catch (ServiceNotAllowedException e) {
			ServiceNotAllowedJSONException exception = new ServiceNotAllowedJSONException("serivce/removeSerivce", request.getServiceKey());
			response.setServiceNotAllowedException(exception);
			return response;
		} catch (UserNotLoggedInException e) {
			UserNotLoggedInJSONException exception = new UserNotLoggedInJSONException("serivce/removeSerivce");
			response.setUserNotLoggedInException(exception);
			return response;
		} catch (UserNotAdminException e) {
			UserIncorrectRoleException exception = new UserIncorrectRoleException("serivce/removeSerivce", Role.ADMIN);
			response.setUserIncorrectRoleException(exception);
			return response;
		}
		
		if (request.getServiceKey().equals(request.getServiceKeyForRemoval())) {
			response.setServiceRemovesItselfJSONException(new ServiceRemovesItselfJSONException("serivce/removeSerivce"));
		}
		
		serviceService.removeService(request.getServiceKeyForRemoval());
		return response;
	}
}
