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

package eu.vranckaert.worktime.service.impl;

import android.content.Context;
import com.google.inject.Inject;
import eu.vranckaert.worktime.dao.TaskDao;
import eu.vranckaert.worktime.dao.TimeRegistrationDao;
import eu.vranckaert.worktime.dao.impl.TaskDaoImpl;
import eu.vranckaert.worktime.dao.impl.TimeRegistrationDaoImpl;
import eu.vranckaert.worktime.exceptions.TaskStillInUseException;
import eu.vranckaert.worktime.model.Project;
import eu.vranckaert.worktime.model.Task;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.service.TaskService;
import eu.vranckaert.worktime.utils.context.Log;

import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 28/03/11
 * Time: 17:28
 */
public class TaskServiceImpl implements TaskService {
    private static final String LOG_TAG = TaskServiceImpl.class.getSimpleName();

    @Inject
    private Context ctx;

    @Inject
    private TaskDao dao;

    @Inject
    private TimeRegistrationDao timeRegistrationDao;

    public TaskServiceImpl(Context ctx) {
        this.ctx = ctx;
        dao = new TaskDaoImpl(ctx);
        timeRegistrationDao = new TimeRegistrationDaoImpl(ctx);
    }

    /**
     * Default constructor required by RoboGuice!
     */
    public TaskServiceImpl() {}

    /**
     * {@inheritDoc}
     */
    public List<Task> findTasksForProject(Project project) {
        return dao.findTasksForProject(project);
    }

    /**
     * {@inheritDoc}
     */
    public List<Task> findNotFinishedTasksForProject(Project project) {
        return dao.findNotFinishedTasksForProject(project);
    }

    /**
     * {@inheritDoc}
     */
    public Task save(Task task) {
        return dao.save(task);
    }

    /**
     * {@inheritDoc}
     */
    public Task update(Task task) {
        return dao.update(task);
    }

    /**
     * {@inheritDoc}
     */
    public void remove(Task task, boolean force) throws TaskStillInUseException {
        List<TimeRegistration> timeRegistrations = timeRegistrationDao.findTimeRegistrationsForTask(task);
        Log.d(ctx, LOG_TAG, timeRegistrations.size() + " timeregistrations found coupled to the project to delete");
        if(!timeRegistrations.isEmpty() && !force) {
            throw new TaskStillInUseException("The task is linked to existing time registrations!");
        } else if(force) {
            Log.d(ctx, LOG_TAG, "Forcing to delete all timeregistrations linked to the tasj first!");
            for (TimeRegistration treg : timeRegistrations) {
                timeRegistrationDao.delete(treg);
            }
        }
        dao.delete(task);
    }

    /**
     * {@inheritDoc}
     */
    public void refresh(Task task) {
        dao.refresh(task);
    }

    @Override
    public void removeAll() {
        dao.deleteAll();
    }
}
