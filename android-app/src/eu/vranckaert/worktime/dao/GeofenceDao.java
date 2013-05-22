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
import eu.vranckaert.worktime.model.Task;
import eu.vranckaert.worktime.model.trigger.GeofenceTrigger;

import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 21/05/13
 * Time: 7:56
 */
public interface GeofenceDao extends GenericDao<GeofenceTrigger, Integer> {
    /**
     * Count the number of {@link eu.vranckaert.worktime.model.trigger.GeofenceTrigger}s with a certain name.
     * @param name The name of the {@link eu.vranckaert.worktime.model.trigger.GeofenceTrigger#name} to look for.
     * @return The number of results found with such a name.
     */
    int getGeofencesByNameCount(String name);

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
     * Find all {@link GeofenceTrigger}s linked to a certain {@ink Task}.
     * @param task The {@link Task} to look for.
     * @return The {@link GeofenceTrigger}s that are configured to start a
     * {@link eu.vranckaert.worktime.model.TimeRegistration} for the specified {@link Task}.
     */
    List<GeofenceTrigger> findGeoFencesForTask(Task task);

    /**
     * Find all {@link GeofenceTrigger}s linked to a certain {@ink Task}s.
     * @param tasks The list of {@link Task}s to look for.
     * @return The {@link GeofenceTrigger}s that are configured to start a
     * {@link eu.vranckaert.worktime.model.TimeRegistration} for one of the specified {@link Task}s.
     */
    List<GeofenceTrigger> findGeoFencesForTasks(List<Task> tasks);
}
