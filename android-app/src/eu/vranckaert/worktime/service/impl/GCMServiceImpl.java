package eu.vranckaert.worktime.service.impl;

import android.app.Activity;
import android.content.Context;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.inject.Inject;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.dao.web.WorkTimeWebDao;
import eu.vranckaert.worktime.exceptions.GooglePlayServiceRequiredException;
import eu.vranckaert.worktime.model.User;
import eu.vranckaert.worktime.service.AccountService;
import eu.vranckaert.worktime.service.GCMService;
import eu.vranckaert.worktime.utils.context.ContextUtils;
import eu.vranckaert.worktime.utils.preferences.Preferences;
import eu.vranckaert.worktime.utils.string.StringUtils;

import java.io.IOException;

/**
 * Date: 29/10/13
 * Time: 09:21
 *
 * @author Dirk Vranckaert
 */
public class GCMServiceImpl implements GCMService {
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9999;
    private final static String SENDER_ID = "863446466275";

    @Inject
    private Context context;

    @Inject
    private AccountService accountService;

    @Inject
    private WorkTimeWebDao workTimeWebDao;

    @Override
    public void updateGCMConfiguration() throws GooglePlayServiceRequiredException {
        User user = accountService.getOfflineUserData();
        boolean googlePlayServiceAvailable = true;
        GooglePlayServiceRequiredException googlePlayServiceRequiredException = null;
        try {
            googlePlayServiceAvailable = checkPlayServices();
        } catch (GooglePlayServiceRequiredException e) {
            googlePlayServiceAvailable = false;
            googlePlayServiceRequiredException = e;
        }
        if (user == null || !accountService.isUserLoggedIn() || !googlePlayServiceAvailable) {
            resetData();
            if (user != null && accountService.isUserLoggedIn() && googlePlayServiceRequiredException != null) {
                throw googlePlayServiceRequiredException;
            }
            return;
        }

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        String registrationId = Preferences.GCM.getRegistrationId(context);

        boolean updateRequired = false;
        int currentAppVersion = ContextUtils.getCurrentApplicationVersionCode(context);
        if (Preferences.GCM.getPreviousGCMAppVersion(context) < currentAppVersion) {
            updateRequired = true;
        }

        if (StringUtils.isBlank(registrationId) || updateRequired) {
            String newRegistrationId = null;
            try {
                newRegistrationId = gcm.register(SENDER_ID);
            } catch (IOException e) {}

            if (StringUtils.isNotBlank(newRegistrationId)) {
                boolean result = false;
                if (StringUtils.isBlank(registrationId)) {
                    result = workTimeWebDao.registerGCMDevice(user, newRegistrationId);
                } else {
                    result = workTimeWebDao.replaceGCMDevice(user, registrationId, newRegistrationId);
                }
                if (result) {
                    Preferences.GCM.setCanShowUpdateDialog(context, true);
                    Preferences.GCM.setRegistrationId(context, newRegistrationId);
                    Preferences.GCM.setPreviousGCMAppVersion(context, currentAppVersion);
                } else {
                    resetData();
                }
            } else {
                resetData();
            }
        }
    }

    private boolean checkPlayServices() throws GooglePlayServiceRequiredException {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode) && Preferences.GCM.canShowUpdateDialog(context)) {
                throw new GooglePlayServiceRequiredException(resultCode);
            }
            return false;
        }
        return true;
    }

    private void resetData() {
        Preferences.GCM.setRegistrationId(context, null);
        Preferences.GCM.setPreviousGCMAppVersion(context, Constants.Preferences.GCM_PREVIOUS_APP_VERSION_DEFAULT_VALUE);
    }
}
