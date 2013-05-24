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

import android.view.View;
import com.mobsandgeeks.saripaar.Rule;

import java.util.Date;

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
}
