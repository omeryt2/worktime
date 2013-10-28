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

import android.content.Context;
import com.google.inject.Inject;
import eu.vranckaert.worktime.dao.ProjectDao;
import eu.vranckaert.worktime.dao.TaskDao;
import eu.vranckaert.worktime.dao.TimeRegistrationDao;
import eu.vranckaert.worktime.dao.WidgetConfigurationDao;
import eu.vranckaert.worktime.dao.impl.*;
import eu.vranckaert.worktime.exceptions.TaskStillInUseException;
import eu.vranckaert.worktime.model.Project;
import eu.vranckaert.worktime.model.Task;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.model.WidgetConfiguration;
import eu.vranckaert.worktime.service.GeofenceService;
import eu.vranckaert.worktime.service.TaskService;
import eu.vranckaert.worktime.utils.context.Log;
import roboguice.inject.ContextSingleton;

import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 28/03/11
 * Time: 17:28
 */
public class TaskServiceImpl implements TaskService {
    private static final String LOG_TAG = TaskServiceImpl.class.getSimpleName();

    @Inject
    @ContextSingleton
    private Context ctx;

    @Inject
    private TaskDao dao;

    @Inject
    private ProjectDao projectDao;

    @Inject
    private WidgetConfigurationDao widgetConfigurationDao;

    @Inject
    private TimeRegistrationDao timeRegistrationDao;

    @Inject
    private GeofenceService geofenceService;

    public TaskServiceImpl(Context ctx) {
        this.ctx = ctx;
        dao = new TaskDaoImpl(ctx, new SyncRemovalCacheDaoImpl(ctx));
        projectDao = new ProjectDaoImpl(ctx, new SyncRemovalCacheDaoImpl(ctx));
        timeRegistrationDao = new TimeRegistrationDaoImpl(ctx, new SyncRemovalCacheDaoImpl(ctx));
        widgetConfigurationDao = new WidgetConfigurationDaoImpl(ctx);
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
        } else {
            if(force) {
                Log.d(ctx, LOG_TAG, "Forcing to delete all timeregistrations and geo fences linked to the task first!");
                for (TimeRegistration treg : timeRegistrations) {
                    timeRegistrationDao.delete(treg);
                }
                geofenceService.checkGeoFencesOnTaskRemoval(task);
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

    @Override
    public Task getSelectedTask(int widgetId) {
        WidgetConfiguration wc = widgetConfigurationDao.findById(widgetId);
        if (wc == null) {
            Log.w(ctx, LOG_TAG, "No widget configuration is found for widget with id " + widgetId + ". One will be created with the default project");

            wc = new WidgetConfiguration(widgetId);
            Project project = projectDao.findDefaultProject();
            if (project != null) {
                List<Task> tasks = dao.findTasksForProject(project);
                if (tasks != null && !tasks.isEmpty()) {
                    Task task = tasks.get(0);
                    wc.setTask(task);
                    widgetConfigurationDao.save(wc);
                    return task;
                } else {
                    return null;
                }
            }
        }

        Task task = null;

        if (wc.getTask() != null) {
            Log.d(ctx, LOG_TAG, "Selected task id found is " + wc.getTask().getId());
            task = dao.findById(wc.getTask().getId());
            if (task != null)
                Log.d(ctx, LOG_TAG, "Selected task has id " + task.getId() + " and name " + task.getName());
        }

        if (task == null) {
            Log.w(ctx, LOG_TAG, "No task is found for widget with id " + widgetId + ", updating the widget configuration to use a task of the default project!");
            Project project = projectDao.findDefaultProject();
            if (project != null) {
                List<Task> tasks = dao.findTasksForProject(project);
                if (tasks != null && !tasks.isEmpty()) {
                    task = tasks.get(0);
                    wc.setTask(task);
                    Log.w(ctx, LOG_TAG, "The first task of the default project is now used as selected task for widget " + widgetId + " and has id " + task.getId() + " and name " + task.getName());
                    widgetConfigurationDao.update(wc);
                }
            }
        }

        return task;
    }

    @Override
    public void setSelectedTask(Integer widgetId, Task task) {
        WidgetConfiguration wc = widgetConfigurationDao.findById(widgetId);
        if (wc == null) {
            wc = new WidgetConfiguration(widgetId);
            wc.setTask(task);
            wc.setProject(null);
            widgetConfigurationDao.save(wc);
        } else {
            wc.setTask(task);
            wc.setProject(null);
            widgetConfigurationDao.update(wc);
        }
    }

    @Override
    public List<Task> findAll() {
        return dao.findAll();
    }

    @Override
    public boolean checkTaskExisting(Task task) {
        if (task.getId() != null) {
            Task existingTask = dao.findById(task.getId());
            if (existingTask != null) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean checkReloadTask(Task task) {
        if (task.getId() != null) {
            Task existingTask = dao.findById(task.getId());
            if (existingTask != null && existingTask.isModifiedAfter(task)) {
                return true;
            }
        }

        return false;
    }
}
