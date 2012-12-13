package eu.vranckaert.worktime.service;

import eu.vranckaert.worktime.exceptions.account.LoginCredentialsMismatchException;
import eu.vranckaert.worktime.exceptions.network.NoNetworkConnectionException;
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
}
