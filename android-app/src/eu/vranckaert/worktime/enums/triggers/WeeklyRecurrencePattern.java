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

package eu.vranckaert.worktime.enums.triggers;

import android.content.Context;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.enums.TranslatableEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 27/05/13
 * Time: 11:07
 */
public enum WeeklyRecurrencePattern implements TranslatableEnum {
    EVERY_DAY(R.string.enum_weekly_recurrence_pattern_every_day),
    WEEKDAYS(R.string.enum_weekly_recurrence_pattern_weekdays),
    WEEKEND(R.string.enum_weekly_recurrence_pattern_weekend),
    CUSTOM(R.string.enum_weekly_recurrence_pattern_custom);

    private int stringResId;

    private WeeklyRecurrencePattern(int stringResId) {
        this.stringResId = stringResId;
    }

    public String getStringValue(Context context) {
        return context.getString(stringResId);
    }

    public static List<String> getStringValues(Context context) {
        List<String> stringValues = new ArrayList<String>();

        WeeklyRecurrencePattern[] recurrencePatterns = WeeklyRecurrencePattern.values();
        for (WeeklyRecurrencePattern recurrencePattern : recurrencePatterns) {
            stringValues.add(recurrencePattern.getStringValue(context));
        }

        return stringValues;
    }
}
