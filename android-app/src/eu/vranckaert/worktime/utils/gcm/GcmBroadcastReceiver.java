package eu.vranckaert.worktime.utils.gcm;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * Date: 29/10/13
 * Time: 11:34
 *
 * @author Dirk Vranckaert
 */
public class GcmBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName comp = new ComponentName(context.getPackageName(), GcmIntentService.class.getName());
        context.startService(intent.setComponent(comp));
        setResultCode(Activity.RESULT_OK);
    }
}
