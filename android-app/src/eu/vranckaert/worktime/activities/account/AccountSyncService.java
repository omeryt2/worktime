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

package eu.vranckaert.worktime.activities.account;

import android.content.Intent;
import android.widget.Toast;
import com.google.inject.Inject;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.exceptions.backup.BackupException;
import eu.vranckaert.worktime.exceptions.network.NoNetworkConnectionException;
import eu.vranckaert.worktime.exceptions.network.WifiConnectionRequiredException;
import eu.vranckaert.worktime.exceptions.worktime.account.LoginCredentialsMismatchException;
import eu.vranckaert.worktime.exceptions.worktime.account.UserNotLoggedInException;
import eu.vranckaert.worktime.exceptions.worktime.sync.SyncAlreadyBusyException;
import eu.vranckaert.worktime.exceptions.worktime.sync.SynchronizationFailedException;
import eu.vranckaert.worktime.service.AccountService;
import eu.vranckaert.worktime.service.ui.StatusBarNotificationService;
import eu.vranckaert.worktime.service.ui.WidgetService;
import eu.vranckaert.worktime.utils.preferences.Preferences;
import eu.vranckaert.worktime.web.json.exception.GeneralWebException;
import roboguice.service.RoboIntentService;

/**
 * User: Dirk Vranckaert
 * Date: 15/01/13
 * Time: 07:50
 */
public class AccountSyncService extends RoboIntentService {
    @Inject private AccountService accountService;
    @Inject private WidgetService widgetService;
    @Inject private StatusBarNotificationService notificationService;

    private int syncTries = 0;

    public AccountSyncService() {
        super(AccountSyncService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        startSync();
    }

    public void startSync() {
        Exception exception = sync();
        handleResult(exception);
    }

    /**
     * Execute synchronization. If anything goes wrong the synchronization will be ended. If the user doesn't seem to be
     * logged in we will try to lookup the user-credentials and log him in again once. If it that doesn't work again
     * the synchronization will end.
     */
    private Exception sync() {
        Exception exception = null;

        syncTries++;
        try {
            accountService.sync();
        } catch (UserNotLoggedInException e) {
            if (syncTries < 2) { // Only tries to execute the sync twice...
                try {
                    accountService.reLogin();
                    return sync();
                } catch (GeneralWebException e1) {
                    exception = e1;
                } catch (NoNetworkConnectionException e1) {
                    exception = e1;
                } catch (LoginCredentialsMismatchException e1) {
                    exception = e1;
                }
            }
        } catch (GeneralWebException e) {
            exception = e;
        } catch (NoNetworkConnectionException e) {
            exception = e;
        } catch (WifiConnectionRequiredException e) {
            exception = e;
        } catch (BackupException e) {
            exception = e;
        } catch (SyncAlreadyBusyException e) {
            exception = e;
        } catch (SynchronizationFailedException e) {
            exception = e;
        }

        return exception;
    }

    private void handleResult(Exception e) {
        if (e == null) {
            showMessageSuccess(R.string.lbl_sync_service_successful_title, R.string.lbl_sync_service_successful_message, R.string.lbl_sync_service_successful_message);
            widgetService.updateAllWidgets();
            notificationService.addOrUpdateNotification(null);
        } else {
            if (e instanceof LoginCredentialsMismatchException) {
                showMessageError(R.string.lbl_sync_service_error_title, R.string.lbl_sync_service_error_message, R.string.lbl_sync_service_error_user_not_logged_in);
                accountService.logout();
                Intent intent = new Intent(AccountSyncService.this, AccountLoginActivity.class);
                startActivity(intent);
            } else if (e instanceof GeneralWebException) {
                showMessageError(R.string.lbl_sync_service_error_title, R.string.lbl_sync_service_error_message, R.string.error_general_web_exception);
            } else if (e instanceof NoNetworkConnectionException) {
                showMessageError(R.string.lbl_sync_service_error_title, R.string.lbl_sync_service_error_message, R.string.error_no_network_connection);
            } else if (e instanceof WifiConnectionRequiredException) {
                showMessageError(R.string.lbl_sync_service_error_title, R.string.lbl_sync_service_error_message, R.string.lbl_sync_service_error_wifi_required);
            } else if (e instanceof BackupException) {
                showMessageError(R.string.lbl_sync_service_error_title, R.string.lbl_sync_service_error_message, R.string.lbl_sync_service_error_backup);
            } else if (e instanceof SyncAlreadyBusyException) {
                showMessageError(R.string.lbl_sync_service_error_title, R.string.lbl_sync_service_error_message, R.string.lbl_sync_service_error_already_busy);
            } else if (e instanceof SynchronizationFailedException) {
                showMessageError(R.string.lbl_sync_service_error_title, R.string.lbl_sync_service_error_message, R.string.lbl_sync_service_error_sync_failed);
            }
        }
    }

    private void showMessageSuccess(int titleResId, int smallMsgResId, int msgResId) {
        if (Preferences.Account.syncShowNotificationsOnSuccess(AccountSyncService.this)) {
            notificationService.addStatusBarNotificationForSync(titleResId, smallMsgResId, msgResId);
        } else {
            showText(msgResId);
        }
    }

    private void showMessageError(int titleResId, int smallMsgResId, int msgResId) {
        if (Preferences.Account.syncShowNotificationsOnError(AccountSyncService.this)) {
            notificationService.addStatusBarNotificationForSync(titleResId, smallMsgResId, msgResId);
        } else {
            showText(msgResId);
        }
    }

    private void showText(int resId) {
        Toast.makeText(AccountSyncService.this, resId, Toast.LENGTH_LONG).show();
    }
}
