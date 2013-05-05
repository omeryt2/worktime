package eu.vranckaert.worktime.broadcastreceiver;

import android.content.Context;
import android.content.Intent;
import com.google.inject.Inject;
import eu.vranckaert.worktime.service.ui.StatusBarNotificationService;
import roboguice.receiver.RoboBroadcastReceiver;

/**
 * Date: 3/05/13
 * Time: 15:59
 *
 * @author Dirk Vranckaert
 */
public class ResetNotificationAreaBroadcastReceiver extends RoboBroadcastReceiver {
    @Inject
    private StatusBarNotificationService statusBarNotificationService;

    @Override
    protected void handleReceive(Context context, Intent intent) {
        statusBarNotificationService.addOrUpdateNotification(null);
    }
}
