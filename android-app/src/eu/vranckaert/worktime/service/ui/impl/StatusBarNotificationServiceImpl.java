/*
 * Copyright 2012 Dirk Vranckaert
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

package eu.vranckaert.worktime.service.ui.impl;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.inject.Inject;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.activities.notifcationbar.EndTimeRegistration;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.service.ProjectService;
import eu.vranckaert.worktime.service.TaskService;
import eu.vranckaert.worktime.service.TimeRegistrationService;
import eu.vranckaert.worktime.service.impl.ProjectServiceImpl;
import eu.vranckaert.worktime.service.impl.TaskServiceImpl;
import eu.vranckaert.worktime.service.impl.TimeRegistrationServiceImpl;
import eu.vranckaert.worktime.service.ui.StatusBarNotificationService;
import eu.vranckaert.worktime.utils.preferences.Preferences;

public class StatusBarNotificationServiceImpl implements StatusBarNotificationService {
    private static final String LOG_TAG = StatusBarNotificationServiceImpl.class.getSimpleName();

    @Inject
    private Context context;

    @Inject
    private TaskService taskService;

    @Inject
    private ProjectService projectService;

    @Inject
    private TimeRegistrationService timeRegistrationService;

    public StatusBarNotificationServiceImpl(Context context) {
        this.context = context;
        getServices(context);
    }

    /**
     * Default constructor required by RoboGuice!
     */
    public StatusBarNotificationServiceImpl() {}

    @Override
    public void removeOngoingTimeRegistrationNotification() {
        //Remove the status bar notifications
        removeMessage(Constants.StatusBarNotificationIds.ONGOING_TIME_REGISTRATION_MESSAGE);
    }

    @Override
    public void addOrUpdateNotification(TimeRegistration registration) {
        Log.d(LOG_TAG, "Handling status bar notifications...");

        boolean showStatusBarNotifications = Preferences.getShowStatusBarNotificationsPreference(context);
        Log.d(LOG_TAG, "Status bar notifications enabled? " + (showStatusBarNotifications?"Yes":"No"));

        if (registration == null) {
            registration = timeRegistrationService.getLatestTimeRegistration();
            if (registration == null) {
                Log.d(LOG_TAG, "Cannot add a notification because no time registration is found!");
                return;
            }
        }

        if (showStatusBarNotifications && registration != null && registration.isOngoingTimeRegistration()) {
            //Create the status bar notifications
            Log.d(LOG_TAG, "Ongoing time registration... Refreshing the task and project...");
            taskService.refresh(registration.getTask());
            projectService.refresh(registration.getTask().getProject());

            String projectName = registration.getTask().getProject().getName();
            String taskName = registration.getTask().getName();

            String title = context.getString(R.string.lbl_notif_title_ongoing_tr);
            String message = context.getString(R.string.lbl_notif_project_task_name, projectName, taskName);
            String ticker = null;

            Intent intent = new Intent(context, EndTimeRegistration.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

            if (registration.getId() == null) {
                // creating...
                Log.d(LOG_TAG, "A status bar notifications for project '" + projectName + "' and task '" + taskName + "' will be created");
                ticker = context.getString(R.string.lbl_notif_new_tr_started);
            } else {
                // updating...
                Log.d(LOG_TAG, "The status bar notifications for project '" + projectName + "' and task '" + taskName + "' will be updated");
                ticker = context.getString(R.string.lbl_notif_update_tr);
            }

            setStatusBarNotification(
                    title, message, ticker, intent
            );
        }
    }

    /**
     * Get an instance of the Android {@link NotificationManager}.
     * @return An instance of the {@link NotificationManager}.
     */
    private NotificationManager getNotificationManager() {
        Log.d(LOG_TAG, "Creating a NoticationManager instance");

        String ns = Context.NOTIFICATION_SERVICE;
        return (NotificationManager) context.getSystemService(ns);
    }

    /**
     * Remove a message form the notification bar.
     * @param id The id of the message to be removed from the notification bar. The id should be found in
     * {@link Constants.StatusBarNotificationIds};
     */
    private void removeMessage(int id) {
        Log.d(LOG_TAG, "Remove status bar notification messages with ID: " + id);

        NotificationManager notificationManager = getNotificationManager();
        notificationManager.cancel(id);
    }

    /**
     * Removes all messages from the notification bar.
     */
    private void removeAllMessages() {
        Log.d(LOG_TAG, "Remove all messages");

        NotificationManager notificationManager = getNotificationManager();
        notificationManager.cancelAll();
    }

    /**
     * Creates a new status bar notification message.
     * @param title The title of the notification.
     * @param message The message of the notification.
     * @param ticker The ticker-text shown when the notification is created.
     * @param intent The intent to be launched when the notification is selected.
     */
    private void setStatusBarNotification(String title, String message, String ticker, Intent intent) {
        NotificationManager notificationManager = getNotificationManager();

        int icon = R.drawable.logo_notif_bar;
        Long when = System.currentTimeMillis();

        Notification notification = new Notification(icon, ticker, when);
        notification.flags |= Notification.FLAG_NO_CLEAR;

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
        notification.setLatestEventInfo(context, title, message, contentIntent);

        notificationManager.notify(
                Constants.StatusBarNotificationIds.ONGOING_TIME_REGISTRATION_MESSAGE,
                notification
        );
    }

    /**
     * Create all the required service instances.
     * @param ctx The widget's context.
     */
    private void getServices(Context ctx) {
        this.timeRegistrationService = new TimeRegistrationServiceImpl(ctx);
        this.projectService = new ProjectServiceImpl(ctx);
        this.taskService = new TaskServiceImpl(ctx);
        Log.d(LOG_TAG, "Services ok!");
    }
}
