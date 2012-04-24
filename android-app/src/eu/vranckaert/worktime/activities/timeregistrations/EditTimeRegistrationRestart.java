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

import android.os.Bundle;
import com.google.inject.Inject;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.service.TimeRegistrationService;
import eu.vranckaert.worktime.service.ui.StatusBarNotificationService;
import eu.vranckaert.worktime.service.ui.WidgetService;
import roboguice.activity.GuiceActivity;
import roboguice.inject.InjectExtra;

/**
 * User: DIRK VRANCKAERT
 * Date: 10/08/11
 * Time: 20:09
 */
public class EditTimeRegistrationRestart extends GuiceActivity {
    @InjectExtra(Constants.Extras.TIME_REGISTRATION)
    private TimeRegistration timeRegistration;

    @Inject
    private TimeRegistrationService timeRegistrationService;

    @Inject
    private WidgetService widgetService;

    @Inject
    private StatusBarNotificationService statusBarNotificationService;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        timeRegistration.setEndTime(null);
        timeRegistrationService.update(timeRegistration);
        widgetService.updateWidget();
        statusBarNotificationService.addOrUpdateNotification(timeRegistration);
        setResult(RESULT_OK);
        finish();
    }
}