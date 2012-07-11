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
import com.google.inject.Inject;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import eu.vranckaert.worktime.dao.ProjectDao;
import eu.vranckaert.worktime.dao.generic.GenericDaoImpl;
import eu.vranckaert.worktime.exceptions.CorruptProjectDataException;
import eu.vranckaert.worktime.model.Project;
import eu.vranckaert.worktime.utils.context.Log;

import java.sql.SQLException;
import java.util.List;


public class ProjectDaoImpl extends GenericDaoImpl<Project, Integer> implements ProjectDao {
    private static final String LOG_TAG = ProjectDaoImpl.class.getSimpleName();

    @Inject
    public ProjectDaoImpl(final Context context) {
        super(Project.class, context);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isNameAlreadyUsed(String projectName) {
        List<Project> projects = null;
        QueryBuilder<Project, Integer> qb = dao.queryBuilder();
        try {
            qb.where().eq("name", projectName);
            PreparedQuery<Project> pq = qb.prepare();
            projects = dao.query(pq);
        } catch (SQLException e) {
            Log.d(getContext(), LOG_TAG, "Could not execute the query... Returning false");
            return false;
        }

        if(projects == null || projects.size() == 0) {
            Log.d(getContext(), LOG_TAG, "The name is not yet used!");
            return false;
        }
        Log.d(getContext(), LOG_TAG, "The name is already in use!");
        return true;
    }

    public Project findDefaultProject() {
        List<Project> projects = null;

        QueryBuilder<Project, Integer> qb = dao.queryBuilder();
        try {
            qb.where().eq("defaultValue", true);
            PreparedQuery<Project> pq = qb.prepare();
            projects = dao.query(pq);
        } catch (SQLException e) {
            Log.e(getContext(), LOG_TAG, "Could not execute the query... Returning null.", e);
            return null;
        }

        if(projects == null || projects.size() == 0 || projects.size() > 1) {
            String message = null;
            if (projects == null || projects.size() == 0) {
                message = "The project data is corrupt. No default project is found!";
            } else {
                message = "The project data is corrupt. More than one default project is found in the database!";
            }
            Log.e(getContext(), LOG_TAG, message);
            throw new CorruptProjectDataException(message);
        } else {
            return projects.get(0);
        }
    }

    @Override
    public List<Project> findProjectsOnFinishedFlag(boolean finished) {
        QueryBuilder<Project, Integer> qb = dao.queryBuilder();
        try {
            qb.where().eq("finished", finished);
            PreparedQuery<Project> pq = qb.prepare();
            return dao.query(pq);
        } catch (SQLException e) {
            Log.e(getContext(), LOG_TAG, "Could not execute the query... Returning null.", e);
            return null;
        }
    }
}
