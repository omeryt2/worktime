package eu.vranckaert.worktime.dao.web;

import eu.vranckaert.worktime.exceptions.account.LoginCredentialsMismatchException;
import eu.vranckaert.worktime.exceptions.network.NoNetworkConnectionException;
import eu.vranckaert.worktime.web.json.JsonWebService;
import eu.vranckaert.worktime.web.json.exception.GeneralWebException;

/**
 * User: Dirk Vranckaert
 * Date: 12/12/12
 * Time: 11:36
 */
public interface AccountWebDao extends JsonWebService {
    /**
     * Login the user with the specified email and password to the WorkTime webservice.
     * @param email The email.
     * @param password The password in plain text.
     * @return The session key of the registered user.
     * @throws NoNetworkConnectionException No working network connection is found.
     * @throws GeneralWebException Some kind of exception occurred during the web request.
     * @throws LoginCredentialsMismatchException The credentials provided are not correct and so the user is not logged
     * in!
     */
    public String login(String email, String password) throws NoNetworkConnectionException, GeneralWebException, LoginCredentialsMismatchException;
}
