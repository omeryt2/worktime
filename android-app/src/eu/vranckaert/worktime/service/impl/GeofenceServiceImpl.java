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

package eu.vranckaert.worktime.service.impl;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.inject.Inject;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.dao.GeofenceDao;
import eu.vranckaert.worktime.exceptions.worktime.trigger.geofence.DuplicateGeofenceNameException;
import eu.vranckaert.worktime.model.Project;
import eu.vranckaert.worktime.model.Task;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.model.trigger.GeofenceTrigger;
import eu.vranckaert.worktime.service.GeofenceService;
import eu.vranckaert.worktime.service.ProjectService;
import eu.vranckaert.worktime.service.TaskService;
import eu.vranckaert.worktime.service.TimeRegistrationService;
import eu.vranckaert.worktime.service.ui.StatusBarNotificationService;
import eu.vranckaert.worktime.utils.preferences.Preferences;
import roboguice.inject.ContextSingleton;

import java.util.*;

/**
 * User: DIRK VRANCKAERT
 * Date: 21/05/13
 * Time: 8:03
 */
public class GeofenceServiceImpl implements GeofenceService {
    private static final String LOG_TAG = GeofenceServiceImpl.class.getSimpleName();

    @Inject @ContextSingleton private Context context;
    @Inject private GeofenceDao dao;
    @Inject private TimeRegistrationService timeRegistrationService;
    @Inject private StatusBarNotificationService statusBarNotificationService;
    @Inject private TaskService taskService;
    @Inject private ProjectService projectService;

    private LocationClient mLocationClient = null;

    @Override
    public GeofenceTrigger storeGeofence(GeofenceTrigger geofenceTrigger) throws DuplicateGeofenceNameException {
        if (dao.getGeofencesByNameCount(geofenceTrigger.getName()) > 0) {
            throw new DuplicateGeofenceNameException();
        }

        if (geofenceTrigger.getExpirationDate() != null) {
            Calendar expiration = Calendar.getInstance();
            expiration.setTime(geofenceTrigger.getExpirationDate());
            expiration.set(Calendar.HOUR_OF_DAY, expiration.getActualMaximum(Calendar.HOUR_OF_DAY));
            expiration.set(Calendar.MINUTE, expiration.getActualMaximum(Calendar.MINUTE));
            expiration.set(Calendar.SECOND, expiration.getActualMaximum(Calendar.SECOND));
            expiration.set(Calendar.MILLISECOND, expiration.getActualMaximum(Calendar.MILLISECOND));
            geofenceTrigger.setExpirationDate(expiration.getTime());
        }

        geofenceTrigger.setGeofenceRequestId(getNewUniqueGeofenceId());

        geofenceTrigger = dao.save(geofenceTrigger);
        return geofenceTrigger;
    }

    /**
     * Get a new and always unique {@link eu.vranckaert.worktime.model.trigger.GeofenceTrigger#geofenceRequestId}.
     * @return The always unique {@link eu.vranckaert.worktime.model.trigger.GeofenceTrigger#geofenceRequestId}.
     */
    private String getNewUniqueGeofenceId() {
        return "WORKTIME_" + UUID.randomUUID().toString();
    }

    @Override
    public GeofenceTrigger updatGeofence(GeofenceTrigger geofenceTrigger) throws DuplicateGeofenceNameException {
        GeofenceTrigger oldGeofenceTrigger = dao.findById(geofenceTrigger.getId());

        int maxCount = 0;
        if (oldGeofenceTrigger.getName().equals(geofenceTrigger.getName())) {
            maxCount = 1;
        }

        if (dao.getGeofencesByNameCount(geofenceTrigger.getName()) > maxCount) {
            throw new DuplicateGeofenceNameException();
        }

        geofenceTrigger = dao.update(geofenceTrigger);
        return geofenceTrigger;
    }

    @Override
    public void deleteGeofence(GeofenceTrigger geofenceTrigger) {
        dao.delete(geofenceTrigger);
    }

    @Override
    public List<GeofenceTrigger> findAllNonExpired() {
        return dao.findAllNonExpired();
    }

    @Override
    public GeofenceTrigger findGeofenceTriggerByGeofenceRequestId(String requestId) {
        return dao.findGeofenceTriggerByGeofenceRequestId(requestId);
    }

    @Override
    public Boolean geofenceTriggered(Geofence geofence, GeofenceTrigger geofenceTrigger, int transition) {
        TimeRegistration ongoingTimeRegistration = timeRegistrationService.getLatestTimeRegistration();
        if (ongoingTimeRegistration != null && !ongoingTimeRegistration.isOngoingTimeRegistration()) {
            ongoingTimeRegistration = null;
        }

        if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.d(LOG_TAG, "Entering geo fence '" + geofenceTrigger.getName() + "'");
            // Entering a geo fence...
            if (ongoingTimeRegistration != null && !geofenceTrigger.isEntered()) {
                // If we just entered a geo location and there is already another ongoing time registration, do not start a
                // new one!
                Log.d(LOG_TAG, "GEOFENCE_TRIGGER: Cannot start (We just entered the geo fence but a time registration is already busy) - " + geofenceTrigger.getName());
                statusBarNotificationService.addNotificationForGeofence(
                        context.getString(R.string.lbl_trigger_geo_fencing_broadcast_notification_error_ongoing_time_registration_title),
                        context.getString(R.string.lbl_trigger_geo_fencing_broadcast_notification_error_ongoing_time_registration_message_short, geofenceTrigger.getName()),
                        context.getString(R.string.lbl_trigger_geo_fencing_broadcast_notification_error_ongoing_time_registration_message, geofenceTrigger.getName())
                );
                return null;
            } else if (ongoingTimeRegistration != null && geofenceTrigger.isEntered()) {
                // If we already entered this geo fence, no action is to be taken!
                Log.d(LOG_TAG, "GEOFENCE_TRIGGER: No action (We already have entered this geo fence and an ongoing time registration is found) - " + geofenceTrigger.getName());
                return null;
            }

            Log.d(LOG_TAG, "GEOFENCE_TRIGGER: Start TR (A new time registration will be started and the GeofenceTrigger will be marked as 'entered') - " + geofenceTrigger.getName());
            TimeRegistration timeRegistration = timeRegistrationService.create(new Date(), geofenceTrigger.getTask());
            geofenceTrigger.setEntered(true);
            dao.update(geofenceTrigger);

            // Warn the user if the project and/or task for this new TR are already finished.
            taskService.refresh(geofenceTrigger.getTask());
            projectService.refresh(geofenceTrigger.getTask().getProject());
            if (geofenceTrigger.getTask().isFinished() && geofenceTrigger.getTask().getProject().isFinished()) {
                statusBarNotificationService.addNotificationForGeofence(
                        context.getString(R.string.lbl_trigger_geo_fencing_broadcast_notification_warn_finished_task_project_title),
                        context.getString(R.string.lbl_trigger_geo_fencing_broadcast_notification_warn_finished_task_project_message_short, geofenceTrigger.getName()),
                        context.getString(R.string.lbl_trigger_geo_fencing_broadcast_notification_warn_finished_task_project_message, geofenceTrigger.getName())
                );
            } else if (geofenceTrigger.getTask().isFinished()) {
                statusBarNotificationService.addNotificationForGeofence(
                        context.getString(R.string.lbl_trigger_geo_fencing_broadcast_notification_warn_finished_task_project_title),
                        context.getString(R.string.lbl_trigger_geo_fencing_broadcast_notification_warn_finished_task_message_short, geofenceTrigger.getName()),
                        context.getString(R.string.lbl_trigger_geo_fencing_broadcast_notification_warn_finished_task_message, geofenceTrigger.getName())
                );
            } else if (geofenceTrigger.getTask().isFinished()) {
                statusBarNotificationService.addNotificationForGeofence(
                        context.getString(R.string.lbl_trigger_geo_fencing_broadcast_notification_warn_finished_task_project_title),
                        context.getString(R.string.lbl_trigger_geo_fencing_broadcast_notification_warn_finished_project_message_short, geofenceTrigger.getName()),
                        context.getString(R.string.lbl_trigger_geo_fencing_broadcast_notification_warn_finished_project_message, geofenceTrigger.getName())
                );
            }

            return true;
        } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.d(LOG_TAG, "Leaving geo fence '" + geofenceTrigger.getName() + "'");
            // Leaving a geo fence...
            if (ongoingTimeRegistration == null) {
                Log.d(LOG_TAG, "GEOFENCE_TRIGGER: No action (There are no ongoing time registration so nothing should be stopped) - " + geofenceTrigger.getName());
                return null;
            } else if (geofenceTrigger.isEntered()) {
                if (!Preferences.TriggersGeofence.doNotPunchOutOnLeavingGeofence(context)) {
                    ongoingTimeRegistration.setEndTime(new Date());
                    timeRegistrationService.update(ongoingTimeRegistration);
                } else  if(Preferences.TriggersGeofence.showNotificationWhenNotPunchedOut(context)) {
                    statusBarNotificationService.addNotificationForGeofence(
                            context.getString(R.string.lbl_trigger_geo_fencing_broadcast_notification_warn_time_registration_not_ended_title),
                            context.getString(R.string.lbl_trigger_geo_fencing_broadcast_notification_warn_time_registration_not_ended_message_short, geofenceTrigger.getName()),
                            null
                    );
                }
                return true;
            } else {
                if (!Preferences.TriggersGeofence.doNotPunchOutOnLeavingGeofence(context)) {
                    Log.d(LOG_TAG, "GEOFENCE_TRIGGER: Invalid GeoFenceTrigger (Cannot stop a time registration because this geo fence has not been entered...) - " + geofenceTrigger.getName());
                    return false;
                } else {
                    return true;
                }
            }
        }

        return false; // Would mean that another geofence transition (other then enter and exit) is passed...
    }

    @Override
    public void checkGeoFencesOnTaskRemoval(Task task) {
        List<GeofenceTrigger> geofenceTriggers = dao.findGeoFencesForTask(task);
        List<String> requestIds = new ArrayList<String>();
        for (GeofenceTrigger geofenceTrigger : geofenceTriggers) {
            requestIds.add(geofenceTrigger.getGeofenceRequestId());
            dao.delete(geofenceTrigger);
        }
        deleteGeofences(requestIds);
    }

    @Override
    public void checkGeoFencesOnProjectRemoval(Project project) {
        List<GeofenceTrigger> geofenceTriggers = dao.findGeoFencesForTasks(taskService.findTasksForProject(project));
        List<String> requestIds = new ArrayList<String>();
        for (GeofenceTrigger geofenceTrigger : geofenceTriggers) {
            requestIds.add(geofenceTrigger.getGeofenceRequestId());
            dao.delete(geofenceTrigger);
        }
        deleteGeofences(requestIds);
    }

    @Override
    public void deleteGeofence(final String requestId) {
        deleteGeofences(Arrays.asList(new String[]{requestId}));
    }

    @Override
    public void deleteGeofences(final List<String> requestIds) {
        mLocationClient = new LocationClient(context, new GooglePlayServicesClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                mLocationClient.removeGeofences(requestIds, new LocationClient.OnRemoveGeofencesResultListener() {
                    @Override
                    public void onRemoveGeofencesByRequestIdsResult(int i, String[] strings) {}

                    @Override
                    public void onRemoveGeofencesByPendingIntentResult(int i, PendingIntent pendingIntent) {}
                });
            }
            @Override
            public void onDisconnected() {}
        }, new GooglePlayServicesClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {}
        });
        mLocationClient.connect();
    }
}
