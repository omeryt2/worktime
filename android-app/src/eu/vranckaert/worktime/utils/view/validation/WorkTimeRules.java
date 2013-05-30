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

package eu.vranckaert.worktime.utils.view.validation;

import android.view.View;
import android.widget.CheckBox;
import com.mobsandgeeks.saripaar.Rule;

import java.util.Date;
import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 24/05/13
 * Time: 13:34
 */
public class WorkTimeRules {
    public static Rule<View> objectRequired(final String failureMessage, final Object object) {
        return new ObjectRequiredRule(failureMessage, object);
    }

    public static Rule<View> mustBeAfter(String failureMessage, Date date, Date limit, boolean greaterThanOrEqual) {
        return new MustBeAfterRule(failureMessage, date, limit, greaterThanOrEqual);
    }

    public static Rule<View> atLeastOneChecked(final String failureMessage, final boolean checked, final List<CheckBox> checkBoxes) {
        return new Rule<View>(failureMessage) {
            @Override
            public boolean isValid(View view) {
                int countChecked = 0;
                int countUnchecked = 0;

                for (CheckBox checkBox : checkBoxes) {
                    if (checkBox.isChecked()) {
                        countChecked ++;
                    } else {
                        countUnchecked ++;
                    }
                }

                if (checked && countChecked > 0) {
                    return true;
                }

                if (!checked && countUnchecked > 0) {
                    return true;
                }

                return false;
            }
        };
    }
}
