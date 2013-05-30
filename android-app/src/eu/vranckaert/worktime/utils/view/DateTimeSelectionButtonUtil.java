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

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.utils.date.DateFormat;
import eu.vranckaert.worktime.utils.date.DateUtils;
import eu.vranckaert.worktime.utils.date.TimeFormat;

import java.util.Calendar;
import java.util.Date;

/**
 * User: DIRK VRANCKAERT
 * Date: 30/05/13
 * Time: 9:32
 */
public class DateTimeSelectionButtonUtil {
    private Date selectedDate = null;
    private Type type;

    private DateTimeSelectionButtonUtil(Context context, Button button, Type type, Date defaultValue, Integer buttonTextResId) {
        this.type = type;

        if (buttonTextResId == null && defaultValue == null) {
            switch (type) {
                case DATE:
                    button.setText(R.string.date_time_selection_button_text_date);
                    break;
                case TIME:
                    button.setText(R.string.date_time_selection_button_text_time);
                    break;
            }
        } else if (buttonTextResId != null && defaultValue == null) {
            button.setText(buttonTextResId);
        } else if (defaultValue != null) {
            this.selectedDate = defaultValue;
            switch (type) {
                case DATE:
                    button.setText(DateUtils.DateTimeConverter.convertDateToString(defaultValue, DateFormat.MEDIUM, context));
                    break;
                case TIME:
                    button.setText(DateUtils.DateTimeConverter.convertTimeToString(defaultValue, TimeFormat.MEDIUM, context));
                    break;
            }
        }

        switch (type) {
            case DATE:
                button.setOnClickListener(new DateClickListener(context, button));
                break;
            case TIME:
                button.setOnClickListener(new TimeClickListener(context, button));
                break;
        }
    }

    public static DateTimeSelectionButtonUtil getInstance(Context context, Button button, Type type, Date defaultValue, Integer buttonTextResId) {
        return new DateTimeSelectionButtonUtil(context, button, type, defaultValue, buttonTextResId);
    }

    public static DateTimeSelectionButtonUtil getInstance(Context context, Button button, Type type, Date defaultValue) {
        return getInstance(context, button, type, defaultValue, null);
    }

    public enum Type {
        DATE,
        TIME;
    }

    private class DateClickListener implements View.OnClickListener {
        private Context context;
        private Button button;
        public DateClickListener(Context context, Button button) {
            this.context = context;
            this.button = button;
        }

        @Override
        public void onClick(View v) {
            Date selection = selectedDate;
            if (selection == null) {
                selection = new Date();
            }
            Calendar selectionCalendar = Calendar.getInstance();
            selectionCalendar.setTime(selection);

            DatePickerDialog dialog = new DatePickerDialog(
                    context,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year, monthOfYear, dayOfMonth);
                            selectedDate = calendar.getTime();
                            button.setText(DateUtils.DateTimeConverter.convertDateToString(selectedDate, DateFormat.MEDIUM, context));
                        }
                    },
                    selectionCalendar.get(Calendar.YEAR),
                    selectionCalendar.get(Calendar.MONTH),
                    selectionCalendar.get(Calendar.DAY_OF_MONTH)
            );
            dialog.show();
        }
    }

    private class TimeClickListener implements View.OnClickListener {
        private Context context;
        private Button button;
        public TimeClickListener(Context context, Button button) {
            this.context = context;
            this.button = button;
        }

        @Override
        public void onClick(View v) {
            Date selection = selectedDate;
            if (selection == null) {
                selection = new Date();
            }
            Calendar selectionCalendar = Calendar.getInstance();
            selectionCalendar.setTime(selection);

            TimePickerDialog dialog = new TimePickerDialog(
                    context,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendar.set(Calendar.MINUTE, minute);
                            selectedDate = calendar.getTime();
                            button.setText(DateUtils.DateTimeConverter.convertTimeToString(selectedDate, TimeFormat.MEDIUM, context));
                        }
                    },
                    selectionCalendar.get(Calendar.HOUR_OF_DAY),
                    selectionCalendar.get(Calendar.MINUTE),
                    DateUtils.System.is24HourClock(context)
            );
            dialog.show();
        }
    }

    public Date getSelectedDateOrTime() {
        if (selectedDate == null) {
            return null;
        }

        switch (type) {
            case DATE:
                return DateUtils.DateTimeConverter.convertToDateOnly(selectedDate, false);
            case TIME:
                return DateUtils.DateTimeConverter.convertToTimeOnly(selectedDate, false);
            default:
                return null;
        }
    }
}
