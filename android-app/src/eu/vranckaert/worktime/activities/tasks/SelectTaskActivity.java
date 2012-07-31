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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.inject.Inject;
import com.google.inject.internal.Nullable;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.comparators.task.TaskByNameComparator;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.model.Project;
import eu.vranckaert.worktime.model.Task;
import eu.vranckaert.worktime.service.ProjectService;
import eu.vranckaert.worktime.service.TaskService;
import eu.vranckaert.worktime.service.ui.WidgetService;
import eu.vranckaert.worktime.utils.preferences.Preferences;
import eu.vranckaert.worktime.utils.string.StringUtils;
import roboguice.activity.GuiceActivity;
import roboguice.inject.InjectExtra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 02/03/11
 * Time: 20:56
 */
public class SelectTaskActivity extends GuiceActivity {
    @Inject
    private ProjectService projectService;

    @Inject
    private TaskService taskService;

    @Inject
    private WidgetService widgetService;

    @InjectExtra(value = Constants.Extras.WIDGET_ID, optional = true)
    @Nullable
    private Integer widgetId;

    @InjectExtra(value = Constants.Extras.ONLY_SELECT, optional = true)
    private boolean onlySelect = false;

    @InjectExtra(value = Constants.Extras.ENABLE_SELECT_NONE_OPTION, optional = true)
    private boolean enableSelectNoneOption = false;

    @InjectExtra(value = Constants.Extras.UPDATE_WIDGET, optional = true)
    private boolean updateWidget = false;

    private List<Task> selectableTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Find all tasks and sort by name
        if (widgetId != null) {
            Project project = projectService.getSelectedProject(widgetId);
            if (Preferences.getSelectTaskHideFinished(SelectTaskActivity.this)) {
                selectableTasks = taskService.findNotFinishedTasksForProject(project);
            } else {
                selectableTasks = taskService.findTasksForProject(project);
            }
        } else {
            selectableTasks = taskService.findAll();
        }


        if (selectableTasks == null || selectableTasks.size() == 0) {
            showDialog(Constants.Dialog.NO_TASKS_AVAILABLE);
        } else if (selectableTasks.size() == 1) {
            if (Preferences.getWidgetAskForTaskSelectionIfOnlyOnePreference(SelectTaskActivity.this)) {
                showDialog(Constants.Dialog.CHOOSE_TASK);
            } else {
                Task task = selectableTasks.get(0);

                Intent resultValue = new Intent();
                resultValue.putExtra(Constants.Extras.TASK, task);
                setResult(RESULT_OK, resultValue);
                SelectTaskActivity.this.finish();
            }
        } else {
            showDialog(Constants.Dialog.CHOOSE_TASK);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;

        switch (id) {
            case Constants.Dialog.NO_TASKS_AVAILABLE: {
                AlertDialog.Builder alertNoTaskAvailable = new AlertDialog.Builder(this);
                alertNoTaskAvailable.setMessage(R.string.msg_no_tasks_available_choose_other_project)
                        .setCancelable(true)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                removeDialog(Constants.Dialog.NO_TASKS_AVAILABLE);
                                SelectTaskActivity.this.finish();
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                removeDialog(Constants.Dialog.NO_TASKS_AVAILABLE);
                                finish();
                            }
                        });
                dialog = alertNoTaskAvailable.create();
                break;
            }
            case Constants.Dialog.CHOOSE_TASK: {
                Collections.sort(selectableTasks, new TaskByNameComparator());
                final List<Task> availableTasks = selectableTasks;

                Task selectedTask = null;
                int selectedTaskIndex = -1;

                List<String> tasks = new ArrayList<String>();
                for (Task task : availableTasks) {
                    tasks.add(task.getName());
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.lbl_widget_title_select_task)
                       .setSingleChoiceItems(
                               StringUtils.convertListToArray(tasks),
                               selectedTaskIndex,
                               new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface, int index) {
                                        Task newSelectedTask = availableTasks.get(index);
                                        if (!onlySelect && widgetId != null)
                                            taskService.setSelectedTask(widgetId, newSelectedTask);

                                        if (updateWidget)
                                            widgetService.updateWidget(widgetId);

                                        Intent resultValue = new Intent();
                                        resultValue.putExtra(Constants.Extras.TASK, newSelectedTask);
                                        setResult(RESULT_OK, resultValue);
                                        SelectTaskActivity.this.finish();
                                    }
                               }
                       )
                       .setOnCancelListener(new DialogInterface.OnCancelListener() {
                           public void onCancel(DialogInterface dialogInterface) {
                               setResult(RESULT_CANCELED);
                               SelectTaskActivity.this.finish();
                           }
                       });

                if (enableSelectNoneOption) {
                    builder.setNeutralButton(R.string.lbl_widget_select_no_task_option, new AlertDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setResult(RESULT_OK);
                            SelectTaskActivity.this.finish();
                        }
                    });
                }
                dialog = builder.create();
                break;
            }
        }

        return dialog;
    }
}
