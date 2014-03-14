package eu.vranckaert.worktime.json.endpoint;

import javax.ws.rs.core.Response;

import eu.vranckaert.worktime.json.exception.user.EmailOrPasswordIncorrectJSONException;
import eu.vranckaert.worktime.json.exception.user.UserNotFoundJSONException;
import eu.vranckaert.worktime.json.request.user.ResetPasswordRequest;
import eu.vranckaert.worktime.json.request.user.UserChangePasswordRequest;
import eu.vranckaert.worktime.json.request.user.UserChangePermissionsRequest;
import eu.vranckaert.worktime.json.request.user.UserLoginRequest;
import eu.vranckaert.worktime.json.request.user.UserRegistrationRequest;
import eu.vranckaert.worktime.json.response.user.AuthenticationResponse;
import eu.vranckaert.worktime.json.response.user.ChangePermissionsResponse;
import eu.vranckaert.worktime.json.response.user.ResetPasswordResponse;
import eu.vranckaert.worktime.json.response.user.UserProfileResponse;
import eu.vranckaert.worktime.security.exception.EmailAlreadyInUseException;

public interface UserEndPointInterface {
	/**
	 * Registers a new user to the application.
	 * @param request The request containing the registration details.
	 * @return Returns an instance of {@link AuthenticationResponse} containing
	 * the session key of the newly registered user so he is automatically 
	 * logged in. If the email address is already in use the reponse will 
	 * contain an {@link EmailAlreadyInUseException}.
	 */
	AuthenticationResponse register(UserRegistrationRequest request);
	
	/**
	 * Login to the system using a specific email and password.
	 * @param request The request containing the login parameters.
	 * @return Returns an instance of {@link AuthenticationResponse} containing
	 * the session key of the user. If the email address is not found or the 
	 * email and password do not match the reponse will contain a 
	 * {@link EmailOrPasswordIncorrectJSONException}.
	 */
	AuthenticationResponse login(UserLoginRequest request);
	
	/**
	 * Change the password of the user account. The user that is going to change
	 * his password needs to be logged in.
	 * @param request The request.
	 * @return An instance of {@link AuthenticationResponse} containing the new
	 * session key of the user. The session key of the user changes after he
	 * changes his password. If the old password and username do not match (or
	 * the user is not found in the database) then the response will contain a 
	 * {@link EmailOrPasswordIncorrectJSONException}.
	 */
	AuthenticationResponse changePassword(UserChangePasswordRequest request);
	
	/**
	 * Changes the permissions of a user account.
	 * @param request The request.
	 * @return If the permission cannot be changed because the user is not found
	 * the reponse will contain a {@link UserNotFoundJSONException}.
	 */
	ChangePermissionsResponse changePermissions(UserChangePermissionsRequest request);

	/**
	 * Request a password reset of the user with a certain email address. This
	 * will trigger an email to be sent to the user with a hyperlink that
	 * contains a reset key for the user.
	 * @param serviceKey The key of the services that is wanting to access this
	 * GET method.
	 * @param email The email address to which the reset-mail should be sent.
	 * This is also the unique key to identify a user.
	 * @return A response with status code 200 always except when the service is
	 * not allowed then the status code will be 405.
	 */
	Response resetPasswordRequest(String serviceKey, String email);
	
	/**
	 * Actually reset the password after a 
	 * {@link UserEndPointInterface#resetPasswordRequest(String, String)} has 
	 * been executed.
	 * @param resetPasswordRequest The {@link ResetPasswordRequest} with the
	 * reset-key and new password filled in.
	 * @return The {@link ResetPasswordResponse}.
	 */
	ResetPasswordResponse resetPassword(ResetPasswordRequest resetPasswordRequest);

	/**
	 * Retrieves the user-profile data using a GET-method.
	 * @param serviceKey The service key.
	 * @param email The email of the user.
	 * @param sessionKey The session key with which the user is logged in.
	 * @return The user-profile.
	 */
	UserProfileResponse profile(String serviceKey, String email, String sessionKey);

	/**
	 * Log the user out of the system using a GET-method.
	 * @param serviceKey The service key.
	 * @param email The email of the user.
	 * @param sessionKey The session key with which the user is logged in and 
	 * that should be logged out.
	 * @return A response with status code 200 always except when the service is
	 * not allowed then the status code will be 405.
	 */
	Response logout(String serviceKey, String email, String sessionKey);
}
