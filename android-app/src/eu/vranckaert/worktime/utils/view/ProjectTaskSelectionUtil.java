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

package eu.vranckaert.worktime.utils.view;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.comparators.project.ProjectByNameComparator;
import eu.vranckaert.worktime.comparators.task.TaskByNameComparator;
import eu.vranckaert.worktime.model.Project;
import eu.vranckaert.worktime.model.Task;
import eu.vranckaert.worktime.service.ProjectService;
import eu.vranckaert.worktime.service.TaskService;
import eu.vranckaert.worktime.service.impl.ProjectServiceImpl;
import eu.vranckaert.worktime.service.impl.TaskServiceImpl;
import eu.vranckaert.worktime.utils.preferences.Preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 21/05/13
 * Time: 13:12
 */
public class ProjectTaskSelectionUtil {
    private Activity activity;

    private ProjectService projectService;
    private TaskService taskService;
    private Spinner projectSpinner;
    private Spinner taskSpinner;

    private Project selectedProject;
    private Task selectedTask;

    private List<Project> availableProjects;
    private List<Task> availableTasks;
    private Task initialTask = null;
    private boolean projectTaskTriggerEnabled = true;

    private ProjectTaskSelectionUtil() {}

    public static ProjectTaskSelectionUtil getInstance(Activity activity) {
        return getInstance(activity, null);
    }

    public static ProjectTaskSelectionUtil getInstance(Activity activity, Task task) {
        ProjectTaskSelectionUtil util = new ProjectTaskSelectionUtil();

        util.activity = activity;

        util.projectService = new ProjectServiceImpl(activity);
        util.taskService = new TaskServiceImpl(activity);
        util.projectSpinner = (Spinner) activity.findViewById(R.id.project_selection);
        util.taskSpinner = (Spinner) activity.findViewById(R.id.task_selection);
        util.taskSpinner.setEnabled(false);

        Project project = null;
        if (task != null) {
            util.taskService.refresh(task);
            util.projectService.refresh(task.getProject());
            project = task.getProject();
        }

        util.projectTaskTriggerEnabled = false;
        util.initialTask = task;
        List<Project> projects = util.setupProjectTaskSelection(project);
        List<Task> tasks = util.setupTaskSelection(util.selectedProject, task);
        util.setupSelectionListeners();

        return util;
    }

    private List<Project> setupProjectTaskSelection(Project project) {
        List<String> projectNames = new ArrayList<String>();
        List<Project> selectableProjects;
        if (Preferences.getSelectProjectHideFinished(activity)) {
            selectableProjects = projectService.findUnfinishedProjects();
        } else {
            selectableProjects = projectService.findAll();
        }
        availableProjects = selectableProjects;
        Collections.sort(availableProjects, new ProjectByNameComparator());
        int selectedProjectIndex = -1;
        for (Project mProject : availableProjects) {
            projectNames.add(mProject.getName());
            if (project != null && mProject.getId().equals(project.getId())) {
                selectedProjectIndex = availableProjects.indexOf(mProject);
            }
        }
        ArrayAdapter<String> projectAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, projectNames);
        projectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        projectSpinner.setAdapter(projectAdapter);

        if (project != null) {
            projectSpinner.setSelection(selectedProjectIndex);
            selectedProject = project;
        } else if (availableProjects.size() > 0) {
            projectSpinner.setSelection(0);
            selectedProject = availableProjects.get(0);
        }

        return availableProjects;
    }

    private List<Task> setupTaskSelection(Project project, Task task) {
        taskSpinner.setEnabled(true);

        List<String> taskNames = new ArrayList<String>();
        List<Task> selectableTasks;
        if (Preferences.getSelectTaskHideFinished(activity)) {
            selectableTasks = taskService.findNotFinishedTasksForProject(project);
        } else {
            selectableTasks = taskService.findTasksForProject(project);
        }
        availableTasks = selectableTasks;
        Collections.sort(availableTasks, new TaskByNameComparator());
        int selectedTaskIndex = -1;
        for (Task mTask : availableTasks) {
            taskNames.add(mTask.getName());
            if (task != null && mTask.getId().equals(task.getId())) {
                selectedTaskIndex = availableTasks.indexOf(mTask);
            }
        }
        ArrayAdapter<String> taskAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, taskNames);
        taskAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        taskSpinner.setAdapter(taskAdapter);

        if (task != null) {
            taskSpinner.setSelection(selectedTaskIndex);
            selectedTask = task;
        } else if (availableTasks.size() > 0) {
            taskSpinner.setSelection(0);
            selectedTask = availableTasks.get(0);
        }

        return availableTasks;
    }

    private void setupSelectionListeners() {
        projectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedProject = availableProjects.get(position);
                if (projectTaskTriggerEnabled) {
                    List<Task> tasks = setupTaskSelection(selectedProject, null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        taskSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTask = availableTasks.get(position);
                if (initialTask != null && selectedTask.getId().equals(initialTask.getId())) {
                    projectTaskTriggerEnabled = true;
                } else if (initialTask == null) {
                    projectTaskTriggerEnabled = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public Project getSelectedProject() {
        return selectedProject;
    }

    public Task getSelectedTask() {
        return selectedTask;
    }
}
