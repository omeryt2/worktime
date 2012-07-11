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

package eu.vranckaert.worktime.service.ui.impl;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import com.google.inject.Inject;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.activities.HomeActivity;
import eu.vranckaert.worktime.activities.timeregistrations.TimeRegistrationActionActivity;
import eu.vranckaert.worktime.activities.widget.SelectProjectActivity;
import eu.vranckaert.worktime.activities.widget.StartTimeRegistrationActivity;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.dao.WidgetConfigurationDao;
import eu.vranckaert.worktime.dao.impl.WidgetConfigurationDaoImpl;
import eu.vranckaert.worktime.model.Project;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.model.WidgetConfiguration;
import eu.vranckaert.worktime.providers.WorkTimeWidgetProvider_1x1;
import eu.vranckaert.worktime.providers.WorkTimeWidgetProvider_2x2;
import eu.vranckaert.worktime.service.ProjectService;
import eu.vranckaert.worktime.service.TaskService;
import eu.vranckaert.worktime.service.TimeRegistrationService;
import eu.vranckaert.worktime.service.impl.ProjectServiceImpl;
import eu.vranckaert.worktime.service.impl.TaskServiceImpl;
import eu.vranckaert.worktime.service.impl.TimeRegistrationServiceImpl;
import eu.vranckaert.worktime.service.ui.WidgetService;
import eu.vranckaert.worktime.utils.context.Log;
import eu.vranckaert.worktime.utils.widget.WidgetUtil;

import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 09/02/11
 * Time: 19:13
 */
public class WidgetServiceImpl implements WidgetService {
    private static final String LOG_TAG = WidgetServiceImpl.class.getName();

    @Inject
    private Context ctx;

    @Inject
    private TimeRegistrationService timeRegistrationService;

    @Inject
    private TaskService taskService;

    @Inject
    private ProjectService projectService;

    @Inject
    private WidgetConfigurationDao widgetConfigurationDao;

    private RemoteViews views;

    public WidgetServiceImpl(Context ctx) {
        this.ctx = ctx;
        getServices(ctx);
        getDaos(ctx);
    }

    /**
     * Default constructor required by RoboGuice!
     */
    public WidgetServiceImpl() {}

    @Override
    public void updateAllWidgets() {
        Log.d(ctx, LOG_TAG, "Updating all widgets...");
        updateWidgets(WidgetUtil.getAllWidgetIds(ctx));
    }

    @Override
    public void updateWidgets(List<Integer> widgetIds) {
        for (int widgetId : widgetIds) {
            updateWidget(widgetId);
        }
    }

    @Override
    public void updateWidgetsForProject(Project project) {
        List<WidgetConfiguration> wcs = widgetConfigurationDao.findPerProjectId(project.getId());

        for (WidgetConfiguration wc : wcs) {
            updateWidget(wc.getWidgetId());
        }
    }

    @Override
    public void updateWidget(int id) {
        AppWidgetManager awm = AppWidgetManager.getInstance(ctx);
        AppWidgetProviderInfo info = awm.getAppWidgetInfo(id);
        ComponentName componentName = info.provider;
        if (componentName.getClassName().equals(WorkTimeWidgetProvider_1x1.class.getName())) {
            updateWidget1x1(id);
        } else if (componentName.getClassName().equals(WorkTimeWidgetProvider_2x2.class.getName())) {
            updateWidget2x2(id);
        }
    }

    @Override
    public void updateWidget1x1(int widgetId) {
        Log.d(ctx, LOG_TAG, "Updating widget (1x1) with id " + widgetId);

        getViews(ctx, R.layout.worktime_appwidget_1x1);

        // TODO widget specific stuff...

        commitView(ctx, widgetId, views, WorkTimeWidgetProvider_2x2.class);
    }

    @Override
    public void updateWidget2x2(int widgetId) {
        Log.d(ctx, LOG_TAG, "Updating widget (2x2) with id " + widgetId);

        getViews(ctx, R.layout.worktime_appwidget_2x2);

        boolean timeRegistrationStarted = false;

        Long numberOfTimeRegs = timeRegistrationService.count();

        TimeRegistration lastTimeRegistration = null;
        if(numberOfTimeRegs > 0L) {
            lastTimeRegistration = timeRegistrationService.getLatestTimeRegistration();
            timeRegistrationService.fullyInitialize(lastTimeRegistration);
            Log.d(ctx, LOG_TAG, "The last time registration has ID " + lastTimeRegistration.getId());
        } else {
            Log.d(ctx, LOG_TAG, "No timeregstrations found yet!");
        }

        //Update the selected project
        Project selectedProject = projectService.getSelectedProject(widgetId);
        views.setCharSequence(R.id.widget_projectname, "setText", selectedProject.getName());

        if(numberOfTimeRegs == 0L || (lastTimeRegistration != null &&
                (!lastTimeRegistration.isOngoingTimeRegistration() || !lastTimeRegistration.getTask().getProject().getId().equals(selectedProject.getId())) )) {
            Log.d(ctx, LOG_TAG, "No timeregistrations found yet or it's an ended timeregistration");
            views.setCharSequence(R.id.widget_actionbtn, "setText", ctx.getString(R.string.btn_widget_start));
            //Enable on click for the start button
            Log.d(ctx, LOG_TAG, "Couple the start button to an on click action");
            startBackgroundWorkActivity(ctx, R.id.widget_actionbtn, StartTimeRegistrationActivity.class, null, widgetId);
        } else if(lastTimeRegistration != null && lastTimeRegistration.isOngoingTimeRegistration() && lastTimeRegistration.getTask().getProject().getId().equals(selectedProject.getId())) {
            Log.d(ctx, LOG_TAG, "This is an ongoing time registration");
            views.setCharSequence(R.id.widget_actionbtn, "setText", ctx.getString(R.string.btn_widget_stop));
            //Enable on click for the stop button
            Log.d(ctx, LOG_TAG, "Couple the stop button to an on click action.");
            startBackgroundWorkActivity(ctx, R.id.widget_actionbtn, TimeRegistrationActionActivity.class, lastTimeRegistration, widgetId);
            timeRegistrationStarted = true;
        }

        //Enable on click for the entire widget to open the app
        Log.d(ctx, LOG_TAG, "Couple the widget background to an on click action. On click opens the home activity");
        Intent homeAppIntent = new Intent(ctx, HomeActivity.class);
        PendingIntent homeAppPendingIntent = PendingIntent.getActivity(ctx, 0, homeAppIntent, 0);
        views.setOnClickPendingIntent(R.id.widget, homeAppPendingIntent);

        //Enable on click for the widget title to open the app if a registration is just started, or to open the
        //"select project" popup to change the selected project.
        Log.d(ctx, LOG_TAG, "Couple the widget title background to an on click action.");
        if (timeRegistrationStarted) {
            Log.d(ctx, LOG_TAG, "On click opens the home activity");
            views.setOnClickPendingIntent(R.id.widget_bgtop, homeAppPendingIntent);
        } else {
            Log.d(ctx, LOG_TAG, "On click opens a chooser-dialog for selecting the a project");
            startBackgroundWorkActivity(ctx, R.id.widget_bgtop, SelectProjectActivity.class, null, widgetId);
        }

        commitView(ctx, widgetId, views, WorkTimeWidgetProvider_2x2.class);
    }

    @Override
    public void removeWidget(int widgetId) {
        WidgetConfiguration wc = widgetConfigurationDao.findById(widgetId);
        if (wc != null) {
            widgetConfigurationDao.delete(wc);
            Log.d(ctx, LOG_TAG, "Widget configuration for widget with id " + widgetId + " has been removed");
        } else {
            Log.d(ctx, LOG_TAG, "No widget configuration found for widget-id: " + widgetId);
        }
    }

    /**
     * Starts an activity that should do something in the background after clicking a button on the widget. That doesn't
     * mean that the activity cannot ask the user for any input/choice/... It only means that the launched
     * {@link Intent} by default enables on flag: {@link Intent#FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS} which forces the
     * activity to not be shown in the recent launched apps/activities. Other flags can be defined in the method call.
     * @param ctx The widget's context.
     * @param resId The resource id of the view on the widget on which to bind the on click action.
     * @param activity The activity that will do some background processing.
     * @param extraFlags Extra flags for the activities.
     */
    private void startBackgroundWorkActivity(Context ctx, int resId, Class<? extends Activity> activity, TimeRegistration timeRegistration, int widgetId, int... extraFlags) {
        int defaultFlags = Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK;

        Intent intent = new Intent(ctx, activity);
        if (timeRegistration != null)
            intent.putExtra(Constants.Extras.TIME_REGISTRATION, timeRegistration);
        intent.putExtra(Constants.Extras.WIDGET_ID, widgetId);
        intent.setFlags(defaultFlags);

        if(extraFlags != null) {
            for (int flag : extraFlags) {
                if (flag != defaultFlags) {
                    intent.setFlags(flag);
                }
            }
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(resId, pendingIntent);
    }

    /**
     * Every {@link RemoteViews} that is updated should be committed!
     * @param ctx The context.
     * @param widgetId The id of the widget to be updated.
     * @param updatedView The updated {@link RemoteViews}.
     * @param clazz The implementation class of the {@link AppWidgetProvider}.
     */
    private void commitView(Context ctx, int widgetId, RemoteViews updatedView, Class clazz) {
        Log.d(ctx, LOG_TAG, "Committing update view...");
        AppWidgetManager mgr = AppWidgetManager.getInstance(ctx);
        mgr.updateAppWidget(widgetId, updatedView);
        Log.d(ctx, LOG_TAG, "Updated view committed!");
    }

    /**
     * Find the views to be updated for the widget with the specified view resource id.
     * @param ctx The widget's context.
     * @param viewResId The view resource id of the widget that is going to be updated.
     */
    private void getViews(Context ctx, int viewResId) {
        this.views = new RemoteViews(ctx.getPackageName(), viewResId);
        Log.d(ctx, LOG_TAG, "I just got the view which we'll start updating!");
    }

    /**
     * Create all the required DAO instances.
     * @param ctx The widget's context.
     */
    private void getDaos(Context ctx) {
        this.widgetConfigurationDao = new WidgetConfigurationDaoImpl(ctx);
        Log.d(ctx, LOG_TAG, "DAOS are loaded...");
    }

    /**
     * Create all the required service instances.
     * @param ctx The widget's context.
     */
    private void getServices(Context ctx) {
        this.projectService = new ProjectServiceImpl(ctx);
        this.taskService = new TaskServiceImpl(ctx);
        this.timeRegistrationService = new TimeRegistrationServiceImpl(ctx);
        Log.d(ctx, LOG_TAG, "Services are loaded...");
    }
}
