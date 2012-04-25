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

package eu.vranckaert.worktime.utils.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.test.AndroidTestCase;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.enums.export.ExportCsvSeparator;
import eu.vranckaert.worktime.enums.export.ExportData;
import eu.vranckaert.worktime.enums.export.ExportType;
import eu.vranckaert.worktime.utils.date.HourPreference12Or24;

/**
 * User: DIRK VRANCKAERT
 * Date: 20/01/12
 * Time: 12:33
 */
public class PreferencesTest extends AndroidTestCase {
    private Context ctx;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ctx = getContext();
    }

    public void testClass() {
        Preferences preferences = new Preferences();
        assertNotNull(preferences);
    }
    
    public void testSelectedProjectId() {
        Preferences.removePreference(ctx, Constants.Preferences.Keys.SELECTED_PROJECT_ID);

        int projectIdNotFound = Preferences.getSelectedProjectId(ctx);
        assertEquals("No project id should be found (result should be -1)", Constants.Preferences.SELECTED_PROJECT_ID_DEFAULT_VALUE, projectIdNotFound);
        
        int projectId = 100;
        Preferences.setSelectedProjectId(ctx, projectId);
        int projectIdFound = Preferences.getSelectedProjectId(ctx);
        assertEquals("A project id is required (" + projectId + ")", projectId, projectIdFound);

        Preferences.removePreference(ctx, Constants.Preferences.Keys.SELECTED_PROJECT_ID);
        int projectIdDeleted = Preferences.getSelectedProjectId(ctx);
        assertEquals("No project id should be found (result should be -1)", Constants.Preferences.SELECTED_PROJECT_ID_DEFAULT_VALUE, projectIdDeleted);
    }

    public void testWidgetAskForTaskSelectionIfOnlyOne() {
        Preferences.removePreference(ctx, Constants.Preferences.Keys.WIDGET_ASK_FOR_TASK_SELECTION_IF_ONLY_ONE);

        boolean preferenceNotFound = Preferences.getWidgetAskForTaskSelectionIfOnlyOnePreference(ctx);
        assertEquals("No preference should be found!", Constants.Preferences.WIDGET_ASK_FOR_TASK_SELECTION_IF_ONLY_ONE_DEFAULT_VALUE, preferenceNotFound);

        boolean preference = !Constants.Preferences.WIDGET_ASK_FOR_TASK_SELECTION_IF_ONLY_ONE_DEFAULT_VALUE;
        Preferences.setWidgetAskForTaskSelectionIfOnlyOnePreference(ctx, preference);
        boolean preferenceFound = Preferences.getWidgetAskForTaskSelectionIfOnlyOnePreference(ctx);
        assertEquals("A preference should be found (" + preference + ")", preference, preferenceFound);

        Preferences.removePreference(ctx, Constants.Preferences.Keys.WIDGET_ASK_FOR_TASK_SELECTION_IF_ONLY_ONE);
        boolean preferenceDeleted = Preferences.getWidgetAskForTaskSelectionIfOnlyOnePreference(ctx);
        assertEquals("No preference should be found!", Constants.Preferences.WIDGET_ASK_FOR_TASK_SELECTION_IF_ONLY_ONE_DEFAULT_VALUE, preferenceDeleted);
    }

    public void testWidgetEndingTimeRegistrationComment() {
        Preferences.removePreference(ctx, Constants.Preferences.Keys.WIDGET_ENDING_TIME_REGISTRATION_COMMENT_PREFERENCE);

        boolean preferenceNotFound = Preferences.getWidgetEndingTimeRegistrationCommentPreference(ctx);
        assertEquals("No preference should be found!", Constants.Preferences.WIDGET_ENDING_TIME_REGISTRATION_COMMENT_PREFERENCE_DEFAULT_VALUE, preferenceNotFound);

        boolean preference = !Constants.Preferences.WIDGET_ENDING_TIME_REGISTRATION_COMMENT_PREFERENCE_DEFAULT_VALUE;
        Preferences.setWidgetEndingTimeRegistrationCommentPreference(ctx, preference);
        boolean preferenceFound = Preferences.getWidgetEndingTimeRegistrationCommentPreference(ctx);
        assertEquals("A preference should be found (" + preference + ")", preference, preferenceFound);

        Preferences.removePreference(ctx, Constants.Preferences.Keys.WIDGET_ENDING_TIME_REGISTRATION_COMMENT_PREFERENCE);
        boolean preferenceDeleted = Preferences.getWidgetEndingTimeRegistrationCommentPreference(ctx);
        assertEquals("No preference should be found!", Constants.Preferences.WIDGET_ENDING_TIME_REGISTRATION_COMMENT_PREFERENCE_DEFAULT_VALUE, preferenceDeleted);
    }

    public void testWidgetEndingTimeRegistrationFinishTask() {
        Preferences.removePreference(ctx, Constants.Preferences.Keys.WIDGET_ENDING_TIME_REGISTRATION_FINISH_TASK_PREFERENCE);

        boolean preferenceNotFound = Preferences.getWidgetEndingTimeRegistrationFinishTaskPreference(ctx);
        assertEquals("No preference should be found!", Constants.Preferences.WIDGET_ENDING_TIME_REGISTRATION_FINISH_TASK_PREFERENCE_DEFAULT_VALUE, preferenceNotFound);

        boolean preference = !Constants.Preferences.WIDGET_ENDING_TIME_REGISTRATION_FINISH_TASK_PREFERENCE_DEFAULT_VALUE;
        Preferences.setWidgetEndingTimeRegistrationFinishTaskPreference(ctx, preference);
        boolean preferenceFound = Preferences.getWidgetEndingTimeRegistrationFinishTaskPreference(ctx);
        assertEquals("A preference should be found (" + preference + ")", preference, preferenceFound);

        Preferences.removePreference(ctx, Constants.Preferences.Keys.WIDGET_ENDING_TIME_REGISTRATION_FINISH_TASK_PREFERENCE);
        boolean preferenceDeleted = Preferences.getWidgetEndingTimeRegistrationFinishTaskPreference(ctx);
        assertEquals("No preference should be found!", Constants.Preferences.WIDGET_ENDING_TIME_REGISTRATION_FINISH_TASK_PREFERENCE_DEFAULT_VALUE, preferenceDeleted);
    }

    public void testShowStatusBarNotifications() {
        Preferences.removePreference(ctx, Constants.Preferences.Keys.SHOW_STATUS_BAR_NOTIFICATIONS_PREFERENCE);

        boolean preferenceNotFound = Preferences.getShowStatusBarNotificationsPreference(ctx);
        assertEquals("No preference should be found!", Constants.Preferences.SHOW_STATUS_BAR_NOTIFICATIONS_PREFERENCE_DEFAULT_VALUE, preferenceNotFound);

        boolean preference = !Constants.Preferences.SHOW_STATUS_BAR_NOTIFICATIONS_PREFERENCE_DEFAULT_VALUE;
        Preferences.setShowStatusBarNotificationsPreference(ctx, preference);
        boolean preferenceFound = Preferences.getShowStatusBarNotificationsPreference(ctx);
        assertEquals("A preference should be found (" + preference + ")", preference, preferenceFound);

        Preferences.removePreference(ctx, Constants.Preferences.Keys.SHOW_STATUS_BAR_NOTIFICATIONS_PREFERENCE);
        boolean preferenceDeleted = Preferences.getShowStatusBarNotificationsPreference(ctx);
        assertEquals("No preference should be found!", Constants.Preferences.SHOW_STATUS_BAR_NOTIFICATIONS_PREFERENCE_DEFAULT_VALUE, preferenceDeleted);
    }
    
    public void testDisplayHour1224Format() {
        HourPreference12Or24 preference = HourPreference12Or24.HOURS_12;
        Preferences.setDisplayHour1224Format(ctx, preference);
        HourPreference12Or24 preferenceFound = Preferences.getDisplayHour1224Format(ctx);
        assertEquals("A preference should be found (" + preference.getValue() + ")", preference.getValue(), preferenceFound.getValue());

        Preferences.removePreference(ctx, Constants.Preferences.Keys.DISPLAY_HOUR_12_24_FORMAT);

        preference = HourPreference12Or24.HOURS_24;
        Preferences.setDisplayHour1224Format(ctx, preference);
        preferenceFound = Preferences.getDisplayHour1224Format(ctx);
        assertEquals("A preference should be found (" + preference.getValue() + ")", preference.getValue(), preferenceFound.getValue());

        Preferences.removePreference(ctx, Constants.Preferences.Keys.DISPLAY_HOUR_12_24_FORMAT);
    }

    public void testSelectTaskHideFinished() {
        Preferences.removePreference(ctx, Constants.Preferences.Keys.SELECT_TASK_HIDE_FINISHED);

        boolean preferenceNotFound = Preferences.getSelectTaskHideFinished(ctx);
        assertEquals("No preference should be found!", Constants.Preferences.SELECT_TASK_HIDE_FINISHED_DEFAULT_VALUE, preferenceNotFound);

        boolean preference = !Constants.Preferences.SELECT_TASK_HIDE_FINISHED_DEFAULT_VALUE;
        Preferences.setSelectTaskHideFinished(ctx, preference);
        boolean preferenceFound = Preferences.getSelectTaskHideFinished(ctx);
        assertEquals("A preference should be found (" + preference + ")", preference, preferenceFound);

        Preferences.removePreference(ctx, Constants.Preferences.Keys.SELECT_TASK_HIDE_FINISHED);
        boolean preferenceDeleted = Preferences.getSelectTaskHideFinished(ctx);
        assertEquals("No preference should be found!", Constants.Preferences.SELECT_TASK_HIDE_FINISHED_DEFAULT_VALUE, preferenceDeleted);
    }

    public void testDisplayTasksHideFinished() {
        Preferences.removePreference(ctx, Constants.Preferences.Keys.DISPLAY_TASKS_HIDE_FINISHED);

        boolean preferenceNotFound = Preferences.getDisplayTasksHideFinished(ctx);
        assertEquals("No preference should be found!", Constants.Preferences.DISPLAY_TASKS_HIDE_FINISHED_DEFAULT_VALUE, preferenceNotFound);

        boolean preference = !Constants.Preferences.DISPLAY_TASKS_HIDE_FINISHED_DEFAULT_VALUE;
        Preferences.setDisplayTasksHideFinished(ctx, preference);
        boolean preferenceFound = Preferences.getDisplayTasksHideFinished(ctx);
        assertEquals("A preference should be found (" + preference + ")", preference, preferenceFound);

        Preferences.removePreference(ctx, Constants.Preferences.Keys.DISPLAY_TASKS_HIDE_FINISHED);
        boolean preferenceDeleted = Preferences.getDisplayTasksHideFinished(ctx);
        assertEquals("No preference should be found!", Constants.Preferences.DISPLAY_TASKS_HIDE_FINISHED_DEFAULT_VALUE, preferenceDeleted);
    }

    private static final SharedPreferences getSharedPreferences(Context ctx) {
        return ctx.getSharedPreferences(Constants.Preferences.PREFERENCES_NAME, Activity.MODE_PRIVATE);
    }

    public void testWeekStartsOn() {
        Preferences.removePreference(ctx, Constants.Preferences.Keys.WEEK_STARTS_ON);

        int preferenceNotFound = Preferences.getWeekStartsOn(ctx);
        assertEquals("No preference should be found!", Integer.parseInt(Constants.Preferences.WEEK_STARTS_ON_DEFAULT_VALUE), preferenceNotFound);

        int preference = 3;
        Preferences.setWeekStartsOn(ctx, preference);
        int preferenceFound = Preferences.getWeekStartsOn(ctx);
        assertEquals("A preference should be found (" + preference + ")", preference, preferenceFound);
        
        String invalidPreference = "azerty";
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(Constants.Preferences.Keys.WEEK_STARTS_ON, invalidPreference);
        editor.commit();
        Integer result = Preferences.getWeekStartsOn(ctx);
        assertEquals("No preference should be found (" + invalidPreference + ")", null, result);

        Preferences.removePreference(ctx, Constants.Preferences.Keys.WEEK_STARTS_ON);
        int preferenceDeleted = Preferences.getWeekStartsOn(ctx);
        assertEquals("No preference should be found!", Integer.parseInt(Constants.Preferences.WEEK_STARTS_ON_DEFAULT_VALUE), preferenceDeleted);
    }

    public void testReportingExportFileName() {
        Preferences.removePreference(ctx, Constants.Preferences.Keys.REPORTING_EXPORT_FILE_NAME);

        String preferenceNotFound = Preferences.getReportingExportFileName(ctx);
        assertEquals("No preference should be found!", Constants.Preferences.REPORTING_EXPORT_FILE_NAME_DEFAULT_VALUE, preferenceNotFound);

        String preference = "My unit testing export file name";
        Preferences.setReportingExportFileName(ctx, preference);
        String preferenceFound = Preferences.getReportingExportFileName(ctx);
        assertEquals("A preference should be found (" + preference + ")", preference, preferenceFound);
        
        preference = null;
        Preferences.setReportingExportFileName(ctx, preference);
        preferenceFound = Preferences.getReportingExportFileName(ctx);
        assertEquals("A preference should be found", Constants.Preferences.REPORTING_EXPORT_FILE_NAME_DEFAULT_VALUE, preferenceFound);

        Preferences.removePreference(ctx, Constants.Preferences.Keys.REPORTING_EXPORT_FILE_NAME);
        String preferenceDeleted = Preferences.getReportingExportFileName(ctx);
        assertEquals("No preference should be found!", Constants.Preferences.REPORTING_EXPORT_FILE_NAME_DEFAULT_VALUE, preferenceDeleted);
    }

    public void testTimeRegistrationsAutoClose60sGap() {
        Preferences.removePreference(ctx, Constants.Preferences.Keys.TIME_REGISTRATION_AUTO_CLOSE_60S_GAP);

        boolean preferenceNotFound = Preferences.getTimeRegistrationsAutoClose60sGap(ctx);
        assertEquals("No preference should be found!", Constants.Preferences.TIME_REGISTRATION_AUTO_CLOSE_60S_GAP_DEFAULT_VALUE, preferenceNotFound);

        boolean preference = !Constants.Preferences.TIME_REGISTRATION_AUTO_CLOSE_60S_GAP_DEFAULT_VALUE;
        Preferences.setTimeRegistrationsAutoClose60sGap(ctx, preference);
        boolean preferenceFound = Preferences.getTimeRegistrationsAutoClose60sGap(ctx);
        assertEquals("A preference should be found (" + preference + ")", preference, preferenceFound);

        Preferences.removePreference(ctx, Constants.Preferences.Keys.TIME_REGISTRATION_AUTO_CLOSE_60S_GAP);
        boolean preferenceDeleted = Preferences.getTimeRegistrationsAutoClose60sGap(ctx);
        assertEquals("No preference should be found!", Constants.Preferences.TIME_REGISTRATION_AUTO_CLOSE_60S_GAP_DEFAULT_VALUE, preferenceDeleted);
    }

    public void testTimePrecision() {
        Preferences.removePreference(ctx, Constants.Preferences.Keys.TIME_PRECISION);

        TimePrecisionPreference preferenceNotFound = Preferences.getTimePrecision(ctx);
        assertEquals("No preference should be found!", TimePrecisionPreference.getPreferenceForValue(TimePrecisionPreference.getDefaultValue()), preferenceNotFound);

        TimePrecisionPreference preference = TimePrecisionPreference.SECOND;
        Preferences.setTimePrecision(ctx, preference);
        TimePrecisionPreference preferenceFound = Preferences.getTimePrecision(ctx);
        assertEquals("A preference should be found (" + preference + ")", preference, preferenceFound);

        Preferences.removePreference(ctx, Constants.Preferences.Keys.TIME_PRECISION);
        TimePrecisionPreference preferenceDeleted = Preferences.getTimePrecision(ctx);
        assertEquals("No preference should be found!", TimePrecisionPreference.getPreferenceForValue(TimePrecisionPreference.getDefaultValue()), preferenceDeleted);
    }

    public void testSelectProjectHideFinished() {
        Preferences.removePreference(ctx, Constants.Preferences.Keys.SELECT_PROJECT_HIDE_FINISHED);

        boolean preferenceNotFound = Preferences.getSelectProjectHideFinished(ctx);
        assertEquals("No preference should be found!", Constants.Preferences.SELECT_PROJECT_HIDE_FINISHED_DEFAULT_VALUE, preferenceNotFound);

        boolean preference = !Constants.Preferences.SELECT_PROJECT_HIDE_FINISHED_DEFAULT_VALUE;
        Preferences.setSelectProjectHideFinished(ctx, preference);
        boolean preferenceFound = Preferences.getSelectProjectHideFinished(ctx);
        assertEquals("A preference should be found (" + preference + ")", preference, preferenceFound);

        Preferences.removePreference(ctx, Constants.Preferences.Keys.SELECT_PROJECT_HIDE_FINISHED);
        boolean preferenceDeleted = Preferences.getSelectProjectHideFinished(ctx);
        assertEquals("No preference should be found!", Constants.Preferences.SELECT_TASK_HIDE_FINISHED_DEFAULT_VALUE, preferenceDeleted);
    }

    public void testDisplayProjectsHideFinished() {
        Preferences.removePreference(ctx, Constants.Preferences.Keys.DISPLAY_PROJECTS_HIDE_FINISHED);

        boolean preferenceNotFound = Preferences.getDisplayProjectsHideFinished(ctx);
        assertEquals("No preference should be found!", Constants.Preferences.DISPLAY_PROJECTS_HIDE_FINISHED_DEFAULT_VALUE, preferenceNotFound);

        boolean preference = !Constants.Preferences.DISPLAY_PROJECTS_HIDE_FINISHED_DEFAULT_VALUE;
        Preferences.setDisplayProjectsHideFinished(ctx, preference);
        boolean preferenceFound = Preferences.getDisplayProjectsHideFinished(ctx);
        assertEquals("A preference should be found (" + preference + ")", preference, preferenceFound);

        Preferences.removePreference(ctx, Constants.Preferences.Keys.DISPLAY_PROJECTS_HIDE_FINISHED);
        boolean preferenceDeleted = Preferences.getDisplayProjectsHideFinished(ctx);
        assertEquals("No preference should be found!", Constants.Preferences.DISPLAY_PROJECTS_HIDE_FINISHED_DEFAULT_VALUE, preferenceDeleted);
    }

    public void testTimeRegistrationPunchBarEnabledFromHomeScreen() {
        Preferences.removePreference(ctx, Constants.Preferences.Keys.TIME_REGISTRATION_PUNCH_BAR_ENABLED_FROM_HOME_SCREEN);

        boolean preferenceNotFound = Preferences.getTimeRegistrationPunchBarEnabledFromHomeScreen(ctx);
        assertEquals("No preference should be found!", Constants.Preferences.TIME_REGISTRATION_PUNCH_BAR_ENABLED_FROM_HOME_SCREEN_DEFAULT_VALUE, preferenceNotFound);

        boolean preference = !Constants.Preferences.TIME_REGISTRATION_PUNCH_BAR_ENABLED_FROM_HOME_SCREEN_DEFAULT_VALUE;
        Preferences.setTimeRegistrationPunchBarEnabledFromHomeScreen(ctx, preference);
        boolean preferenceFound = Preferences.getTimeRegistrationPunchBarEnabledFromHomeScreen(ctx);
        assertEquals("A preference should be found (" + preference + ")", preference, preferenceFound);

        Preferences.removePreference(ctx, Constants.Preferences.Keys.TIME_REGISTRATION_PUNCH_BAR_ENABLED_FROM_HOME_SCREEN);
        boolean preferenceDeleted = Preferences.getTimeRegistrationPunchBarEnabledFromHomeScreen(ctx);
        assertEquals("No preference should be found!", Constants.Preferences.TIME_REGISTRATION_PUNCH_BAR_ENABLED_FROM_HOME_SCREEN_DEFAULT_VALUE, preferenceDeleted);
    }

    public void testTimeRegistrationPunchBarEnabledOnAllScreens() {
        Preferences.removePreference(ctx, Constants.Preferences.Keys.TIME_REGISTRATION_PUNCH_BAR_ENABLED_ON_ALL_SCREENS);

        boolean preferenceNotFound = Preferences.getTimeRegistrationPunchBarEnabledOnAllScreens(ctx);
        assertEquals("No preference should be found!", Constants.Preferences.TIME_REGISTRATION_PUNCH_BAR_ENABLED_ON_ALL_SCREENS_DEFAULT_VALUE, preferenceNotFound);

        boolean preference = !Constants.Preferences.TIME_REGISTRATION_PUNCH_BAR_ENABLED_ON_ALL_SCREENS_DEFAULT_VALUE;
        Preferences.setTimeRegistrationPunchBarEnabledOnAllScreens(ctx, preference);
        boolean preferenceFound = Preferences.getTimeRegistrationPunchBarEnabledOnAllScreens(ctx);
        assertEquals("A preference should be found (" + preference + ")", preference, preferenceFound);

        Preferences.removePreference(ctx, Constants.Preferences.Keys.TIME_REGISTRATION_PUNCH_BAR_ENABLED_ON_ALL_SCREENS);
        boolean preferenceDeleted = Preferences.getTimeRegistrationPunchBarEnabledOnAllScreens(ctx);
        assertEquals("No preference should be found!", Constants.Preferences.TIME_REGISTRATION_PUNCH_BAR_ENABLED_ON_ALL_SCREENS_DEFAULT_VALUE, preferenceDeleted);
    }

    public void testPreferredExportType() {
        Preferences.removePreference(ctx, Constants.Preferences.Keys.EXPORT_TYPE);

        ExportType preferenceNotFound = Preferences.getPreferredExportType(ctx);
        assertEquals("No preference should be found!", ExportType.XLS, preferenceNotFound);

        ExportType preference = ExportType.CSV;
        Preferences.setPreferredExportType(ctx, preference);
        ExportType preferenceFound = Preferences.getPreferredExportType(ctx);
        assertEquals("A preference should be found", preference, preferenceFound);

        Preferences.removePreference(ctx, Constants.Preferences.Keys.EXPORT_TYPE);
        ExportType preferenceDeleted = Preferences.getPreferredExportType(ctx);
        assertEquals("No preference should be found!", ExportType.XLS, preferenceDeleted);
    }

    public void testPreferredExportData() {
        Preferences.removePreference(ctx, Constants.Preferences.Keys.EXPORT_DATA);

        ExportData preferenceNotFound = Preferences.getPreferredExportData(ctx);
        assertEquals("No preference should be found!", ExportData.REPORT, preferenceNotFound);

        ExportData preference = ExportData.RAW_DATA;
        Preferences.setPreferredExportData(ctx, preference);
        ExportData preferenceFound = Preferences.getPreferredExportData(ctx);
        assertEquals("A preference should be found", preference, preferenceFound);

        Preferences.removePreference(ctx, Constants.Preferences.Keys.EXPORT_DATA);
        ExportData preferenceDeleted = Preferences.getPreferredExportData(ctx);
        assertEquals("No preference should be found!", ExportData.REPORT, preferenceDeleted);
    }

    public void testPreferredExportCSVSeparator() {
        Preferences.removePreference(ctx, Constants.Preferences.Keys.EXPORT_CSV_SEPARATOR);

        ExportCsvSeparator preferenceNotFound = Preferences.getPreferredExportCSVSeparator(ctx);
        assertEquals("No preference should be found!", ExportCsvSeparator.SEMICOLON, preferenceNotFound);

        ExportCsvSeparator preference = ExportCsvSeparator.COMMA;
        Preferences.setPreferredExportCSVSeparator(ctx, preference);
        ExportCsvSeparator preferenceFound = Preferences.getPreferredExportCSVSeparator(ctx);
        assertEquals("A preference should be found", preference, preferenceFound);

        Preferences.removePreference(ctx, Constants.Preferences.Keys.EXPORT_CSV_SEPARATOR);
        ExportCsvSeparator preferenceDeleted = Preferences.getPreferredExportCSVSeparator(ctx);
        assertEquals("No preference should be found!", ExportCsvSeparator.SEMICOLON, preferenceDeleted);
    }

    public void testTimeRegistrationSplitDefaultGap() {
        Preferences.removePreference(ctx, Constants.Preferences.Keys.TIME_REGISTRATION_SPLIT_DEFAULT_GAP);

        int resultNotFound = Preferences.getTimeRegistrationSplitDefaultGap(ctx);
        assertEquals("No value should be found (result should be 30)", Constants.Preferences.TIME_REGISTRATION_SPLIT_DEFAULT_GAP_DEFAULT_VALUE, resultNotFound);

        int value = 344;
        Preferences.setTimeRegistrationSplitDefaultGap(ctx, value);
        int minutesFound = Preferences.getTimeRegistrationSplitDefaultGap(ctx);
        assertEquals("A value is expected (" + value + ")", value, minutesFound);

        Preferences.removePreference(ctx, Constants.Preferences.Keys.TIME_REGISTRATION_SPLIT_DEFAULT_GAP);
        int valueDeleted = Preferences.getTimeRegistrationSplitDefaultGap(ctx);
        assertEquals("No value should be found (result should be 30)", Constants.Preferences.TIME_REGISTRATION_SPLIT_DEFAULT_GAP_DEFAULT_VALUE, valueDeleted);
    }
}
