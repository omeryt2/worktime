/*
 *  Copyright 2012 Dirk Vranckaert
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package eu.vranckaert.worktime.test;

import java.util.Calendar;
import java.util.Date;

import static junit.framework.Assert.assertEquals;

/**
 * User: DIRK VRANCKAERT
 * Date: 26/03/12
 * Time: 15:07
 */
public class Assert {
    public static void assertSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        assertEquals("The years should be equal", cal1.get(Calendar.YEAR), cal2.get(Calendar.YEAR));
        assertEquals("The month should be equal", cal1.get(Calendar.MONTH), cal2.get(Calendar.MONTH));
        assertEquals("The day of month should be equal", cal1.get(Calendar.DAY_OF_MONTH), cal2.get(Calendar.DAY_OF_MONTH));
        assertEquals("The day of week should be equal", cal1.get(Calendar.DAY_OF_WEEK), cal2.get(Calendar.DAY_OF_WEEK));
        assertEquals("The day of week in month should be equal", cal1.get(Calendar.DAY_OF_WEEK_IN_MONTH), cal2.get(Calendar.DAY_OF_WEEK_IN_MONTH));
        assertEquals("The day of year should be equal", cal1.get(Calendar.DAY_OF_YEAR), cal2.get(Calendar.DAY_OF_YEAR));
        assertEquals("The hour should be equal", cal1.get(Calendar.HOUR), cal2.get(Calendar.HOUR));
        assertEquals("The hour of day should be equal", cal1.get(Calendar.HOUR_OF_DAY), cal2.get(Calendar.HOUR_OF_DAY));
        assertEquals("The minute should be equal", cal1.get(Calendar.MINUTE), cal2.get(Calendar.MINUTE));
        assertEquals("The second should be equal", cal1.get(Calendar.SECOND), cal2.get(Calendar.SECOND));
        assertEquals("The milisecond should be equal", cal1.get(Calendar.MILLISECOND), cal2.get(Calendar.MILLISECOND));
    }
}
