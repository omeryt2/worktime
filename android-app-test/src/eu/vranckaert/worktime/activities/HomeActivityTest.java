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

import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.activities.about.AboutActivity;
import eu.vranckaert.worktime.activities.preferences.PreferencesActivity;
import eu.vranckaert.worktime.activities.projects.ManageProjectsActivity;
import eu.vranckaert.worktime.activities.reporting.ReportingCriteriaActivity;
import eu.vranckaert.worktime.activities.timeregistrations.TimeRegistrationsActivity;
import eu.vranckaert.worktime.dao.CommentHistoryDao;
import eu.vranckaert.worktime.dao.ProjectDao;
import eu.vranckaert.worktime.dao.TaskDao;
import eu.vranckaert.worktime.dao.TimeRegistrationDao;
import eu.vranckaert.worktime.test.ActionBar;
import eu.vranckaert.worktime.test.cases.ActivityTestCase;

/**
 * User: DIRK VRANCKAERT
 * Date: 20/01/12
 * Time: 9:54
 */
public class HomeActivityTest extends ActivityTestCase<HomeActivity> {
    public HomeActivityTest() {
        super(HomeActivity.class);
    }

    @Override
    public void beforeTestInsertData(TimeRegistrationDao trDao, ProjectDao pDao, TaskDao tDao, CommentHistoryDao cDao) {
    }

    public void testClickTimeRegistrationsButton() {
        solo.assertCurrentActivity("The home activity is expected", HomeActivity.class);
        solo.clickOnButton(0);
        solo.waitForActivity(TimeRegistrationsActivity.class.getSimpleName());
        solo.assertCurrentActivity("The time registrations activity is expected", TimeRegistrationsActivity.class);
    }

    public void testClickProjectsButton() {
        solo.assertCurrentActivity("The home activity is expected", HomeActivity.class);
        solo.clickOnButton(1);
        solo.waitForActivity(ManageProjectsActivity.class.getSimpleName());
        solo.assertCurrentActivity("The projects activity is expected", ManageProjectsActivity.class);
    }

    public void testClickReportingButton() {
        solo.assertCurrentActivity("The home activity is expected", HomeActivity.class);
        solo.clickOnButton(2);
        solo.waitForActivity(ReportingCriteriaActivity.class.getSimpleName());
        solo.assertCurrentActivity("The reporting criteria activity is expected", ReportingCriteriaActivity.class);
    }

    public void testClickPreferencesButton() {
        solo.assertCurrentActivity("The home activity is expected", HomeActivity.class);
        solo.clickOnButton(3);
        solo.waitForActivity(PreferencesActivity.class.getSimpleName());
        solo.assertCurrentActivity("The preferences activity is expected", PreferencesActivity.class);
    }

    public void testClickAboutButton() {
        solo.assertCurrentActivity("The home activity is expected", HomeActivity.class);
        ActionBar.clickMenuItem(R.id.menu_home_activity_about, solo.getCurrentActivity());
        solo.waitForActivity(AboutActivity.class.getSimpleName());
        solo.assertCurrentActivity("The about activity is expected", AboutActivity.class);
    }
}
