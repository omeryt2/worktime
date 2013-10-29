package eu.vranckaert.worktime.utils.gcm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.inject.Inject;
import eu.vranckaert.worktime.activities.account.AccountSyncService;
import roboguice.service.RoboIntentService;

/**
 * Date: 29/10/13
 * Time: 11:37
 *
 * @author Dirk Vranckaert
 */
public class GcmIntentService extends RoboIntentService {
    private static final String LOG_TAG = GcmIntentService.class.getSimpleName();

    @Inject
    private Context context;

    public GcmIntentService() {
        super(RoboIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                int type = Integer.parseInt(extras.getString("type"));
                String notification = extras.getString("notification");
                String notificationTitle = extras.getString("notification");

                Log.d(LOG_TAG, "Received GCM message: " + extras.toString());
                switch (type) {
                    case 100:
                        Intent syncIntent = new Intent(context, AccountSyncService.class);
                        syncIntent.putExtra(AccountSyncService.EXTRA_SYNC_TRIGGERED_FROM_OTHER_DEVICE, true);
                        startService(syncIntent);
                        break;
                    case 200:
                        // TODO notification
                        break;
                    case 300:
                        // TODO remote logout
                        break;
                }
            }
        }
    }
}
