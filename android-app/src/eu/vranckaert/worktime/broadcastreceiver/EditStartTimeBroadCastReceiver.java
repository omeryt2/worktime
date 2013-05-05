package eu.vranckaert.worktime.broadcastreceiver;

import android.content.Context;
import android.content.Intent;
import eu.vranckaert.worktime.activities.timeregistrations.TimeRegistrationEditStartTimeActivity;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.model.TimeRegistration;
import roboguice.receiver.RoboBroadcastReceiver;

/**
 * Date: 3/05/13
 * Time: 14:13
 *
 * @author Dirk Vranckaert
 */
public class EditStartTimeBroadCastReceiver extends RoboBroadcastReceiver {
    @Override
    protected void handleReceive(Context context, Intent intent) {
        TimeRegistration timeRegistration = (TimeRegistration) intent.getExtras().get(Constants.Extras.TIME_REGISTRATION);
        TimeRegistration timeRegistrationPrevious = (TimeRegistration) intent.getExtras().get(Constants.Extras.TIME_REGISTRATION_PREVIOUS);

        Intent actionIntent = new Intent(context, TimeRegistrationEditStartTimeActivity.class);
        actionIntent.putExtra(Constants.Extras.TIME_REGISTRATION, timeRegistration);
        actionIntent.putExtra(Constants.Extras.TIME_REGISTRATION_PREVIOUS, timeRegistrationPrevious);
        actionIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(actionIntent);
    }
}
