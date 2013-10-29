package eu.vranckaert.worktime.utils.gcm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.inject.Inject;
import roboguice.service.RoboIntentService;

/**
 * Date: 29/10/13
 * Time: 11:37
 *
 * @author Dirk Vranckaert
 */
public class GcmIntentService extends RoboIntentService {
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
                Toast.makeText(context, "Received a GCM message!!! Message is: " + extras.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
