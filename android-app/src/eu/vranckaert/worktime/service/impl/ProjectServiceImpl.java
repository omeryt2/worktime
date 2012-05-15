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
import android.util.Log;
import com.google.inject.Inject;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.dao.ProjectDao;
import eu.vranckaert.worktime.dao.TaskDao;
import eu.vranckaert.worktime.dao.TimeRegistrationDao;
import eu.vranckaert.worktime.dao.impl.ProjectDaoImpl;
import eu.vranckaert.worktime.dao.impl.TimeRegistrationDaoImpl;
import eu.vranckaert.worktime.exceptions.AtLeastOneProjectRequiredException;
import eu.vranckaert.worktime.exceptions.ProjectStillInUseException;
import eu.vranckaert.worktime.model.Project;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.service.ProjectService;
import eu.vranckaert.worktime.utils.preferences.Preferences;

import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 06/02/11
 * Time: 04:20
 */
public class ProjectServiceImpl implements ProjectService {
    private static final String LOG_TAG = ProjectServiceImpl.class.getSimpleName();

    @Inject
    private ProjectDao dao;

    @Inject
    private Context ctx;

    @Inject
    private TimeRegistrationDao timeRegistrationDao;

    @Inject
    private TaskDao taskDao;

    /**
     * Enables the use of this service outside of RoboGuice!
     * @param ctx The context to insert
     */
    public ProjectServiceImpl(Context ctx) {
        this.ctx = ctx;
        dao = new ProjectDaoImpl(ctx);
        timeRegistrationDao = new TimeRegistrationDaoImpl(ctx);
    }

    /**
     * Default constructor required by RoboGuice!
     */
    public ProjectServiceImpl() {}

    /**
     * {@inheritDoc}
     */
    public Project save(Project project) {
        return dao.save(project);
    }

    /**
     * {@inheritDoc}
     */
    public List<Project> findAll() {
        return dao.findAll();
    }

    /**
     * {@inheritDoc}
     */
    public void remove(Project project) throws AtLeastOneProjectRequiredException, ProjectStillInUseException {
        if (dao.count() > 1) {
            Long taskCount = taskDao.count();
            if (taskCount > 0) {
                throw new ProjectStillInUseException("The project is still linked with " + taskCount + " tasks! Remove them first!");
            }
            dao.delete(project);
            if (project.isDefaultValue()) {
                changeDefaultProjectUponProjectRemoval(project);
            }
            checkSelectedProjectUponProjectRemoval(project);
        } else {
            throw new AtLeastOneProjectRequiredException("At least on project is required so this project cannot be removed");
        }
    }

    /**
     * Change the default project upon removing a project which is set to be the default.
     * @param projectForRemoval The default project to be removed.
     */
    private void changeDefaultProjectUponProjectRemoval(Project projectForRemoval) {
        if (!projectForRemoval.isDefaultValue()) {
            return;
        }
        Log.d(LOG_TAG, "Trying to remove project " + projectForRemoval.getName() + " while it's a default project");

        List<Project> availableProjects = dao.findAll();
        availableProjects.remove(projectForRemoval);

        if (availableProjects.size() > 0) {
            Log.d(LOG_TAG, availableProjects.size() + " projects found to be the new default project");
            Project newDefaultProject = availableProjects.get(0);
            Log.d(LOG_TAG, "New default project is " + newDefaultProject.getName());
            newDefaultProject.setDefaultValue(true);
            dao.update(newDefaultProject);
        }
    }

    /**
     * Checks if the removed project was the selected project to link to new {@link TimeRegistration} instances. If so
     * set the default project as selected project.
     * @param project The project to check for.
     */
    private void checkSelectedProjectUponProjectRemoval(Project project) {
        int projectId = Preferences.getSelectedProjectId(ctx);
        if (project.getId() == projectId) {
            setSelectedProject(dao.findDefaultProject());
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isNameAlreadyUsed(String projectName) {
        return dao.isNameAlreadyUsed(projectName);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isNameAlreadyUsed(String projectName, Project excludedProject) {
        if (excludedProject.getName().equals(projectName)) {
            return false;
        }
        return dao.isNameAlreadyUsed(projectName);
    }

    /**
     * {@inheritDoc}
     */
    public Project getSelectedProject() {
        int projectId = Preferences.getSelectedProjectId(ctx);
        Log.d(LOG_TAG, "Selected project id found is " + projectId);

        if (projectId == Constants.Preferences.SELECTED_PROJECT_ID_DEFAULT_VALUE) {
            Log.d(LOG_TAG, "No project is found yet. Get the default project.");
            Project project = dao.findDefaultProject();
            Log.d(LOG_TAG, "Set the default project in the preferences to be the selected project");
            Preferences.setSelectedProjectId(ctx, project.getId());
            return project;
        } else {
            Project project = dao.findById(projectId);
            if (project == null) {
                project = dao.findDefaultProject();
            }
            Log.d(LOG_TAG, "The selected project has id " + project.getId() + " and name " + project.getName());
            return project;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setSelectedProject(Project project) {
        Preferences.setSelectedProjectId(ctx, project.getId());
    }

    /**
     * {@inheritDoc}
     */
    public Project update(Project project) {
        return dao.update(project);
    }

    /**
     * {@inheritDoc}
     */
    public void refresh(Project project) {
        dao.refresh(project);
    }

    /**
     * {@inheritDoc}
     */
    public List<Project> findUnfinishedProjects() {
        return dao.findProjectsOnFinishedFlag(false);
    }

    /**
     * {@inheritDoc}
     */
    public Project changeDefaultProjectUponProjectMarkedFinished(Project projectMarkedFinished) {
        if (!projectMarkedFinished.isDefaultValue()) {
            return dao.findDefaultProject();
        }
        Log.d(LOG_TAG, "Trying to mark project " + projectMarkedFinished.getName() + " finished while it's a default project");

        List<Project> availableProjects = findUnfinishedProjects();
        availableProjects.remove(projectMarkedFinished);

        projectMarkedFinished.setDefaultValue(false);
        dao.update(projectMarkedFinished);

        if (availableProjects.size() > 0) {
            Log.d(LOG_TAG, availableProjects.size() + " projects found to be the new default project");
            Project newDefaultProject = availableProjects.get(0);
            Log.d(LOG_TAG, "New default project is " + newDefaultProject.getName());
            newDefaultProject.setDefaultValue(true);
            dao.update(newDefaultProject);
            return newDefaultProject;
        }
        return dao.findDefaultProject();
    }
}
