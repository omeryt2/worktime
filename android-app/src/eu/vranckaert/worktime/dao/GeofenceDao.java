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

package eu.vranckaert.worktime.dao;

import eu.vranckaert.worktime.dao.generic.GenericDao;
import eu.vranckaert.worktime.model.trigger.Geofence;

import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 21/05/13
 * Time: 7:56
 */
public interface GeofenceDao extends GenericDao<Geofence, Integer> {
    /**
     * Count the number of {@link Geofence}s with a certain name.
     * @param name The name of the {@link Geofence#name} to look for.
     * @return The number of results found with such a name.
     */
    int getGeofencesByNameCount(String name);

    /**
     * Find all {@link Geofence} for which the {@link Geofence#expirationDate} has not yet passed.
     * @return The list of valid {@link Geofence}s.
     */
    List<Geofence> findAllNonExpired();
}
