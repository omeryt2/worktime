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

package eu.vranckaert.worktime.activities.triggers;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.inject.Inject;
import com.mobsandgeeks.saripaar.Rules;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.enums.triggers.Recurrence;
import eu.vranckaert.worktime.enums.triggers.RecurrenceEnd;
import eu.vranckaert.worktime.enums.triggers.WeeklyRecurrencePattern;
import eu.vranckaert.worktime.model.trigger.RecurrenceTrigger;
import eu.vranckaert.worktime.service.RecurrenceService;
import eu.vranckaert.worktime.utils.context.IntentUtil;
import eu.vranckaert.worktime.utils.date.DateUtils;
import eu.vranckaert.worktime.utils.preferences.Preferences;
import eu.vranckaert.worktime.utils.view.DateTimeSelectionButtonUtil;
import eu.vranckaert.worktime.utils.view.ProjectTaskSelectionUtil;
import eu.vranckaert.worktime.utils.view.SpinnerUtil;
import eu.vranckaert.worktime.utils.view.actionbar.RoboSherlockActivity;
import eu.vranckaert.worktime.utils.view.validation.WorkTimeRules;
import eu.vranckaert.worktime.utils.view.validation.WorkTimeValidator;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 27/05/13
 * Time: 9:11
 */
@ContentView(R.layout.activity_trigger_recurring_date_time_add_edit)
public class TriggerRecurringAddEditActivity extends RoboSherlockActivity {
    @InjectView(R.id.activity_trigger_recurring_date_time_recurrence) private Spinner recurrence;
    @InjectView(R.id.activity_trigger_recurring_date_time_tr_start_time) private Button trStartTime;
    @InjectView(R.id.activity_trigger_recurring_date_time_tr_end_time) private Button trEndTime;
    @InjectView(R.id.activity_trigger_recurring_date_time_daily_container) private View dailyContainer;
    @InjectView(R.id.activity_trigger_recurring_date_time_daily_only_weekdays) private CheckBox dailyWeekdaysOnly;
    @InjectView(R.id.activity_trigger_recurring_date_time_weekly_container) private View weeklyContainer;
    @InjectView(R.id.activity_trigger_recurring_date_time_weekly_pattern) private Spinner weeklyPattern;
    @InjectView(R.id.activity_trigger_recurring_date_time_starts_on_date_button) private Button startsOnDateButton;
    @InjectView(R.id.activity_trigger_recurring_date_time_ends_on) private Spinner endsOn;
    @InjectView(R.id.activity_trigger_recurring_date_time_ends_on_times) private View endsOnTimes;
    @InjectView(R.id.activity_trigger_recurring_date_time_ends_on_times_edit_text) private EditText endsOnTimesEditText;
    @InjectView(R.id.activity_trigger_recurring_date_time_ends_on_date) private View endsOnDate;
    @InjectView(R.id.activity_trigger_recurring_date_time_ends_on_date_button) private Button endsOnDateButton;

    @InjectView(R.id.activity_trigger_recurring_date_time_weekly_week_selection_textview_0) private TextView weekSelectionTextview0;
    @InjectView(R.id.activity_trigger_recurring_date_time_weekly_week_selection_textview_1) private TextView weekSelectionTextview1;
    @InjectView(R.id.activity_trigger_recurring_date_time_weekly_week_selection_textview_2) private TextView weekSelectionTextview2;
    @InjectView(R.id.activity_trigger_recurring_date_time_weekly_week_selection_textview_3) private TextView weekSelectionTextview3;
    @InjectView(R.id.activity_trigger_recurring_date_time_weekly_week_selection_textview_4) private TextView weekSelectionTextview4;
    @InjectView(R.id.activity_trigger_recurring_date_time_weekly_week_selection_textview_5) private TextView weekSelectionTextview5;
    @InjectView(R.id.activity_trigger_recurring_date_time_weekly_week_selection_textview_6) private TextView weekSelectionTextview6;
    @InjectView(R.id.activity_trigger_recurring_date_time_weekly_week_selection_checkbox_0) private CheckBox weekSelectionCheckbox0;
    @InjectView(R.id.activity_trigger_recurring_date_time_weekly_week_selection_checkbox_1) private CheckBox weekSelectionCheckbox1;
    @InjectView(R.id.activity_trigger_recurring_date_time_weekly_week_selection_checkbox_2) private CheckBox weekSelectionCheckbox2;
    @InjectView(R.id.activity_trigger_recurring_date_time_weekly_week_selection_checkbox_3) private CheckBox weekSelectionCheckbox3;
    @InjectView(R.id.activity_trigger_recurring_date_time_weekly_week_selection_checkbox_4) private CheckBox weekSelectionCheckbox4;
    @InjectView(R.id.activity_trigger_recurring_date_time_weekly_week_selection_checkbox_5) private CheckBox weekSelectionCheckbox5;
    @InjectView(R.id.activity_trigger_recurring_date_time_weekly_week_selection_checkbox_6) private CheckBox weekSelectionCheckbox6;

    @Inject private RecurrenceService recurrenceService;

    private SpinnerUtil<Recurrence> recurrenceSpinnerUtil;
    private SpinnerUtil<WeeklyRecurrencePattern> weeklyRecurrencePatternSpinnerUtil;
    private ProjectTaskSelectionUtil projectTaskSelectionUtil;
    private SpinnerUtil<RecurrenceEnd> endsOnSpinnerUtil;
    private List<TextView> weekSelectionTextViews;
    private List<CheckBox> weekSelectionCheckBoxes;
    private boolean checkBoxChangeCanBeTriggered;

    private DateTimeSelectionButtonUtil startTimeSelectionUtil;
    private DateTimeSelectionButtonUtil endTimeSelectionUtil;
    private DateTimeSelectionButtonUtil rangeStartDateSelectionUtil;
    private DateTimeSelectionButtonUtil rangeEndDateSelectionUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.lbl_trigger_recurring_add_edit_add_title);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Recurrence defaultRecurrence = Recurrence.DAILY;
        WeeklyRecurrencePattern defaultWeeklyRecurrencePattern = WeeklyRecurrencePattern.WEEKDAYS;
        RecurrenceEnd defaultRecurrenceEnd = RecurrenceEnd.DOESNT_END;

        initSpinners(defaultRecurrence, defaultWeeklyRecurrencePattern, defaultRecurrenceEnd);
        initDatePickers(null, null, null, null);
        setupWeekSelectionDays();
        updateViewOnRecurrenceSelected(defaultRecurrence);
        projectTaskSelectionUtil = ProjectTaskSelectionUtil.getInstance(this);
        updateViewOnRecurrenceEndSelected(defaultRecurrenceEnd);
    }

    private void initDatePickers(Date trStart, Date trEnd, Date rangeStart, Date rangeEnd) {
        startTimeSelectionUtil = DateTimeSelectionButtonUtil.getInstance(this, trStartTime, DateTimeSelectionButtonUtil.Type.TIME, trStart);
        endTimeSelectionUtil = DateTimeSelectionButtonUtil.getInstance(this, trEndTime, DateTimeSelectionButtonUtil.Type.TIME, trEnd);
        rangeStartDateSelectionUtil = DateTimeSelectionButtonUtil.getInstance(this, startsOnDateButton, DateTimeSelectionButtonUtil.Type.DATE, rangeStart);
        rangeEndDateSelectionUtil = DateTimeSelectionButtonUtil.getInstance(this, endsOnDateButton, DateTimeSelectionButtonUtil.Type.DATE, rangeEnd);
    }

    private void initSpinners(Recurrence recurrence, WeeklyRecurrencePattern weeklyRecurrencePattern, RecurrenceEnd recurrenceEnd) {
        recurrenceSpinnerUtil = new SpinnerUtil<Recurrence>(TriggerRecurringAddEditActivity.this.recurrence, this, Recurrence.values(), Recurrence.getStringValues(this), recurrence) {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, Recurrence recurrence, String listEntry) {
                updateViewOnRecurrenceSelected(recurrence);
            }
        };
        weeklyRecurrencePatternSpinnerUtil = new SpinnerUtil<WeeklyRecurrencePattern>(TriggerRecurringAddEditActivity.this.weeklyPattern, this, WeeklyRecurrencePattern.values(), WeeklyRecurrencePattern.getStringValues(this), weeklyRecurrencePattern) {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, WeeklyRecurrencePattern object, String listEntry) {
                if (weekSelectionCheckBoxes != null) {
                    if (object == WeeklyRecurrencePattern.EVERY_DAY) {
                        setWeeklyCheckboxes(true, true, true, true, true, true, true);
                    } else if (object == WeeklyRecurrencePattern.WEEKDAYS) {
                        setWeeklyCheckboxes(true, true, true, true, true, false, false);
                    } else if (object == WeeklyRecurrencePattern.WEEKEND) {
                        setWeeklyCheckboxes(false, false, false, false, false, true, true);
                    }
                }
            }
        };
        endsOnSpinnerUtil = new SpinnerUtil<RecurrenceEnd>(TriggerRecurringAddEditActivity.this.endsOn, this, RecurrenceEnd.values(), RecurrenceEnd.getStringValues(this), recurrenceEnd) {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, RecurrenceEnd object, String listEntry) {
                updateViewOnRecurrenceEndSelected(object);
            }
        };
    }

    private void setupWeekSelectionDays() {
        int weekStartsOn = Preferences.getWeekStartsOn(this) - 1;
        weekSelectionTextViews = new ArrayList<TextView>();
        weekSelectionCheckBoxes = new ArrayList<CheckBox>();
        weekSelectionTextViews.add(weekSelectionTextview0);
        weekSelectionTextViews.add(weekSelectionTextview1);
        weekSelectionTextViews.add(weekSelectionTextview2);
        weekSelectionTextViews.add(weekSelectionTextview3);
        weekSelectionTextViews.add(weekSelectionTextview4);
        weekSelectionTextViews.add(weekSelectionTextview5);
        weekSelectionTextViews.add(weekSelectionTextview6);
        weekSelectionCheckBoxes.add(weekSelectionCheckbox0);
        weekSelectionCheckBoxes.add(weekSelectionCheckbox1);
        weekSelectionCheckBoxes.add(weekSelectionCheckbox2);
        weekSelectionCheckBoxes.add(weekSelectionCheckbox3);
        weekSelectionCheckBoxes.add(weekSelectionCheckbox4);
        weekSelectionCheckBoxes.add(weekSelectionCheckbox5);
        weekSelectionCheckBoxes.add(weekSelectionCheckbox6);

        switch (weekStartsOn) {
            case 0: // MONDAY
                weekSelectionTextview0.setText(R.string.dayMondayShort);
                weekSelectionTextview1.setText(R.string.dayTuesdayShort);
                weekSelectionTextview2.setText(R.string.dayWednesdayShort);
                weekSelectionTextview3.setText(R.string.dayThursdayShort);
                weekSelectionTextview4.setText(R.string.dayFridayShort);
                weekSelectionTextview5.setText(R.string.daySaturdayShort);
                weekSelectionTextview6.setText(R.string.daySundayShort);
                break;
            case 1: // TUESDAY
                weekSelectionTextview6.setText(R.string.dayMondayShort);
                weekSelectionTextview0.setText(R.string.dayTuesdayShort);
                weekSelectionTextview1.setText(R.string.dayWednesdayShort);
                weekSelectionTextview2.setText(R.string.dayThursdayShort);
                weekSelectionTextview3.setText(R.string.dayFridayShort);
                weekSelectionTextview4.setText(R.string.daySaturdayShort);
                weekSelectionTextview5.setText(R.string.daySundayShort);
                break;
            case 2: // WEDNESDAY
                weekSelectionTextview5.setText(R.string.dayMondayShort);
                weekSelectionTextview6.setText(R.string.dayTuesdayShort);
                weekSelectionTextview0.setText(R.string.dayWednesdayShort);
                weekSelectionTextview1.setText(R.string.dayThursdayShort);
                weekSelectionTextview3.setText(R.string.dayFridayShort);
                weekSelectionTextview3.setText(R.string.daySaturdayShort);
                weekSelectionTextview4.setText(R.string.daySundayShort);
                break;
            case 3: // THURSDAY
                weekSelectionTextview4.setText(R.string.dayMondayShort);
                weekSelectionTextview5.setText(R.string.dayTuesdayShort);
                weekSelectionTextview6.setText(R.string.dayWednesdayShort);
                weekSelectionTextview0.setText(R.string.dayThursdayShort);
                weekSelectionTextview1.setText(R.string.dayFridayShort);
                weekSelectionTextview2.setText(R.string.daySaturdayShort);
                weekSelectionTextview3.setText(R.string.daySundayShort);
                break;
            case 4: // FRIDAY
                weekSelectionTextview3.setText(R.string.dayMondayShort);
                weekSelectionTextview4.setText(R.string.dayTuesdayShort);
                weekSelectionTextview5.setText(R.string.dayWednesdayShort);
                weekSelectionTextview6.setText(R.string.dayThursdayShort);
                weekSelectionTextview0.setText(R.string.dayFridayShort);
                weekSelectionTextview1.setText(R.string.daySaturdayShort);
                weekSelectionTextview2.setText(R.string.daySundayShort);
                break;
            case 5: // SATURDAY
                weekSelectionTextview2.setText(R.string.dayMondayShort);
                weekSelectionTextview3.setText(R.string.dayTuesdayShort);
                weekSelectionTextview4.setText(R.string.dayWednesdayShort);
                weekSelectionTextview5.setText(R.string.dayThursdayShort);
                weekSelectionTextview6.setText(R.string.dayFridayShort);
                weekSelectionTextview0.setText(R.string.daySaturdayShort);
                weekSelectionTextview1.setText(R.string.daySundayShort);
                break;
            case 6: // SUNDAY
                weekSelectionTextview1.setText(R.string.dayMondayShort);
                weekSelectionTextview2.setText(R.string.dayTuesdayShort);
                weekSelectionTextview3.setText(R.string.dayWednesdayShort);
                weekSelectionTextview4.setText(R.string.dayThursdayShort);
                weekSelectionTextview5.setText(R.string.dayFridayShort);
                weekSelectionTextview6.setText(R.string.daySaturdayShort);
                weekSelectionTextview0.setText(R.string.daySundayShort);
                break;
        }

        if (weekStartsOn > 0) {
            int shiftSize = 7 - weekStartsOn;

            List<TextView> shiftedTextViews = new ArrayList<TextView>();
            List<CheckBox> shiftedCheckBoxes = new ArrayList<CheckBox>();
            List<TextView> textViewForRemoval = new ArrayList<TextView>();
            List<CheckBox> checkBoxesForRemoval = new ArrayList<CheckBox>();
            for (int i=0; i<shiftSize; i++) {
                TextView textView = weekSelectionTextViews.get(i);
                CheckBox checkBox = weekSelectionCheckBoxes.get(i);
                shiftedTextViews.add(textView);
                shiftedCheckBoxes.add(checkBox);
                textViewForRemoval.add(textView);
                checkBoxesForRemoval.add(checkBox);
            }
            weekSelectionTextViews.removeAll(textViewForRemoval);
            weekSelectionCheckBoxes.removeAll(checkBoxesForRemoval);
            weekSelectionTextViews.addAll(shiftedTextViews);
            weekSelectionCheckBoxes.addAll(shiftedCheckBoxes);
        }

        for (CheckBox checkBox : weekSelectionCheckBoxes) {
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (checkBoxChangeCanBeTriggered) {
                        weeklyRecurrencePatternSpinnerUtil.setSelectedItem(WeeklyRecurrencePattern.CUSTOM);
                    }
                }
            });
        }
    }

    private void setWeeklyCheckboxes(boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, boolean saturday, boolean sunday) {
        checkBoxChangeCanBeTriggered = false;
        weekSelectionCheckBoxes.get(0).setChecked(monday);
        weekSelectionCheckBoxes.get(1).setChecked(tuesday);
        weekSelectionCheckBoxes.get(2).setChecked(wednesday);
        weekSelectionCheckBoxes.get(3).setChecked(thursday);
        weekSelectionCheckBoxes.get(4).setChecked(friday);
        weekSelectionCheckBoxes.get(5).setChecked(saturday);
        weekSelectionCheckBoxes.get(6).setChecked(sunday);
        checkBoxChangeCanBeTriggered = true;
    }

    private void updateViewOnRecurrenceSelected(Recurrence recurrence) {
        if (recurrence == Recurrence.DAILY) {
            dailyContainer.setVisibility(View.VISIBLE);
            weeklyContainer.setVisibility(View.GONE);
        } else if (recurrence == Recurrence.WEEKLY) {
            dailyContainer.setVisibility(View.GONE);
            weeklyContainer.setVisibility(View.VISIBLE);
        }
    }

    private void updateViewOnRecurrenceEndSelected(RecurrenceEnd recurrenceEnd) {
        if (recurrenceEnd == RecurrenceEnd.DOESNT_END) {
            endsOnTimes.setVisibility(View.GONE);
            endsOnDate.setVisibility(View.GONE);
        } else if (recurrenceEnd == RecurrenceEnd.AFTER_CERTAIN_TIMES) {
            endsOnTimes.setVisibility(View.VISIBLE);
            endsOnDate.setVisibility(View.GONE);
        } else if (recurrenceEnd == RecurrenceEnd.AFTER_CERTAIN_DATE) {
            endsOnTimes.setVisibility(View.GONE);
            endsOnDate.setVisibility(View.VISIBLE);
        }
    }

    private void validateAndSave() {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.setTime(new Date());
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        tomorrow.set(Calendar.HOUR_OF_DAY, 0);
        tomorrow.set(Calendar.MINUTE, 0);
        tomorrow.set(Calendar.SECOND, 0);
        tomorrow.set(Calendar.MILLISECOND, 0);

        WorkTimeValidator validator = new WorkTimeValidator(this);
        validator.put(trStartTime, WorkTimeRules.objectRequired(getString(R.string.lbl_trigger_recurring_add_edit_error_required_start_time), startTimeSelectionUtil.getSelectedDateOrTime()));
        validator.put(trEndTime, WorkTimeRules.objectRequired(getString(R.string.lbl_trigger_recurring_add_edit_error_required_end_time), endTimeSelectionUtil.getSelectedDateOrTime()));
        validator.put(trEndTime, WorkTimeRules.mustBeAfter(getString(R.string.lbl_trigger_recurring_add_edit_error_end_after_start), endTimeSelectionUtil.getSelectedDateOrTime(), startTimeSelectionUtil.getSelectedDateOrTime(), false));
        validator.put(findViewById(R.id.task_selection), WorkTimeRules.objectRequired(getString(R.string.lbl_trigger_recurring_add_edit_error_required_task), projectTaskSelectionUtil.getSelectedTask()));
        if (recurrenceSpinnerUtil.getSelectedItem().equals(Recurrence.WEEKLY)) {
            validator.put(weeklyContainer, WorkTimeRules.atLeastOneChecked(getString(R.string.lbl_trigger_recurring_add_edit_error_required_at_least_one_week_day), true, weekSelectionCheckBoxes));
        }
        validator.put(startsOnDateButton, WorkTimeRules.objectRequired(getString(R.string.lbl_trigger_recurring_add_edit_error_required_start_date), rangeStartDateSelectionUtil.getSelectedDateOrTime()));
        if (endsOnSpinnerUtil.getSelectedItem().equals(RecurrenceEnd.AFTER_CERTAIN_DATE)) {
            validator.put(endsOnDateButton, WorkTimeRules.objectRequired(getString(R.string.lbl_trigger_recurring_add_edit_error_required_end_date), rangeEndDateSelectionUtil.getSelectedDateOrTime()));
            validator.put(endsOnDateButton, WorkTimeRules.mustBeAfter(getString(R.string.lbl_trigger_recurring_add_edit_error_range_end_date_after_range_start_date), rangeEndDateSelectionUtil.getSelectedDateOrTime(), rangeStartDateSelectionUtil.getSelectedDateOrTime(), false));
        } else if (endsOnSpinnerUtil.getSelectedItem().equals(RecurrenceEnd.AFTER_CERTAIN_TIMES)) {
            validator.put(endsOnTimesEditText, Rules.required(getString(R.string.lbl_trigger_recurring_add_edit_error_required_end_after_times), true));
        }
        validator.validate();
    }

    private void save() {
        RecurrenceTrigger trigger = new RecurrenceTrigger();
        trigger.setTriggerStartDate(rangeStartDateSelectionUtil.getSelectedDateOrTime());
        if (endsOnSpinnerUtil.getSelectedItem().equals(RecurrenceEnd.AFTER_CERTAIN_DATE)) {
            trigger.setTriggerEndDate(DateUtils.DateTimeConverter.convertToDateOnly(rangeEndDateSelectionUtil.getSelectedDateOrTime(), true));
        } else if (endsOnSpinnerUtil.getSelectedItem().equals(RecurrenceEnd.AFTER_CERTAIN_TIMES)) {
            trigger.setTriggerEndTimes(Integer.parseInt(endsOnTimesEditText.getText().toString()));
        }

        trigger.setTimeRegistrationStartTime(startTimeSelectionUtil.getSelectedDateOrTime());
        trigger.setTimeRegistrationEndTime(endTimeSelectionUtil.getSelectedDateOrTime());

        trigger.setTask(projectTaskSelectionUtil.getSelectedTask());
        trigger.setRecurrence(recurrenceSpinnerUtil.getSelectedItem());
        if (trigger.getRecurrence().equals(Recurrence.DAILY)) {
            trigger.setMonday(true);
            trigger.setTuesday(true);
            trigger.setWednesday(true);
            trigger.setThursday(true);
            trigger.setFriday(true);
            if (!dailyWeekdaysOnly.isChecked()) {
                trigger.setSaturday(true);
                trigger.setSunday(true);
            } else {
                trigger.setSaturday(false);
                trigger.setSunday(false);
            }
        } else {
            trigger.setMonday(weekSelectionCheckBoxes.get(0).isChecked());
            trigger.setTuesday(weekSelectionCheckBoxes.get(1).isChecked());
            trigger.setWednesday(weekSelectionCheckBoxes.get(2).isChecked());
            trigger.setThursday(weekSelectionCheckBoxes.get(3).isChecked());
            trigger.setFriday(weekSelectionCheckBoxes.get(4).isChecked());
            trigger.setSaturday(weekSelectionCheckBoxes.get(5).isChecked());
            trigger.setSunday(weekSelectionCheckBoxes.get(6).isChecked());
        }
        trigger.setActive(true);

        recurrenceService.save(trigger);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.ab_activity_trigger_recurring_add_edit_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                IntentUtil.goBack(this);
                break;
            }
            case R.id.menu_trigger_recurring_activity_add_edit: {
                validateAndSave();
                break;
            }
        }

        return true;
    }
}
