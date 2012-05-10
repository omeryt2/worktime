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

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.google.inject.Inject;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.activities.reporting.ReportingCriteriaActivity;
import eu.vranckaert.worktime.activities.timeregistrations.listadapter.TimeRegistrationsListAdapter;
import eu.vranckaert.worktime.activities.widget.TimeRegistrationActionActivity;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.constants.TrackerConstants;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.service.ProjectService;
import eu.vranckaert.worktime.service.TaskService;
import eu.vranckaert.worktime.service.TimeRegistrationService;
import eu.vranckaert.worktime.service.ui.StatusBarNotificationService;
import eu.vranckaert.worktime.service.ui.WidgetService;
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

                Intent intent = new Intent(TimeRegistrationsActivity.this, RegistrationDetailsActivity.class);
                intent.putExtra(Constants.Extras.TIME_REGISTRATION, selectedRegistration);
                intent.putExtra(Constants.Extras.TIME_REGISTRATION_PREVIOUS, previousTimeRegistration);
                intent.putExtra(Constants.Extras.TIME_REGISTRATION_NEXT, nextTimeRegistration);
                startActivityForResult(intent, Constants.IntentRequestCodes.REGISTRATION_DETAILS);
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
            TimeRegistrationsListAdapter adapter = new TimeRegistrationsListAdapter(TimeRegistrationsActivity.this, listOfNewTimeRegistrations);
            setListAdapter(adapter);
        } else {
            ((TimeRegistrationsListAdapter) getListView().getAdapter()).refill(listOfNewTimeRegistrations);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.IntentRequestCodes.TIME_REGISTRATION_ACTION : {
                if (resultCode == RESULT_OK) {
                    Log.d(LOG_TAG, "The time registration has been updated");
                    loadTimeRegistrations(true, false);
                } else if (resultCode == Constants.IntentResultCodes.RESULT_OK_SPLIT) {
                    Log.d(LOG_TAG, "The time registration has been split");
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

    // TODO add only one option to the context menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.time_registrations_list_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int element = info.position;
        TimeRegistration timeRegistration = timeRegistrations.get(element);

        switch (item.getItemId()) {
            case R.id.registrations_edit:
                Intent intent = new Intent(TimeRegistrationsActivity.this, TimeRegistrationActionActivity.class);
                intent.putExtra(Constants.Extras.TIME_REGISTRATION, timeRegistration);
                startActivityForResult(intent, Constants.IntentRequestCodes.TIME_REGISTRATION_ACTION);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
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
