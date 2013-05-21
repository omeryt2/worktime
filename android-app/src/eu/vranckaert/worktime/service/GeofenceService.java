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

import eu.vranckaert.worktime.exceptions.worktime.trigger.geofence.DuplicateGeofenceNameException;
import eu.vranckaert.worktime.model.trigger.Geofence;

import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 21/05/13
 * Time: 8:00
 */
public interface GeofenceService {
    /**
     * Store a new {@link Geofence} object.
     * @param geofence The {@link Geofence} to store.
     * @return The saved instance with the {@link Geofence#geofenceRequestId} and {@link Geofence#id} set.
     * @throws DuplicateGeofenceNameException Thrown when trying to save a {@link Geofence} with a non-unique name.
     */
    Geofence storeGeofence(Geofence geofence) throws DuplicateGeofenceNameException;

    /**
     * Update an existing {@link Geofence}.
     * @param geofence The {@link Geofence} to be updated.
     * @return The updated instance of the {@link Geofence}.
     * @throws DuplicateGeofenceNameException Thrown when trying to update a {@link Geofence} with a non-unique name.
     */
    Geofence updatGeofence(Geofence geofence) throws DuplicateGeofenceNameException;

    /**
     * Deletes a specific {@link Geofence}.
     * @param geofence The {@link Geofence} to be deleted.
     */
    void deleteGeofence(Geofence geofence);

    /**
     * Find all {@link Geofence} for which the {@link Geofence#expirationDate} has not yet passed.
     * @return The list of valid {@link Geofence}s.
     */
    List<Geofence> findAllNonExpired();
}
