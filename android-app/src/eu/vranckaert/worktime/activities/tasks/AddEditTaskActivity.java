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
package eu.vranckaert.worktime.activities.tasks;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.inject.Inject;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.constants.TextConstants;
import eu.vranckaert.worktime.constants.TrackerConstants;
import eu.vranckaert.worktime.model.Project;
import eu.vranckaert.worktime.model.Task;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.service.TaskService;
import eu.vranckaert.worktime.service.TimeRegistrationService;
import eu.vranckaert.worktime.service.ui.StatusBarNotificationService;
import eu.vranckaert.worktime.service.ui.WidgetService;
import eu.vranckaert.worktime.utils.context.IntentUtil;
import eu.vranckaert.worktime.utils.tracker.AnalyticsTracker;
import roboguice.activity.GuiceActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

/**
 * User: DIRK VRANCKAERT
 * Date: 30/03/11
 * Time: 00:19
 */
public class AddEditTaskActivity extends GuiceActivity {
    private static final String LOG_TAG = AddEditTaskActivity.class.getSimpleName();

    @InjectView(R.id.project_name)
    private TextView projectName;

    @InjectView(R.id.task_name_required)
    private TextView taskNameRequiredMessage;

    @InjectView(R.id.task_name)
    private TextView taskName;

    @InjectView(R.id.title_refresh_progress)
    private ProgressBar progressBar;

    @InjectView(R.id.btn_save)
    private ImageView saveButton;

    @InjectView(R.id.title_text)
    private TextView titleText;

    @InjectExtra(Constants.Extras.PROJECT)
    private Project project;

    @InjectExtra(value = Constants.Extras.TASK, optional = true)
    private Task editTask;

    @Inject
    private TaskService taskService;

    @Inject
    private WidgetService widgetService;

    @Inject
    private StatusBarNotificationService statusBarNotificationService;

    @Inject
    private TimeRegistrationService timeRegistrationService;

    private AnalyticsTracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_edit_task);

        tracker = AnalyticsTracker.getInstance(getApplicationContext());
        tracker.trackPageView(TrackerConstants.PageView.ADD_EDIT_TASK_ACTIVITY);

        if (!inUpdateMode()) {
            Log.d(LOG_TAG, "Adding task for project " + project.getName());
        } else {
            Log.d(LOG_TAG, "Editing task for project " + project.getName());
            titleText.setText(R.string.lbl_edit_task_title);
            taskName.setText(editTask.getName());
        }
        projectName.setText(TextConstants.SPACE + project.getName());
    }

    public void onHomeClick(View view) {
        IntentUtil.goHome(this);
    }

    public void onSaveClick(View view) {
        if (taskName.getText().length() == 0) {
            taskNameRequiredMessage.setVisibility(View.VISIBLE);
        } else {
            taskNameRequiredMessage.setVisibility(View.GONE);
            saveOrUpdateTaskForProject(project, taskName.getText().toString());
        }
    }

    /**
     * Creates the task based on the provided project and name of the task. Before saving the task the save button is
     * removed and a progress bar is shown.
     * @param project The {@link Project} for which to create a task.
     * @param taskNameText The name of the task to create.
     */
    private void saveOrUpdateTaskForProject(Project project, String taskNameText) {
        saveButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        Task task;
        if (!inUpdateMode()) {
            task = new Task();
            task.setProject(project);
        } else {
            task = editTask;
        }
        task.setName(taskNameText);

        if (!inUpdateMode()) {
            taskService.save(task);
            tracker.trackEvent(
                TrackerConstants.EventSources.ADD_EDIT_TASK_ACTIVITY,
                TrackerConstants.EventActions.ADD_TASK
            );
            Log.d(LOG_TAG, "New task persisted");
        } else {
            taskService.update(task);
            tracker.trackEvent(
                    TrackerConstants.EventSources.ADD_EDIT_TASK_ACTIVITY,
                    TrackerConstants.EventActions.EDIT_TASK
            );
            Log.d(LOG_TAG, "Task with id " + task.getId() + " and name " + task.getName() + " is updated");
            Log.d(LOG_TAG, "About to update the wiget and notifications");
            TimeRegistration tr = timeRegistrationService.getLatestTimeRegistration();
            if (tr != null && tr.getTask().getId().equals(task.getId())) {
                widgetService.updateWidget();
                statusBarNotificationService.addOrUpdateNotification(tr);
            }
        }

        setResult(RESULT_OK);
        finish();
    }

    /**
     * Checks if the activity is in update mode. If not it's create mode!
     * @return {@link Boolean#TRUE} if the task is about to be updated, {@link Boolean#FALSE} if in creation mode.
     */
    private boolean inUpdateMode() {
        if (editTask == null || editTask.getId() < 0) {
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tracker.stopSession();
    }
}
