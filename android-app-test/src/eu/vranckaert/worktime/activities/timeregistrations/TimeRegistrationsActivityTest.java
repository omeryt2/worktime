/*
 *  Copyright 2012 Dirk Vranckaert
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
package eu.vranckaert.worktime.activities.timeregistrations;

import android.app.ListActivity;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.activities.HomeActivity;
import eu.vranckaert.worktime.test.cases.ActivityTestCase;
import eu.vranckaert.worktime.activities.widget.StopTimeRegistrationActivity;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.test.utils.TestUtil;

/**
 * User: DIRK VRANCKAERT
 * Date: 20/03/12
 * Time: 9:41
 */
public class TimeRegistrationsActivityTest extends ActivityTestCase<TimeRegistrationsActivity> {
    public TimeRegistrationsActivityTest() {
        super(TimeRegistrationsActivity.class);
    }

    public void testGoHome() {
        solo.assertCurrentActivity("The time registrations activity is expected", TimeRegistrationsActivity.class);
        solo.clickOnImageButton(0);
        solo.assertCurrentActivity("The home activity is expected", HomeActivity.class);
    }

    public void testDefaultEmptyList() {
        int trCount = ((ListActivity)solo.getCurrentActivity()).getListView().getAdapter().getCount();
        assertEquals("No TR's are expected!", 0, trCount);
    }
}
