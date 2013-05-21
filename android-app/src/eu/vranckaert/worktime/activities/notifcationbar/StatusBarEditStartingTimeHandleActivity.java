/*
 * Copyright 2013 Dirk Vranckaert
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

package eu.vranckaert.worktime.activities.notifcationbar;

import android.content.Intent;
import android.os.Bundle;
import com.google.inject.Inject;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.service.TimeRegistrationService;
import roboguice.activity.RoboActivity;

/**
 * Date: 3/05/13
 * Time: 12:59
 *
 * @author Dirk Vranckaert
 */
public class StatusBarEditStartingTimeHandleActivity extends RoboActivity {
    @Inject
    private TimeRegistrationService timeRegistrationService;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        launchTimeRegistrationActionsDialog();
    }

    private void launchTimeRegistrationActionsDialog() {
        TimeRegistration timeRegistration = timeRegistrationService.getLatestTimeRegistration();
        TimeRegistration timeRegistrationPrevious = timeRegistrationService.getPreviousTimeRegistration(timeRegistration);

        if (timeRegistration != null) {
            Intent intent = new Intent();
            intent.setAction(Constants.Broadcast.TIME_REGISTRATION_EDIT_START_TIME);
            intent.putExtra(Constants.Extras.TIME_REGISTRATION, timeRegistration);
            intent.putExtra(Constants.Extras.TIME_REGISTRATION_PREVIOUS, timeRegistrationPrevious);
            sendBroadcast(intent);
        }
        finish();
    }
}
