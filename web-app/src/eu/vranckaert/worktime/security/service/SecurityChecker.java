package eu.vranckaert.worktime.security.service;

import eu.vranckaert.worktime.json.base.request.AuthenticatedUserRequest;
import eu.vranckaert.worktime.json.base.request.RegisteredServiceRequest;
import eu.vranckaert.worktime.security.exception.ServiceNotAllowedException;
import eu.vranckaert.worktime.security.exception.UserNotAdminException;
import eu.vranckaert.worktime.security.exception.UserNotLoggedInException;

public interface SecurityChecker {
	/**
	 * Checks if a service is allowed to access the application data.
	 * @param request The request containing the service key that is requesting
	 * access.
	 * @throws ServiceNotAllowedException Thrown if access is not allowed.
	 */
	void checkService(RegisteredServiceRequest request) throws ServiceNotAllowedException;
	
	/**
	 * Checks if the provided user is logged in. This method also checks for the
	 * service to be allowed.
	 * @param request The request containing the email and the session key of
	 * the user. It also contains the the service-key of the service that wants
	 * access.
	 * @throws ServiceNotAllowedException Thrown if access for the service is 
	 * not allowed. 
	 * @throws UserNotLoggedInException Thrown if the user is not logged in
	 * (means that email is not found, the session key is not found or the
	 * provided session key does not match with the user).
	 */
	void checkUserLoggedIn(AuthenticatedUserRequest request) throws ServiceNotAllowedException, UserNotLoggedInException;
	
	/**
	 * Checks if the provided user is logged in and has admin rights. This 
	 * method also checks for the service to be allowed.
	 * @param request The request containing the email and the session key of
	 * the user. It also contains the the service-key of the service that wants
	 * access.
	 * @throws ServiceNotAllowedException Thrown if access for the service is 
	 * not allowed. 
	 * @throws UserNotLoggedInException Thrown if the user is not logged in
	 * (means that email is not found, the session key is not found or the
	 * provided session key does not match with the user).
	 * @throws UserNotAdminException Thrown in the specified logged in user does
	 * not have the admin role assigned.
	 */
	void checkUserIsAdmin(AuthenticatedUserRequest request) throws ServiceNotAllowedException, UserNotLoggedInException, UserNotAdminException;
}
