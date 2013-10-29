/*
 * Copyright 2013 Dirk Vranckaert
 *
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
import eu.vranckaert.worktime.dao.web.WorkTimeWebDao;
import eu.vranckaert.worktime.dao.web.model.base.request.UserChangePasswordRequest;
import eu.vranckaert.worktime.dao.web.model.request.sync.WorkTimeSyncRequest;
import eu.vranckaert.worktime.dao.web.model.request.user.ResetPasswordRequest;
import eu.vranckaert.worktime.dao.web.model.request.user.UserLoginRequest;
import eu.vranckaert.worktime.dao.web.model.request.user.UserRegistrationRequest;
import eu.vranckaert.worktime.dao.web.model.response.gcm.GCMResponse;
import eu.vranckaert.worktime.dao.web.model.response.sync.WorkTimeSyncResponse;
import eu.vranckaert.worktime.dao.web.model.response.user.AuthenticationResponse;
import eu.vranckaert.worktime.dao.web.model.response.user.ResetPasswordResponse;
import eu.vranckaert.worktime.dao.web.model.response.user.UserProfileResponse;
import eu.vranckaert.worktime.exceptions.network.NoNetworkConnectionException;
import eu.vranckaert.worktime.exceptions.worktime.account.*;
import eu.vranckaert.worktime.exceptions.worktime.sync.CorruptSyncDataException;
import eu.vranckaert.worktime.exceptions.worktime.sync.SyncAlreadyBusyException;
import eu.vranckaert.worktime.exceptions.worktime.sync.SynchronizationFailedException;
import eu.vranckaert.worktime.guice.Application;
import eu.vranckaert.worktime.model.Project;
import eu.vranckaert.worktime.model.Task;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.model.User;
import eu.vranckaert.worktime.utils.network.NetworkUtil;
import eu.vranckaert.worktime.utils.preferences.Preferences;
import eu.vranckaert.worktime.web.json.JsonWebServiceImpl;
import eu.vranckaert.worktime.web.json.exception.CommunicationException;
import eu.vranckaert.worktime.web.json.exception.GeneralWebException;
import eu.vranckaert.worktime.web.json.exception.WebException;
import eu.vranckaert.worktime.web.json.model.JsonResult;

import java.util.*;

/**
 * User: Dirk Vranckaert
 * Date: 12/12/12
 * Time: 11:36
 */
public class WorkTimeWebDaoImpl extends JsonWebServiceImpl implements WorkTimeWebDao {
    private static final String LOG_TAG = WorkTimeWebDaoImpl.class.getSimpleName();

    private static final String ENDPOINT_BASE_URL = EnvironmentConstants.WorkTimeWeb.ENDPOINT_URL;
    private static final String ENDPOINT_TEST = ENDPOINT_BASE_URL + "rest/test/sayHello";
    private static final String ENDPOINT_REST = "rest/";
    private static final String ENDPOINT_METHOD_LOGIN = "user/login";
    private static final String ENDPOINT_METHOD_REGISTER = "user/register";
    private static final String ENDPOINT_METHOD_CHANGE_PASSWORD = "user/changePassword";
    private static final String ENDPOINT_METHOD_PROFILE = "user/profile";
    private static final String ENDPOINT_METHOD_LOGOUT = "user/logout";
    private static final String ENDPOINT_METHOD_SYNC = "sync/all";
    private static final String ENDPOINT_METHOD_RESET_PASSWORD_REQUEST = "user/resetPasswordRequest";
    private static final String ENDPOINT_METHOD_RESET_PASSWORD = "user/resetPassword";
    private static final String ENDPOINT_METHOD_REGISTER_ANDROID_DEVICE = "push/registerAndroidDevice";
    private static final String ENDPOINT_METHOD_REPLACE_ANDROID_DEVICE = "push/replaceAndroidDevice";

    private Context context;

    @Inject
    public WorkTimeWebDaoImpl(Application application, Context context) {
        super((Application) application);
        this.context = context;
    }

    private void checkNetworkConnection() throws NoNetworkConnectionException {
        if (!NetworkUtil.canSurf(context, ENDPOINT_TEST)) {
            Log.w(LOG_TAG, "Cannot reach endpoint (" + ENDPOINT_TEST + "), device seems to be offline!");
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
            String msg = "Cannot register due to a web exception... Exception is: " + e.getMessage();
            Log.e(LOG_TAG, msg, e);
            throw new GeneralWebException(msg);
        } catch (CommunicationException e) {
            String msg = "Cannot register due to a communication exception... Exception is: " + e.getMessage();
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
    public String changePassword(User user, String newPassword) throws NoNetworkConnectionException, GeneralWebException, LoginCredentialsMismatchException {
        checkNetworkConnection();

        UserChangePasswordRequest request = new UserChangePasswordRequest();
        request.setSessionKey(user.getSessionKey());
        request.setEmail(user.getEmail());
        request.setOldPassword(user.getPassword());
        request.setNewPassword(newPassword);

        JsonResult result = null;
        try {
            result = webInvokePost(ENDPOINT_BASE_URL + ENDPOINT_REST, ENDPOINT_METHOD_CHANGE_PASSWORD, null, null, request, null);
        } catch (WebException e) {
            String msg = "Cannot change the password due to a web exception... Exception is: " + e.getMessage();
            Log.e(LOG_TAG, msg, e);
            throw new GeneralWebException(msg);
        } catch (CommunicationException e) {
            String msg = "Cannot change the password due to a communication exception... Exception is: " + e.getMessage();
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
            String msg = "Cannot load profile due to a web exception... Exception is: " + e.getMessage();
            Log.e(LOG_TAG, msg, e);
            throw new GeneralWebException(msg);
        } catch (CommunicationException e) {
            String msg = "Cannot load profile due to a communication exception... Exception is: " + e.getMessage();
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
    public List<Object> sync(User user, String conflictConfiguration, Date lastSuccessfulSyncDate, List<Project> projects, List<Task> tasks, List<TimeRegistration> timeRegistrations, Map<String, String> syncRemovalMap, boolean triggeredFromOtherDevice) throws NoNetworkConnectionException, GeneralWebException, UserNotLoggedInException, SynchronizationFailedException, SyncAlreadyBusyException, CorruptSyncDataException {
        checkNetworkConnection();

        WorkTimeSyncRequest request = new WorkTimeSyncRequest();
        request.setEmail(user.getEmail());
        request.setSessionKey(user.getSessionKey());
        request.setConflictConfiguration(conflictConfiguration);
        request.setLastSuccessfulSyncDate(lastSuccessfulSyncDate);
        request.setProjects(projects);
        request.setTasks(tasks);
        request.setTimeRegistrations(timeRegistrations);
        request.setSyncRemovalMap(syncRemovalMap);
        request.setTriggerSyncOnOtherDevices(!triggeredFromOtherDevice);
        request.setAndroidPushRegistrationId(Preferences.GCM.getRegistrationId(context));

        JsonResult result = null;
        try {
            result = webInvokePost(ENDPOINT_BASE_URL + ENDPOINT_REST, ENDPOINT_METHOD_SYNC, null, null, request, null);
        } catch (WebException e) {
            String msg = "Cannot sync due to a web exception... Exception is: " + e.getMessage();
            Log.e(LOG_TAG, msg, e);
            throw new GeneralWebException(msg);
        } catch (CommunicationException e) {
            String msg = "Cannot sync due to a communication exception... Exception is: " + e.getMessage();
            Log.e(LOG_TAG, msg, e);
            throw new GeneralWebException(msg);
        }

        if (result == null) {
            return null;
        }

        WorkTimeSyncResponse response = result.getSingleResult(WorkTimeSyncResponse.class);
        if (!response.isResultOk()) {
            if (response.getUserNotLoggedInException() != null) {
                throw new UserNotLoggedInException();
            } else if (response.getSyncronisationFailedJSONException() != null) {
                throw new SynchronizationFailedException();
            } else if (response.getSynchronisationLockedJSONException() != null) {
                throw new SyncAlreadyBusyException();
            } else if (response.getCorruptDataJSONException() != null) {
                throw new CorruptSyncDataException();
            } else {
                throw  new RuntimeException("Something went wrong...");
            }
        } else {
            List<Object> resultList = new ArrayList<Object>();
            resultList.add(response.getProjectsSinceLastSync());
            resultList.add(response.getTasksSinceLastSync());
            resultList.add(response.getTimeRegistrationsSinceLastSync());
            resultList.add(response.getSyncResult());
            resultList.add(response.getSyncRemovalMap());
            return resultList;
        }
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
            String msg = "Cannot logout due to a web exception... Exception is: " + e.getMessage();
            Log.e(LOG_TAG, msg, e);
        } catch (CommunicationException e) {
            String msg = "Cannot logout due to a communication exception... Exception is: " + e.getMessage();
            Log.e(LOG_TAG, msg, e);
        }
    }

    @Override
    public void resetPasswordRequest(String email) throws NoNetworkConnectionException, GeneralWebException {
        checkNetworkConnection();

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("serviceKey", EnvironmentConstants.WorkTimeWeb.SERVICE_KEY);
        parameters.put("email", email);

        try {
            webInvokeGet(ENDPOINT_BASE_URL + ENDPOINT_REST, ENDPOINT_METHOD_RESET_PASSWORD_REQUEST, null, parameters, null);
        } catch (WebException e) {
            String msg = "Cannot request password reset due to a web exception... Exception is: " + e.getMessage();
            Log.e(LOG_TAG, msg, e);
            throw new GeneralWebException(msg);
        } catch (CommunicationException e) {
            String msg = "Cannot request password reset due to a communication exception... Exception is: " + e.getMessage();
            Log.e(LOG_TAG, msg, e);
            throw new GeneralWebException(msg);
        }
    }

    @Override
    public void resetPassword(String passwordResetRequestKey, String newPassword) throws NoNetworkConnectionException, GeneralWebException, PasswordLengthValidationException, InvalidPasswordResetKeyException, PasswordResetKeyAlreadyUsedException, PasswordResetKeyExpiredException {
        checkNetworkConnection();

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setPasswordResetKey(passwordResetRequestKey);
        request.setNewPassword(newPassword);

        JsonResult result = null;
        try {
            result = webInvokePost(ENDPOINT_BASE_URL + ENDPOINT_REST, ENDPOINT_METHOD_RESET_PASSWORD, null, null, request, null);
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
            return;
        }

        ResetPasswordResponse response = result.getSingleResult(ResetPasswordResponse.class);
        if (!response.isResultOk()) {
            if (response.getFieldRequiredJSONException() != null) {
                // nothing to be done
            } else if (response.getPasswordLengthInvalidJSONException() != null) {
                throw new PasswordLengthValidationException();
            } else if (response.getInvalidPasswordResetKeyJSONException() != null) {
                throw  new InvalidPasswordResetKeyException();
            } else if (response.getPasswordResetKeyAlreadyUsedJSONException() != null) {
                throw new PasswordResetKeyAlreadyUsedException();
            } else if (response.getPasswordResetKeyExpiredJSONException() != null) {
                throw new PasswordResetKeyExpiredException();
            } else if (response.getServiceNotAllowedException() != null) {
                throw new RuntimeException("Your service is not allowed to access the application-server");
            } else {
                throw  new RuntimeException("Something went wrong...");
            }
        }
    }

    @Override
    public boolean registerGCMDevice(User user, String registrationId) {
        try {
            checkNetworkConnection();
        } catch (NoNetworkConnectionException e) {
            return false;
        }

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("serviceKey", EnvironmentConstants.WorkTimeWeb.SERVICE_KEY);
        parameters.put("email", user.getEmail());
        parameters.put("sessionKey", user.getSessionKey());
        parameters.put("registrationId", registrationId);

        JsonResult result = null;
        try {
            result = webInvokeGet(ENDPOINT_BASE_URL + ENDPOINT_REST, ENDPOINT_METHOD_REGISTER_ANDROID_DEVICE, null, parameters);
        } catch (WebException e) {
            String msg = "Cannot register a GCM device due to a web exception... Exception is: " + e.getMessage();
            Log.e(LOG_TAG, msg, e);
            return false;
        } catch (CommunicationException e) {
            String msg = "Cannot register a GCM device due to a communication exception... Exception is: " + e.getMessage();
            Log.e(LOG_TAG, msg, e);
            return false;
        }

        if (result == null) {
            return false;
        }

        GCMResponse response = result.getSingleResult(GCMResponse.class);
        return response.isResultOk();
    }

    @Override
    public boolean replaceGCMDevice(User user, String oldRegistrationId, String newRegistrationId) {
        try {
            checkNetworkConnection();
        } catch (NoNetworkConnectionException e) {
            return false;
        }

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("serviceKey", EnvironmentConstants.WorkTimeWeb.SERVICE_KEY);
        parameters.put("email", user.getEmail());
        parameters.put("sessionKey", user.getSessionKey());
        parameters.put("oldRegistrationId", oldRegistrationId);
        parameters.put("newRegistrationId", newRegistrationId);

        JsonResult result = null;
        try {
            result = webInvokeGet(ENDPOINT_BASE_URL + ENDPOINT_REST, ENDPOINT_METHOD_REPLACE_ANDROID_DEVICE, null, parameters);
        } catch (WebException e) {
            String msg = "Cannot replace a GCM device due to a web exception... Exception is: " + e.getMessage();
            Log.e(LOG_TAG, msg, e);
            return false;
        } catch (CommunicationException e) {
            String msg = "Cannot replace a GCM device due to a communication exception... Exception is: " + e.getMessage();
            Log.e(LOG_TAG, msg, e);
            return false;
        }

        if (result == null) {
            return false;
        }

        GCMResponse response = result.getSingleResult(GCMResponse.class);
        return response.isResultOk();
    }
}
