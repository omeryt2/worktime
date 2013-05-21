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
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.inject.Inject;
import eu.vranckaert.worktime.service.ui.StatusBarNotificationService;
import roboguice.receiver.RoboBroadcastReceiver;

import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 17/05/13
 * Time: 15:12
 */
public class GeofenceBroadcastReceiver extends RoboBroadcastReceiver {
    @Inject private StatusBarNotificationService statusBarNotificationService;

    @Override
    protected void handleReceive(Context context, Intent intent) {
        int transition = LocationClient.getGeofenceTransition(intent);
        List<Geofence> crossedGeofences = LocationClient.getTriggeringGeofences(intent);

        // TODO this is for testing purpose!
        String title = "GeoFence Test";
        String message = "";
        if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            message = "Entering geofence...";
        } else if(transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            message = "Leaving geofence...";
        }
        statusBarNotificationService.addNotificationForGeofence(title, message);
    }
}
