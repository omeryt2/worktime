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

package eu.vranckaert.worktime.service;

import com.google.android.gms.location.Geofence;
import eu.vranckaert.worktime.exceptions.worktime.trigger.geofence.DuplicateGeofenceNameException;
import eu.vranckaert.worktime.model.Project;
import eu.vranckaert.worktime.model.Task;
import eu.vranckaert.worktime.model.trigger.GeofenceTrigger;

import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 21/05/13
 * Time: 8:00
 */
public interface GeofenceService {
    /**
     * Store a new {@link eu.vranckaert.worktime.model.trigger.GeofenceTrigger} object.
     * @param geofenceTrigger The {@link eu.vranckaert.worktime.model.trigger.GeofenceTrigger} to store.
     * @return The saved instance with the {@link eu.vranckaert.worktime.model.trigger.GeofenceTrigger#geofenceRequestId} and {@link eu.vranckaert.worktime.model.trigger.GeofenceTrigger#id} set.
     * @throws DuplicateGeofenceNameException Thrown when trying to save a {@link eu.vranckaert.worktime.model.trigger.GeofenceTrigger} with a non-unique name.
     */
    GeofenceTrigger storeGeofence(GeofenceTrigger geofenceTrigger) throws DuplicateGeofenceNameException;

    /**
     * Update an existing {@link eu.vranckaert.worktime.model.trigger.GeofenceTrigger}.
     * @param geofenceTrigger The {@link eu.vranckaert.worktime.model.trigger.GeofenceTrigger} to be updated.
     * @return The updated instance of the {@link eu.vranckaert.worktime.model.trigger.GeofenceTrigger}.
     * @throws DuplicateGeofenceNameException Thrown when trying to update a {@link eu.vranckaert.worktime.model.trigger.GeofenceTrigger} with a non-unique name.
     */
    GeofenceTrigger updatGeofence(GeofenceTrigger geofenceTrigger) throws DuplicateGeofenceNameException;

    /**
     * Deletes a specific {@link eu.vranckaert.worktime.model.trigger.GeofenceTrigger}.
     * @param geofenceTrigger The {@link eu.vranckaert.worktime.model.trigger.GeofenceTrigger} to be deleted.
     */
    void deleteGeofence(GeofenceTrigger geofenceTrigger);

    /**
     * Find all {@link eu.vranckaert.worktime.model.trigger.GeofenceTrigger} for which the {@link eu.vranckaert.worktime.model.trigger.GeofenceTrigger#expirationDate} has not yet passed.
     * @return The list of valid {@link eu.vranckaert.worktime.model.trigger.GeofenceTrigger}s.
     */
    List<GeofenceTrigger> findAllNonExpired();

    /**
     * Find one unique {@link GeofenceTrigger}.
     * @param requestId The unique request id.
     * @return The {@link GeofenceTrigger} if one is found, null otherwise.
     */
    GeofenceTrigger findGeofenceTriggerByGeofenceRequestId(String requestId);

    /**
     * Executed when a {@link Geofence} has been triggered.
     * @param geofence           The {@link Geofence} that has been triggered
     * @param geofenceTrigger    The {@link GeofenceTrigger} that is stored in our db according to the {@link Geofence}.
     * @param transition         The transition, can be {@link Geofence#GEOFENCE_TRANSITION_ENTER} or
     *                           {@link Geofence#GEOFENCE_TRANSITION_EXIT}.
     * @return Returns null if no action could be performed and when that's ok. Returns {@link Boolean#TRUE} if an action
     * was taken successful, returns {@link Boolean#FALSE} if no action could be performed and another geofence (if any)
     * can be tried.
     */
    Boolean geofenceTriggered(Geofence geofence, GeofenceTrigger geofenceTrigger, int transition);

    /**
     * Do all necessary checks upon removing a {@link Task} for removing all corresponding {@link GeofenceTrigger}s.
     * @param task The {@link Task} that is being removed.
     */
    void checkGeoFencesOnTaskRemoval(Task task);

    /**
     * Do all necessary checks upon removing a {@link Project} for removing all corresponding {@link GeofenceTrigger}s.
     * @param project The {@link Project} that is being removed.
     */
    void checkGeoFencesOnProjectRemoval(Project project);

    /**
     * Delete a {@link Geofence} with a certain {@link com.google.android.gms.location.Geofence#getRequestId()}.
     * @param requestId The request id of the {@link Geofence}.
     */
    void deleteGeofence(final String requestId);

    /**
     * Delete all {@link Geofence}s that have a certain {@link com.google.android.gms.location.Geofence#getRequestId()}.
     * @param requestIds The request ids of the {@link Geofence}s to be deleted.
     */
    void deleteGeofences(final List<String> requestIds);
}
