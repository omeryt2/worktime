package eu.vranckaert.worktime.security.service;

import java.util.Date;
import java.util.List;

import eu.vranckaert.worktime.model.PasswordResetRequest;
import eu.vranckaert.worktime.model.Role;
import eu.vranckaert.worktime.model.Session;
import eu.vranckaert.worktime.model.User;
import eu.vranckaert.worktime.security.exception.EmailAlreadyInUseException;
import eu.vranckaert.worktime.security.exception.InvalidPasswordResetKeyException;
import eu.vranckaert.worktime.security.exception.PasswordIncorrectException;
import eu.vranckaert.worktime.security.exception.PasswordLenghtInvalidException;
import eu.vranckaert.worktime.security.exception.PasswordResetKeyAlreadyUsedException;
import eu.vranckaert.worktime.security.exception.PasswordResetKeyExpiredException;
import eu.vranckaert.worktime.security.exception.UserNotFoundException;

/**
 * 
 * @author Dirk Vranckaert
 */
public interface UserService {
	/**
	 * Register a new user in the system.
	 * @param user The user to be registered in the system.
	 * @param password The password in plain text.
	 * @return Returns the session key for this user if registration is 
	 * successful.
	 * @throws EmailAlreadyInUseException Thrown if there is already a user in 
	 * the system registered for the provided email address (which is the key).
	 * @throws PasswordLenghtInvalidException If the lenght of the password is
	 * invalid. Minimum should be 6 characters, maximum is 30 characters.
	 */
	String register(User user, String password) throws EmailAlreadyInUseException, PasswordLenghtInvalidException;
	
	/**
	 * Try to login a user with a specific email address and password.
	 * @param email The email.
	 * @param password The password in plain text.
	 * @return The session key for the logged in user.
	 * @throws UserNotFoundException The user is not found based on the provided
	 * email address.
	 * @throws PasswordIncorrectException The password did not match the user.
	 */
	String login(String email, String password) throws UserNotFoundException, PasswordIncorrectException;
	
	/**
	 * Change the password of a certain account to a new password. If password 
	 * change is successful a new session key is generated and all 'old' session
	 * keys are removed so the user will be logged out on all other platforms.
	 * @param email The email of the account to reset.
	 * @param oldPassword The old password.
	 * @param newPassword The new password.
	 * @return The new generated session key.
	 * @throws UserNotFoundException The user is not found based on the provided
	 * email address.
	 * @throws PasswordIncorrectException The password did not match the user.
	 */
	String changePassword(String email, String oldPassword, String newPassword) throws UserNotFoundException, PasswordIncorrectException;

	/**
	 * Checks if a certain user is logged in based on it's sessionKey.
	 * @param email The email of the user.
	 * @param sessionKey The session key.
	 * @return True if the user is logged in, false if not.
	 */
	boolean isLoggedIn(String email, String sessionKey);
	
	/**
	 * Increase the {@link Session#getTimesUsed()} with one.
	 * @param email The email of the user.
	 * @param sessionKey The session key.
	 */
	void markSessionUsed(String email, String sessionKey);

	/**
	 * Find a specific user.
	 * @param email The email of the user.
	 * @return The user instance if found, otherwise null.
	 */
	User findUser(String email);

	/**
	 * Change the permission of a certain user
	 * @param email The email of the user for which the roles needs to be 
	 * changed.
	 * @param newRole The new role to be assigned.
	 * @throws UserNotFoundException Thrown if the user for which the roles are 
	 * going to be changed is not found.
	 */
	void changePermissions(String email, Role newRole) throws UserNotFoundException;

	/**
	 * Start the reset password procedure.
	 * @param email The email address of the user account for which the password
	 * reset procedure needs to be started.
	 */
	void resetPasswordRequest(String email);
	
	/**
	 * Get the {@link PasswordResetRequest} for the specified passwordResetKey.
	 * @param passwordResetKey The reset key which is unique.
	 * @return The {@link PasswordResetRequest} that matches the reset key.
	 * @throws InvalidPasswordResetKeyException Thrown if the reset key is not known.
	 * @throws PasswordResetKeyAlreadyUsedException Thrown if the reset key has been used before.
	 * @throws PasswordResetKeyExpiredException Thrown if the reset key has expired (after 24 hours).
	 */
	PasswordResetRequest getPasswordResetRequestKey(String passwordResetKey) 
			throws InvalidPasswordResetKeyException, PasswordResetKeyAlreadyUsedException, 
			PasswordResetKeyExpiredException;
	
	/**
	 * Reset the password of a certain user that matches the provided password
	 * reset key and set it's password to the new password.
	 * @param passwordResetKey The password reset key.
	 * @param newPassword The new password.
	 * @throws PasswordLenghtInvalidException If the lenght of the password is
	 * invalid. Minimum should be 6 characters, maximum is 30 characters.
	 * @throws InvalidPasswordResetKeyException Thrown if the reset key is not known.
	 * @throws PasswordResetKeyAlreadyUsedException Thrown if the reset key has been used before.
	 * @throws PasswordResetKeyExpiredException Thrown if the reset key has expired (after 24 hours).
	 */
	void resetPassword(String passwordResetKey, String newPassword) throws PasswordLenghtInvalidException, 
	InvalidPasswordResetKeyException, PasswordResetKeyAlreadyUsedException, PasswordResetKeyExpiredException;

	/**
	 * Find the date and time for which the specified user logged in with the
	 * specified key.
	 * @param user The user.
	 * @param sessionKey The session key.
	 * @return The date and time the user logged in with this key.
	 */
	Date getLogInTime(User user, String sessionKey);

	/**
	 * Logs the specified user out for the specified session.
	 * @param email The email to identify the user.
	 * @param sessionKey The session key that matches the user-account.
	 */
	void logout(String email, String sessionKey);

	/**
	 * Find all {@link User}s registered in the system.
	 * @return The list of {@link User}s.
	 */
	List<User> findAll();
}
