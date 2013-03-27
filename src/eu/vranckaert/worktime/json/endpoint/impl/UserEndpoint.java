package eu.vranckaert.worktime.json.endpoint.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import com.google.inject.Inject;

import eu.vranckaert.worktime.json.base.request.AuthenticatedUserRequest;
import eu.vranckaert.worktime.json.base.request.RegisteredServiceRequest;
import eu.vranckaert.worktime.json.endpoint.UserEndPointInterface;
import eu.vranckaert.worktime.json.exception.FieldRequiredJSONException;
import eu.vranckaert.worktime.json.exception.security.ServiceNotAllowedJSONException;
import eu.vranckaert.worktime.json.exception.security.UserIncorrectRoleException;
import eu.vranckaert.worktime.json.exception.security.UserNotLoggedInJSONException;
import eu.vranckaert.worktime.json.exception.user.EmailOrPasswordIncorrectJSONException;
import eu.vranckaert.worktime.json.exception.user.InvalidEmailJSONException;
import eu.vranckaert.worktime.json.exception.user.PasswordLengthInvalidJSONException;
import eu.vranckaert.worktime.json.exception.user.RegisterEmailAlreadyInUseJSONException;
import eu.vranckaert.worktime.json.exception.user.UserNotFoundJSONException;
import eu.vranckaert.worktime.json.request.user.UserChangePasswordRequest;
import eu.vranckaert.worktime.json.request.user.UserChangePermissionsRequest;
import eu.vranckaert.worktime.json.request.user.UserLoginRequest;
import eu.vranckaert.worktime.json.request.user.UserRegistrationRequest;
import eu.vranckaert.worktime.json.response.user.AuthenticationResponse;
import eu.vranckaert.worktime.json.response.user.ChangePermissionsResponse;
import eu.vranckaert.worktime.json.response.user.UserProfileResponse;
import eu.vranckaert.worktime.model.Role;
import eu.vranckaert.worktime.model.User;
import eu.vranckaert.worktime.security.exception.EmailAlreadyInUseException;
import eu.vranckaert.worktime.security.exception.PasswordIncorrectException;
import eu.vranckaert.worktime.security.exception.PasswordLenghtInvalidException;
import eu.vranckaert.worktime.security.exception.ServiceNotAllowedException;
import eu.vranckaert.worktime.security.exception.UserNotAdminException;
import eu.vranckaert.worktime.security.exception.UserNotFoundException;
import eu.vranckaert.worktime.security.exception.UserNotLoggedInException;
import eu.vranckaert.worktime.security.service.SecurityChecker;
import eu.vranckaert.worktime.security.service.UserService;

@Path("user")
public class UserEndpoint implements UserEndPointInterface {
	@Inject
	private UserService userService;
	
	@Inject
	private SecurityChecker securityChecker;
	
	@POST
	@Path("register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public AuthenticationResponse register(UserRegistrationRequest request) {
		AuthenticationResponse response = new AuthenticationResponse();
		
		try {
			securityChecker.checkService(request);
		} catch (ServiceNotAllowedException ee) {
			ServiceNotAllowedJSONException exception = new ServiceNotAllowedJSONException("user/register", request.getServiceKey());
			response.setServiceNotAllowedException(exception);
			return response;
		}
		
		if (StringUtils.isBlank(request.getEmail())) {
			response.setFieldRequiredJSONException(new FieldRequiredJSONException("user/register", request, "email"));
			return response;
		} else if (StringUtils.isBlank(request.getPassword())) {
			response.setFieldRequiredJSONException(new FieldRequiredJSONException("user/register", request, "password"));
			return response;
		} else if (StringUtils.isBlank(request.getFirstName())) {
			response.setFieldRequiredJSONException(new FieldRequiredJSONException("user/register", request, "firstName"));
			return response;
		} else if (StringUtils.isBlank(request.getLastName())) {
			response.setFieldRequiredJSONException(new FieldRequiredJSONException("user/register", request, "lastName"));
			return response;
		}
		
		if (!EmailValidator.getInstance().isValid(request.getEmail())) {
			response.setInvalidEmailJSONException(new InvalidEmailJSONException("user/register", request.getEmail()));
			return response;
		}
		
		User user = new User();
		user.setEmail(request.getEmail().toLowerCase());
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
				
		try {
			String sessionKey = userService.register(user, request.getPassword());
			response.setSessionKey(sessionKey);
			return response;
		} catch (EmailAlreadyInUseException e) {
			response.setRegisterEmailAlreadyInUseJSONException(new RegisterEmailAlreadyInUseJSONException("user/register", request.getEmail()));
			return response;
		} catch (PasswordLenghtInvalidException e) {
			response.setPasswordLengthInvalidJSONException(new PasswordLengthInvalidJSONException("user/register"));
			return response;
		}
	}
	
	@POST
	@Path("login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public AuthenticationResponse login(UserLoginRequest request) {
		AuthenticationResponse response = new AuthenticationResponse();
		
		try {
			securityChecker.checkService(request);
		} catch (ServiceNotAllowedException e1) {
			ServiceNotAllowedJSONException exception = new ServiceNotAllowedJSONException("user/login", request.getServiceKey());
			response.setServiceNotAllowedException(exception);
			return response;
		}
		
		try {
			String sessionKey = userService.login(request.getEmail(), request.getPassword());
			response.setSessionKey(sessionKey);
			return response;
		} catch (UserNotFoundException e) {
			response.setEmailOrPasswordIncorrectJSONException(new EmailOrPasswordIncorrectJSONException("user/login"));
			return response;
		} catch (PasswordIncorrectException e) {
			response.setEmailOrPasswordIncorrectJSONException(new EmailOrPasswordIncorrectJSONException("user/login"));
			return response;
		}
	}
	
	@POST
	@Path("changePassword")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public AuthenticationResponse changePassword(UserChangePasswordRequest request) {
		AuthenticationResponse response = new AuthenticationResponse();
		
		try {
			securityChecker.checkUserLoggedIn(request);
		} catch (ServiceNotAllowedException e) {
			ServiceNotAllowedJSONException exception = new ServiceNotAllowedJSONException("user/changePassword", request.getServiceKey());
			response.setServiceNotAllowedException(exception);
			return response;
		} catch (UserNotLoggedInException e) {
			UserNotLoggedInJSONException exception = new UserNotLoggedInJSONException("user/changePassword");
			response.setUserNotLoggedInException(exception);
			return response;
		}
		
		try {
			String sessionKey = userService.changePassword(request.getEmail(), request.getOldPassword(), request.getNewPassword());
			response.setSessionKey(sessionKey);
			return response;
		} catch (UserNotFoundException e) {
			response.setEmailOrPasswordIncorrectJSONException(new EmailOrPasswordIncorrectJSONException("user/changePassword"));
			return response;
		} catch (PasswordIncorrectException e) {
			response.setEmailOrPasswordIncorrectJSONException(new EmailOrPasswordIncorrectJSONException("user/changePassword"));
			return response;
		}
	}
	
	@GET
	@Path("resetPassword")
	@Consumes(MediaType.TEXT_PLAIN)
	@Override
	public Response resetPassword(@QueryParam("serviceKey") String serviceKey, @QueryParam("email") String email) {
		RegisteredServiceRequest request = new RegisteredServiceRequest() {};
		request.setServiceKey(serviceKey);
		
		try {
			securityChecker.checkService(request);
		} catch (ServiceNotAllowedException e) {
			return Response.status(405).build();
		}
		
		userService.startResetPassword(email);
		
		return Response.status(200).build();
	}
	
	@POST
	@Path("changePermissions")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public ChangePermissionsResponse changePermissions(UserChangePermissionsRequest request) {
		ChangePermissionsResponse response = new ChangePermissionsResponse();
		
		try {
			securityChecker.checkUserIsAdmin(request);
		} catch (ServiceNotAllowedException e) {
			ServiceNotAllowedJSONException exception = new ServiceNotAllowedJSONException("user/changePermissions", request.getServiceKey());
			response.setServiceNotAllowedException(exception);
			return response;
		} catch (UserNotLoggedInException e) {
			UserNotLoggedInJSONException exception = new UserNotLoggedInJSONException("user/changePermissions");
			response.setUserNotLoggedInException(exception);
			return response;
		} catch (UserNotAdminException e) {
			UserIncorrectRoleException exception = new UserIncorrectRoleException("user/changePermissions", Role.ADMIN);
			response.setUserIncorrectRoleException(exception);
			return response;
		}
		
		try {
			userService.changePermissions(request.getUserToChange(), request.getNewRole());
			return response;
		} catch (UserNotFoundException e) {
			response.setUserNotFoundJSONException(new UserNotFoundJSONException("user/changePermissions", request.getUserToChange()));
			return response;
		}
	}
	
	@GET
	@Path("logout")
	@Consumes(MediaType.TEXT_PLAIN)
	@Override
	public Response logout(@QueryParam("serviceKey") String serviceKey, @QueryParam("email") String email, @QueryParam("sessionKey") String sessionKey) {
		RegisteredServiceRequest request = new RegisteredServiceRequest() {};
		request.setServiceKey(serviceKey);
		
		try {
			securityChecker.checkService(request);
		} catch (ServiceNotAllowedException e) {
			return Response.status(405).build();
		}
		
		if (StringUtils.isBlank(email) || StringUtils.isBlank(sessionKey)) {
			return Response.status(400).build();
		}
		
		userService.logout(email, sessionKey);
		
		return Response.status(200).build();
	}
	
	@GET
	@Path("profile")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public UserProfileResponse profile(@QueryParam("serviceKey") String serviceKey, @QueryParam("email") String email, @QueryParam("sessionKey") String sessionKey) {
		UserProfileResponse response = new UserProfileResponse();
		
		AuthenticatedUserRequest request = new AuthenticatedUserRequest() {};
		request.setServiceKey(serviceKey);
		request.setEmail(email);
		request.setSessionKey(sessionKey);
		
		try {
			securityChecker.checkUserLoggedIn(request);
		} catch (ServiceNotAllowedException e) {
			ServiceNotAllowedJSONException exception = new ServiceNotAllowedJSONException("user/profile", request.getServiceKey());
			response.setServiceNotAllowedException(exception);
			return response;
		} catch (UserNotLoggedInException e) {
			UserNotLoggedInJSONException exception = new UserNotLoggedInJSONException("user/profile");
			response.setUserNotLoggedInException(exception);
			return response;
		}
		
		User user = userService.findUser(email);
		
		response.setFirstName(user.getFirstName());
		response.setLastName(user.getLastName());
		response.setEmail(user.getEmail());
		response.setRegisteredSince(user.getRegistrationDate());
		response.setRole(user.getRole());
		response.setLoggedInSince(userService.getLogInTime(user, sessionKey));
		
		return response;
	}
}
