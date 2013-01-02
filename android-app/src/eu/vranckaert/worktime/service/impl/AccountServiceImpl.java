package eu.vranckaert.worktime.service.impl;

import com.google.inject.Inject;
import eu.vranckaert.worktime.dao.AccountDao;
import eu.vranckaert.worktime.dao.web.AccountWebDao;
import eu.vranckaert.worktime.exceptions.account.LoginCredentialsMismatchException;
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
    AccountWebDao accountWebDao;

    @Inject
    AccountDao accountDao;

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
}
