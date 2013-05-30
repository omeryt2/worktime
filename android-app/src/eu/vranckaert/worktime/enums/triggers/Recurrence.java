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
 * Time: 9:35
 */
public enum Recurrence implements TranslatableEnum {
    DAILY(R.string.enum_recurrence_daily),
    WEEKLY(R.string.enum_recurrence_weekly);

    private int stringResId;

    private Recurrence(int stringResId) {
        this.stringResId = stringResId;
    }

    public String getStringValue(Context context) {
        return context.getString(stringResId);
    }

    public static List<String> getStringValues(Context context) {
        List<String> stringValues = new ArrayList<String>();

        Recurrence[] recurrences = Recurrence.values();
        for (Recurrence recurrence : recurrences) {
            stringValues.add(recurrence.getStringValue(context));
        }

        return stringValues;
    }
}