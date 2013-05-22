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
import android.util.Log;
import com.google.inject.Inject;
import eu.vranckaert.worktime.dao.ProjectDao;
import eu.vranckaert.worktime.dao.TaskDao;
import eu.vranckaert.worktime.dao.TimeRegistrationDao;
import eu.vranckaert.worktime.dao.impl.ProjectDaoImpl;
import eu.vranckaert.worktime.dao.impl.SyncRemovalCacheDaoImpl;
import eu.vranckaert.worktime.dao.impl.TaskDaoImpl;
import eu.vranckaert.worktime.dao.impl.TimeRegistrationDaoImpl;
import eu.vranckaert.worktime.model.Project;
import eu.vranckaert.worktime.model.Task;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.service.TimeRegistrationService;
import eu.vranckaert.worktime.utils.date.DateUtils;
import eu.vranckaert.worktime.utils.preferences.Preferences;
import org.joda.time.Duration;
import roboguice.inject.ContextSingleton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 07/02/11
 * Time: 00:14
 */
public class TimeRegistrationServiceImpl implements TimeRegistrationService {
    private static final String LOG_TAG = TimeRegistrationServiceImpl.class.getSimpleName();

    @Inject
    @ContextSingleton
    private Context ctx;

    @Inject
    private TimeRegistrationDao dao;

    @Inject
    private ProjectDao projectDao;

    @Inject
    private TaskDao taskDao;

    /**
     * Enables the use of this service outside of RoboGuice!
     * @param ctx The context to insert
     */
    public TimeRegistrationServiceImpl(Context ctx) {
        this.ctx = ctx;
        dao = new TimeRegistrationDaoImpl(ctx, new SyncRemovalCacheDaoImpl(ctx));
        projectDao = new ProjectDaoImpl(ctx, new SyncRemovalCacheDaoImpl(ctx));
        taskDao = new TaskDaoImpl(ctx, new SyncRemovalCacheDaoImpl(ctx));
    }

    /**
     * Default constructor required by RoboGuice!
     */
    public TimeRegistrationServiceImpl() {}

    /**
     * {@inheritDoc}
     */
    public List<TimeRegistration> findAll() {
        List<TimeRegistration> timeRegistrations = dao.findAll();
        for(TimeRegistration timeRegistration : timeRegistrations) {
            Log.d(LOG_TAG, "Found timeregistration with ID: " + timeRegistration.getId() + " and according task with ID: " + timeRegistration.getTask().getId());
            taskDao.refresh(timeRegistration.getTask());
            projectDao.refresh(timeRegistration.getTask().getProject());
        }
        return timeRegistrations;
    }

    /**
     * {@inheritDoc}
     */
    public List<TimeRegistration> getTimeRegistrationForTasks(List<Task> tasks) {
        return dao.findTimeRegistrationsForTaks(tasks);
    }

    /**
     * {@inheritDoc}
     */
    public List<TimeRegistration> getTimeRegistrations(Date startDate, Date endDate, Project project, Task task) {
        List<Task> tasks = new ArrayList<Task>();
        if (task != null) {
            Log.d(LOG_TAG, "Querying for 1 specific task!");
            tasks.add(task);
        } else if(project != null) {
            Log.d(LOG_TAG, "Querying for a specific project!");
            tasks = taskDao.findTasksForProject(project);
            Log.d(LOG_TAG, "Number of tasks found for that project: " + tasks.size());
        }

        return dao.getTimeRegistrations(startDate, endDate, tasks);
    }

    /**
     * {@inheritDoc}
     */
    public TimeRegistration create(Date startTime, Task task) {
        TimeRegistration timeRegistration = new TimeRegistration();
        timeRegistration.setTask(task);
        timeRegistration.setStartTime(startTime);

                /*
                 * Issue 61
                 * If the start time of registration, and the end time of the previous registration, have a difference
                 * off less than 60 seconds, we start the time registration at the same time the previous one is ended.
                 * This is to prevent gaps in the time registrations that should be modified manual. This is default
                 * configured to happen (defined in the preferences).
                 */
        if (Preferences.getTimeRegistrationsAutoClose60sGap(ctx)) {
            Log.d(LOG_TAG, "Check for gap between this new time registration and the previous one");
            TimeRegistration previousTimeRegistration = getPreviousTimeRegistration(timeRegistration);
            if (previousTimeRegistration != null) {
                Log.d(LOG_TAG, "The previous time registrations ended on " + previousTimeRegistration.getEndTime());
                Log.d(LOG_TAG, "The new time registration starts on " + timeRegistration.getStartTime());
                Duration duration = DateUtils.TimeCalculator.calculateExactDuration(
                        ctx,
                        timeRegistration.getStartTime(),
                        previousTimeRegistration.getEndTime()
                );
                Log.d(LOG_TAG, "The duration between the previous end time and the new start time is " + duration);
                long durationMillis = duration.getMillis();
                Log.d(LOG_TAG, "The duration in milliseconds is " + durationMillis);
                if (durationMillis < 60000) {
                    Log.d(LOG_TAG, "Gap is less than 60 seconds, setting start time to end time of previous registration");
                    timeRegistration.setStartTime(previousTimeRegistration.getEndTime());
                }
            }
        }
        timeRegistration = dao.save(timeRegistration);
        return timeRegistration;
    }

    /**
     * {@inheritDoc}
     */
    public void update(TimeRegistration timeRegistration) {
        dao.update(timeRegistration);
    }

    /**
     * {@inheritDoc}
     */
    public TimeRegistration getLatestTimeRegistration() {
        return dao.getLatestTimeRegistration();
    }

    /**
     * {@inheritDoc}
     */
    public void remove(TimeRegistration timeRegistration) {
        dao.delete(timeRegistration);
    }

    /**
     * {@inheritDoc}
     */
    public TimeRegistration get(Integer id) {
        return dao.findById(id);
    }

    @Override
    public List<TimeRegistration> findAll(int lowerLimit, int maxRows) {
        List<TimeRegistration> timeRegistrations = dao.findAll(lowerLimit, maxRows);
        for(TimeRegistration timeRegistration : timeRegistrations) {
            Log.d(LOG_TAG, "Found timeregistration with ID: " + timeRegistration.getId() + " and according task with ID: " + timeRegistration.getTask().getId());
            taskDao.refresh(timeRegistration.getTask());
            projectDao.refresh(timeRegistration.getTask().getProject());
        }
        return timeRegistrations;
    }

    @Override
    public Long count() {
        return dao.count();
    }

    @Override
    public TimeRegistration getPreviousTimeRegistration(TimeRegistration timeRegistration) {
        return dao.getPreviousTimeRegistration(timeRegistration);
    }

    @Override
    public TimeRegistration getNextTimeRegistration(TimeRegistration timeRegistration) {
        return dao.getNextTimeRegistration(timeRegistration);
    }

    @Override
    public void fullyInitialize(TimeRegistration timeRegistration) {
        if (timeRegistration != null) {
            taskDao.refresh(timeRegistration.getTask());
            projectDao.refresh(timeRegistration.getTask().getProject());
        }
    }

    @Override
    public void removeAll() {
        dao.deleteAll();
    }

    @Override
    public long removeAllInRange(Date minBoundary, Date maxBoundary) {
        return dao.deleteAllInRange(minBoundary, maxBoundary);
    }

    @Override
    public boolean doesInterfereWithTimeRegistration(Date time) {
        return dao.doesInterfereWithTimeRegistration(time);
    }

    @Override
    public void refresh(TimeRegistration timeRegistration) {
        dao.refresh(timeRegistration);
    }

    @Override
    public boolean checkTimeRegistrationExisting(TimeRegistration timeRegistration) {
        if (timeRegistration.getId() != null) {
            TimeRegistration existingTimeRegistration = dao.findById(timeRegistration.getId());
            if (existingTimeRegistration != null) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean checkReloadTimeRegistration(TimeRegistration timeRegistration) {
        if (timeRegistration.getId() != null) {
            TimeRegistration existingTimeRegistration = dao.findById(timeRegistration.getId());
            if (existingTimeRegistration != null && existingTimeRegistration.isModifiedAfter(timeRegistration)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public TimeRegistration create(TimeRegistration timeRegistration) {
        return dao.save(timeRegistration);
    }
}
