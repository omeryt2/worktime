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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.google.inject.Inject;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.activities.reporting.ReportingCriteriaActivity;
import eu.vranckaert.worktime.activities.timeregistrations.listadapter.TimRegistrationsListAdapter;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.constants.TrackerConstants;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.service.ProjectService;
import eu.vranckaert.worktime.service.TaskService;
import eu.vranckaert.worktime.service.TimeRegistrationService;
import eu.vranckaert.worktime.service.ui.StatusBarNotificationService;
import eu.vranckaert.worktime.service.ui.WidgetService;
import eu.vranckaert.worktime.utils.context.ContextMenuUtils;
import eu.vranckaert.worktime.utils.context.IntentUtil;
import eu.vranckaert.worktime.utils.punchbar.PunchBarUtil;
import eu.vranckaert.worktime.utils.tracker.AnalyticsTracker;
import roboguice.activity.GuiceListActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 05/02/11
 * Time: 18:58
 */
public class TimeRegistrationsActivity extends GuiceListActivity {
    private static final String LOG_TAG = TimeRegistrationsActivity.class.getSimpleName();

    @Inject
    private TimeRegistrationService timeRegistrationService;
    @Inject
    private TaskService taskService;
    @Inject
    private ProjectService projectService;
    @Inject
    private WidgetService widgetService;
    @Inject
    private StatusBarNotificationService statusBarNotificationService;

    List<TimeRegistration> timeRegistrations;
    //Vars for deleting time registrations
    TimeRegistration timeRegistrationToDelete = null;

    private AnalyticsTracker tracker;

    private Long initialRecordCount = 0L;
    private int currentLowerLimit = 0;
    private final int maxRecordsToLoad = 10;
    public TimeRegistration loadExtraTimeRegistration = null;
    private boolean initialLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrations);
        tracker = AnalyticsTracker.getInstance(getApplicationContext());
        tracker.trackPageView(TrackerConstants.PageView.TIME_REGISTRATIONS_ACTIVITY);

        loadExtraTimeRegistration = new TimeRegistration();
        loadExtraTimeRegistration.setId(-1);

        loadTimeRegistrations(true, true);

        getListView().setOnItemClickListener(new ListView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOG_TAG, "Clicked on TR-item " + position);
                TimeRegistration selectedRegistration = timeRegistrations.get(position);

                if (selectedRegistration.getId() == loadExtraTimeRegistration.getId()) {
                    loadExtraTimeRegistrations(view.findViewById(R.id.progress_timeregistration_load_more));
                    return;
                }

                TimeRegistration previousTimeRegistration = null;
                if (getTimeRegistrationsSize() > position + 1) {
                    previousTimeRegistration = timeRegistrations.get(position + 1);
                }

                TimeRegistration nextTimeRegistration = null;
                if (position > 0) {
                    nextTimeRegistration = timeRegistrations.get(position - 1);
                }
                IntentUtil.openRegistrationDetailActivity(TimeRegistrationsActivity.this, selectedRegistration,
                        previousTimeRegistration, nextTimeRegistration);
            }
        });

        registerForContextMenu(getListView());
    }

    /**
     * Load time registrations.
     * @param dbReload Reload the time registrations from the database if set to {@link Boolean#TRUE}. Otherwise only
     * reset the adapter.
     * @param startFresh This means that you will start from the first page again if set to {@link Boolean#TRUE}. If
     * set to {@link Boolean#FALSE} the same amount of time registrations will be reloaded as that are currently loaded.
     */
    private void loadTimeRegistrations(boolean dbReload, boolean startFresh) {
        Long recordCount = timeRegistrationService.count();
        if (!dbReload && initialRecordCount != recordCount) {
            dbReload = true;
        }
        if (startFresh && !dbReload) {
            dbReload = true;
        }

        if (dbReload) {
            initialRecordCount = recordCount;
            Log.d(LOG_TAG, "totoal count of timeregistrations is " + initialRecordCount);
            currentLowerLimit = 0;
            if (startFresh) {
                //(Re)Load the time registrations for the 'page'
                this.timeRegistrations = timeRegistrationService.findAll(currentLowerLimit, maxRecordsToLoad);
            } else {
                //(Re)Load all time registrations that were loaded before (same range)
                int maxRecords = getTimeRegistrationsSize();
                this.timeRegistrations = timeRegistrationService.findAll(currentLowerLimit, maxRecords);
            }

            if (initialRecordCount > getTimeRegistrationsSize()) {
                timeRegistrations.add(loadExtraTimeRegistration);
            }

            Log.d(LOG_TAG, getTimeRegistrationsSize() + " timeregistrations loaded!");
        }

        refillListView(timeRegistrations);
    }

    /**
     * Load extra time registrations and add them to the list.
     * @param progressBar The progress bar.
     */
    private void loadExtraTimeRegistrations(final View progressBar) {
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected Object doInBackground(Object... objects) {
                Long recordCount = timeRegistrationService.count();
                if (!initialRecordCount.equals(recordCount)) {
                    return null;
                }

                currentLowerLimit = currentLowerLimit + maxRecordsToLoad;
                List<TimeRegistration> extraTimeRegistrations = timeRegistrationService.findAll(currentLowerLimit, maxRecordsToLoad);
                Log.d(LOG_TAG, "Loaded " + extraTimeRegistrations.size() + " extra time registrations");

                timeRegistrations.remove(loadExtraTimeRegistration);
                for (TimeRegistration timeRegistration : extraTimeRegistrations) {
                    timeRegistrations.add(timeRegistration);
                }

                Log.d(LOG_TAG, "Total time registrations loaded now: " + getTimeRegistrationsSize());

                if (initialRecordCount > getTimeRegistrationsSize()) {
                    Log.d(LOG_TAG, "We need an extra item in the list to load more time registrations!");
                    timeRegistrations.add(loadExtraTimeRegistration);
                }
                return timeRegistrations;
            }

            @Override
            protected void onPostExecute(Object object) {
                progressBar.setVisibility(View.INVISIBLE);
                if (object == null) {
                    Log.w(LOG_TAG, "Loading extra items failed, reloading entire list!");
                    loadTimeRegistrations(true, false);
                    return;
                }
                Log.d(LOG_TAG, "Applying the changes...");
                refillListView((List<TimeRegistration>) object);
            }
        };
        asyncTask.execute();
    }

    private void refillListView(List<TimeRegistration> timeRegistrations) {
        List<TimeRegistration> listOfNewTimeRegistrations = new ArrayList<TimeRegistration>();
        listOfNewTimeRegistrations.addAll(timeRegistrations);

        if (getListView().getAdapter() == null) {
            TimRegistrationsListAdapter adapter = new TimRegistrationsListAdapter(TimeRegistrationsActivity.this, listOfNewTimeRegistrations);
            setListAdapter(adapter);
        } else {
            ((TimRegistrationsListAdapter) getListView().getAdapter()).refill(listOfNewTimeRegistrations);
        }
    }

    /**
     * Go Home.
     * @param view The view.
     */
    public void onHomeClick(View view) {
        IntentUtil.goHome(this);
    }

    /**
     * Add a time registration.
     * @param view The view.
     */
    public void onAddClick(View view) {
        //Not yet implemented
    }

    /**
     * Disk the time registrations.
     * @param view The view.
     */
    public void onExportClick(View view) {
        Intent intent = new Intent(TimeRegistrationsActivity.this, ReportingCriteriaActivity.class);
        startActivity(intent);
    }

    public void onPunchButtonClick(View view) {
        PunchBarUtil.onPunchButtonClick(TimeRegistrationsActivity.this, timeRegistrationService);
    }

    public int getTimeRegistrationsSize() {
        int size = timeRegistrations.size();

        //Check if latest time registration is a dummy time registration, if so the size of loaded registrations is size - 1
        if (timeRegistrations.size() > 0 && timeRegistrations.get(timeRegistrations.size()-1).getId() != null
                && timeRegistrations.get(timeRegistrations.size()-1).getId().equals(loadExtraTimeRegistration.getId())) {
            size--;
        }

        return size;
    }

    private void deleteTimeRegistration(final TimeRegistration timeRegistration, boolean askPermission) {
        if(askPermission) {
            timeRegistrationToDelete = timeRegistration;
            showDialog(Constants.Dialog.DELETE_TIME_REGISTRATION_YES_NO);
            return;
        }

        timeRegistrationService.remove(timeRegistration);
        timeRegistrations.remove(timeRegistration);
        initialRecordCount--;
        currentLowerLimit--;

        tracker.trackEvent(
                TrackerConstants.EventSources.TIME_REGISTRATIONS_ACTIVITY,
                TrackerConstants.EventActions.DELETE_TIME_REGISTRATION
        );

        timeRegistrationToDelete = null;
        widgetService.updateWidget();
        if (timeRegistration.isOngoingTimeRegistration()) {
            statusBarNotificationService.removeOngoingTimeRegistrationNotification();
        }
        loadTimeRegistrations(false, false);

        PunchBarUtil.configurePunchBar(TimeRegistrationsActivity.this, timeRegistrationService, taskService, projectService);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
            case Constants.Dialog.DELETE_TIME_REGISTRATION_YES_NO: {
                AlertDialog.Builder alertRemoveAllRegs = new AlertDialog.Builder(this);
				alertRemoveAllRegs
						   .setMessage(R.string.msg_delete_registration_confirmation)
						   .setCancelable(false)
						   .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int which) {
                                   deleteTimeRegistration(timeRegistrationToDelete, false);
                                   dialog.cancel();
                               }
                           })
						   .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int which) {
                                   timeRegistrationToDelete = null;
                                   dialog.cancel();
                               }
                           });
				dialog = alertRemoveAllRegs.create();
                break;
            }
        }
        return dialog;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.IntentRequestCodes.REGISTRATION_DETAILS : {
                if (resultCode == RESULT_OK) {
                    Log.d(LOG_TAG, "A TR has been updated on the registrations details view, it's necessary to reload the list of time registrations upon return!");
                    loadTimeRegistrations(true, false);
                } else if (resultCode == Constants.IntentResultCodes.RESULT_OK_SPLIT) {
                    Log.d(LOG_TAG, "A TR has been split on the registrations details view, it's necessary to reload the list of time registrations upon return!");
                    timeRegistrations.add(0, new TimeRegistration()); //Forces when reloading to load one extra record!
                    loadTimeRegistrations(true, false);
                }
                break;
            }
            case Constants.IntentRequestCodes.REGISTRATION_EDIT_DIALOG: {
                if (resultCode == RESULT_OK) {
                    Log.d(LOG_TAG, "The time registration has been updated!");
                    loadTimeRegistrations(true, false);
                }
                break;
            }
            case Constants.IntentRequestCodes.REGISTRATION_SPLIT_DIALOG: {
                if (resultCode == RESULT_OK) {
                    Log.d(LOG_TAG, "The time registration has been split!");
                    timeRegistrations.add(0, new TimeRegistration()); //Forces when reloading to load one extra record!
                    loadTimeRegistrations(true, false);
                }
                break;
            }
            case Constants.IntentRequestCodes.PUNCH_BAR_START_TIME_REGISTRATION: {
                PunchBarUtil.configurePunchBar(TimeRegistrationsActivity.this, timeRegistrationService, taskService, projectService);
                break;
            }
            case Constants.IntentRequestCodes.PUNCH_BAR_END_TIME_REGISTRATION: {
                PunchBarUtil.configurePunchBar(TimeRegistrationsActivity.this, timeRegistrationService, taskService, projectService);
                break;
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        Log.d(LOG_TAG, "In method onCreateContextMenu(...)");
        if (v.getId() == android.R.id.list) {
            super.onCreateContextMenu(menu, v, menuInfo);
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            TimeRegistration timeRegistration = timeRegistrations.get(info.position);
            ContextMenuUtils.createTimeRegistrationEditContextMenu(
                    getApplicationContext(),
                    timeRegistration,
                    menu,
                    false
            );

            if (info.position > 0 || timeRegistration.isOngoingTimeRegistration()) {
                menu.removeItem(Constants.ContentMenuItemIds.TIME_REGISTRATION_RESTART);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int element = info.position;
        TimeRegistration timeRegistrationForContext = timeRegistrations.get(element);

        TimeRegistration previousTimeRegistration = null;
        if (getTimeRegistrationsSize() > element + 1) {
            previousTimeRegistration = timeRegistrations.get(element + 1);
        } else if (initialRecordCount > timeRegistrations.size()) {
            Log.d(LOG_TAG, "The previous time registration is not yet loaded, loading it now");
            previousTimeRegistration = timeRegistrationService.getPreviousTimeRegistration(timeRegistrationForContext);
        }

        TimeRegistration nextTimeRegistration = null;
        if (element > 0) {
            nextTimeRegistration = timeRegistrations.get(element - 1);
        }

        if (item.getItemId() == Constants.ContentMenuItemIds.TIME_REGISTRATION_DELETE) {
            deleteTimeRegistration(timeRegistrationForContext, true);
            return true;
        }

        return ContextMenuUtils.handleTimeRegistrationEditContextMenuSelection(
                TimeRegistrationsActivity.this,
                item,
                timeRegistrationForContext,
                previousTimeRegistration,
                nextTimeRegistration,
                tracker
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        PunchBarUtil.configurePunchBar(TimeRegistrationsActivity.this, timeRegistrationService, taskService, projectService);
        
        if (initialLoad) {
            initialLoad = false;
            return;
        }
        
        Long recordCount = timeRegistrationService.count();
        int recordCountDiff = recordCount.intValue() - initialRecordCount.intValue();
        
        if (recordCountDiff > 0) {
            for (int i=0; i<recordCountDiff; i++) {
                timeRegistrations.add(0, new TimeRegistration());
            }
        }

        loadTimeRegistrations(true, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tracker.stopSession();
    }
}
