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
