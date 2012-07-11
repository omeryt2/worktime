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

package eu.vranckaert.worktime.providers;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import eu.vranckaert.worktime.service.ui.StatusBarNotificationService;
import eu.vranckaert.worktime.service.ui.WidgetService;
import eu.vranckaert.worktime.service.ui.impl.StatusBarNotificationServiceImpl;
import eu.vranckaert.worktime.service.ui.impl.WidgetServiceImpl;

/**
 * User: DIRK VRANCKAERT
 * Date: 07/02/11
 * Time: 20:58
 */
public class WorkTimeWidgetProvider_1x1 extends AppWidgetProvider {
    private static final String LOG_TAG = WorkTimeWidgetProvider_1x1.class.getName();

    private WidgetService widgetService;
    private StatusBarNotificationService statusBarNotificationService;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "RECEIVE");
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(LOG_TAG, "UPDATE");
        Log.d(LOG_TAG, "Number of widgets found: " + appWidgetIds.length);

        widgetService = new WidgetServiceImpl(context);
        statusBarNotificationService = new StatusBarNotificationServiceImpl(context);

        for(int appWidgetId : appWidgetIds) {
            AppWidgetProviderInfo widgetProviderInfo = appWidgetManager.getAppWidgetInfo(appWidgetId);
            Log.d(LOG_TAG, "STARTING FOR WIDGET ID: " + appWidgetId);
            Log.d(LOG_TAG, "PROVIDER: " + widgetProviderInfo.provider.toString());

            widgetService.updateWidget1x1(appWidgetId);
            statusBarNotificationService.addOrUpdateNotification(null);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.d(LOG_TAG, "DELETED");
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        Log.d(LOG_TAG, "ENABLED");
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        Log.d(LOG_TAG, "DISABLED");
        super.onDisabled(context);
    }
}
