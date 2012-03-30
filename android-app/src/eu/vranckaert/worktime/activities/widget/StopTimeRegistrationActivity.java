/*
 *  Copyright 2011 Dirk Vranckaert
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package eu.vranckaert.worktime.activities.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.inject.Inject;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.activities.HomeActivity;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.constants.TrackerConstants;
import eu.vranckaert.worktime.model.Task;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.service.CommentHistoryService;
import eu.vranckaert.worktime.service.TaskService;
import eu.vranckaert.worktime.service.TimeRegistrationService;
import eu.vranckaert.worktime.service.WidgetService;
import eu.vranckaert.worktime.utils.context.ContextUtils;
import eu.vranckaert.worktime.utils.notifications.NotificationBarManager;
import eu.vranckaert.worktime.utils.preferences.Preferences;
import eu.vranckaert.worktime.utils.string.StringUtils;
import eu.vranckaert.worktime.utils.tracker.AnalyticsTracker;
import roboguice.activity.GuiceActivity;

import java.util.Date;

/**
 * User: DIRK VRANCKAERT
 * Date: 09/02/11
 * Time: 23:25
 */
public class StopTimeRegistrationActivity extends GuiceActivity {
    private static final String LOG_TAG = StopTimeRegistrationActivity.class.getSimpleName();

    @Inject
    private WidgetService widgetService;

    @Inject
    private TimeRegistrationService timeRegistrationService;

    @Inject
    private CommentHistoryService commentHistoryService;

    @Inject
    private TaskService taskService;

    private AnalyticsTracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tracker = AnalyticsTracker.getInstance(getApplicationContext());
        Log.d(LOG_TAG, "Started the STOP TimeRegistration acitivity");

        if (Preferences.getWidgetEndingTimeRegistrationCommentPreference(getApplicationContext())) {
            showDialog(Constants.Dialog.ENTER_COMMENT_FOR_TR);
        } else {
            endTimeRegistration(null);
        }
    }

    private void endTimeRegistration(final String comment) {
        AsyncTask threading = new AsyncTask() {

            @Override
            protected void onPreExecute() {
                showDialog(Constants.Dialog.LOADING_TIMEREGISTRATION_CHANGE);
            }

            @Override
            protected Object doInBackground(Object... objects) {
                Log.d(LOG_TAG, "Is there already a looper? " + (Looper.myLooper() != null));
                if(Looper.myLooper() == null) {
                    Looper.prepare();
                }

                Date endTime = new Date();

                TimeRegistration latestRegistration = timeRegistrationService.getLatestTimeRegistration();

                if (latestRegistration == null || latestRegistration.getEndTime() != null) {
                    Log.w(LOG_TAG, "Data must be incorrupt! Please clear all the data through the system settings of the application!");
                    return new Object();
                } else {
                    latestRegistration.setEndTime(endTime);
                    // Issue 102 - If no comment is entered when ending TR (and thus the parameter 'comment' is null),
                    // then the already entered comment is gone...
                    // latestRegistration.setComment(null);
                    if (StringUtils.isNotBlank(comment)) {
                        latestRegistration.setComment(comment);
                        tracker.trackEvent(
                                TrackerConstants.EventSources.STOP_TIME_REGISTRATION_ACTIVITY,
                                TrackerConstants.EventActions.ADD_TR_COMMENT
                        );
                    }
                    timeRegistrationService.update(latestRegistration);

                    tracker.trackEvent(
                            TrackerConstants.EventSources.STOP_TIME_REGISTRATION_ACTIVITY,
                            TrackerConstants.EventActions.END_TIME_REGISTRATION
                    );

                    NotificationBarManager notificationBarManager =
                            NotificationBarManager.getInstance(getApplicationContext());
                    notificationBarManager.removeMessage(
                            NotificationBarManager.NotificationIds.ONGOING_TIME_REGISTRATION_MESSAGE
                    );

                    widgetService.updateWidget(StopTimeRegistrationActivity.this);
                }

                if (StringUtils.isNotBlank(comment)) {
                    commentHistoryService.updateLastComment(comment);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                removeDialog(Constants.Dialog.LOADING_TIMEREGISTRATION_CHANGE);
                Log.d(LOG_TAG, "Loading dialog removed from UI");
                if (o != null) {
                    Log.d(LOG_TAG, "Something went wrong...");
                    Toast.makeText(StopTimeRegistrationActivity.this, R.string.err_widget_corrupt_data, Toast.LENGTH_LONG).show();
                } else if (o == null) {
                    Log.d(LOG_TAG, "Successfully ended time registration");
                    Toast.makeText(StopTimeRegistrationActivity.this, R.string.msg_widget_time_reg_ended, Toast.LENGTH_LONG).show();
                }
                Log.d(LOG_TAG, "Finishing activity...");

                boolean askFinishTask = Preferences.getWidgetEndingTimeRegistrationFinishTaskPreference(getApplicationContext());
                if (!askFinishTask) {
                    finish();
                }
                showDialog(Constants.Dialog.ASK_FINISH_TASK);
            }
        };
        threading.execute();
    }

    private void finishTask(Task task) {
        task.setFinished(true);
        taskService.update(task);

        tracker.trackEvent(
                TrackerConstants.EventSources.STOP_TIME_REGISTRATION_ACTIVITY,
                TrackerConstants.EventActions.MARK_TASK_FINISHED
        );

        finish();
    }

    @Override
    protected Dialog onCreateDialog(int dialogId) {
        Dialog dialog = null;
        switch(dialogId) {
            case Constants.Dialog.LOADING_TIMEREGISTRATION_CHANGE: {
                Log.d(LOG_TAG, "Creating loading dialog for ending the active time registration");
                dialog = ProgressDialog.show(
                        StopTimeRegistrationActivity.this,
                        "",
                        getString(R.string.lbl_widget_ending_timeregistration),
                        true,
                        false
                );
                break;
            }

            case Constants.Dialog.ENTER_COMMENT_FOR_TR: {
                Log.d(LOG_TAG, "Creating enter comment dialog for ending a time registration");
                AlertDialog.Builder enterComment = new AlertDialog.Builder(this);

                final Context mContext = StopTimeRegistrationActivity.this;
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                final View layout = inflater.inflate(R.layout.dialog_add_tr_comment,
                                               (ViewGroup) findViewById(R.id.dialog_layout_root));
                // Issue 89: No null-check on latestTimeRegistration required because it can never be null as at least
                // one time registration should be started in order to be able to stop one..!
                TimeRegistration latestRegistration = timeRegistrationService.getLatestTimeRegistration();
                final EditText commentEditText = (EditText) layout.findViewById(R.id.tr_comment);
                if (latestRegistration.getComment() != null) {
                    commentEditText.setText(latestRegistration.getComment());
                }

                enterComment.setTitle(R.string.lbl_widget_dialog_title_enter_comment);
                enterComment.setCancelable(false);
                enterComment.setPositiveButton(R.string.btn_widget_stop, new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(LOG_TAG, "CommentHistory entered, ready to save...");
                        removeDialog(Constants.Dialog.ENTER_COMMENT_FOR_TR);
//                        AutoCompleteTextView commentEditText =
//                                (AutoCompleteTextView) layout.findViewById(R.id.tr_comment);
                        EditText commentEditText = (EditText) layout.findViewById(R.id.tr_comment);
                        String comment = commentEditText.getText().toString();
                        ContextUtils.hideKeyboard(mContext, commentEditText);
                        Log.d(LOG_TAG, "Time Registration will be saved with comment: " + comment);
                        endTimeRegistration(comment);
                    }
                });
                enterComment.setNeutralButton(R.string.btn_widget_open_app, new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(LOG_TAG, "Launching application when about to enter comment...");
                        removeDialog(Constants.Dialog.ENTER_COMMENT_FOR_TR);

                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);

                        finish();
                    }
                });
                enterComment.setNegativeButton(R.string.cancel, new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(LOG_TAG, "Cancelled ending TR when about to enter comment...");
                        removeDialog(Constants.Dialog.ENTER_COMMENT_FOR_TR);
                        finish();
                    }
                });

                Button reuseComment = (Button) layout.findViewById(R.id.tr_reuse_btn);
                reuseComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String comment = commentHistoryService.findLastComment();
                        if (comment != null) {
                            commentEditText.setText(comment);
                        }
                    }
                });

//                AutoCompleteTextView commentEditText = (AutoCompleteTextView) layout.findViewById(R.id.tr_comment);
//                List<String> options = commentHistoryService.getAll();
//                ArrayAdapter<String> autoCompleteAdapter =
//                        new ArrayAdapter<String>(this, R.layout.autocomplete_list_item, options);
//                commentEditText.setAdapter(autoCompleteAdapter);

                enterComment.setView(layout);
                dialog = enterComment.create();

                break;
            }
            case Constants.Dialog.ASK_FINISH_TASK: {
                // Issue 89: No null-check on latestTimeRegistration required because it can never be null as at least
                // one time registration should be started in order to be able to stop one..!
                final Task task = timeRegistrationService.getLatestTimeRegistration().getTask();
                taskService.refresh(task);

                AlertDialog.Builder alertRemoveAllRegs = new AlertDialog.Builder(this);
				alertRemoveAllRegs.setTitle(R.string.msg_widget_ask_finish_task_title)
						   .setMessage(getString(R.string.msg_widget_ask_finish_task_message, task.getName()))
						   .setCancelable(false)
						   .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									finishTask(task);
                                    removeDialog(Constants.Dialog.ASK_FINISH_TASK);
								}
							})
						   .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
                                    finish();
									removeDialog(Constants.Dialog.ASK_FINISH_TASK);
								}
							});
				dialog = alertRemoveAllRegs.create();
                break;
            }
        };
        return dialog;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tracker.stopSession();
    }
}
