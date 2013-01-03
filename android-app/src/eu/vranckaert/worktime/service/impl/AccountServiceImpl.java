package eu.vranckaert.worktime.service.impl;

import com.google.inject.Inject;
import eu.vranckaert.worktime.dao.AccountDao;
import eu.vranckaert.worktime.dao.web.AccountWebDao;
import eu.vranckaert.worktime.exceptions.account.*;
import eu.vranckaert.worktime.exceptions.network.NoNetworkConnectionException;
import eu.vranckaert.worktime.model.User;
import eu.vranckaert.worktime.service.AccountService;
import eu.vranckaert.worktime.web.json.exception.GeneralWebException;

/**
 * User: DIRK VRANCKAERT
 * Date: 12/12/12
 * Time: 20:04
 */
public class AccountServiceImpl implements AccountService {
    @Inject
    private AccountWebDao accountWebDao;

    @Inject
    private AccountDao accountDao;

    @Override
    public boolean isUserLoggedIn() {
        User user = accountDao.getLoggedInUser();
        return user!=null;
    }

    @Override
    public void login(String email, String password) throws GeneralWebException, NoNetworkConnectionException, LoginCredentialsMismatchException {
        String sessionKey = accountWebDao.login(email, password);

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setSessionKey(sessionKey);

        accountDao.storeLoggedInUser(user);
    }

    @Override
    public void register(String email, String firstName, String lastName, String password) throws GeneralWebException, NoNetworkConnectionException, RegisterEmailAlreadyInUseException, PasswordLengthValidationException, RegisterFieldRequiredException {
        String sessionKey = accountWebDao.register(email, firstName, lastName, password);

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setSessionKey(sessionKey);

        accountDao.storeLoggedInUser(user);
    }

    @Override
    public User loadUserData() throws UserNotLoggedInException, GeneralWebException, NoNetworkConnectionException {
        User user = accountDao.getLoggedInUser();

        User updatedUser = null;
        try {
            updatedUser = accountWebDao.loadProfile(user);
        } catch (UserNotLoggedInException e) {
            accountDao.delete(user);
            throw e;
        }

        return updatedUser;
    }

    @Override
    public void logout() {
        User user = accountDao.getLoggedInUser();
        accountDao.delete(user);
        accountWebDao.logout(user);
    }
}
