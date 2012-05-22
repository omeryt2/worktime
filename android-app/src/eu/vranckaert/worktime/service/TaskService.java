/*
 * Copyright 2012 Dirk Vranckaert
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

import eu.vranckaert.worktime.exceptions.TaskStillInUseException;
import eu.vranckaert.worktime.model.Project;
import eu.vranckaert.worktime.model.Task;

import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 28/03/11
 * Time: 17:28
 */
public interface TaskService {
    /**
     * Find all tasks linked to a certain {@link Project}.
     * @param project The project.
     * @return A list of {@link Task} instances linked to the {@link Project}.
     */
    List<Task> findTasksForProject(Project project);

    /**
     * Find all tasks linked to a certain {@link Project} for which the flag {@link Task#finished} is
     * {@link Boolean#FALSE}.
     * @param project The project.
     * @return A list of {@link Task} instances linked to the {@link Project} and with the flag {@link Task#finished}
     * set to {@link Boolean#FALSE}.
     */
    List<Task> findNotFinishedTasksForProject(Project project);

    /**
     * Persist a new {@link Task} instance.
     * @param task The new {@link Task} to persist.
     * @return The persisted {@link Task} instance.
     */
    Task save(Task task);

    /**
     * Updates an existing {@link Task} instance.
     * @param task The {@link Task} instance to update.
     * @return The updated instance.
     */
    Task update(Task task);

    /**
     * Remove a task.
     * @param task The task to remove.
     * @param force If set to {@link Boolean#TRUE} all {@link eu.vranckaert.worktime.model.TimeRegistration} instances
     * linked to the task will be deleted first, then the tasj itself. If set to {@link Boolean#FALSE} nothing will
     * happen.
     * @throws TaskStillInUseException If the task is coupled to time registrations and the force-option is not
     * used this exception is thrown.
     */
    void remove(Task task, boolean force) throws TaskStillInUseException;

    /**
     * Refreshes the task status.
     * @param task The task to refresh.
     */
    void refresh(Task task);

    /**
     * Removes all items.
     */
    void removeAll();
}
