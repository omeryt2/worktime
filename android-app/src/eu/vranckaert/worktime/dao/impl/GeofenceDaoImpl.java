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

package eu.vranckaert.worktime.dao.impl;

import android.content.Context;
import com.google.inject.Inject;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import eu.vranckaert.worktime.dao.GeofenceDao;
import eu.vranckaert.worktime.dao.SyncRemovalCacheDao;
import eu.vranckaert.worktime.dao.generic.GenericDaoImpl;
import eu.vranckaert.worktime.model.Task;
import eu.vranckaert.worktime.model.trigger.GeofenceTrigger;
import eu.vranckaert.worktime.utils.context.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 21/05/13
 * Time: 7:57
 */
public class GeofenceDaoImpl extends GenericDaoImpl<GeofenceTrigger, Integer> implements GeofenceDao {
    private static final String LOG_TAG = GeofenceDaoImpl.class.getSimpleName();

    private SyncRemovalCacheDao syncRemovalCache;

    @Inject
    public GeofenceDaoImpl(final Context context) {
        super(GeofenceTrigger.class, context);
    }

    @Override
    public int getGeofencesByNameCount(String name) {
        int rowCount = 0;
        List<String[]> results = null;
        try {
            GenericRawResults rawResults = dao.queryRaw("select count(*) from GeofenceTrigger where name = '" + name + "'");
            results = rawResults.getResults();
        } catch (SQLException e) {
            throwFatalException(e);
        }

        if (results != null && results.size() > 0) {
            rowCount = Integer.parseInt(results.get(0)[0]);
        }

        Log.d(getContext(), LOG_TAG, "Rowcount: " + rowCount);

        return rowCount;
    }

    @Override
    public List<GeofenceTrigger> findAllNonExpired() {
        QueryBuilder<GeofenceTrigger,Integer> qb = dao.queryBuilder();
        try {
            qb.where().isNull("expirationDate").or().ge("expirationDate", new Date());
            PreparedQuery<GeofenceTrigger> pq = qb.prepare();
            return dao.query(pq);
        } catch (SQLException e) {
            Log.e(getContext(), LOG_TAG, "Could not start the query...");
            throwFatalException(e);
        }

        return null;
    }

    @Override
    public GeofenceTrigger findGeofenceTriggerByGeofenceRequestId(String requestId) {
        QueryBuilder<GeofenceTrigger,Integer> qb = dao.queryBuilder();
        try {
            qb.where().eq("geofenceRequestId", requestId);
            PreparedQuery<GeofenceTrigger> pq = qb.prepare();
            return dao.queryForFirst(pq);
        } catch (SQLException e) {
            Log.e(getContext(), LOG_TAG, "Could not start the query...");
            throwFatalException(e);
        }

        return null;
    }

    @Override
    public List<GeofenceTrigger> findGeoFencesForTask(Task task) {
        QueryBuilder<GeofenceTrigger,Integer> qb = dao.queryBuilder();
        try {
            qb.where().eq("taskId", task.getId());
            PreparedQuery<GeofenceTrigger> pq = qb.prepare();
            return dao.query(pq);
        } catch (SQLException e) {
            Log.e(getContext(), LOG_TAG, "Could not start the query...");
            throwFatalException(e);
        }

        return null;
    }

    @Override
    public List<GeofenceTrigger> findGeoFencesForTasks(List<Task> tasks) {
        List<Integer> taskIds = new ArrayList<Integer>();
        for (Task task : tasks) {
            taskIds.add(task.getId());
        }

        QueryBuilder<GeofenceTrigger,Integer> qb = dao.queryBuilder();
        try {
            qb.where().in("taskId", taskIds);
            PreparedQuery<GeofenceTrigger> pq = qb.prepare();
            return dao.query(pq);
        } catch (SQLException e) {
            Log.e(getContext(), LOG_TAG, "Could not start the query...");
            throwFatalException(e);
        }

        return null;
    }
}
