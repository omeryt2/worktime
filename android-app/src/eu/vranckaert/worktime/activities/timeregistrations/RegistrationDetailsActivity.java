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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.google.inject.Inject;
import com.google.inject.internal.Nullable;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.constants.TextConstants;
import eu.vranckaert.worktime.constants.TrackerConstants;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.service.ProjectService;
import eu.vranckaert.worktime.service.TaskService;
import eu.vranckaert.worktime.service.TimeRegistrationService;
import eu.vranckaert.worktime.service.ui.StatusBarNotificationService;
import eu.vranckaert.worktime.service.ui.WidgetService;
import eu.vranckaert.worktime.utils.context.ContextMenuUtils;
import eu.vranckaert.worktime.utils.context.IntentUtil;
import eu.vranckaert.worktime.utils.date.DateFormat;
import eu.vranckaert.worktime.utils.date.DateUtils;
import eu.vranckaert.worktime.utils.date.TimeFormat;
import eu.vranckaert.worktime.utils.punchbar.PunchBarUtil;
import eu.vranckaert.worktime.utils.string.StringUtils;
import eu.vranckaert.worktime.utils.tracker.AnalyticsTracker;
import roboguice.activity.GuiceActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

/**
 * User: DIRK VRANCKAERT
 * Date: 27/04/11
 * Time: 15:59
 */
public class RegistrationDetailsActivity extends GuiceActivity {
    private static final String LOG_TAG = RegistrationDetailsActivity.class.getSimpleName();

    @InjectView(R.id.start)
    private TextView timeRegistrationStart;
    @InjectView(R.id.end)
    private TextView timeRegistrationEnd;
    @InjectView(R.id.duration)
    private TextView timeRegistrationDuration;
    @InjectView(R.id.comment)
    private TextView timeRegistrationComment;
    @InjectView(R.id.comment_label)
    private TextView timeRegistrationCommentLabel;
    @InjectView(R.id.project)
    private TextView timeRegistrationProject;
    @InjectView(R.id.task)
    private TextView timeRegistrationTask;

    @InjectExtra(Constants.Extras.TIME_REGISTRATION)
    private TimeRegistration registration;

    @InjectExtra(Constants.Extras.TIME_REGISTRATION_PREVIOUS)
    @Nullable
    private TimeRegistration previousRegistration;

    @InjectExtra(Constants.Extras.TIME_REGISTRATION_NEXT)
    @Nullable
    private TimeRegistration nextRegistration;

    @Inject
    private TimeRegistrationService timeRegistrationService;

    @Inject
    private WidgetService widgetService;

    @Inject
    private StatusBarNotificationService statusBarNotificationService;

    @Inject
    private TaskService taskService;

    @Inject
    private ProjectService projectService;

    private boolean isUpdated = false;
    private boolean isSplit = false;
    private boolean initialLoad = true;

    private AnalyticsTracker tracker;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registration_details);

        tracker = AnalyticsTracker.getInstance(getApplicationContext());
        tracker.trackPageView(TrackerConstants.PageView.REGISTRATIONS_DETAILS_ACTIVITY);

        updateView();
    }

    /**
     * Updates all of the layout fields...
     */
    private void updateView() {
        timeRegistrationStart.setText(
                TextConstants.SPACE +
                DateUtils.DateTimeConverter.convertDateTimeToString(
                        registration.getStartTime(),
                        DateFormat.MEDIUM,
                        TimeFormat.MEDIUM,
                        getApplicationContext()
                )
        );
        timeRegistrationDuration.setText(
                TextConstants.SPACE +
                DateUtils.TimeCalculator.calculatePeriod(getApplicationContext(), registration)
        );
        timeRegistrationProject.setText(TextConstants.SPACE + registration.getTask().getProject().getName());
        timeRegistrationTask.setText(TextConstants.SPACE + registration.getTask().getName());

        if (registration.isOngoingTimeRegistration()) {
            timeRegistrationEnd.setText(TextConstants.SPACE + getString(R.string.now));
        } else {
            timeRegistrationEnd.setVisibility(View.VISIBLE);

            timeRegistrationEnd.setText(
                    TextConstants.SPACE +
                    DateUtils.DateTimeConverter.convertDateTimeToString(
                            registration.getEndTime(),
                            DateFormat.MEDIUM,
                            TimeFormat.MEDIUM,
                            getApplicationContext()
                    )
            );
        }

        if (StringUtils.isNotBlank(registration.getComment())) {
            timeRegistrationCommentLabel.setVisibility(View.VISIBLE);
            timeRegistrationComment.setVisibility(View.VISIBLE);
            timeRegistrationComment.setText(registration.getComment());
        } else {
            timeRegistrationCommentLabel.setVisibility(View.GONE);
            timeRegistrationComment.setVisibility(View.GONE);
        }
    }

    /**
     * Navigate home.
     * @param view The view.
     */
    public void onHomeClick(View view) {
        IntentUtil.goHome(this);
    }

    /**
     * Navigate home.
     * @param view The view.
     */
    public void onEditClick(View view) {
        registerForContextMenu(view);
        view.showContextMenu();
    }

    /**
     * Navigate home.
     * @param view The view.
     */
    public void onDeleteClick(View view) {
        deleteTimeRegistration(registration, true);
    }

    public void onPunchButtonClick(View view) {
        PunchBarUtil.onPunchButtonClick(RegistrationDetailsActivity.this, timeRegistrationService);
    }

    /**
     * Delete the instance of this {@link TimeRegistration}.
     * @param timeRegistration The time registration to delete.
     * @param askPermission Indicates if the user-permission for the deletion should be asked!
     */
    private void deleteTimeRegistration(final TimeRegistration timeRegistration, boolean askPermission) {
        if(askPermission) {
            showDialog(Constants.Dialog.DELETE_TIME_REGISTRATION_YES_NO);
            return;
        }

        timeRegistrationService.remove(timeRegistration);
        widgetService.updateWidget();

        if (timeRegistration.isOngoingTimeRegistration()) {
            statusBarNotificationService.removeOngoingTimeRegistrationNotification();
        }
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
            case Constants.Dialog.DELETE_TIME_REGISTRATION_YES_NO: {
                AlertDialog.Builder alertRemoveReg = new AlertDialog.Builder(this);
				alertRemoveReg
						   .setMessage(R.string.msg_delete_registration_confirmation)
						   .setCancelable(false)
						   .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int which) {
                                   deleteTimeRegistration(registration, false);
                                   dialog.cancel();
                               }
                           })
						   .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int which) {
                                   dialog.cancel();
                               }
                           });
				dialog = alertRemoveReg.create();
                break;
            }
        }
        return dialog;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        Log.d(LOG_TAG, "In method onCreateContextMenu(...)");
        super.onCreateContextMenu(menu, v, menuInfo);
        ContextMenuUtils.createTimeRegistrationEditContextMenu(
                getApplicationContext(),
                registration,
                menu,
                true
        );
        TimeRegistration latestRegistration = timeRegistrationService.getLatestTimeRegistration();
        if (latestRegistration != null) {
            Log.d(LOG_TAG, "Latest time registration id: " + latestRegistration.getId());
            Log.d(LOG_TAG, "Current viewing time registration id: " + registration.getId());
            if (registration.isOngoingTimeRegistration() || !registration.getId().equals(latestRegistration.getId())) {
                // Only remove this menu-option if the viewing registration is not the last one!
                menu.removeItem(Constants.ContentMenuItemIds.TIME_REGISTRATION_RESTART);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return ContextMenuUtils.handleTimeRegistrationEditContextMenuSelection(
                RegistrationDetailsActivity.this,
                item,
                registration,
                previousRegistration,
                nextRegistration,
                tracker
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.IntentRequestCodes.REGISTRATION_EDIT_DIALOG: {
                if (resultCode == RESULT_OK) {
                    Log.d(LOG_TAG, "The time registration has been updated!");
                    isUpdated = true;
                    registration = timeRegistrationService.get(registration.getId());
                    taskService.refresh(registration.getTask());
                    projectService.refresh(registration.getTask().getProject());
                    updateView();
                }
                break;
            }
            case Constants.IntentRequestCodes.REGISTRATION_SPLIT_DIALOG: {
                if (resultCode == RESULT_OK) {
                    Log.d(LOG_TAG, "The time registration has been split!");
                    isUpdated = true;
                    isSplit = true;
                    registration = timeRegistrationService.get(registration.getId());
                    taskService.refresh(registration.getTask());
                    projectService.refresh(registration.getTask().getProject());
                    updateView();
                }
                break;
            }
            case Constants.IntentRequestCodes.PUNCH_BAR_START_TIME_REGISTRATION: {
                PunchBarUtil.configurePunchBar(RegistrationDetailsActivity.this, timeRegistrationService, taskService, projectService);
                break;
            }
            case Constants.IntentRequestCodes.PUNCH_BAR_END_TIME_REGISTRATION: {
                PunchBarUtil.configurePunchBar(RegistrationDetailsActivity.this, timeRegistrationService, taskService, projectService);
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        PunchBarUtil.configurePunchBar(RegistrationDetailsActivity.this, timeRegistrationService, taskService, projectService);

        if (initialLoad) {
            initialLoad = false;
            return;
        }

        registration = timeRegistrationService.get(registration.getId());
        taskService.refresh(registration.getTask());
        projectService.refresh(registration.getTask().getProject());
        if (!registration.isOngoingTimeRegistration()) {
            nextRegistration = timeRegistrationService.getNextTimeRegistration(registration);
        }

        updateView();
    }

    @Override
    public void finish() {
        if (isUpdated && isSplit) {
            setResult(Constants.IntentResultCodes.RESULT_OK_SPLIT);
        } else if (isUpdated) {
            setResult(RESULT_OK);
        }
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tracker.stopSession();
    }
}