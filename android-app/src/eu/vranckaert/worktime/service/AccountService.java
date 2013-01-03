package eu.vranckaert.worktime.service;

import eu.vranckaert.worktime.exceptions.account.*;
import eu.vranckaert.worktime.exceptions.network.NoNetworkConnectionException;
import eu.vranckaert.worktime.model.User;
import eu.vranckaert.worktime.web.json.exception.GeneralWebException;

/**
 * User: DIRK VRANCKAERT
 * Date: 12/12/12
 * Time: 20:04
 */
public interface AccountService {
    /**
     * Check if the user is logged in on this device or not.
     * @return True if logged in, false if not.
     */
    boolean isUserLoggedIn();

    /**
     * Log the user in using the provided email and password.
     * @param email The email of the user.
     * @param password The password of the user in plain text.
     * @throws NoNetworkConnectionException No working network connection is found.
     * @throws GeneralWebException Some kind of exception occurred during the web request.
     * @throws LoginCredentialsMismatchException The credentials provided are not correct and so the user is not logged
     * in!
     */
    void login(String email, String password) throws GeneralWebException, NoNetworkConnectionException, LoginCredentialsMismatchException;

    /**
     * Register a new user-account with the provided details.
     * @param email The email of the user.
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @param password The passwod of the user.
     * @throws NoNetworkConnectionException No working network connection is found.
     * @throws GeneralWebException Some kind of exception occurred during the web request.
     * @throws RegisterEmailAlreadyInUseException If an account already exists for this email address.
     * @throws PasswordLengthValidationException If the password length is invalid (< 6 or > 30 characters).
     * @throws RegisterFieldRequiredException If one of the required fields is missing.
     */
    void register(String email, String firstName, String lastName, String password) throws GeneralWebException, NoNetworkConnectionException, RegisterEmailAlreadyInUseException, PasswordLengthValidationException, RegisterFieldRequiredException;

    /**
     * Loads the user data (full profile) from the database and updates it with information from the webservice.
     * @return The full {@link User} instance.
     * @throws NoNetworkConnectionException No working network connection is found.
     * @throws GeneralWebException Some kind of exception occurred during the web request.
     * @throws UserNotLoggedInException The user is not logged in, authentication failed...
     */
    User loadUserData() throws UserNotLoggedInException, GeneralWebException, NoNetworkConnectionException;

    /**
     * Log the current logged in user out.
     */
    void logout();
}
