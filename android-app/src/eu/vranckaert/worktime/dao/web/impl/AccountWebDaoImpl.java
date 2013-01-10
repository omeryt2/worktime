/*
 * Copyright 2013 Dirk Vranckaert
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.vranckaert.worktime.dao.web.impl;

import android.content.Context;
import android.util.Log;
import com.google.inject.Inject;
import eu.vranckaert.worktime.constants.EnvironmentConstants;
import eu.vranckaert.worktime.dao.web.AccountWebDao;
import eu.vranckaert.worktime.dao.web.model.request.user.UserLoginRequest;
import eu.vranckaert.worktime.dao.web.model.request.user.UserRegistrationRequest;
import eu.vranckaert.worktime.dao.web.model.response.user.AuthenticationResponse;
import eu.vranckaert.worktime.dao.web.model.response.user.UserProfileResponse;
import eu.vranckaert.worktime.exceptions.account.*;
import eu.vranckaert.worktime.exceptions.network.NoNetworkConnectionException;
import eu.vranckaert.worktime.guice.Application;
import eu.vranckaert.worktime.model.User;
import eu.vranckaert.worktime.utils.network.NetworkUtil;
import eu.vranckaert.worktime.web.json.JsonWebServiceImpl;
import eu.vranckaert.worktime.web.json.exception.CommunicationException;
import eu.vranckaert.worktime.web.json.exception.GeneralWebException;
import eu.vranckaert.worktime.web.json.exception.WebException;
import eu.vranckaert.worktime.web.json.model.JsonResult;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Dirk Vranckaert
 * Date: 12/12/12
 * Time: 11:36
 */
public class AccountWebDaoImpl extends JsonWebServiceImpl implements AccountWebDao {
    private static final String LOG_TAG = AccountWebDaoImpl.class.getSimpleName();

    private static final String ENDPOINT_BASE_URL = EnvironmentConstants.WorkTimeWeb.ENDPOINT_URL;
    private static final String ENDPOINT_TEST = "";
    private static final String ENDPOINT_REST = "rest/";
    private static final String ENDPOINT_METHOD_LOGIN = "user/login";
    private static final String ENDPOINT_METHOD_REGISTER = "user/register";
    private static final String ENDPOINT_METHOD_PROFILE = "user/profile";
    private static final String ENDPOINT_METHOD_LOGOUT = "user/logout";

    private Context context;

    @Inject
    public AccountWebDaoImpl(Application application, Context context) {
        super((Application) application);
        this.context = context;
    }

    private void checkNetworkConnection() throws NoNetworkConnectionException {
        if (!NetworkUtil.canSurf(context, ENDPOINT_BASE_URL + ENDPOINT_TEST)) {
            Log.w(LOG_TAG, "Cannot reach endpoint (" + ENDPOINT_BASE_URL + ENDPOINT_TEST + "), device seems to be offline!");
            throw new NoNetworkConnectionException();
        }
    }

    @Override
    public String login(String email, String password) throws NoNetworkConnectionException, GeneralWebException, LoginCredentialsMismatchException {
        checkNetworkConnection();

        UserLoginRequest request = new UserLoginRequest();
        request.setEmail(email);
        request.setPassword(password);

        JsonResult result = null;
        try {
            result = webInvokePost(ENDPOINT_BASE_URL + ENDPOINT_REST, ENDPOINT_METHOD_LOGIN, null, null, request, null);
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

    @Override
    public String register(String email, String firstName, String lastName, String password) throws NoNetworkConnectionException, GeneralWebException, RegisterEmailAlreadyInUseException, PasswordLengthValidationException, RegisterFieldRequiredException {
        checkNetworkConnection();

        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail(email);
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setPassword(password);

        JsonResult result = null;
        try {
            result = webInvokePost(ENDPOINT_BASE_URL + ENDPOINT_REST, ENDPOINT_METHOD_REGISTER, null, null, request, null);
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
            if (response.getFieldRequiredJSONException() != null) {
                throw new RegisterFieldRequiredException(response.getFieldRequiredJSONException().getFieldName());
            } else if (response.getRegisterEmailAlreadyInUseJSONException() != null) {
                throw new RegisterEmailAlreadyInUseException();
            } else if (response.getPasswordLengthInvalidJSONException() != null) {
                throw new PasswordLengthValidationException();
            } else if (response.getServiceNotAllowedException() != null) {
                throw new RuntimeException("Your service is not allowed to access the application-server");
            } else {
                throw  new RuntimeException("Something went wrong...");
            }
        }

        return response.getSessionKey();
    }

    @Override
    public User loadProfile(User user) throws NoNetworkConnectionException, GeneralWebException, UserNotLoggedInException {
        checkNetworkConnection();

        JsonResult result = null;

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("serviceKey", EnvironmentConstants.WorkTimeWeb.SERVICE_KEY);
        parameters.put("email", user.getEmail());
        parameters.put("sessionKey", user.getSessionKey());
        try {
            result = webInvokeGet(ENDPOINT_BASE_URL + ENDPOINT_REST, ENDPOINT_METHOD_PROFILE, null, parameters, null);
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
            return user;
        }

        UserProfileResponse response = result.getSingleResult(UserProfileResponse.class);
        if (!response.isResultOk()) {
            if (response.getUserNotLoggedInException() != null) {
                throw new UserNotLoggedInException();
            } else {
                throw  new RuntimeException("Something went wrong...");
            }
        } else {
            user.setFirstName(response.getFirstName());
            user.setLastName(response.getLastName());
            user.setLoggedInSince(response.getLoggedInSince());
            user.setRegisteredSince(response.getRegisteredSince());
            user.setRole(response.getRole());
        }

        return user;
    }

    @Override
    public void logout(User user) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("serviceKey", EnvironmentConstants.WorkTimeWeb.SERVICE_KEY);
        parameters.put("email", user.getEmail());
        parameters.put("sessionKey", user.getSessionKey());

        try {
            webInvokeGet(ENDPOINT_BASE_URL + ENDPOINT_REST, ENDPOINT_METHOD_LOGOUT, null, parameters, null);
        } catch (WebException e) {
            String msg = "Cannot login due to a web exception... Exception is: " + e.getMessage();
            Log.e(LOG_TAG, msg, e);
        } catch (CommunicationException e) {
            String msg = "Cannot login due to a communication exception... Exception is: " + e.getMessage();
            Log.e(LOG_TAG, msg, e);
        }
    }
}
