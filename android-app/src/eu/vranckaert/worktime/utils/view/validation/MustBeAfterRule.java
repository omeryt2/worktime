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
import com.mobsandgeeks.saripaar.Rule;

import java.util.Date;

/**
 * User: DIRK VRANCKAERT
 * Date: 24/05/13
 * Time: 13:43
 */
public class MustBeAfterRule extends Rule<View> {
    private Date date;
    private Date limit;
    private boolean greaterThanOrEqual;
    /**
     * Creates a new custom_validation Rule.
     *
     * @param failureMessage The failure message associated with the Rule.
     */
    public MustBeAfterRule(String failureMessage, Date date, Date limit, boolean greaterThanOrEqual) {
        super(failureMessage);
        this.date = date;
        this.limit = limit;
        this.greaterThanOrEqual = greaterThanOrEqual;
    }

    @Override
    public boolean isValid(View view) {
        boolean greaterThan = date.after(limit);
        boolean equals = date.equals(limit);

        if (greaterThanOrEqual) {
            return greaterThan || equals;
        } else {
            return greaterThan;
        }
    }
}
