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

package eu.vranckaert.worktime.activities.preferences;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import com.google.inject.Inject;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.dao.ProjectDao;
import eu.vranckaert.worktime.dao.TaskDao;
import eu.vranckaert.worktime.dao.TimeRegistrationDao;
import eu.vranckaert.worktime.model.Project;
import eu.vranckaert.worktime.model.Task;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.utils.context.AsyncHelper;
import eu.vranckaert.worktime.utils.view.actionbar.RoboSherlockActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: 4/04/13
 * Time: 8:50
 *
 * @author Dirk Vranckaert
 */
public class CleanupCorruptDataPreferencesActivity extends RoboSherlockActivity {
    private static final String LOG_TAG = CleanupCorruptDataPreferencesActivity.class.getSimpleName();

    @Inject private TaskDao taskDao;
    @Inject private ProjectDao projectDao;
    @Inject private TimeRegistrationDao timeRegistrationDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cleanup_corrupt_data);

        setTitle(R.string.pref_cleanup_corrupt_data_category_title);

        AsyncHelper.start(new CleanupCorruptDataTask());
    }

    private class CleanupCorruptDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // Tasks
            List<Project> projects = projectDao.findAll();
            for (Project project : projects) {
                Log.d(LOG_TAG, "Checking tasks for project " + project.getName());
                Map<String, List<Integer>> taskMapping = new HashMap<String, List<Integer>>();
                List<Task> tasks = taskDao.findTasksForProject(project);
                for (Task task : tasks) {
                    Log.d(LOG_TAG, "Adding task with name " + task.getName() + " and id " + task.getId() + " to map");
                    if (taskMapping.containsKey(task.getName())) {
                        List<Integer> ids = taskMapping.get(task.getName());
                        ids.add(task.getId());
                        taskMapping.put(task.getName(), ids);
                    } else {
                        List<Integer> ids = new ArrayList<Integer>();
                        ids.add(task.getId());
                        taskMapping.put(task.getName(), ids);
                    }
                }

                for (Map.Entry<String, List<Integer>> entry : taskMapping.entrySet()) {
                    String name = entry.getKey();
                    List<Integer> ids = entry.getValue();

                    if (ids.size() > 1) {
                        Log.d(LOG_TAG, "Found more than one task with name " + name + ", will now check which one to be deleted!");
                        List<Integer> notDeletedTasks = new ArrayList<Integer>();
                        for (Integer taskId : ids) {
                            Log.d(LOG_TAG, "Checking task with id " + taskId);
                            Task task = taskDao.findById(taskId);
                            List<TimeRegistration> timeRegistrations = timeRegistrationDao.findTimeRegistrationsForTask(task);
                            if (timeRegistrations.size() == 0) {
                                Log.d(LOG_TAG, "No time registrations found for task, deleting now (" + taskId + ")");
                                taskDao.delete(task);
                            } else {
                                Log.d(LOG_TAG, "Found time registrations for task, cannot delete task (" + taskId + ")");
                                notDeletedTasks.add(taskId);
                            }
                        }

                        if (notDeletedTasks.size() > 1) {
                            Log.w(LOG_TAG, "More than one task is still in the DB with the name " + name + ". Ids are: " + notDeletedTasks.toArray());
                        }
                    } else {
                        Log.d(LOG_TAG, "Only one task found with name " + name + ", no further actions needed for this task");
                    }
                }
            }

            // Projects
            Map<String, List<Integer>> projectMapping = new HashMap<String, List<Integer>>();
            for (Project project : projects) {
                Log.d(LOG_TAG, "Adding project with name " + project.getName() + " and id " + project.getId() + " to map");
                if (projectMapping.containsKey(project.getName())) {
                    List<Integer> ids = projectMapping.get(project.getName());
                    ids.add(project.getId());
                    projectMapping.put(project.getName(), ids);
                } else {
                    List<Integer> ids = new ArrayList<Integer>();
                    ids.add(project.getId());
                    projectMapping.put(project.getName(), ids);
                }
            }

            for (Map.Entry<String, List<Integer>> entry : projectMapping.entrySet()) {
                String name = entry.getKey();
                List<Integer> ids = entry.getValue();

                if (ids.size() > 1) {
                    Log.d(LOG_TAG, "Found more than one project with name " + name + ", will now check which one to be deleted!");
                    List<Integer> notDeletedProjects = new ArrayList<Integer>();
                    for (Integer projectId : ids) {
                        Log.d(LOG_TAG, "Checking project with id " + projectId);
                        Project project = projectDao.findById(projectId);
                        List<Task> tasksForProject = taskDao.findTasksForProject(project);
                        if (tasksForProject.size() == 0) {
                            Log.d(LOG_TAG, "No tasks found for project, deleting now (" + projectId + ")");
                            projectDao.delete(project);
                        } else {
                            Log.d(LOG_TAG, "Found tasks for project, cannot delete project (" + projectId + ")");
                            notDeletedProjects.add(projectId);
                        }
                    }

                    if (notDeletedProjects.size() > 1) {
                        Log.w(LOG_TAG, "More than one project is still in the DB with the name " + name + ". Ids are: " + notDeletedProjects.toArray());
                    }
                } else {
                    Log.d(LOG_TAG, "Only one project found with name " + name + ", no further actions needed for this project");
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            finish();
        }
    }
}
