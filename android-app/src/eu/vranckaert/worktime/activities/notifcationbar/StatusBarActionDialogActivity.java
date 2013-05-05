package eu.vranckaert.worktime.activities.notifcationbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.google.inject.Inject;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.activities.timeregistrations.TimeRegistrationActionActivity;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.enums.timeregistration.TimeRegistrationAction;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.service.TimeRegistrationService;
import eu.vranckaert.worktime.service.ui.StatusBarNotificationService;
import eu.vranckaert.worktime.utils.preferences.Preferences;
import roboguice.activity.RoboActivity;

/**
 * Date: 3/05/13
 * Time: 15:31
 *
 * @author Dirk Vranckaert
 */
public abstract class StatusBarActionDialogActivity extends RoboActivity {
    @Inject
    private TimeRegistrationService timeRegistrationService;

    @Inject
    private StatusBarNotificationService statusBarNotificationService;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        launchStopTimeRegistrationActivity();
    }

    private void launchStopTimeRegistrationActivity() {
        TimeRegistration timeRegistration = timeRegistrationService.getLatestTimeRegistration();
        if (timeRegistration != null) {
            Intent intent = new Intent(getApplicationContext(), TimeRegistrationActionActivity.class);
            intent.putExtra(Constants.Extras.TIME_REGISTRATION, timeRegistration);
            intent.putExtra(Constants.Extras.DEFAULT_ACTION, getTimeRegistrationAction());
            if (Preferences.getImmediatePunchOut(this)) {
                intent.putExtra(Constants.Extras.SKIP_DIALOG, true);
            } else {
                intent.putExtra(Constants.Extras.ONLY_ACTION, true);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(intent, Constants.IntentRequestCodes.TIME_REGISTRATION_ACTION);
        } else {
            Toast.makeText(this, R.string.lbl_notif_no_tr_found, Toast.LENGTH_LONG);
            statusBarNotificationService.removeOngoingTimeRegistrationNotification();
        }
    }

    protected abstract TimeRegistrationAction getTimeRegistrationAction();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        finish();
    }
}
