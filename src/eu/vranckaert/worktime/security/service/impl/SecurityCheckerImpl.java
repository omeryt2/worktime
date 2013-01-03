package eu.vranckaert.worktime.security.service.impl;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;

import eu.vranckaert.worktime.json.base.request.AuthenticatedUserRequest;
import eu.vranckaert.worktime.json.base.request.RegisteredServiceRequest;
import eu.vranckaert.worktime.model.Role;
import eu.vranckaert.worktime.model.User;
import eu.vranckaert.worktime.security.exception.ServiceNotAllowedException;
import eu.vranckaert.worktime.security.exception.UserNotAdminException;
import eu.vranckaert.worktime.security.exception.UserNotLoggedInException;
import eu.vranckaert.worktime.security.service.SecurityChecker;
import eu.vranckaert.worktime.security.service.ServiceService;
import eu.vranckaert.worktime.security.service.UserService;

public class SecurityCheckerImpl implements SecurityChecker {
	@Inject ServiceService serviceService;
	@Inject UserService userService;
	
	public void checkService(RegisteredServiceRequest request) throws ServiceNotAllowedException {
		if (!serviceService.isServiceAllowed(request.getServiceKey())) {
			throw new ServiceNotAllowedException();
		}
	}
	
	public void checkUserLoggedIn(AuthenticatedUserRequest request) throws ServiceNotAllowedException, UserNotLoggedInException {
		checkService(request);
		
		if (StringUtils.isBlank(request.getEmail()) || StringUtils.isBlank(request.getSessionKey())) {
			throw new UserNotLoggedInException();
		}
		
		if (!userService.isLoggedIn(request.getEmail(), request.getSessionKey())) {
			throw new UserNotLoggedInException();
		}
	}
	
	public void checkUserIsAdmin(AuthenticatedUserRequest request) throws ServiceNotAllowedException, UserNotLoggedInException, UserNotAdminException {
		checkUserLoggedIn(request);
		
		User user = userService.findUser(request.getEmail());
		
		if (user.getRole() != Role.ADMIN) {
			throw new UserNotAdminException();
		}
	}
}
