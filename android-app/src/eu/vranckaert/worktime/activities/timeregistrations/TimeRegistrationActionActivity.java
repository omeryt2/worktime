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

package eu.vranckaert.worktime.activities.timeregistrations;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.Toast;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.activities.timeregistrations.listadapter.TimeRegistrationActionListAdapter;
import eu.vranckaert.worktime.activities.widget.StartTimeRegistrationActivity;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.constants.TrackerConstants;
import eu.vranckaert.worktime.enums.timeregistration.TimeRegistrationAction;
import eu.vranckaert.worktime.model.Task;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.service.BackupService;
import eu.vranckaert.worktime.service.CommentHistoryService;
import eu.vranckaert.worktime.service.TaskService;
import eu.vranckaert.worktime.service.TimeRegistrationService;
import eu.vranckaert.worktime.service.impl.CommentHistoryServiceImpl;
import eu.vranckaert.worktime.service.impl.DatabaseFileBackupServiceImpl;
import eu.vranckaert.worktime.service.impl.TaskServiceImpl;
import eu.vranckaert.worktime.service.impl.TimeRegistrationServiceImpl;
import eu.vranckaert.worktime.service.ui.StatusBarNotificationService;
import eu.vranckaert.worktime.service.ui.WidgetService;
import eu.vranckaert.worktime.service.ui.impl.StatusBarNotificationServiceImpl;
import eu.vranckaert.worktime.service.ui.impl.WidgetServiceImpl;
import eu.vranckaert.worktime.utils.context.ContextUtils;
import eu.vranckaert.worktime.utils.context.Log;
import eu.vranckaert.worktime.utils.date.DateFormat;
import eu.vranckaert.worktime.utils.date.DateUtils;
import eu.vranckaert.worktime.utils.preferences.Preferences;
import eu.vranckaert.worktime.utils.string.StringUtils;
import eu.vranckaert.worktime.utils.tracker.AnalyticsTracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 09/02/11
 * Time: 23:25
 */
public class TimeRegistrationActionActivity extends Activity {
    /**
     * LOG_TAG for logging
     */
    private static final String LOG_TAG = TimeRegistrationActionActivity.class.getSimpleName();

    /**
     * Services
     */
    private WidgetService widgetService;
    private StatusBarNotificationService statusBarNotificationService;
    private TimeRegistrationService timeRegistrationService;
    private CommentHistoryService commentHistoryService;
    private TaskService taskService;
    private BackupService backupService;

    /**
     * Extras
     */
    private TimeRegistration timeRegistration;
    private Integer widgetId;

    /**
     * Google Analytics Tracker
     */
    private AnalyticsTracker tracker;

    /**
     * Vars
     */
    private Calendar removeRangeMinBoundary = null;
    private Calendar removeRangeMaxBoundary = null;
    private Button deleteRangeFromButton = null;
    private Button deleteRangeToButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadExtras();
        loadServices();

        tracker = AnalyticsTracker.getInstance(getApplicationContext());
        Log.d(getApplicationContext(), LOG_TAG, "Started the TimeRegistration Action activity");

        if (timeRegistration == null) {
            Log.e(getApplicationContext(), LOG_TAG, "The time registration should never be null in the TimeRegistrationActionActivity!");
            throw new RuntimeException("The time registration should never be null in the TimeRegistrationActionActivity!");
        } else {
            timeRegistrationService.fullyInitialize(timeRegistration);
            Log.d(getApplicationContext(), LOG_TAG, "Launching action-activity with timeRegistration " + timeRegistration);
        }

        showDialog(Constants.Dialog.TIME_REGISTRATION_ACTION);
    }

    /**
     * Get all the extras...
     */
    private void loadExtras() {
        timeRegistration = (TimeRegistration) getIntent().getExtras().get(Constants.Extras.TIME_REGISTRATION);
        widgetId = (Integer) getIntent().getExtras().get(Constants.Extras.WIDGET_ID);
    }

    /**
     * Load all the services...
     */
    private void loadServices() {
        widgetService = new WidgetServiceImpl(TimeRegistrationActionActivity.this);
        statusBarNotificationService = new StatusBarNotificationServiceImpl(TimeRegistrationActionActivity.this);
        timeRegistrationService = new TimeRegistrationServiceImpl(TimeRegistrationActionActivity.this);
        commentHistoryService = new CommentHistoryServiceImpl(TimeRegistrationActionActivity.this);
        taskService = new TaskServiceImpl(TimeRegistrationActionActivity.this);
        backupService = new DatabaseFileBackupServiceImpl();
    }

    /**
     * Updates the {@link TimeRegistration} with a comment (if entered) and sets an end date and time. If the preference
     * {@link Preferences#getWidgetEndingTimeRegistrationFinishTaskPreference(android.content.Context)} returns
     * {@link Boolean#TRUE} the user will be asked to end the task.
     * @param comment The comment to be put in the {@link TimeRegistration}.
     * @param startNew If a new time registration should be started after ending the current one.
     */
    private void endTimeRegistration(final String comment, final boolean startNew) {
        AsyncTask threading = new AsyncTask() {

            @Override
            protected void onPreExecute() {
                showDialog(Constants.Dialog.LOADING_TIME_REGISTRATION_CHANGE);
            }

            @Override
            protected Object doInBackground(Object... objects) {
                Log.d(getApplicationContext(), LOG_TAG, "Is there already a looper? " + (Looper.myLooper() != null));
                if(Looper.myLooper() == null) {
                    Looper.prepare();
                }

                Date endTime = new Date();

                if (timeRegistration.getEndTime() != null) {
                    Log.w(getApplicationContext(), LOG_TAG, "Data must be corrupt, time registration is already ended! Please clear all the data through the system settings of the application!");
                    return new Object();
                } else {
                    timeRegistration.setEndTime(endTime);
                    // Issue 102 - If no comment is entered when ending TR (and thus the parameter 'comment' is null),
                    // then the already entered comment is gone.
                    // latestRegistration.setComment(null);
                    if (StringUtils.isNotBlank(comment)) {
                        timeRegistration.setComment(comment);
                        tracker.trackEvent(
                                TrackerConstants.EventSources.TIME_REGISTRATION_ACTION_ACTIVITY,
                                TrackerConstants.EventActions.ADD_TR_COMMENT
                        );
                    }
                    timeRegistrationService.update(timeRegistration);

                    tracker.trackEvent(
                            TrackerConstants.EventSources.TIME_REGISTRATION_ACTION_ACTIVITY,
                            TrackerConstants.EventActions.END_TIME_REGISTRATION
                    );

                    statusBarNotificationService.removeOngoingTimeRegistrationNotification();

                    widgetService.updateAllWidgets();
                }

                if (StringUtils.isNotBlank(comment)) {
                    commentHistoryService.updateLastComment(comment);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                removeDialog(Constants.Dialog.LOADING_TIME_REGISTRATION_CHANGE);
                Log.d(getApplicationContext(), LOG_TAG, "Loading dialog removed from UI");
                if (o != null) {
                    Log.d(getApplicationContext(), LOG_TAG, "Something went wrong, the data is corrupt");
                    Toast.makeText(TimeRegistrationActionActivity.this, R.string.err_time_registration_actions_dialog_corrupt_data, Toast.LENGTH_LONG).show();
                } else if (o == null) {
                    Log.d(getApplicationContext(), LOG_TAG, "Successfully ended time registration");
                    Toast.makeText(TimeRegistrationActionActivity.this, R.string.msg_widget_time_reg_ended, Toast.LENGTH_LONG).show();
                }
                Log.d(getApplicationContext(), LOG_TAG, "Finishing activity...");

                boolean askFinishTask = Preferences.getWidgetEndingTimeRegistrationFinishTaskPreference(getApplicationContext());
                if (startNew) {
                    if (widgetId == null) {
                        widgetId = Constants.Others.PUNCH_BAR_WIDGET_ID;
                    }
                    Intent intent = new Intent(TimeRegistrationActionActivity.this, StartTimeRegistrationActivity.class);
                    intent.putExtra(Constants.Extras.WIDGET_ID, widgetId);
                    intent.putExtra(Constants.Extras.UPDATE_WIDGET, true);
                    startActivityForResult(intent, Constants.IntentRequestCodes.START_TIME_REGISTRATION);
                } else if (askFinishTask) {
                    showDialog(Constants.Dialog.ASK_FINISH_TASK);
                } else {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        };
        threading.execute();
    }

    /**
     * After a positive response from the user the specified task will be marked as finished. This will only be
     * triggered when ending a {@link TimeRegistration} and when the preference
     * {@link Preferences#getWidgetEndingTimeRegistrationFinishTaskPreference(android.content.Context)} returns
     * {@link Boolean#TRUE}.
     * @param task The {@link Task} that should be marked as finished ({@link Task#finished}).
     */
    private void finishTask(Task task) {
        task.setFinished(true);
        taskService.update(task);

        tracker.trackEvent(
                TrackerConstants.EventSources.TIME_REGISTRATION_ACTION_ACTIVITY,
                TrackerConstants.EventActions.MARK_TASK_FINISHED
        );

        setResult(RESULT_OK);
        finish();
    }

    private void deleteTimeRegistrations(Date minBoundary, Date maxBoundary) {
        AsyncTask<Date, Void, Long> threading = new AsyncTask<Date, Void, Long>() {
            @Override
            protected void onPreExecute() {
                showDialog(Constants.Dialog.TIME_REGISTRATIONS_DELETE_LOADING);
            }

            @Override
            protected Long doInBackground(Date... boundaries) {
                Log.d(getApplicationContext(), LOG_TAG, "Is there already a looper? " + (Looper.myLooper() != null));
                if(Looper.myLooper() == null) {
                    Looper.prepare();
                }

                Date minBoundary = boundaries[0];
                if (minBoundary != null)
                    minBoundary = DateUtils.Various.setMinTimeValueOfDay(minBoundary);
                Date maxBoundary = boundaries[1];
                if (maxBoundary != null)
                    maxBoundary = DateUtils.Various.setMaxTimeValueOfDay(maxBoundary);

                long count = timeRegistrationService.removeAllInRange(minBoundary, maxBoundary);

                widgetService.updateAllWidgets();

                TimeRegistration latestTimeRegistration = timeRegistrationService.getLatestTimeRegistration();
                if (latestTimeRegistration != null && latestTimeRegistration.isOngoingTimeRegistration()) {
                    statusBarNotificationService.addOrUpdateNotification(latestTimeRegistration);
                } else {
                    statusBarNotificationService.removeOngoingTimeRegistrationNotification();
                }

                tracker.trackEvent(
                        TrackerConstants.EventSources.TIME_REGISTRATION_ACTION_ACTIVITY,
                        TrackerConstants.EventActions.DELETE_TIME_REGISTRATIONS_IN_RANGE
                );

                return count;
            }

            @Override
            protected void onPostExecute(Long count) {
                removeDialog(Constants.Dialog.TIME_REGISTRATIONS_DELETE_LOADING);
                Log.d(getApplicationContext(), LOG_TAG, "Loading dialog removed from UI");

                String message = "";
                if (count == 1l) {
                    message = getString(R.string.msg_time_registration_actions_dialog_removing_time_registrations_range_done_single, count);
                } else {
                    message = getString(R.string.msg_time_registration_actions_dialog_removing_time_registrations_range_done_multiple, count);
                }
                Toast.makeText(TimeRegistrationActionActivity.this, message, Toast.LENGTH_LONG).show();

                Log.d(getApplicationContext(), LOG_TAG, "Finishing activity...");
                setResult(Constants.IntentResultCodes.RESULT_DELETED);
                finish();
            }
        };
        threading.execute(minBoundary, maxBoundary);
    }

    /**
     * Deletes a {@link TimeRegistration} instance from the database.
     */
    private void deleteTimeRegistration() {
        AsyncTask threading = new AsyncTask() {

            @Override
            protected void onPreExecute() {
                showDialog(Constants.Dialog.TIME_REGISTRATION_DELETE_LOADING);
            }

            @Override
            protected Object doInBackground(Object... objects) {
                Log.d(getApplicationContext(), LOG_TAG, "Is there already a looper? " + (Looper.myLooper() != null));
                if(Looper.myLooper() == null) {
                    Looper.prepare();
                }

                timeRegistrationService.remove(timeRegistration);

                widgetService.updateAllWidgets();

                if (timeRegistration.isOngoingTimeRegistration()) {
                    statusBarNotificationService.removeOngoingTimeRegistrationNotification();
                }

                tracker.trackEvent(
                        TrackerConstants.EventSources.TIME_REGISTRATION_ACTION_ACTIVITY,
                        TrackerConstants.EventActions.DELETE_TIME_REGISTRATION
                );

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                removeDialog(Constants.Dialog.TIME_REGISTRATION_DELETE_LOADING);
                Log.d(getApplicationContext(), LOG_TAG, "Loading dialog removed from UI");
                if (o != null) {
                    Log.d(getApplicationContext(), LOG_TAG, "Something went wrong...");
                    Toast.makeText(TimeRegistrationActionActivity.this, R.string.err_time_registration_actions_dialog_corrupt_data, Toast.LENGTH_LONG).show();
                }

                Log.d(getApplicationContext(), LOG_TAG, "Finishing activity...");
                setResult(Constants.IntentResultCodes.RESULT_DELETED);
                finish();
            }
        };
        threading.execute();
    }

    /**
     * Updates the comment of a {@link TimeRegistration}.
     * @param comment The comment to be put in the {@link TimeRegistration}.
     */
    private void updateTimeRegistration(final String comment) {
        AsyncTask threading = new AsyncTask() {

            @Override
            protected void onPreExecute() {
                showDialog(Constants.Dialog.TIME_REGISTRATION_ACTION_LOADING);
            }

            @Override
            protected Object doInBackground(Object... objects) {
                Log.d(getApplicationContext(), LOG_TAG, "Is there already a looper? " + (Looper.myLooper() != null));
                if(Looper.myLooper() == null) {
                    Looper.prepare();
                }

                timeRegistration.setComment(comment);
                tracker.trackEvent(
                        TrackerConstants.EventSources.TIME_REGISTRATION_ACTION_ACTIVITY,
                        TrackerConstants.EventActions.ADD_TR_COMMENT
                );
                timeRegistrationService.update(timeRegistration);
                widgetService.updateWidgetsForTask(timeRegistration.getTask());
                statusBarNotificationService.addOrUpdateNotification(timeRegistration);

                if (StringUtils.isNotBlank(comment)) {
                    commentHistoryService.updateLastComment(comment);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                removeDialog(Constants.Dialog.TIME_REGISTRATION_ACTION_LOADING);
                Log.d(getApplicationContext(), LOG_TAG, "Loading dialog removed from UI");
                Log.d(getApplicationContext(), LOG_TAG, "Finishing activity...");
                setResult(RESULT_OK);
                finish();
            }
        };
        threading.execute();
    }

    /**
     * Resets the end date of a {@link TimeRegistration} so that the
     * {@link eu.vranckaert.worktime.model.TimeRegistration#isOngoingTimeRegistration()} is {@link Boolean#TRUE} again.
     */
    private void restartTimeRegistration() {
        AsyncTask threading = new AsyncTask() {

            @Override
            protected void onPreExecute() {
                showDialog(Constants.Dialog.TIME_REGISTRATION_ACTION_LOADING);
            }

            @Override
            protected Object doInBackground(Object... objects) {
                Log.d(getApplicationContext(), LOG_TAG, "Is there already a looper? " + (Looper.myLooper() != null));
                if(Looper.myLooper() == null) {
                    Looper.prepare();
                }

                timeRegistration.setEndTime(null);
                timeRegistrationService.update(timeRegistration);

                widgetService.updateAllWidgets();

                statusBarNotificationService.addOrUpdateNotification(timeRegistration);

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                removeDialog(Constants.Dialog.TIME_REGISTRATION_ACTION_LOADING);
                Log.d(getApplicationContext(), LOG_TAG, "Loading dialog removed from UI");
                if (o != null) {
                    Log.d(getApplicationContext(), LOG_TAG, "Something went wrong...");
                    Toast.makeText(TimeRegistrationActionActivity.this, R.string.err_time_registration_actions_dialog_corrupt_data, Toast.LENGTH_LONG).show();
                }

                Log.d(getApplicationContext(), LOG_TAG, "Finishing activity...");
                setResult(RESULT_OK);
                finish();
            }
        };
        threading.execute();
    }

    @Override
    protected Dialog onCreateDialog(int dialogId) {
        Dialog dialog = null;
        switch(dialogId) {
            case Constants.Dialog.TIME_REGISTRATION_ACTION: {
                Log.d(getApplicationContext(), LOG_TAG, "Building the actions dialog");
                AlertDialog.Builder actionsDialog = new AlertDialog.Builder(this);

                final Context mContext = TimeRegistrationActionActivity.this;
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                final View layout = inflater.inflate(R.layout.dialog_time_registration_actions,
                        (ViewGroup) findViewById(R.id.dialog_layout_root));

                final EditText commentEditText = (EditText) layout.findViewById(R.id.tr_comment);
                if (timeRegistration.getComment() != null) {
                    commentEditText.setText(timeRegistration.getComment());
                }

                // Attach the button
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

                // Create the spinner content
                final List<TimeRegistrationAction> actions = TimeRegistrationAction.getTimeRegistrationActions(timeRegistration);
                final Spinner actionSpinner = (Spinner) layout.findViewById(R.id.tr_action_spinner);
                TimeRegistrationActionListAdapter actionsAdapter = getFilteredActionsAdapter(actions);
                actionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                actionSpinner.setAdapter(actionsAdapter);
                // Set default value...
                if (timeRegistration.isOngoingTimeRegistration()) {
                    actionSpinner.setSelection(TimeRegistrationAction.PUNCH_OUT.getOrder());
                    if (Preferences.getEndingTimeRegistrationCommentPreference(getApplicationContext())) {
                        commentEditText.setVisibility(View.VISIBLE);
                    }
                } else {
                    actionSpinner.setSelection(TimeRegistrationAction.SPLIT.getOrder());
                }
                actionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        final TimeRegistrationAction action = TimeRegistrationAction.getByIndex(actions, position);
                        final View commentContainer = layout.findViewById(R.id.tr_comment_container);
                        final View deleteContainer = layout.findViewById(R.id.tr_delete_container);

                        commentContainer.setVisibility(View.GONE);
                        deleteContainer.setVisibility(View.GONE);

                        switch (action) {
                            case PUNCH_OUT:
                            case PUNCH_OUT_AND_START_NEXT:
                                if (Preferences.getEndingTimeRegistrationCommentPreference(getApplicationContext())) {
                                    commentContainer.setVisibility(View.VISIBLE);
                                }
                                break;
                            case SET_COMMENT:
                                commentContainer.setVisibility(View.VISIBLE);
                                break;
                            case DELETE_TIME_REGISTRATION:
                                deleteContainer.setVisibility(View.VISIBLE);
                                final TableLayout deleteRangeContainer  = (TableLayout) layout.findViewById(R.id.tr_delete_range_container);
                                deleteRangeFromButton = (Button) layout.findViewById(R.id.tr_delete_range_date_from);
                                deleteRangeToButton = (Button) layout.findViewById(R.id.tr_delete_range_date_to);

                                removeRangeMinBoundary = Calendar.getInstance();
                                removeRangeMinBoundary.setTime(new Date());
                                removeRangeMaxBoundary = Calendar.getInstance();
                                removeRangeMaxBoundary.setTime(new Date());

                                updateDateRangeSelectionButtons();

                                RadioGroup deleteRadioContainer = (RadioGroup) layout.findViewById(R.id.tr_delete_radio_container);
                                deleteRadioContainer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(RadioGroup radioGroup, int index) {
                                        int checkedId = radioGroup.getCheckedRadioButtonId();
                                        switch (checkedId) {
                                            case R.id.tr_delete_current: {
                                                deleteRangeContainer.setVisibility(View.GONE);
                                                break;
                                            }
                                            case R.id.tr_delete_range: {
                                                deleteRangeContainer.setVisibility(View.VISIBLE);

                                                deleteRangeFromButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        showDialog(Constants.Dialog.TIME_REGISTRATION_DELETE_RANGE_MIN_BOUNDARY);
                                                    }
                                                });
                                                deleteRangeToButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        showDialog(Constants.Dialog.TIME_REGISTRATION_DELETE_RANGE_MAX_BOUNDARY);
                                                    }
                                                });

                                                break;
                                            }
                                        }
                                    }
                                });
                                deleteRadioContainer.check(R.id.tr_delete_current);
                                break;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        // NA
                    }
                });

                actionsDialog.setTitle(R.string.lbl_time_registration_actions_dialog_title_choose_action);
                actionsDialog.setCancelable(true);
                actionsDialog.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeDialog(Constants.Dialog.TIME_REGISTRATION_ACTION);
                        ContextUtils.hideKeyboard(mContext, commentEditText);
                        TimeRegistrationAction action = TimeRegistrationAction.getByIndex(actions, actionSpinner.getSelectedItemPosition());
                        handleTimeRegistrationAction(action, commentEditText, (RadioGroup) layout.findViewById(R.id.tr_delete_radio_container));
                    }
                });
                actionsDialog.setNegativeButton(android.R.string.cancel, new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(getApplicationContext(), LOG_TAG, "Cancelled ending TR when about to enter comment...");
                        removeDialog(Constants.Dialog.TIME_REGISTRATION_ACTION);
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                });
                actionsDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Log.d(getApplicationContext(), LOG_TAG, "Cancelled ending TR when about to enter comment...");
                        removeDialog(Constants.Dialog.TIME_REGISTRATION_ACTION);
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                });

                actionsDialog.setView(layout);
                dialog = actionsDialog.create();

                break;
            }
            case Constants.Dialog.LOADING_TIME_REGISTRATION_CHANGE: {
                Log.d(getApplicationContext(), LOG_TAG, "Creating loading dialog for ending the active time registration");
                dialog = ProgressDialog.show(
                        TimeRegistrationActionActivity.this,
                        "",
                        getString(R.string.lbl_time_registration_actions_punching_out),
                        true,
                        false
                );
                break;
            }
            case Constants.Dialog.TIME_REGISTRATION_ACTION_LOADING: {
                Log.d(getApplicationContext(), LOG_TAG, "Creating loading dialog for executing a tr-action");
                dialog = ProgressDialog.show(
                        TimeRegistrationActionActivity.this,
                        "",
                        getString(R.string.lbl_time_registration_actions_dialog_updating_time_registration),
                        true,
                        false
                );
                break;
            }
            case Constants.Dialog.TIME_REGISTRATION_DELETE_LOADING: {
                Log.d(getApplicationContext(), LOG_TAG, "Creating loading dialog for deleting tr");
                dialog = ProgressDialog.show(
                        TimeRegistrationActionActivity.this,
                        "",
                        getString(R.string.lbl_time_registration_actions_dialog_removing_time_registration),
                        true,
                        false
                );
                break;
            }
            case Constants.Dialog.TIME_REGISTRATIONS_DELETE_LOADING: {
                Log.d(getApplicationContext(), LOG_TAG, "Creating loading dialog for deleting tr's");
                dialog = ProgressDialog.show(
                        TimeRegistrationActionActivity.this,
                        "",
                        getString(R.string.lbl_time_registration_actions_dialog_removing_time_registrations),
                        true,
                        false
                );
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
                                    setResult(RESULT_OK);
                                    finish();
									removeDialog(Constants.Dialog.ASK_FINISH_TASK);
								}
							});
				dialog = alertRemoveAllRegs.create();
                break;
            }
            case Constants.Dialog.DELETE_TIME_REGISTRATION_YES_NO: {
                AlertDialog.Builder alertRemoveReg = new AlertDialog.Builder(this);
                alertRemoveReg
                        .setMessage(R.string.msg_time_registration_actions_dialog_removing_time_registration_confirmation)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                removeDialog(Constants.Dialog.DELETE_TIME_REGISTRATION_YES_NO);
                                deleteTimeRegistration();
                                setResult(RESULT_OK);
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                removeDialog(Constants.Dialog.DELETE_TIME_REGISTRATION_YES_NO);
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
                dialog = alertRemoveReg.create();
                break;
            }
            case Constants.Dialog.DELETE_TIME_REGISTRATIONS_YES_NO: {
                AlertDialog.Builder alertRemoveReg = new AlertDialog.Builder(this);
                alertRemoveReg
                        .setMessage(R.string.msg_time_registration_actions_dialog_removing_time_registrations_confirmation)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                removeDialog(Constants.Dialog.DELETE_TIME_REGISTRATIONS_YES_NO);
                                deleteTimeRegistrations(removeRangeMinBoundary != null ? removeRangeMinBoundary.getTime() : null, removeRangeMaxBoundary != null ? removeRangeMaxBoundary.getTime() : null);
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                removeDialog(Constants.Dialog.DELETE_TIME_REGISTRATIONS_YES_NO);
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
                dialog = alertRemoveReg.create();
                break;
            }
            case Constants.Dialog.TIME_REGISTRATION_DELETE_RANGE_MIN_BOUNDARY: {
                // dialog code for min boundary
                Calendar temp = removeRangeMinBoundary;
                if (temp == null) {
                    temp = Calendar.getInstance();
                }
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        TimeRegistrationActionActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker datePickerView
                                    , int year, int monthOfYear, int dayOfMonth) {
                                if (removeRangeMinBoundary == null) {
                                    removeRangeMinBoundary = Calendar.getInstance();
                                }

                                removeRangeMinBoundary.set(Calendar.YEAR, year);
                                removeRangeMinBoundary.set(Calendar.MONTH, monthOfYear);
                                removeRangeMinBoundary.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                updateDateRangeSelectionButtons();

                                if (removeRangeMaxBoundary != null && removeRangeMinBoundary != null &&
                                        removeRangeMinBoundary.after(removeRangeMaxBoundary)) {
                                    showDialog(Constants.Dialog.TIME_REGISTRATION_DELETE_RANGE_MAX_BOUNDARY);
                                }

                                removeDialog(Constants.Dialog.TIME_REGISTRATION_DELETE_RANGE_MIN_BOUNDARY);
                            }
                        },
                        temp.get(Calendar.YEAR),
                        temp.get(Calendar.MONTH),
                        temp.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.setTitle(R.string.lbl_time_registration_actions_dialog_removing_time_registrations_range_selection_min_boundary_title);
                datePickerDialog.setButton2(getText(R.string.lbl_time_registration_actions_dialog_removing_time_registrations_range_clear_date), new DatePickerDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeRangeMinBoundary = null;
                        updateDateRangeSelectionButtons();
                        removeDialog(Constants.Dialog.TIME_REGISTRATION_DELETE_RANGE_MIN_BOUNDARY);
                    }
                });
                datePickerDialog.setButton3(getString(android.R.string.cancel), new DatePickerDialog.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeDialog(Constants.Dialog.TIME_REGISTRATION_DELETE_RANGE_MIN_BOUNDARY);
                    }
                });
                datePickerDialog.show();
                break;
            }
            case Constants.Dialog.TIME_REGISTRATION_DELETE_RANGE_MAX_BOUNDARY: {
                // dialog code for min boundary
                Calendar temp = removeRangeMaxBoundary;
                if (temp == null) {
                    temp = Calendar.getInstance();
                }
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        TimeRegistrationActionActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker datePickerView
                                    , int year, int monthOfYear, int dayOfMonth) {
                                if (removeRangeMaxBoundary == null) {
                                    removeRangeMaxBoundary = Calendar.getInstance();
                                }

                                removeRangeMaxBoundary.set(Calendar.YEAR, year);
                                removeRangeMaxBoundary.set(Calendar.MONTH, monthOfYear);
                                removeRangeMaxBoundary.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                updateDateRangeSelectionButtons();

                                if (removeRangeMaxBoundary != null && removeRangeMinBoundary != null &&
                                        removeRangeMaxBoundary.before(removeRangeMinBoundary)) {
                                    showDialog(Constants.Dialog.TIME_REGISTRATION_DELETE_RANGE_MIN_BOUNDARY);
                                }

                                removeDialog(Constants.Dialog.TIME_REGISTRATION_DELETE_RANGE_MAX_BOUNDARY);
                            }
                        },
                        temp.get(Calendar.YEAR),
                        temp.get(Calendar.MONTH),
                        temp.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.setTitle(R.string.lbl_time_registration_actions_dialog_removing_time_registrations_range_selection_max_boundary_title);
                datePickerDialog.setButton2(getText(R.string.lbl_time_registration_actions_dialog_removing_time_registrations_range_clear_date), new DatePickerDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeRangeMaxBoundary = null;
                        updateDateRangeSelectionButtons();
                        removeDialog(Constants.Dialog.TIME_REGISTRATION_DELETE_RANGE_MIN_BOUNDARY);
                    }
                });
                datePickerDialog.setButton3(getString(android.R.string.cancel), new DatePickerDialog.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeDialog(Constants.Dialog.TIME_REGISTRATION_DELETE_RANGE_MAX_BOUNDARY);
                    }
                });
                datePickerDialog.show();
                break;
            }
            case Constants.Dialog.TIME_REGISTRATION_DELETE_RANGE_BOUNDARY_PROBLEM: {
                AlertDialog.Builder alertDeleteBoundariesProblem = new AlertDialog.Builder(this);
                alertDeleteBoundariesProblem
                        .setMessage(R.string.msg_time_registration_actions_dialog_removing_time_registrations_range_boundary_problem)
                        .setCancelable(true)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                removeDialog(Constants.Dialog.TIME_REGISTRATION_DELETE_RANGE_BOUNDARY_PROBLEM);
                            }
                        });
                dialog = alertDeleteBoundariesProblem.create();
                break;
            }
        };
        return dialog;
    }

    private void updateDateRangeSelectionButtons() {
        if (deleteRangeFromButton != null && removeRangeMinBoundary != null)
            deleteRangeFromButton.setText(
                DateUtils.DateTimeConverter.convertDateToString(removeRangeMinBoundary.getTime(), DateFormat.MEDIUM, TimeRegistrationActionActivity.this)
            );
        else if (deleteRangeFromButton != null)
            deleteRangeFromButton.setText(R.string.none);

        if (deleteRangeToButton != null && removeRangeMaxBoundary != null)
            deleteRangeToButton.setText(
                DateUtils.DateTimeConverter.convertDateToString(removeRangeMaxBoundary.getTime(), DateFormat.MEDIUM, TimeRegistrationActionActivity.this)
            );
        else if (deleteRangeToButton != null)
            deleteRangeToButton.setText(R.string.none);
    }

    /**
     * Handles all the possible {@link TimeRegistrationAction}s.
     * @param action The actions to handle of type {@link TimeRegistrationAction}.
     * @param commentEditText The {@link EditText} that is used for entering a comment in certain cases.
     */
    private void handleTimeRegistrationAction(TimeRegistrationAction action, EditText commentEditText, RadioGroup deleteContainer) {
        Log.i(getApplicationContext(), LOG_TAG, "Handling Time Registration action: " + action.toString());
        switch (action) {
            case PUNCH_OUT: {
                String comment = commentEditText.getText().toString();
                if (comment != null)
                    Log.d(getApplicationContext(), LOG_TAG, "Time Registration will be saved with comment: " + comment);
                endTimeRegistration(comment, false);
                break;
            }
            case PUNCH_OUT_AND_START_NEXT: {
                String comment = commentEditText.getText().toString();
                if (comment != null)
                    Log.d(getApplicationContext(), LOG_TAG, "Time Registration will be saved with comment: " + comment);
                endTimeRegistration(comment, true);
                break;
            }
            case SPLIT: {
                Intent intent = new Intent(this, EditTimeRegistrationSplitActivity.class);
                intent.putExtra(Constants.Extras.TIME_REGISTRATION, timeRegistration);
                startActivityForResult(intent, Constants.IntentRequestCodes.TIME_REGISTRATION_EDIT_DIALOG);
                break;
            }
            case TIME_REGISTRATION_DETAILS: {
                TimeRegistration previousTimeRegistration = timeRegistrationService.getPreviousTimeRegistration(timeRegistration);
                timeRegistrationService.fullyInitialize(previousTimeRegistration);
                TimeRegistration nextTimeRegistration = timeRegistrationService.getNextTimeRegistration(timeRegistration);
                timeRegistrationService.fullyInitialize(nextTimeRegistration);

                Intent intent = new Intent(this, TimeRegistrationDetailsActivity.class);
                intent.putExtra(Constants.Extras.TIME_REGISTRATION, timeRegistration);
                intent.putExtra(Constants.Extras.TIME_REGISTRATION_PREVIOUS, previousTimeRegistration);
                intent.putExtra(Constants.Extras.TIME_REGISTRATION_NEXT, nextTimeRegistration);
                startActivityForResult(intent, Constants.IntentRequestCodes.REGISTRATION_DETAILS);
                break;
            }
            case EDIT_STARTING_TIME: {
                TimeRegistration previousTimeRegistration = timeRegistrationService.getPreviousTimeRegistration(timeRegistration);
                timeRegistrationService.fullyInitialize(previousTimeRegistration);

                Intent intent = new Intent(this, EditTimeRegistrationStartTimeActivity.class);
                intent.putExtra(Constants.Extras.TIME_REGISTRATION, timeRegistration);
                intent.putExtra(Constants.Extras.TIME_REGISTRATION_PREVIOUS, previousTimeRegistration);
                startActivityForResult(intent, Constants.IntentRequestCodes.TIME_REGISTRATION_EDIT_DIALOG);
                break;
            }
            case EDIT_END_TIME: {
                TimeRegistration nextTimeRegistration = timeRegistrationService.getNextTimeRegistration(timeRegistration);
                timeRegistrationService.fullyInitialize(nextTimeRegistration);

                Intent intent = new Intent(this, EditTimeRegistrationEndTimeActivity.class);
                intent.putExtra(Constants.Extras.TIME_REGISTRATION, timeRegistration);
                intent.putExtra(Constants.Extras.TIME_REGISTRATION_NEXT, nextTimeRegistration);
                startActivityForResult(intent, Constants.IntentRequestCodes.TIME_REGISTRATION_EDIT_DIALOG);
                break;
            }
            case RESTART_TIME_REGISTRATION: {
                restartTimeRegistration();
                break;
            }
            case EDIT_PROJECT_AND_TASK: {
                Intent intent = new Intent(this, EditTimeRegistrationProjectAndTask.class);
                intent.putExtra(Constants.Extras.TIME_REGISTRATION, timeRegistration);
                startActivityForResult(intent, Constants.IntentRequestCodes.TIME_REGISTRATION_EDIT_DIALOG);
                break;
            }
            case SET_COMMENT: {
                String comment = commentEditText.getText().toString();
                updateTimeRegistration(comment);
                break;
            }
            case DELETE_TIME_REGISTRATION: {
                int radioButtonId = deleteContainer.getCheckedRadioButtonId();

                if (radioButtonId == R.id.tr_delete_current) {
                    Log.d(getApplicationContext(), LOG_TAG, "Deleting current time registration");

                    removeDialog(Constants.Dialog.TIME_REGISTRATION_ACTION);
                    showDialog(Constants.Dialog.DELETE_TIME_REGISTRATION_YES_NO);
                } else if (radioButtonId == R.id.tr_delete_range) {
                    Log.d(getApplicationContext(), LOG_TAG, "Deleting all time registrations in range");
                    if (removeRangeMinBoundary.after(removeRangeMaxBoundary)) {
                        showDialog(Constants.Dialog.TIME_REGISTRATION_DELETE_RANGE_BOUNDARY_PROBLEM);
                    } else {
                        removeDialog(Constants.Dialog.TIME_REGISTRATION_ACTION);
                        showDialog(Constants.Dialog.DELETE_TIME_REGISTRATIONS_YES_NO);
                    }
                }
                break;
            }
        }
    }

    /**
     * Builds the list adapter based on the allowed actions.
     * @param allowedActions The allowed {@link TimeRegistrationAction}s retrieved by for a specific
     * {@link TimeRegistration} that can be retrieved using
     * {@link TimeRegistrationAction#getTimeRegistrationActions(eu.vranckaert.worktime.model.TimeRegistration)}.
     */
    private TimeRegistrationActionListAdapter getFilteredActionsAdapter(List<TimeRegistrationAction> allowedActions) {
        List<TimeRegistrationAction> allActions = Arrays.asList(TimeRegistrationAction.values());
        List<CharSequence> availableSpinnerItems = Arrays.asList(getResources().getTextArray(R.array.array_time_registration_actions_dialog_choose_action_spinner));

        List<Object> allowedElements = new ArrayList<Object>();
        for (TimeRegistrationAction action : allActions) {
            if (allowedActions.contains(action)) {
                allowedElements.add(availableSpinnerItems.get(action.getOriginalOrder()));
            }
        }

        return new TimeRegistrationActionListAdapter(TimeRegistrationActionActivity.this, allowedElements);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        setResult(resultCode);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tracker.stopSession();
    }
}
