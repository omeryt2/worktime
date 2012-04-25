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
package eu.vranckaert.worktime.activities.preferences;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.view.View;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.constants.TrackerConstants;
import eu.vranckaert.worktime.utils.context.IntentUtil;
import eu.vranckaert.worktime.utils.preferences.Preferences;
import eu.vranckaert.worktime.utils.tracker.AnalyticsTracker;
import roboguice.activity.GuicePreferenceActivity;

/**
 * User: DIRK VRANCKAERT
 * Date: 05/02/11
 * Time: 19:09
 */
public class PreferencesActivity extends GuicePreferenceActivity {
    private static final String LOG_TAG = PreferencesActivity.class.getSimpleName();

    private AnalyticsTracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        tracker = AnalyticsTracker.getInstance(getApplicationContext());
        tracker.trackPageView(TrackerConstants.PageView.PREFERENCES_ACTIVITY);

        configurePreferences(PreferencesActivity.this);
        createPreferences(PreferencesActivity.this);
    }

    private void configurePreferences(GuicePreferenceActivity ctx) {
        ctx.getPreferenceManager().setSharedPreferencesName(Constants.Preferences.PREFERENCES_NAME);
    }

    private void createPreferences(GuicePreferenceActivity ctx) {
        PreferenceScreen preferences = ctx.getPreferenceManager().createPreferenceScreen(ctx);
        setPreferenceScreen(preferences);

        //Category TIME REGISTRATIONS
        createCategoryButton(ctx, preferences, R.string.pref_time_registrations_category_title, TimeRegistrationsPreferencesActivity.class);

        //Category PROJECTS AND TASKS
        createCategoryButton(ctx, preferences, R.string.pref_projects_tasks_category_title, ProjectsAndTasksPreferencesActivity.class);

        //Category DATE AND TIME
        createCategoryButton(ctx, preferences, R.string.pref_date_and_time_category_title, DateTimePreferencesActivity.class);

        //Category WIDGET
        createCategoryButton(ctx, preferences, R.string.pref_widget_category_title, WidgetPreferencesActivity.class);

        //Category NOTIFICATIONS
        createCategoryButton(ctx, preferences, R.string.pref_stat_bar_notifs_category_title, NotificationsPreferencesActivity.class);

        //Category BACKUP
        createCategoryButton(ctx, preferences, R.string.pref_backup_category_title, BackupPreferencesActivity.class);

        //Option RESET ALL PREFERENCES
        Preference resetItem = new Preference(ctx);
        resetItem.setTitle(R.string.pref_reset_category_title);
        resetItem.setSummary(R.string.pref_reset_category_summary);
        preferences.addPreference(resetItem);
        resetItem.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Preferences.removeAllPreferences(PreferencesActivity.this);
                return true;
            }
        });
    }

    private void createCategoryButton(final Context ctx, final PreferenceScreen preferences,
                                      final int textResId, final Class preferenceActivity) {
        Preference preferencesItem = new Preference(ctx);
        preferencesItem.setTitle(textResId);
        preferences.addPreference(preferencesItem);
        preferencesItem.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(PreferencesActivity.this, preferenceActivity);
                startActivity(intent);
                return true;
            }
        });
    }

    public void onHomeClick(View view) {
        IntentUtil.goHome(this);
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tracker.stopSession();
    }
}
