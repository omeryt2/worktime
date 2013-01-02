package eu.vranckaert.worktime.dao.web.impl;

import android.content.Context;
import android.util.Log;
import com.google.inject.Inject;
import eu.vranckaert.worktime.constants.EnvironmentConstants;
import eu.vranckaert.worktime.dao.web.AccountWebDao;
import eu.vranckaert.worktime.dao.web.model.request.user.UserLoginRequest;
import eu.vranckaert.worktime.dao.web.model.response.user.AuthenticationResponse;
import eu.vranckaert.worktime.exceptions.account.LoginCredentialsMismatchException;
import eu.vranckaert.worktime.exceptions.network.NoNetworkConnectionException;
import eu.vranckaert.worktime.guice.Application;
import eu.vranckaert.worktime.utils.network.NetworkUtil;
import eu.vranckaert.worktime.web.json.JsonWebServiceImpl;
import eu.vranckaert.worktime.web.json.exception.CommunicationException;
import eu.vranckaert.worktime.web.json.exception.GeneralWebException;
import eu.vranckaert.worktime.web.json.exception.WebException;
import eu.vranckaert.worktime.web.json.model.JsonResult;

/**
 * User: Dirk Vranckaert
 * Date: 12/12/12
 * Time: 11:36
 */
public class AccountWebDaoImpl extends JsonWebServiceImpl implements AccountWebDao {
    private static final String LOG_TAG = AccountWebDaoImpl.class.getSimpleName();

    private static final String ENDPOINT_BASE_URL = EnvironmentConstants.WorkTimeWeb.ENDPOINT_URL;
    //private static final String ENDPOINT_BASE_URL = "http://192.168.2.103:8888/";
    private static final String ENDPOINT_TEST = "";
    private static final String ENDPOINT_REST = "rest/";
    private static final String ENDPOINT_METHOD_LOGIN = "user/login";
    private static final String ENDPOINT_METHOD_REGISTER = "user/register";

    private Context context;

    @Inject
    public AccountWebDaoImpl(Application application, Context context) {
        super((Application) application);
        this.context = context;
    }

    @Override
    public String login(String email, String password) throws NoNetworkConnectionException, GeneralWebException, LoginCredentialsMismatchException {
        if (!NetworkUtil.canSurf(context, ENDPOINT_BASE_URL + ENDPOINT_TEST)) {
            Log.w(LOG_TAG, "Cannot reach endpoint (" + ENDPOINT_BASE_URL + ENDPOINT_TEST + "), device seems to be offline!");
            throw new NoNetworkConnectionException();
        }

        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setEmail(email);
        userLoginRequest.setPassword(password);

        JsonResult result = null;
        try {
            result = webInvokePost(ENDPOINT_BASE_URL + ENDPOINT_REST, ENDPOINT_METHOD_LOGIN, null, null, userLoginRequest, null);
        } catch (WebException e) {
            String msg = "Cannot login due to a web exception... Exception is: " + e.getMessage();
            Log.e(LOG_TAG, msg, e);
            throw new GeneralWebException(msg);
        } catch (CommunicationException e) {
            String msg = "Cannot login due to a communication exception... Exception is: " + e.getMessage();
            Log.e(LOG_TAG, msg, e);
            throw new GeneralWebException(msg);
        }

        if (result == null) {
            return null;
        }

        AuthenticationResponse response = result.getSingleResult(AuthenticationResponse.class);
        if (!response.isResultOk()) {
            if (response.getEmailOrPasswordIncorrectJSONException() != null) {
                throw new LoginCredentialsMismatchException();
            } else if (response.getServiceNotAllowedException() != null) {
                throw new RuntimeException("Your service is not allowed to access the application-server");
            } else {
                throw  new RuntimeException("Something went wrong...");
            }
        }

        return response.getSessionKey();
    }
}
