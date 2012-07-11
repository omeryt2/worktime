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
import com.jakewharton.notificationcompat2.NotificationCompat2;
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
import eu.vranckaert.worktime.utils.date.DateFormat;
import eu.vranckaert.worktime.utils.date.DateUtils;
import eu.vranckaert.worktime.utils.date.TimeFormat;
import eu.vranckaert.worktime.utils.preferences.Preferences;
import eu.vranckaert.worktime.utils.string.StringUtils;

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

            String startTime = DateUtils.DateTimeConverter.convertDateTimeToString(registration.getStartTime(), DateFormat.MEDIUM, TimeFormat.MEDIUM, context);
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

            String bigText = context.getString(R.string.lbl_notif_big_text_project) + ": " + projectName + "\n"
                    + context.getString(R.string.lbl_notif_big_text_task) + ": " + taskName + "\n"
                    + context.getString(R.string.lbl_notif_big_text_started_at) + " " + startTime;

            if (StringUtils.isNotBlank(registration.getComment())) {
                bigText += "\n" + context.getString(R.string.lbl_notif_big_text_comment) + ": " + registration.getComment();
            }

            setStatusBarNotification(
                    title, message, ticker, intent, bigText
            );
        }
    }

    @Override
    public void addStatusBarNotificationForRestore() {
        CharSequence ticker = context.getString(R.string.msg_restore_notification_ticker);
        CharSequence title = context.getString(R.string.msg_restore_notification_title);
        CharSequence message = context.getString(R.string.msg_restore_notification_message);

        NotificationManager notificationManager = getNotificationManager();

        int icon = R.drawable.logo_notif_bar;
        Long when = System.currentTimeMillis();

        Notification notification = new Notification(icon, ticker, when);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, null, 0);
        notification.setLatestEventInfo(context, title, message, contentIntent);

        notificationManager.notify(
                Constants.StatusBarNotificationIds.RESTORE_SUCCESSFUL,
                notification
        );
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
    private void setStatusBarNotification(String title, String message, String ticker, Intent intent, String bigText) {
        // NotificationManager notificationManager = getNotificationManager();

        int icon = R.drawable.logo_notif_bar;

        // Notification notification = new Notification(icon, ticker, when);
        // notification.flags |= Notification.FLAG_NO_CLEAR;

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
        // notification.setLatestEventInfo(context, title, message, contentIntent);

        // notificationManager.notify(
        //         Constants.StatusBarNotificationIds.ONGOING_TIME_REGISTRATION_MESSAGE,
        //         notification
        // );


        final NotificationManager mgr = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        NotificationCompat2.Builder builder = new NotificationCompat2.Builder(context)
                .setSmallIcon(icon)
                .setTicker(ticker)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat2.PRIORITY_LOW);

        Notification notification = new NotificationCompat2.BigTextStyle(builder).bigText(bigText).build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        mgr.notify(Constants.StatusBarNotificationIds.ONGOING_TIME_REGISTRATION_MESSAGE, notification);
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
