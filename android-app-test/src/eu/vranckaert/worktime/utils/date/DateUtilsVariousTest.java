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

package eu.vranckaert.worktime.utils.date;

import android.test.AndroidTestCase;

import java.util.Calendar;
import java.util.Date;

/**
 * User: DIRK VRANCKAERT
 * Date: 20/01/12
 * Time: 14:44
 */
public class DateUtilsVariousTest extends AndroidTestCase {
    public void testResetToMidnight() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 555);
        calendar.set(Calendar.SECOND, 34);
        calendar.set(Calendar.MINUTE, 31);
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        
        Date result = DateUtils.Various.setMinTimeValueOfDay(calendar.getTime());
        
        Calendar resultCal = Calendar.getInstance();
        resultCal.setTime(result);

        assertEquals("Milliseconds should be zero", resultCal.get(Calendar.MILLISECOND), 0);
        assertEquals("Seconds should be zero", resultCal.get(Calendar.SECOND), 0);
        assertEquals("Minutes should be zero", resultCal.get(Calendar.MINUTE), 0);
        assertEquals("Hour should be zero", resultCal.get(Calendar.HOUR), 0);
        assertEquals("HourOfDay should be zero", resultCal.get(Calendar.HOUR_OF_DAY), 0);
    }
}
