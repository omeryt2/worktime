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

package eu.vranckaert.worktime.broadcastreceiver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.inject.Inject;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.model.trigger.GeofenceTrigger;
import eu.vranckaert.worktime.service.GeofenceService;
import eu.vranckaert.worktime.service.ui.StatusBarNotificationService;
import eu.vranckaert.worktime.service.ui.WidgetService;
import roboguice.receiver.RoboBroadcastReceiver;

import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 17/05/13
 * Time: 15:12
 */
public class TriggerGeofenceBroadcastReceiver extends RoboBroadcastReceiver {
    private static final String LOG_TAG = TriggerGeofenceBroadcastReceiver.class.getSimpleName();

    @Inject private StatusBarNotificationService statusBarNotificationService;
    @Inject private WidgetService widgetService;
    @Inject private GeofenceService geofenceService;

    @Override
    protected void handleReceive(Context context, Intent intent) {
        int transition = LocationClient.getGeofenceTransition(intent);

        List<Geofence> crossedGeofences = LocationClient.getTriggeringGeofences(intent);

        Log.d(LOG_TAG, (transition == Geofence.GEOFENCE_TRANSITION_ENTER ? "Entered" : "Left") + " geofence, number of geofences crossed: " + crossedGeofences.size());
        // TODO debug notifications should be removed!
        if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            statusBarNotificationService.addDebugNotification("WorkTime - GeoFence", "Entered geofence, " + crossedGeofences.size() + " geofence(s) crossed!", null);
        } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            statusBarNotificationService.addDebugNotification("WorkTime - GeoFence", "Left geofence, " + crossedGeofences.size() + " geofence(s) crossed!", null);
        }

        String geofenceNames = "";
        Boolean result = null;
        for (Geofence geofence : crossedGeofences) {
            GeofenceTrigger geofenceTrigger = geofenceService.findGeofenceTriggerByGeofenceRequestId(geofence.getRequestId());
            if (geofenceTrigger == null) {
                geofenceService.deleteGeofence(geofence.getRequestId());
            } else {
                if (geofenceNames.length() > 0)
                    geofenceNames += ", ";
                geofenceNames += geofenceTrigger.getName();
                result = geofenceService.geofenceTriggered(geofence, geofenceTrigger, transition);
                if (result == null || result) {
                    break;
                }
            }
        }

        if (result != null && !result) {
            String action = "";
            if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                action = context.getString(R.string.lbl_trigger_geo_fencing_entering);
            } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                action = context.getString(R.string.lbl_trigger_geo_fencing_leaving);
            }

            statusBarNotificationService.addNotificationForGeofence(
                    context.getString(R.string.lbl_trigger_geo_fencing_broadcast_notification_error_not_handled_geofence_title),
                    context.getString(R.string.lbl_trigger_geo_fencing_broadcast_notification_error_not_handled_geofence_message_short, action, geofenceNames),
                    context.getString(R.string.lbl_trigger_geo_fencing_broadcast_notification_error_not_handled_geofence_message_short, action, geofenceNames)
            );
        } else {
            widgetService.updateAllWidgets();
            statusBarNotificationService.addOrUpdateNotification(null);
        }
    }
}
