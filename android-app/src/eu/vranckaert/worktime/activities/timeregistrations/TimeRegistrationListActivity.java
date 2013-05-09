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

package eu.vranckaert.worktime.activities.timeregistrations;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.github.espiandev.showcaseview.ShowcaseView;
import com.google.inject.Inject;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.activities.about.AboutActivity;
import eu.vranckaert.worktime.activities.account.AccountLoginActivity;
import eu.vranckaert.worktime.activities.preferences.PreferencesActivity;
import eu.vranckaert.worktime.activities.projects.ManageProjectsActivity;
import eu.vranckaert.worktime.activities.reporting.ReportingCriteriaActivity;
import eu.vranckaert.worktime.activities.timeregistrations.listadapter.SlideInMenuAdapter;
import eu.vranckaert.worktime.activities.timeregistrations.listadapter.TimeRegistrationsListAdapter;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.constants.TrackerConstants;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.service.ProjectService;
import eu.vranckaert.worktime.service.TaskService;
import eu.vranckaert.worktime.service.TimeRegistrationService;
import eu.vranckaert.worktime.service.ui.StatusBarNotificationService;
import eu.vranckaert.worktime.service.ui.WidgetService;
import eu.vranckaert.worktime.utils.context.AsyncHelper;
import eu.vranckaert.worktime.utils.context.ContextUtils;
import eu.vranckaert.worktime.utils.context.Log;
import eu.vranckaert.worktime.utils.preferences.Preferences;
import eu.vranckaert.worktime.utils.punchbar.PunchBarUtil;
import eu.vranckaert.worktime.utils.tracker.AnalyticsTracker;
import eu.vranckaert.worktime.utils.view.actionbar.synclock.SyncLockedListActivity;
import eu.vranckaert.worktime.utils.view.showcase.ShowcaseViewElement;
import eu.vranckaert.worktime.utils.view.showcase.ShowcaseViewUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 05/02/11
 * Time: 18:58
 */
public class TimeRegistrationListActivity extends SyncLockedListActivity implements ShowcaseViewUtility.OnShowcaseEndedListener {
    private static final String LOG_TAG = TimeRegistrationListActivity.class.getSimpleName();

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

    private AnalyticsTracker tracker;

    private SlidingMenu menu;
    private Long initialRecordCount = 0L;
    private int currentLowerLimit = 0;
    private final int maxRecordsToLoad = 10;
    public TimeRegistration loadExtraTimeRegistration = null;
    private boolean initialLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrations);

        setTitle(R.string.lbl_registrations_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Setup slide-in menu
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setBehindWidthRes(R.dimen.slidein_width);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        menu.setMenu(R.layout.activity_time_registration_list_slidein_menu);

        // Construct list of slide-in menu
        ListView listView = (ListView) findViewById(R.id.activity_time_registration_list_slidein_menu);
        List<SlideInMenuAdapter.SlideInMenuItem> menuItems = new ArrayList<SlideInMenuAdapter.SlideInMenuItem>();
        menuItems.add(new SlideInMenuAdapter.SlideInMenuItem(getApplicationContext(), ManageProjectsActivity.class, R.string.home_btn_projects, R.drawable.ic_collections_collection_dark));
        menuItems.add(new SlideInMenuAdapter.SlideInMenuItem(getApplicationContext(), AccountLoginActivity.class, R.string.home_ab_menu_account, R.drawable.ic_social_person_dark));
        menuItems.add(new SlideInMenuAdapter.SlideInMenuItem(getApplicationContext(), ReportingCriteriaActivity.class, R.string.home_btn_reporting, R.drawable.ic_collections_view_as_list_dark));
        menuItems.add(new SlideInMenuAdapter.SlideInMenuItem(getApplicationContext(), PreferencesActivity.class, R.string.home_btn_preferences, R.drawable.ic_action_settings_dark));
        menuItems.add(new SlideInMenuAdapter.SlideInMenuItem(getApplicationContext(), AboutActivity.class, R.string.home_ab_menu_about, R.drawable.ic_action_about_dark));
        SlideInMenuAdapter menuAdapter = new SlideInMenuAdapter(TimeRegistrationListActivity.this, menuItems);
        listView.setAdapter(menuAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SlideInMenuAdapter.SlideInMenuItem menuItem = (SlideInMenuAdapter.SlideInMenuItem) parent.getItemAtPosition(position);
                if (menuItem.getRequestCode() == -1) {
                    startActivity(menuItem.getIntent());
                } else {
                    startActivityForResult(menuItem.getIntent(), menuItem.getRequestCode());
                }
            }
        });

        tracker = AnalyticsTracker.getInstance(getApplicationContext());
        tracker.trackPageView(TrackerConstants.PageView.TIME_REGISTRATIONS_ACTIVITY);

        loadExtraTimeRegistration = new TimeRegistration();
        loadExtraTimeRegistration.setId(-1);

        loadTimeRegistrations(true, true);

        getListView().setOnItemClickListener(new ListView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(getApplicationContext(), LOG_TAG, "Clicked on TR-item " + position);
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

                Intent intent = new Intent(TimeRegistrationListActivity.this, TimeRegistrationDetailActivity.class);
                intent.putExtra(Constants.Extras.TIME_REGISTRATION, selectedRegistration);
                intent.putExtra(Constants.Extras.TIME_REGISTRATION_PREVIOUS, previousTimeRegistration);
                intent.putExtra(Constants.Extras.TIME_REGISTRATION_NEXT, nextTimeRegistration);
                startActivityForResult(intent, Constants.IntentRequestCodes.REGISTRATION_DETAILS);
            }
        });

        registerForContextMenu(getListView());

        showShowcase();
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
            Log.d(getApplicationContext(), LOG_TAG, "totoal count of timeregistrations is " + initialRecordCount);
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

            Log.d(getApplicationContext(), LOG_TAG, getTimeRegistrationsSize() + " timeregistrations loaded!");
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
                Log.d(getApplicationContext(), LOG_TAG, "Loaded " + extraTimeRegistrations.size() + " extra time registrations");

                timeRegistrations.remove(loadExtraTimeRegistration);
                for (TimeRegistration timeRegistration : extraTimeRegistrations) {
                    timeRegistrations.add(timeRegistration);
                }

                Log.d(getApplicationContext(), LOG_TAG, "Total time registrations loaded now: " + getTimeRegistrationsSize());

                if (initialRecordCount > getTimeRegistrationsSize()) {
                    Log.d(getApplicationContext(), LOG_TAG, "We need an extra item in the list to load more time registrations!");
                    timeRegistrations.add(loadExtraTimeRegistration);
                }
                return timeRegistrations;
            }

            @Override
            protected void onPostExecute(Object object) {
                progressBar.setVisibility(View.INVISIBLE);
                if (object == null) {
                    Log.w(getApplicationContext(), LOG_TAG, "Loading extra items failed, reloading entire list!");
                    loadTimeRegistrations(true, false);
                    return;
                }
                Log.d(getApplicationContext(), LOG_TAG, "Applying the changes...");
                refillListView((List<TimeRegistration>) object);
            }
        };
        AsyncHelper.start(asyncTask);
    }

    private void refillListView(List<TimeRegistration> timeRegistrations) {
        List<TimeRegistration> listOfNewTimeRegistrations = new ArrayList<TimeRegistration>();
        listOfNewTimeRegistrations.addAll(timeRegistrations);

        if (getListView().getAdapter() == null) {
            TimeRegistrationsListAdapter adapter = new TimeRegistrationsListAdapter(TimeRegistrationListActivity.this, listOfNewTimeRegistrations);
            setListAdapter(adapter);
        } else {
            ((TimeRegistrationsListAdapter) getListView().getAdapter()).refill(listOfNewTimeRegistrations);
        }
    }

    public void onPunchButtonClick(View view) {
        PunchBarUtil.onPunchButtonClick(TimeRegistrationListActivity.this, timeRegistrationService);
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
        if (resultCode == Constants.IntentResultCodes.GHOST_RECORD) {
            Log.d(getApplicationContext(), LOG_TAG, "A ghost record has been detected (typically after a synchronization) so reloading the entire list...");
            loadTimeRegistrations(true, true);
            return;
        }

        switch (requestCode) {
            case Constants.IntentRequestCodes.TIME_REGISTRATION_ACTION : {
                if (resultCode == RESULT_OK) {
                    Log.d(getApplicationContext(), LOG_TAG, "The time registration has been updated");
                    loadTimeRegistrations(true, false);
                } else if (resultCode == Constants.IntentResultCodes.RESULT_OK_SPLIT) {
                    Log.d(getApplicationContext(), LOG_TAG, "The time registration has been split");
                    timeRegistrations.add(0, new TimeRegistration()); //Forces when reloading to load one extra record!
                    loadTimeRegistrations(true, false);
                }
                break;
            }
            case Constants.IntentRequestCodes.START_TIME_REGISTRATION: {
                PunchBarUtil.configurePunchBar(TimeRegistrationListActivity.this, timeRegistrationService, taskService, projectService);
                break;
            }
            case Constants.IntentRequestCodes.END_TIME_REGISTRATION: {
                PunchBarUtil.configurePunchBar(TimeRegistrationListActivity.this, timeRegistrationService, taskService, projectService);
                break;
            }
        }

        if (resultCode == Constants.IntentResultCodes.RESULT_DELETED) {
            Log.d(getApplicationContext(), LOG_TAG, "One or more time registrations have been deleted.");
            loadTimeRegistrations(true, false);
        }
    }

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        MenuInflater inflater = getSupportMenuInflater();
//        inflater.inflate(R.menu.time_registrations_list_menu, menu);
//    }


    @Override
    public void onCreateContextMenu(android.view.ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        android.view.MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.time_registrations_list_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int element = info.position;
        TimeRegistration timeRegistration = timeRegistrations.get(element);

        if (timeRegistration != null) {
            switch (item.getItemId()) {
                case R.id.registrations_activity_edit:
                    Intent intent = new Intent(TimeRegistrationListActivity.this, TimeRegistrationActionActivity.class);
                    intent.putExtra(Constants.Extras.TIME_REGISTRATION, timeRegistration);
                    startActivityForResult(intent, Constants.IntentRequestCodes.TIME_REGISTRATION_ACTION);
                    break;
                default:
                    return super.onContextItemSelected(item);
            }
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getSupportMenuInflater();
        menuInflater.inflate(R.menu.ab_activity_time_registrations, menu);

        // Calling super after populating the menu is necessary here to ensure that the
        // action bar helpers have a chance to handle this event.
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                IntentUtil.goBack(TimeRegistrationListActivity.this);
                if (menu.isMenuShowing()) {
                    menu.showContent();
                } else {
                    menu.showMenu();
                }
                break;
            case R.id.menu_time_registrations_activity_add:
                Intent addIntent = new Intent(TimeRegistrationListActivity.this, TimeRegistrationAddActivity.class);
                startActivity(addIntent);
                break;
            case R.id.menu_time_registrations_activity_report:
                Intent reportingIntent = new Intent(TimeRegistrationListActivity.this, ReportingCriteriaActivity.class);
                startActivity(reportingIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        PunchBarUtil.configurePunchBar(TimeRegistrationListActivity.this, timeRegistrationService, taskService, projectService);
        
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

    private void showShowcase() {
        if (ContextUtils.getAndroidApiVersion() < 15) {
            return;
        }

        // In case of new features for which the showcase must be shown again on the dashboard this check must include
        // the new app version code.
        if (Preferences.Showcase.getShowcaseLastShownForAppVersion(this) < 274) {
            ShowcaseView.ConfigOptions co = new ShowcaseView.ConfigOptions();
            co.hideOnClickOutside = false;
            co.block = true;
            co.noButton = false;
            co.shotType = ShowcaseView.TYPE_NO_LIMIT;
            co.insert = ShowcaseView.INSERT_TO_DECOR;
            co.alignVertical = ShowcaseView.BOTTOM;
            co.alignHorizontal = ShowcaseView.LEFT;

            View view = findViewById(android.R.id.home);

            List<ShowcaseViewElement> showcaseViewElements = new ArrayList<ShowcaseViewElement>();
            showcaseViewElements.add(new ShowcaseViewElement(view, R.string.showcase_home_title, R.string.showcase_home_text, co));
            View punchBarAction = findViewById(R.id.punchBarActionId);
            if (punchBarAction != null) {
                showcaseViewElements.add(new ShowcaseViewElement(punchBarAction, R.string.showcase_punchbar_title, R.string.showcase_punchbar_text, co));
            }
            ShowcaseViewUtility.start(showcaseViewElements, this).setOnShowcaseEndedListener(this);
        }
    }

    @Override
    public void onShowcaseEndedListener() {
        Preferences.Showcase.setShowcaseLastShownForAppVersion(this, ContextUtils.getCurrentApplicationVersionCode(this));
    }
}
