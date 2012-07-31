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

package eu.vranckaert.worktime.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.google.inject.Inject;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.activities.about.AboutActivity;
import eu.vranckaert.worktime.activities.preferences.PreferencesActivity;
import eu.vranckaert.worktime.activities.projects.ManageProjectsActivity;
import eu.vranckaert.worktime.activities.reporting.ReportingCriteriaActivity;
import eu.vranckaert.worktime.activities.timeregistrations.TimeRegistrationListActivity;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.constants.TrackerConstants;
import eu.vranckaert.worktime.service.BackupService;
import eu.vranckaert.worktime.service.CommentHistoryService;
import eu.vranckaert.worktime.service.ProjectService;
import eu.vranckaert.worktime.service.TaskService;
import eu.vranckaert.worktime.service.TimeRegistrationService;
import eu.vranckaert.worktime.utils.punchbar.PunchBarUtil;
import eu.vranckaert.worktime.utils.tracker.AnalyticsTracker;
import eu.vranckaert.worktime.utils.view.actionbar.ActionBarGuiceActivity;

public class HomeActivity extends ActionBarGuiceActivity {
    private static final String LOG_TAG = HomeActivity.class.getSimpleName();

    @Inject
    private CommentHistoryService commentHistoryService;
    
    @Inject
    private TimeRegistrationService timeRegistrationService;

    @Inject
    private TaskService taskService;

    @Inject
    private BackupService backupService;

    @Inject
    private ProjectService projectService;

    private AnalyticsTracker tracker;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tracker = AnalyticsTracker.getInstance(getApplicationContext());
        tracker.trackPageView(TrackerConstants.PageView.HOME_ACTIVITY);

        initiateDatabase();
    }

    private void initiateDatabase() {
        // By default the database is only initiated upon first call (so when tyring to load tasks/projects or time
        // registrations. We force the creation of the database by loading the last comment.
        commentHistoryService.findLastComment();
    }

    private void launchActivity(Class activity) {
        Intent intent = new Intent(getApplicationContext(), activity);
        startActivity(intent);
    }

    public void onTimeRegistrationsClick(View view) {
        launchActivity(TimeRegistrationListActivity.class);
    }

    public void onProjectsClick(View view) {
        launchActivity(ManageProjectsActivity.class);
    }

    public void onPreferencesClick(View view) {
        launchActivity(PreferencesActivity.class);
    }

    public void onReportingClick(View view) {
        launchActivity(ReportingCriteriaActivity.class);
    }
    
    public void onPunchButtonClick(View view) {
        PunchBarUtil.onPunchButtonClick(HomeActivity.this, timeRegistrationService);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PunchBarUtil.configurePunchBar(HomeActivity.this, timeRegistrationService, taskService, projectService);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.IntentRequestCodes.START_TIME_REGISTRATION
                || requestCode == Constants.IntentRequestCodes.END_TIME_REGISTRATION) {
            PunchBarUtil.configurePunchBar(HomeActivity.this, timeRegistrationService, taskService, projectService);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.ab_activity_home, menu);

        // Calling super after populating the menu is necessary here to ensure that the
        // action bar helpers have a chance to handle this event.
        boolean r = super.onCreateOptionsMenu(menu);

        // Disable click on home-button
        getActionBarHelper().setHomeButtonEnabled(false);
        return r;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                break;
            case R.id.menu_home_activity_about:
                launchActivity(AboutActivity.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tracker.stopSession();
    }
}
