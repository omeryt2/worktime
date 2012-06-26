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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.Toast;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.activities.timeregistrations.listadapter.TimeRegistrationActionListAdapter;
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
import eu.vranckaert.worktime.utils.preferences.Preferences;
import eu.vranckaert.worktime.utils.string.StringUtils;
import eu.vranckaert.worktime.utils.tracker.AnalyticsTracker;

import java.util.ArrayList;
import java.util.Arrays;
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

    /**
     * Google Analytics Tracker
     */
    private AnalyticsTracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadExtras();
        loadServices();

        tracker = AnalyticsTracker.getInstance(getApplicationContext());
        Log.d(LOG_TAG, "Started the TimeRegistration Action activity");

        if (timeRegistration == null) {
            Log.e(LOG_TAG, "The time registration should never be null in the TimeRegistrationActionActivity!");
            throw new RuntimeException("The time registration should never be null in the TimeRegistrationActionActivity!");
        } else {
            timeRegistrationService.fullyInitialize(timeRegistration);
            Log.d(LOG_TAG, "Launching action-activity with timeRegistration " + timeRegistration);
        }

        showDialog(Constants.Dialog.TIME_REGISTRATION_ACTION);
    }

    /**
     * Get all the extras...
     */
    private void loadExtras() {
        timeRegistration = (TimeRegistration) getIntent().getExtras().get(Constants.Extras.TIME_REGISTRATION);
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
     */
    private void endTimeRegistration(final String comment) {
        AsyncTask threading = new AsyncTask() {

            @Override
            protected void onPreExecute() {
                showDialog(Constants.Dialog.LOADING_TIME_REGISTRATION_CHANGE);
            }

            @Override
            protected Object doInBackground(Object... objects) {
                Log.d(LOG_TAG, "Is there already a looper? " + (Looper.myLooper() != null));
                if(Looper.myLooper() == null) {
                    Looper.prepare();
                }

                Date endTime = new Date();

                if (timeRegistration.getEndTime() != null) {
                    Log.w(LOG_TAG, "Data must be corrupt, time registration is already ended! Please clear all the data through the system settings of the application!");
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

                    widgetService.updateWidget();

                    /*
                    * Creates a new backup to be sure that the data is always secure!
                    */
                    backupService.requestBackup(TimeRegistrationActionActivity.this);
                }

                if (StringUtils.isNotBlank(comment)) {
                    commentHistoryService.updateLastComment(comment);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                removeDialog(Constants.Dialog.LOADING_TIME_REGISTRATION_CHANGE);
                Log.d(LOG_TAG, "Loading dialog removed from UI");
                if (o != null) {
                    Log.d(LOG_TAG, "Something went wrong, the data is corrupt");
                    Toast.makeText(TimeRegistrationActionActivity.this, R.string.err_time_registration_actions_dialog_corrupt_data, Toast.LENGTH_LONG).show();
                } else if (o == null) {
                    Log.d(LOG_TAG, "Successfully ended time registration");
                    Toast.makeText(TimeRegistrationActionActivity.this, R.string.msg_widget_time_reg_ended, Toast.LENGTH_LONG).show();
                }
                Log.d(LOG_TAG, "Finishing activity...");

                boolean askFinishTask = Preferences.getWidgetEndingTimeRegistrationFinishTaskPreference(getApplicationContext());
                if (!askFinishTask) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    showDialog(Constants.Dialog.ASK_FINISH_TASK);
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
                Log.d(LOG_TAG, "Is there already a looper? " + (Looper.myLooper() != null));
                if(Looper.myLooper() == null) {
                    Looper.prepare();
                }

                Date endTime = new Date();

                timeRegistrationService.remove(timeRegistration);

                widgetService.updateWidget();

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
                Log.d(LOG_TAG, "Loading dialog removed from UI");
                if (o != null) {
                    Log.d(LOG_TAG, "Something went wrong...");
                    Toast.makeText(TimeRegistrationActionActivity.this, R.string.err_time_registration_actions_dialog_corrupt_data, Toast.LENGTH_LONG).show();
                }

                Log.d(LOG_TAG, "Finishing activity...");
                setResult(RESULT_OK);
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
                Log.d(LOG_TAG, "Is there already a looper? " + (Looper.myLooper() != null));
                if(Looper.myLooper() == null) {
                    Looper.prepare();
                }

                timeRegistration.setComment(comment);
                tracker.trackEvent(
                        TrackerConstants.EventSources.TIME_REGISTRATION_ACTION_ACTIVITY,
                        TrackerConstants.EventActions.ADD_TR_COMMENT
                );
                timeRegistrationService.update(timeRegistration);
                widgetService.updateWidget();
                backupService.requestBackup(TimeRegistrationActionActivity.this);

                if (StringUtils.isNotBlank(comment)) {
                    commentHistoryService.updateLastComment(comment);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                removeDialog(Constants.Dialog.TIME_REGISTRATION_ACTION_LOADING);
                Log.d(LOG_TAG, "Loading dialog removed from UI");
                Log.d(LOG_TAG, "Finishing activity...");
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
                Log.d(LOG_TAG, "Is there already a looper? " + (Looper.myLooper() != null));
                if(Looper.myLooper() == null) {
                    Looper.prepare();
                }

                timeRegistration.setEndTime(null);
                timeRegistrationService.update(timeRegistration);

                widgetService.updateWidget();

                statusBarNotificationService.addOrUpdateNotification(timeRegistration);

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                removeDialog(Constants.Dialog.TIME_REGISTRATION_ACTION_LOADING);
                Log.d(LOG_TAG, "Loading dialog removed from UI");
                if (o != null) {
                    Log.d(LOG_TAG, "Something went wrong...");
                    Toast.makeText(TimeRegistrationActionActivity.this, R.string.err_time_registration_actions_dialog_corrupt_data, Toast.LENGTH_LONG).show();
                }

                Log.d(LOG_TAG, "Finishing activity...");
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
                Log.d(LOG_TAG, "Building the actions dialog");
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
                                final Button deleteRangeFromButton = (Button) layout.findViewById(R.id.tr_delete_range_date_from);
                                final Button deleteRangeToButton = (Button) layout.findViewById(R.id.tr_delete_range_date_to);

                                RadioGroup deleteRadioContainer = (RadioGroup) layout.findViewById(R.id.tr_delete_radio_container);
                                deleteRadioContainer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(RadioGroup radioGroup, int index) {
                                        int checkedId = radioGroup.getCheckedRadioButtonId();
                                        switch (checkedId) {
                                            case R.id.tr_delete_current: {
                                                deleteRangeContainer.setEnabled(false);
                                                deleteRangeFromButton.setEnabled(false);
                                                deleteRangeToButton.setEnabled(false);
                                                break;
                                            }
                                            case R.id.tr_delete_range: {
                                                deleteRangeContainer.setEnabled(true);
                                                deleteRangeFromButton.setEnabled(true);
                                                deleteRangeToButton.setEnabled(true);

                                                // TODO enable date selection for buttons

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
                        Log.d(LOG_TAG, "Cancelled ending TR when about to enter comment...");
                        removeDialog(Constants.Dialog.TIME_REGISTRATION_ACTION);
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                });
                actionsDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Log.d(LOG_TAG, "Cancelled ending TR when about to enter comment...");
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
                Log.d(LOG_TAG, "Creating loading dialog for ending the active time registration");
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
                Log.d(LOG_TAG, "Creating loading dialog for executing a tr-action");
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
                Log.d(LOG_TAG, "Creating loading dialog for deleting tr");
                dialog = ProgressDialog.show(
                        TimeRegistrationActionActivity.this,
                        "",
                        getString(R.string.lbl_time_registration_actions_dialog_removing_time_registration),
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
        };
        return dialog;
    }

    /**
     * Handles all the possible {@link TimeRegistrationAction}s.
     * @param action The actions to handle of type {@link TimeRegistrationAction}.
     * @param commentEditText The {@link EditText} that is used for entering a comment in certain cases.
     */
    private void handleTimeRegistrationAction(TimeRegistrationAction action, EditText commentEditText, RadioGroup deleteContainer) {
        Log.i(LOG_TAG, "Handling Time Registration action: " + action.toString());
        switch (action) {
            case PUNCH_OUT: {
                String comment = commentEditText.getText().toString();
                if (comment != null)
                    Log.d(LOG_TAG, "Time Registration will be saved with comment: " + comment);
                endTimeRegistration(comment);
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
                    Log.d(LOG_TAG, "Deleting current time registration");

                    removeDialog(Constants.Dialog.TIME_REGISTRATION_ACTION);
                    showDialog(Constants.Dialog.DELETE_TIME_REGISTRATION_YES_NO);
                } else if (radioButtonId == R.id.tr_delete_range) {
                    Log.d(LOG_TAG, "Deleting all time registrations in range");
                    // TODO
                    finish();
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
