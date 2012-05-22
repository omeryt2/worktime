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

package eu.vranckaert.worktime.dao.impl;

import android.content.Context;
import android.util.Log;
import com.google.inject.Inject;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import eu.vranckaert.worktime.comparators.timeregistration.TimeRegistrationDescendingByStartdate;
import eu.vranckaert.worktime.dao.TimeRegistrationDao;
import eu.vranckaert.worktime.dao.generic.GenericDaoImpl;
import eu.vranckaert.worktime.dao.utils.DatabaseHelper;
import eu.vranckaert.worktime.model.Task;
import eu.vranckaert.worktime.model.TimeRegistration;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 05/02/11
 * Time: 17:31
 */
public class TimeRegistrationDaoImpl extends GenericDaoImpl<TimeRegistration, Integer> implements TimeRegistrationDao{
    private static final String LOG_TAG = TimeRegistrationDaoImpl.class.getSimpleName();

    @Inject
    public TimeRegistrationDaoImpl(final Context context) {
        super(TimeRegistration.class, context);
    }

    /**
     * {@inheritDoc}
     */
    public TimeRegistration getLatestTimeRegistration() {
        List<TimeRegistration> timeRegistrations = findAll();
        if(timeRegistrations.size() > 0) {
            Collections.sort(timeRegistrations, new TimeRegistrationDescendingByStartdate());
            return timeRegistrations.get(0);
        } else {
            return null;
        }
    }

    public List<TimeRegistration> findTimeRegistrationsForTask(Task task) {
        QueryBuilder<TimeRegistration,Integer> qb = dao.queryBuilder();
        try {
            qb.where().eq("taskId", task.getId());
            PreparedQuery<TimeRegistration> pq = qb.prepare();
            return dao.query(pq);
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Could not execute the query...");
            throwFatalException(e);
        }

        return null;
    }

    public List<TimeRegistration> findTimeRegistrationsForTaks(List<Task> tasks) {
        List<Integer> taskIds = new ArrayList<Integer>();
        for (Task task : tasks) {
            taskIds.add(task.getId());
        }

        QueryBuilder<TimeRegistration,Integer> qb = dao.queryBuilder();
        try {
            qb.where().in("taskId", taskIds);
            PreparedQuery<TimeRegistration> pq = qb.prepare();
            return dao.query(pq);
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Could not execute the query...");
            throwFatalException(e);
        }

        return null;
    }

    public List<TimeRegistration> getTimeRegistrations(Date startDate, Date endDate, List<Task> tasks) {
        List<Integer> taskIds = null;
        if (tasks != null && !tasks.isEmpty()) {
            Log.d(LOG_TAG, tasks.size() + " task(s) are taken into account while querying...");
            for (Task task : tasks) {
                if (taskIds == null) {
                    taskIds = new ArrayList<Integer>();
                }
                taskIds.add(task.getId());
            }
        }

        QueryBuilder<TimeRegistration,Integer> qb = dao.queryBuilder();


        Calendar cal = Calendar.getInstance();
        cal.setTime(endDate);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        endDate = cal.getTime();

        endDate = DatabaseHelper.convertDateToSqliteDate(endDate);
        startDate = DatabaseHelper.convertDateToSqliteDate(startDate);
        boolean includeOngoingTimeRegistration = false;
        Date now = new Date();
        if (endDate.after(now)) {
            Log.e(LOG_TAG, "Ongoing time registration should be included in reporting result...");
            includeOngoingTimeRegistration = true;
        }

        Where where = qb.where();
        try {
            where.ge("startTime", startDate);
            if (includeOngoingTimeRegistration) {
                Where orClause = where.lt("endTime", endDate).or().isNull("endTime");
                where.and(where, orClause);
            } else {
                where.and().le("endTime", endDate);
            }
            if (taskIds != null && !taskIds.isEmpty()) {
                where.and().in("taskId", taskIds);
            }
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Could not build the dates- and tasks-where-clause in the query...");
            throwFatalException(e);
        }
        qb.setWhere(where);

        try {
            PreparedQuery<TimeRegistration> pq = qb.prepare();
            Log.d(LOG_TAG, "Prepared query: " + pq.toString());
            return dao.query(pq);
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Could not execute the query...");
            throwFatalException(e);
        }

        return null;
    }

    @Override
    public List<TimeRegistration> findAll(int lowerLimit, int maxRows) {
        QueryBuilder<TimeRegistration,Integer> qb = dao.queryBuilder();
        try {
            Log.d(LOG_TAG, "The starting row for the query is " + lowerLimit);
            Log.d(LOG_TAG, "The maximum number of rows to load is " + maxRows);
            qb.offset(Long.valueOf(lowerLimit));
            qb.limit(Long.valueOf(maxRows));
            qb.orderBy("startTime", false);
            PreparedQuery<TimeRegistration> pq = qb.prepare();
            Log.d(LOG_TAG, pq.toString());
            return dao.query(pq);
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Could not execute the query...");
            throwFatalException(e);
        }
        return null;
    }

    @Override
    public TimeRegistration getPreviousTimeRegistration(TimeRegistration timeRegistration) {
        QueryBuilder<TimeRegistration,Integer> qb = dao.queryBuilder();
        try {
            qb.limit(1L);
            qb.orderBy("startTime", false);

            Where where = qb.where();
            where.le("endTime", timeRegistration.getStartTime());
            qb.setWhere(where);

            PreparedQuery<TimeRegistration> pq = qb.prepare();
            Log.d(LOG_TAG, pq.toString());
            return dao.queryForFirst(pq);
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Could not execute the query...");
            throwFatalException(e);
        }
        return null;
    }

    @Override
    public TimeRegistration getNextTimeRegistration(TimeRegistration timeRegistration) {
        if (timeRegistration.getEndTime() == null) {
            return null;
        }

        QueryBuilder<TimeRegistration,Integer> qb = dao.queryBuilder();
        try {
            qb.limit(1L);
            qb.orderBy("startTime", true);

            Where where = qb.where();
            where.ge("startTime", timeRegistration.getEndTime());
            qb.setWhere(where);

            PreparedQuery<TimeRegistration> pq = qb.prepare();
            Log.d(LOG_TAG, pq.toString());
            return dao.queryForFirst(pq);
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Could not execute the query...");
            throwFatalException(e);
        }

        return null;
    }
}
