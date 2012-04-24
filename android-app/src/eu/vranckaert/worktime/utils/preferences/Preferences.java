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
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.enums.export.ExportCsvSeparator;
import eu.vranckaert.worktime.enums.export.ExportData;
import eu.vranckaert.worktime.enums.export.ExportType;
import eu.vranckaert.worktime.utils.date.DateUtils;
import eu.vranckaert.worktime.utils.date.HourPreference12Or24;
import eu.vranckaert.worktime.utils.string.StringUtils;

/**
 * Access all the preferences. This class is mainly used to read the preferences but can be used in some rare cases
 * to also update or insert preferences.
 *
 * User: DIRK VRANCKAERT
 * Date: 19/02/11
 * Time: 14:22
 */
public class Preferences {
    /**
     * Get an instance of {@link SharedPreferences} to access the preferences.
     * @param ctx The context when accessing the preferences.
     * @return The instance based on the context.
     */
    private static final SharedPreferences getSharedPreferences(Context ctx) {
        return ctx.getSharedPreferences(Constants.Preferences.PREFERENCES_NAME, Activity.MODE_PRIVATE);
    }

    /**
     * Remove a certain preference from the system.
     * @param ctx The context.
     * @param key The key of the preference to remove.
     */
    public static final void removePreference(Context ctx, String key) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.remove(key);
        editor.commit();
    }

    /**
     * Get the preference for key {@link Constants.Preferences.Keys#SELECTED_PROJECT_ID}. If no selected project id is
     * found the default value will be {@link Constants.Preferences#SELECTED_PROJECT_ID_DEFAULT_VALUE}.
     * @param ctx The context when getting the selected project id.
     * @return The unique identifier of a {@link eu.vranckaert.worktime.model.Project} if one is found. Otherwise the
     * default value {@link Constants.Preferences#SELECTED_PROJECT_ID_DEFAULT_VALUE}.
     */
    public static int getSelectedProjectId(Context ctx) {
        return getSharedPreferences(ctx).getInt(
                Constants.Preferences.Keys.SELECTED_PROJECT_ID,
                Constants.Preferences.SELECTED_PROJECT_ID_DEFAULT_VALUE
        );
    }

    /**
     * Updates the preference {@link Constants.Preferences.Keys#SELECTED_PROJECT_ID}.
     * @param ctx The context when updating the preference.
     * @param projectId The projectId to put in the preferences.
     * @param projectId The projectId to put in the preferences.
     */
    public static void setSelectedProjectId(Context ctx, int projectId) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt(Constants.Preferences.Keys.SELECTED_PROJECT_ID, projectId);
        editor.commit();
    }

    /**
     * Get the preference for key {@link Constants.Preferences.Keys#WIDGET_ASK_FOR_TASK_SELECTION_IF_ONLY_ONE}.
     * If no value is found for the preference the default value will be
     * {@link Constants.Preferences#WIDGET_ASK_FOR_TASK_SELECTION_IF_ONLY_ONE_DEFAULT_VALUE}.
     * @param ctx The context when getting the preference for asking to select a task when starting a time registration.
     * @return The {@link Boolean} which represents weather should be asked to select a task or not if only one task for
     * a project is available.
     */
    public static boolean getWidgetAskForTaskSelectionIfOnlyOnePreference(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(
                Constants.Preferences.Keys.WIDGET_ASK_FOR_TASK_SELECTION_IF_ONLY_ONE,
                Constants.Preferences.WIDGET_ASK_FOR_TASK_SELECTION_IF_ONLY_ONE_DEFAULT_VALUE
        );
    }

    /**
     * Updates the preference {@link Constants.Preferences.Keys#WIDGET_ASK_FOR_TASK_SELECTION_IF_ONLY_ONE}.
     * @param ctx The context when updating the preference.
     * @param askTaskSelection Weather should be asked to select a task or not if only one task for a project is available.
     */
    public static void setWidgetAskForTaskSelectionIfOnlyOnePreference(Context ctx, boolean askTaskSelection) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(Constants.Preferences.Keys.WIDGET_ASK_FOR_TASK_SELECTION_IF_ONLY_ONE, askTaskSelection);
        editor.commit();
    }

    /**
     * Get the preference for key {@link Constants.Preferences.Keys#WIDGET_ENDING_TIME_REGISTRATION_COMMENT_PREFERENCE}.
     * If no value is found for the preference the default value will be
     * {@link Constants.Preferences#WIDGET_ENDING_TIME_REGISTRATION_COMMENT_PREFERENCE_DEFAULT_VALUE}.
     * @param ctx The context when getting the preference for asking a comment when ending a time registration.
     * @return The {@link Boolean} which represents weather should be asked for a comment on ending a time registration
     * or not.
     */
    public static boolean getWidgetEndingTimeRegistrationCommentPreference(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(
                Constants.Preferences.Keys.WIDGET_ENDING_TIME_REGISTRATION_COMMENT_PREFERENCE,
                Constants.Preferences.WIDGET_ENDING_TIME_REGISTRATION_COMMENT_PREFERENCE_DEFAULT_VALUE
        );
    }

    /**
     * Updates the preference {@link Constants.Preferences.Keys#WIDGET_ENDING_TIME_REGISTRATION_COMMENT_PREFERENCE}.
     * @param ctx The context when updating the preference.
     * @param askComment Weather or not to ask for a preference when ending a time registration.
     */
    public static void setWidgetEndingTimeRegistrationCommentPreference(Context ctx, boolean askComment) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(Constants.Preferences.Keys.WIDGET_ENDING_TIME_REGISTRATION_COMMENT_PREFERENCE, askComment);
        editor.commit();
    }

    /**
     * Get the preference for key
     * {@link Constants.Preferences.Keys#WIDGET_ENDING_TIME_REGISTRATION_FINISH_TASK_PREFERENCE}. If no value is found
     * for the preference the default value will be
     * {@link Constants.Preferences#WIDGET_ENDING_TIME_REGISTRATION_FINISH_TASK_PREFERENCE_DEFAULT_VALUE}.
     * @param ctx The context when getting the preference for asking to mark the task finished when ending a
     * time registration.
     * @return The {@link Boolean} which represents weather should be asked for marking the task finished on ending a
     * time registration or not.
     */
    public static boolean getWidgetEndingTimeRegistrationFinishTaskPreference(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(
                Constants.Preferences.Keys.WIDGET_ENDING_TIME_REGISTRATION_FINISH_TASK_PREFERENCE,
                Constants.Preferences.WIDGET_ENDING_TIME_REGISTRATION_FINISH_TASK_PREFERENCE_DEFAULT_VALUE
        );
    }

    /**
     * Updates the preference {@link Constants.Preferences.Keys#WIDGET_ENDING_TIME_REGISTRATION_FINISH_TASK_PREFERENCE}.
     * @param ctx The context when updating the preference.
     * @param finishTask Weather or not to ask for marking the task as finished when ending the time registration.
     */
    public static void setWidgetEndingTimeRegistrationFinishTaskPreference(Context ctx, boolean finishTask) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(Constants.Preferences.Keys.WIDGET_ENDING_TIME_REGISTRATION_FINISH_TASK_PREFERENCE, finishTask);
        editor.commit();
    }

    /**
     * Get the preference for key {@link Constants.Preferences.Keys#SHOW_STATUS_BAR_NOTIFICATIONS_PREFERENCE}. If no
     * value is found for the preference the default value will be
     * {@link Constants.Preferences#SHOW_STATUS_BAR_NOTIFICATIONS_PREFERENCE_DEFAULT_VALUE}.
     * @param ctx The context when getting the preference for showing status bar notifications.
     * @return The {@link boolean} which represents the users' choice to show or hide status bar notifications.
     */
    public static boolean getShowStatusBarNotificationsPreference(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(
                Constants.Preferences.Keys.SHOW_STATUS_BAR_NOTIFICATIONS_PREFERENCE,
                Constants.Preferences.SHOW_STATUS_BAR_NOTIFICATIONS_PREFERENCE_DEFAULT_VALUE
        );
    }

    /**
     * Updates the preference {@link Constants.Preferences.Keys#SHOW_STATUS_BAR_NOTIFICATIONS_PREFERENCE}.
     * @param ctx The context when updating the preference.
     * @param showNotif The {@link boolean} which represents the users' choice to show or hide status bar notifications.
     */
    public static void setShowStatusBarNotificationsPreference(Context ctx, boolean showNotif) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(Constants.Preferences.Keys.SHOW_STATUS_BAR_NOTIFICATIONS_PREFERENCE, showNotif);
        editor.commit();
    }

    /**
     * Get the preference for key {@link Constants.Preferences.Keys#DISPLAY_HOUR_12_24_FORMAT}. If no value is found for
     * the preference the default value will be {@link Constants.Preferences#DISPLAY_HOUR_12_24_FORMAT_DEFAULT_VALUE}.
     * @param ctx The context when getting the preference for the hour display format.
     * @return The {@link HourPreference12Or24} value or null.
     */
    public static HourPreference12Or24 getDisplayHour1224Format(Context ctx) {
        String value = getSharedPreferences(ctx).getString(
                Constants.Preferences.Keys.DISPLAY_HOUR_12_24_FORMAT,
                Constants.Preferences.DISPLAY_HOUR_12_24_FORMAT_DEFAULT_VALUE
        );
        HourPreference12Or24 preference = HourPreference12Or24.findHourPreference12Or24(value);

        if (preference == null) {
            if (DateUtils.System.is24HourClock(ctx)) {
                preference = HourPreference12Or24.HOURS_24;
            } else {
                preference = HourPreference12Or24.HOURS_12;
            }
        }
        return preference;
    }

    /**
     * Updates the preference {@link Constants.Preferences.Keys#DISPLAY_HOUR_12_24_FORMAT}.
     * @param ctx The context when updating the preference.
     * @param preference An {@link HourPreference12Or24} value or null.
     */
    public static void setDisplayHour1224Format(Context ctx, HourPreference12Or24 preference) {
        String value = "system-default";
        if (preference != null) {
            value = preference.getValue();
        }
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(Constants.Preferences.Keys.DISPLAY_HOUR_12_24_FORMAT, value);
        editor.commit();
    }

    /**
     * Get the preference for key {@link Constants.Preferences.Keys#SELECT_TASK_HIDE_FINISHED}. If no
     * value is found for the preference the default value will be
     * {@link Constants.Preferences#SELECT_TASK_HIDE_FINISHED_DEFAULT_VALUE}.
     * @param ctx The context when getting the preference for hiding or showing finished tasks.
     * @return The {@link boolean} which represents the users' choice to show or hide finished tasks.
     */
    public static boolean getSelectTaskHideFinished(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(
                Constants.Preferences.Keys.SELECT_TASK_HIDE_FINISHED,
                Constants.Preferences.SELECT_TASK_HIDE_FINISHED_DEFAULT_VALUE
        );
    }

    /**
     * Updates the preference {@link Constants.Preferences.Keys#SELECT_TASK_HIDE_FINISHED}.
     * @param ctx The context when updating the preference.
     * @param hideFinished The {@link boolean} which represents the users' choice to show or hide finished tasks.
     */
    public static void setSelectTaskHideFinished(Context ctx, boolean hideFinished) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(Constants.Preferences.Keys.SELECT_TASK_HIDE_FINISHED, hideFinished);
        editor.commit();
    }

    /**
     * Get the preference for key {@link Constants.Preferences.Keys#SELECT_PROJECT_HIDE_FINISHED}. If no
     * value is found for the preference the default value will be
     * {@link Constants.Preferences#SELECT_PROJECT_HIDE_FINISHED_DEFAULT_VALUE}.
     * @param ctx The context when getting the preference for hiding or showing finished tasks.
     * @return The {@link boolean} which represents the users' choice to show or hide finished projects.
     */
    public static boolean getSelectProjectHideFinished(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(
                Constants.Preferences.Keys.SELECT_PROJECT_HIDE_FINISHED,
                Constants.Preferences.SELECT_PROJECT_HIDE_FINISHED_DEFAULT_VALUE
        );
    }

    /**
     * Updates the preference {@link Constants.Preferences.Keys#SELECT_PROJECT_HIDE_FINISHED}.
     * @param ctx The context when updating the preference.
     * @param hideFinished The {@link boolean} which represents the users' choice to show or hide finished projects.
     */
    public static void setSelectProjectHideFinished(Context ctx, boolean hideFinished) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(Constants.Preferences.Keys.SELECT_PROJECT_HIDE_FINISHED, hideFinished);
        editor.commit();
    }

    /**
     * Get the preference for key {@link Constants.Preferences.Keys#DISPLAY_TASKS_HIDE_FINISHED}. If no
     * value is found for the preference the default value will be
     * {@link Constants.Preferences#DISPLAY_TASKS_HIDE_FINISHED_DEFAULT_VALUE}.
     * @param ctx The context when getting the preference for hiding or showing finished tasks.
     * @return The {@link boolean} which represents the users' choice to show or hide finished tasks.
     */
    public static boolean getDisplayTasksHideFinished(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(
                Constants.Preferences.Keys.DISPLAY_TASKS_HIDE_FINISHED,
                Constants.Preferences.DISPLAY_TASKS_HIDE_FINISHED_DEFAULT_VALUE
        );
    }

    /**
     * Updates the preference {@link Constants.Preferences.Keys#DISPLAY_TASKS_HIDE_FINISHED}.
     * @param ctx The context when updating the preference.
     * @param hideFinished The {@link boolean} which represents the users' choice to show or hide finished tasks.
     */
    public static void setDisplayTasksHideFinished(Context ctx, boolean hideFinished) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(Constants.Preferences.Keys.DISPLAY_TASKS_HIDE_FINISHED, hideFinished);
        editor.commit();
    }

    /**
     * Get the preference for key {@link Constants.Preferences.Keys#WEEK_STARTS_ON}. If no value is found for
     * the preference the default value will be {@link Constants.Preferences#WEEK_STARTS_ON_DEFAULT_VALUE}.
     * @param ctx The context when getting the preference for the day the week starts on.
     * @return The {@link Integer} value.
     */
    public static Integer getWeekStartsOn(Context ctx) {
        String value = getSharedPreferences(ctx).getString(
                Constants.Preferences.Keys.WEEK_STARTS_ON,
                Constants.Preferences.WEEK_STARTS_ON_DEFAULT_VALUE
        );
        try {
            return Integer.parseInt(value);
        } catch(Exception e) {
            return null;
        }
    }

    /**
     * Updates the preference {@link Constants.Preferences.Keys#WEEK_STARTS_ON}.
     * @param ctx The context when updating the preference.
     * @param weekStartsOn An {@link Integer} value.
     */
    public static void setWeekStartsOn(Context ctx, Integer weekStartsOn) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(Constants.Preferences.Keys.WEEK_STARTS_ON, weekStartsOn.toString());
        editor.commit();
    }

    /**
     * Get the preference for key {@link Constants.Preferences.Keys#REPORTING_EXPORT_FILE_NAME}. If no value is found for
     * the preference the default value will be {@link Constants.Preferences#REPORTING_EXPORT_FILE_NAME_DEFAULT_VALUE}.
     * @param ctx The context when getting the preference for file name of the reporting export.
     * @return The {@link String} value.
     */
    public static String getReportingExportFileName(Context ctx) {
        return getSharedPreferences(ctx).getString(
                Constants.Preferences.Keys.REPORTING_EXPORT_FILE_NAME,
                Constants.Preferences.REPORTING_EXPORT_FILE_NAME_DEFAULT_VALUE
        );
    }

    /**
     * Updates the preference {@link Constants.Preferences.Keys#REPORTING_EXPORT_FILE_NAME}.
     * @param ctx The context when updating the preference.
     * @param reportingExportFileName A {@link String} value.
     */
    public static void setReportingExportFileName(Context ctx, String reportingExportFileName) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        if (StringUtils.isBlank(reportingExportFileName)) {
            reportingExportFileName = Constants.Preferences.REPORTING_EXPORT_FILE_NAME_DEFAULT_VALUE;
        }
        editor.putString(Constants.Preferences.Keys.REPORTING_EXPORT_FILE_NAME, reportingExportFileName);
        editor.commit();
    }

    /**
     * Get the preference for key {@link Constants.Preferences.Keys#TIME_REGISTRATION_AUTO_CLOSE_60S_GAP}. If no value
     * is found for the preference the default value will be
     * {@link Constants.Preferences#TIME_REGISTRATION_AUTO_CLOSE_60S_GAP_DEFAULT_VALUE}.
     * @param ctx The context when getting the preference for file name of the reporting export.
     * @return The {@link Boolean} value.
     */
    public static boolean getTimeRegistrationsAutoClose60sGap(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(
                Constants.Preferences.Keys.TIME_REGISTRATION_AUTO_CLOSE_60S_GAP,
                Constants.Preferences.TIME_REGISTRATION_AUTO_CLOSE_60S_GAP_DEFAULT_VALUE
        );
    }

    /**
     * Updates the preference {@link Constants.Preferences.Keys#TIME_REGISTRATION_AUTO_CLOSE_60S_GAP}.
     * @param ctx The context when updating the preference.
     * @param autoCloseGap The boolean if a gap should automatically be closed or not.
     */
    public static void setTimeRegistrationsAutoClose60sGap(Context ctx, boolean autoCloseGap) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(Constants.Preferences.Keys.TIME_REGISTRATION_AUTO_CLOSE_60S_GAP, autoCloseGap);
        editor.commit();
    }

    /**
     * Get the preference for key {@link Constants.Preferences.Keys#TIME_PRECISION}. If no value is found for the
     * preference the default value will be
     * {@link eu.vranckaert.worktime.utils.preferences.TimePrecisionPreference#getDefaultValue()}.
     * @param ctx The context when getting the preference for the day the week starts on.
     * @return The {@link TimePrecisionPreference}.
     */
    public static TimePrecisionPreference getTimePrecision(Context ctx) {
        String value = getSharedPreferences(ctx).getString(
                Constants.Preferences.Keys.TIME_PRECISION,
                TimePrecisionPreference.getDefaultValue()
        );

        TimePrecisionPreference preference = TimePrecisionPreference.getPreferenceForValue(value);
        return preference;
    }

    /**
     * Updates the preference {@link Constants.Preferences.Keys#TIME_PRECISION}.
     * @param ctx The context when updating the preference.
     * @param preference A {@link TimePrecisionPreference}.
     */
    public static void setTimePrecision(Context ctx, TimePrecisionPreference preference) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(Constants.Preferences.Keys.TIME_PRECISION, preference.getValue());
        editor.commit();
    }

    /**
     * Get the preference for key {@link Constants.Preferences.Keys#DISPLAY_PROJECTS_HIDE_FINISHED}. If no
     * value is found for the preference the default value will be
     * {@link Constants.Preferences#DISPLAY_PROJECTS_HIDE_FINISHED_DEFAULT_VALUE}.
     * @param ctx The context when getting the preference for hiding or showing finished projects.
     * @return The {@link boolean} which represents the users' choice to show or hide finished projects.
     */
    public static boolean getDisplayProjectsHideFinished(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(
                Constants.Preferences.Keys.DISPLAY_PROJECTS_HIDE_FINISHED,
                Constants.Preferences.DISPLAY_PROJECTS_HIDE_FINISHED_DEFAULT_VALUE
        );
    }

    /**
     * Updates the preference {@link Constants.Preferences.Keys#DISPLAY_PROJECTS_HIDE_FINISHED}.
     * @param ctx The context when updating the preference.
     * @param hideFinished The {@link boolean} which represents the users' choice to show or hide finished projects.
     */
    public static void setDisplayProjectsHideFinished(Context ctx, boolean hideFinished) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(Constants.Preferences.Keys.DISPLAY_PROJECTS_HIDE_FINISHED, hideFinished);
        editor.commit();
    }

    /**
     * Get the preference for key {@link Constants.Preferences.Keys#TIME_REGISTRATION_PUNCH_BAR_ENABLED_FROM_HOME_SCREEN}.
     * If no value is found for the preference the default value will be
     * {@link Constants.Preferences#TIME_REGISTRATION_PUNCH_BAR_ENABLED_FROM_HOME_SCREEN_DEFAULT_VALUE}.
     * @param ctx The context when getting the preference for hiding or showing finished projects.
     * @return The {@link boolean} which represents the users' choice to show or hide the in app punch-bar on the home
     * screen.
     */
    public static boolean getTimeRegistrationPunchBarEnabledFromHomeScreen(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(
                Constants.Preferences.Keys.TIME_REGISTRATION_PUNCH_BAR_ENABLED_FROM_HOME_SCREEN,
                Constants.Preferences.TIME_REGISTRATION_PUNCH_BAR_ENABLED_FROM_HOME_SCREEN_DEFAULT_VALUE
        );
    }

    /**
     * Updates the preference {@link Constants.Preferences.Keys#TIME_REGISTRATION_PUNCH_BAR_ENABLED_FROM_HOME_SCREEN}.
     * @param ctx The context when updating the preference.
     * @param hideFinished The {@link boolean} which represents the users' choice to show or hide the in app punch-bar
     * on the home screen.
     */
    public static void setTimeRegistrationPunchBarEnabledFromHomeScreen(Context ctx, boolean hideFinished) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(Constants.Preferences.Keys.TIME_REGISTRATION_PUNCH_BAR_ENABLED_FROM_HOME_SCREEN, hideFinished);
        editor.commit();
    }

    /**
     * Get the preference for key {@link Constants.Preferences.Keys#TIME_REGISTRATION_PUNCH_BAR_ENABLED_ON_ALL_SCREENS}.
     * If no value is found for the preference the default value will be
     * {@link Constants.Preferences#TIME_REGISTRATION_PUNCH_BAR_ENABLED_ON_ALL_SCREENS_DEFAULT_VALUE}.
     * @param ctx The context when getting the preference for hiding or showing finished projects.
     * @return The {@link boolean} which represents the users' choice to show or hide the in app punch-bar.
     */
    public static boolean getTimeRegistrationPunchBarEnabledOnAllScreens(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(
                Constants.Preferences.Keys.TIME_REGISTRATION_PUNCH_BAR_ENABLED_ON_ALL_SCREENS,
                Constants.Preferences.TIME_REGISTRATION_PUNCH_BAR_ENABLED_ON_ALL_SCREENS_DEFAULT_VALUE
        );
    }

    /**
     * Updates the preference {@link Constants.Preferences.Keys#TIME_REGISTRATION_PUNCH_BAR_ENABLED_ON_ALL_SCREENS}.
     * @param ctx The context when updating the preference.
     * @param hideFinished The {@link boolean} which represents the users' choice to show or hide the in app punch-bar.
     */
    public static void setTimeRegistrationPunchBarEnabledOnAllScreens(Context ctx, boolean hideFinished) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(Constants.Preferences.Keys.TIME_REGISTRATION_PUNCH_BAR_ENABLED_ON_ALL_SCREENS, hideFinished);
        editor.commit();
    }

    /**
     * Get the preference for key {@link Constants.Preferences.Keys#EXPORT_TYPE}. If no value is found for the
     * preference the default value will be {@link ExportType#XLS}.
     * @param ctx The context when getting the preference for the export type to use.
     * @return The {@link ExportType} that represents the user's choice (or if no choice available the system's default)
     * for the export type to use.
     */
    public static ExportType getPreferredExportType(Context ctx) {
        String exportType = getSharedPreferences(ctx).getString(
                Constants.Preferences.Keys.EXPORT_TYPE,
                ExportType.XLS.toString()
        );
        return ExportType.valueOf(exportType);
    }

    /**
     * Updates the preference {@link Constants.Preferences.Keys#EXPORT_TYPE}.
     * @param ctx The context when updating the preference.
     * @param exportType The {@link ExportType} which represents the users' choice for the export type to use.
     */
    public static void setPreferredExportType(Context ctx, ExportType exportType) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(Constants.Preferences.Keys.EXPORT_TYPE, exportType.toString());
        editor.commit();
    }

    /**
     * Get the preference for key {@link Constants.Preferences.Keys#EXPORT_CSV_SEPARATOR}. If no value is found for the
     * preference the default value will be {@link ExportCsvSeparator#SEMICOLON}.
     * @param ctx The context when getting the preference for the CSV separator to use during export.
     * @return The {@link ExportCsvSeparator} that represents the user's choice (or if no choice available the system's
     * default) for the CSV separator to use during the export.
     */
    public static ExportCsvSeparator getPreferredExportCSVSeparator(Context ctx) {
        String exportType = getSharedPreferences(ctx).getString(
                Constants.Preferences.Keys.EXPORT_CSV_SEPARATOR,
                ExportCsvSeparator.SEMICOLON.toString()
        );
        return ExportCsvSeparator.valueOf(exportType);
    }

    /**
     * Updates the preference {@link Constants.Preferences.Keys#EXPORT_CSV_SEPARATOR}.
     * @param ctx The context when updating the preference.
     * @param exportType The {@link ExportCsvSeparator} which represents the users' choice for the CSV separator to use
     * during the export.
     */
    public static void setPreferredExportCSVSeparator(Context ctx, ExportCsvSeparator exportType) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(Constants.Preferences.Keys.EXPORT_CSV_SEPARATOR, exportType.toString());
        editor.commit();
    }

    /**
     * Get the preference for key {@link Constants.Preferences.Keys#EXPORT_DATA}. If no value is found for the
     * preference the default value will be {@link ExportData#REPORT}.
     * @param ctx The context when getting the preference for the data to be exported.
     * @return The {@link ExportData} that represents the user's choice (or if no choice available the system's default)
     * for the data to be exported.
     */
    public static ExportData getPreferredExportData(Context ctx) {
        String exportData = getSharedPreferences(ctx).getString(
                Constants.Preferences.Keys.EXPORT_DATA,
                ExportData.REPORT.toString()
        );
        return ExportData.valueOf(exportData);
    }

    /**
     * Updates the preference {@link Constants.Preferences.Keys#EXPORT_DATA}.
     * @param ctx The context when updating the preference.
     * @param exportData The {@link ExportData} which represents the users' choice for the data to be exported.
     */
    public static void setPreferredExportData(Context ctx, ExportData exportData) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(Constants.Preferences.Keys.EXPORT_DATA, exportData.toString());
        editor.commit();
    }

    /**
     * Get the preference for key {@link Constants.Preferences.Keys#TIME_REGISTRATION_SPLIT_DEFAULT_GAP}. If no value is
     * found for the preference the default value will be
     * {@link Constants.Preferences#TIME_REGISTRATION_SPLIT_DEFAULT_GAP_DEFAULT_VALUE}.
     * @param ctx The context when getting the preference for default split-gap in minutes.
     * @return The integer value that represents the user's choice (or if no choice available the system's default)
     * for the default split-gap in minutes.
     */
    public static int getTimeRegistrationSplitDefaultGap(Context ctx) {
        return getSharedPreferences(ctx).getInt(
                Constants.Preferences.Keys.TIME_REGISTRATION_SPLIT_DEFAULT_GAP,
                Constants.Preferences.TIME_REGISTRATION_SPLIT_DEFAULT_GAP_DEFAULT_VALUE
        );
    }

    /**
     * Updates the preference {@link Constants.Preferences.Keys#TIME_REGISTRATION_SPLIT_DEFAULT_GAP}.
     * @param ctx The context when updating the preference.
     * @param minutes The integer value that represents the user's choice for the default split-gap in minutes.
     */
    public static void setTimeRegistrationSplitDefaultGap(Context ctx, int minutes) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt(Constants.Preferences.Keys.TIME_REGISTRATION_SPLIT_DEFAULT_GAP, minutes);
        editor.commit();
    }
}
