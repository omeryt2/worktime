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

import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.enums.reporting.ReportingDisplayDuration;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.test.cases.TestCase;
import eu.vranckaert.worktime.utils.preferences.Preferences;
import eu.vranckaert.worktime.utils.preferences.TimePrecisionPreference;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.util.*;

/**
 * User: DIRK VRANCKAERT
 * Date: 20/01/12
 * Time: 10:09
 */
public class DateUtilsTimeCalculatorTest extends TestCase {
    public void testCalculateWeekBoundariesMiddleOfYearStartingSunday() {
        Preferences.setWeekStartsOn(ctx, 7);

        Calendar queryCalendar = Calendar.getInstance();
        queryCalendar.set(Calendar.DAY_OF_MONTH, 13);
        queryCalendar.set(Calendar.MONTH, 9);
        queryCalendar.set(Calendar.YEAR, 2011);

        int weekDiff = -1;

        int expectedDay1 = 2;
        int expectedMonth1 = 10;
        int expectedYear1 = 2011;

        int expectedDay2 = 8;
        int expectedMonth2 = 10;
        int expectedYear2 = 2011;

        Map<Integer, Date> result = DateUtils.TimeCalculator.calculateWeekBoundaries(weekDiff, queryCalendar.getTime(), ctx);

        assertTrue("Exactly 2 map-items are expected", result.size() == 2);

        Date firstDayOfWeek = result.get(DateConstants.FIRST_DAY_OF_WEEK);
        Date lastDayOfWeek = result.get(DateConstants.LAST_DAY_OF_WEEK);

        Calendar cal = Calendar.getInstance();

        cal.setTime(firstDayOfWeek);
        int day1 = cal.get(Calendar.DAY_OF_MONTH);
        int month1 = cal.get(Calendar.MONTH) + 1;
        int year1 = cal.get(Calendar.YEAR);

        assertEquals(expectedDay1, day1);
        assertEquals(expectedMonth1, month1);
        assertEquals(expectedYear1, year1);

        cal.setTime(lastDayOfWeek);
        int day2 = cal.get(Calendar.DAY_OF_MONTH);
        int month2 = cal.get(Calendar.MONTH) + 1;
        int year2 = cal.get(Calendar.YEAR);

        assertEquals(expectedDay2, day2);
        assertEquals(expectedMonth2, month2);
        assertEquals(expectedYear2, year2);
    }

    public void testCalculateWeekBoundariesFirstWeekOfYearStartingSunday() {
        Preferences.setWeekStartsOn(ctx, 7);

        Calendar queryCalendar = Calendar.getInstance();
        queryCalendar.set(Calendar.DAY_OF_MONTH, 4);
        queryCalendar.set(Calendar.MONTH, 0);
        queryCalendar.set(Calendar.YEAR, 2011);

        int weekDiff = -1;

        int expectedDay1 = 26;
        int expectedMonth1 = 12;
        int expectedYear1 = 2010;

        int expectedDay2 = 1;
        int expectedMonth2 = 1;
        int expectedYear2 = 2011;

        Map<Integer, Date> result = DateUtils.TimeCalculator.calculateWeekBoundaries(weekDiff, queryCalendar.getTime(), ctx);

        assertTrue("Exactly 2 map-items are expected", result.size() == 2);

        Date firstDayOfWeek = result.get(DateConstants.FIRST_DAY_OF_WEEK);
        Date lastDayOfWeek = result.get(DateConstants.LAST_DAY_OF_WEEK);

        Calendar cal = Calendar.getInstance();

        cal.setTime(firstDayOfWeek);
        int day1 = cal.get(Calendar.DAY_OF_MONTH);
        int month1 = cal.get(Calendar.MONTH) + 1;
        int year1 = cal.get(Calendar.YEAR);

        assertEquals(expectedDay1, day1);
        assertEquals(expectedMonth1, month1);
        assertEquals(expectedYear1, year1);

        cal.setTime(lastDayOfWeek);
        int day2 = cal.get(Calendar.DAY_OF_MONTH);
        int month2 = cal.get(Calendar.MONTH) + 1;
        int year2 = cal.get(Calendar.YEAR);

        assertEquals(expectedDay2, day2);
        assertEquals(expectedMonth2, month2);
        assertEquals(expectedYear2, year2);
    }

    public void testCalculateWeekBoundariesLastWeekOfYearStartingMonday() {
        Preferences.setWeekStartsOn(ctx, 7);

        Calendar queryCalendar = Calendar.getInstance();
        queryCalendar.set(Calendar.DAY_OF_MONTH, 31);
        queryCalendar.set(Calendar.MONTH, 11);
        queryCalendar.set(Calendar.YEAR, 2011);

        int weekDiff = 1;

        int expectedDay1 = 1;
        int expectedMonth1 = 1;
        int expectedYear1 = 2012;

        int expectedDay2 = 7;
        int expectedMonth2 = 1;
        int expectedYear2 = 2012;

        Map<Integer, Date> result = DateUtils.TimeCalculator.calculateWeekBoundaries(weekDiff, queryCalendar.getTime(), ctx);

        assertTrue("Exactly 2 map-items are expected", result.size() == 2);

        Date firstDayOfWeek = result.get(DateConstants.FIRST_DAY_OF_WEEK);
        Date lastDayOfWeek = result.get(DateConstants.LAST_DAY_OF_WEEK);

        Calendar cal = Calendar.getInstance();

        cal.setTime(firstDayOfWeek);
        int day1 = cal.get(Calendar.DAY_OF_MONTH);
        int month1 = cal.get(Calendar.MONTH) + 1;
        int year1 = cal.get(Calendar.YEAR);

        assertEquals(expectedDay1, day1);
        assertEquals(expectedMonth1, month1);
        assertEquals(expectedYear1, year1);

        cal.setTime(lastDayOfWeek);
        int day2 = cal.get(Calendar.DAY_OF_MONTH);
        int month2 = cal.get(Calendar.MONTH) + 1;
        int year2 = cal.get(Calendar.YEAR);

        assertEquals(expectedDay2, day2);
        assertEquals(expectedMonth2, month2);
        assertEquals(expectedYear2, year2);
    }

    public void testCalculateWeekBoundariesMiddleOfYearStartingMonday() {
        Preferences.setWeekStartsOn(ctx, 1);

        Calendar queryCalendar = Calendar.getInstance();
        queryCalendar.set(Calendar.DAY_OF_MONTH, 13);
        queryCalendar.set(Calendar.MONTH, 9);
        queryCalendar.set(Calendar.YEAR, 2011);

        int weekDiff = -1;

        int expectedDay1 = 3;
        int expectedMonth1 = 10;
        int expectedYear1 = 2011;

        int expectedDay2 = 9;
        int expectedMonth2 = 10;
        int expectedYear2 = 2011;

        Map<Integer, Date> result = DateUtils.TimeCalculator.calculateWeekBoundaries(weekDiff, queryCalendar.getTime(), ctx);

        assertTrue("Exactly 2 map-items are expected", result.size() == 2);

        Date firstDayOfWeek = result.get(DateConstants.FIRST_DAY_OF_WEEK);
        Date lastDayOfWeek = result.get(DateConstants.LAST_DAY_OF_WEEK);

        Calendar cal = Calendar.getInstance();

        cal.setTime(firstDayOfWeek);
        int day1 = cal.get(Calendar.DAY_OF_MONTH);
        int month1 = cal.get(Calendar.MONTH) + 1;
        int year1 = cal.get(Calendar.YEAR);

        assertEquals(expectedDay1, day1);
        assertEquals(expectedMonth1, month1);
        assertEquals(expectedYear1, year1);

        cal.setTime(lastDayOfWeek);
        int day2 = cal.get(Calendar.DAY_OF_MONTH);
        int month2 = cal.get(Calendar.MONTH) + 1;
        int year2 = cal.get(Calendar.YEAR);

        assertEquals(expectedDay2, day2);
        assertEquals(expectedMonth2, month2);
        assertEquals(expectedYear2, year2);
    }

    public void testCalculateWeekBoundariesMiddleOfYearStartingThursday() {
        Preferences.setWeekStartsOn(ctx, 4);

        Calendar queryCalendar = Calendar.getInstance();
        queryCalendar.set(Calendar.DAY_OF_MONTH, 13);
        queryCalendar.set(Calendar.MONTH, 9);
        queryCalendar.set(Calendar.YEAR, 2011);

        int weekDiff = -1;

        int expectedDay1 = 6;
        int expectedMonth1 = 10;
        int expectedYear1 = 2011;

        int expectedDay2 = 12;
        int expectedMonth2 = 10;
        int expectedYear2 = 2011;

        Map<Integer, Date> result = DateUtils.TimeCalculator.calculateWeekBoundaries(weekDiff, queryCalendar.getTime(), ctx);

        assertTrue("Exactly 2 map-items are expected", result.size() == 2);

        Date firstDayOfWeek = result.get(DateConstants.FIRST_DAY_OF_WEEK);
        Date lastDayOfWeek = result.get(DateConstants.LAST_DAY_OF_WEEK);

        Calendar cal = Calendar.getInstance();

        cal.setTime(firstDayOfWeek);
        int day1 = cal.get(Calendar.DAY_OF_MONTH);
        int month1 = cal.get(Calendar.MONTH) + 1;
        int year1 = cal.get(Calendar.YEAR);

        assertEquals(expectedDay1, day1);
        assertEquals(expectedMonth1, month1);
        assertEquals(expectedYear1, year1);

        cal.setTime(lastDayOfWeek);
        int day2 = cal.get(Calendar.DAY_OF_MONTH);
        int month2 = cal.get(Calendar.MONTH) + 1;
        int year2 = cal.get(Calendar.YEAR);

        assertEquals(expectedDay2, day2);
        assertEquals(expectedMonth2, month2);
        assertEquals(expectedYear2, year2);
    }
    
    private TimeRegistration dummyRegistration(int day, int month, int year, int startHour, int startMinute, int startSecond, int endHour, int endMinute, int endSecond) {
        TimeRegistration registration = new TimeRegistration();
        
        Calendar startCal = Calendar.getInstance();
        startCal.set(Calendar.DAY_OF_MONTH, day);
        startCal.set(Calendar.MONTH, month-1);
        startCal.set(Calendar.YEAR, year);
        startCal.set(Calendar.HOUR_OF_DAY, startHour);
        startCal.set(Calendar.MINUTE, startMinute);
        startCal.set(Calendar.SECOND, startSecond);
        
        registration.setStartTime(startCal.getTime());
        
        Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.DAY_OF_MONTH, day);
        endCal.set(Calendar.MONTH, month-1);
        endCal.set(Calendar.YEAR, year);
        endCal.set(Calendar.HOUR_OF_DAY, endHour);
        endCal.set(Calendar.MINUTE, endMinute);
        endCal.set(Calendar.SECOND, endSecond);
        
        registration.setEndTime(endCal.getTime());
        
        return registration;
    }
    
    public void testCalculatePeriodForSingleTimeRegistrationMinutePrecision() {
        setPreference(Constants.Preferences.Keys.TIME_PRECISION, TimePrecisionPreference.MINUTE.getValue());

        // From:
        //  20/07/2006, 10:00:00
        // Till:
        //  20/07/2006, 11:01:35
        // Duration:
        //  1h 1m
        TimeRegistration dummy = dummyRegistration(20, 7, 2006, 10, 0, 0, 11, 1, 35);

        String expectedResult = "1 " + ctx.getString(R.string.hours) + ", 1 " + ctx.getString(R.string.minutes);
        
        String duration = DateUtils.TimeCalculator.calculatePeriod(ctx, dummy);

        assertEquals(expectedResult, duration);
    }

    public void testCalculatePeriodForSingleTimeRegistrationSecondPrecision() {
        setPreference(Constants.Preferences.Keys.TIME_PRECISION, TimePrecisionPreference.SECOND.getValue());

        // From:
        //  20/07/2006, 10:00:00
        // Till:
        //  20/07/2006, 11:01:35
        // Duration:
        //  1h 1m 35s
        TimeRegistration dummy = dummyRegistration(20, 7, 2006, 10, 0, 0, 11, 1, 35);

        String expectedResult = "1 " + ctx.getString(R.string.hours) + ", 1 " + ctx.getString(R.string.minutes) + ", 35 " + ctx.getString(R.string.seconds);

        String duration = DateUtils.TimeCalculator.calculatePeriod(ctx, dummy);

        assertEquals(expectedResult, duration);
    }

    public void testCalculatePeriodForSecondPrecision() {
        setPreference(Constants.Preferences.Keys.TIME_PRECISION, TimePrecisionPreference.SECOND.getValue());

        // Duration:
        //  2h 42m 9s
        TimeRegistration dummy = dummyRegistration(16, 7, 1987, 6, 29, 58, 9, 12, 7);

        int expectedHours = 2;
        int expectedMinutes = 42;
        int expectedSeconds = 9;

        Period period = DateUtils.TimeCalculator.calculatePeriod(ctx, dummy.getStartTime(), dummy.getEndTime(), PeriodType.time());

        int hours = period.getHours();
        int minutes = period.getMinutes();
        int seconds = period.getSeconds();

        assertEquals(expectedHours, hours);
        assertEquals(expectedMinutes, minutes);
        assertEquals(expectedSeconds, seconds);
    }

    public void testCalculatePeriodForMinutePrecision() {
        setPreference(Constants.Preferences.Keys.TIME_PRECISION, TimePrecisionPreference.MINUTE.getValue());

        // Duration:
        //  2h 42m 9s
        TimeRegistration dummy = dummyRegistration(16, 7, 1987, 6, 29, 58, 9, 12, 7);

        int expectedHours = 2;
        int expectedMinutes = 43;
        int expectedSeconds = 0;

        Period period = DateUtils.TimeCalculator.calculatePeriod(ctx, dummy.getStartTime(), dummy.getEndTime(), PeriodType.time());

        int hours = period.getHours();
        int minutes = period.getMinutes();
        int seconds = period.getSeconds();

        assertEquals(expectedHours, hours);
        assertEquals(expectedMinutes, minutes);
        assertEquals(expectedSeconds, seconds);
    }

    public void testCalculatePeriodForSecondPrecisionForMultipleTimeRegistrationsFor8Hours() {
        setPreference(Constants.Preferences.Keys.TIME_PRECISION, TimePrecisionPreference.SECOND.getValue());
        
        // Duration:
        //  10h 1m 35s
        TimeRegistration dummy1 = dummyRegistration(20, 7, 2006, 10, 0, 0, 20, 1, 35);
        // Duration:
        //  2h 42m 9s
        TimeRegistration dummy2 = dummyRegistration(16, 7, 1987, 6, 29, 58, 9, 12, 7);
        // Total duration:
        // 12h 43m 44s
        // Total duration in 8-hour format
        // 1d 4h 43m 44s
        
        List<TimeRegistration> timeRegistrations = new ArrayList<TimeRegistration>();
        timeRegistrations.add(dummy1);
        timeRegistrations.add(dummy2);

        String expectedResult = "01d 04h 43m 44s";
        
        String result = DateUtils.TimeCalculator.calculatePeriod(ctx, timeRegistrations,  ReportingDisplayDuration.DAYS_HOUR_MINUTES_SECONDS_08H);

        assertEquals(expectedResult, result);
    }

    public void testCalculatePeriodForSecondPrecisionForMultipleTimeRegistrationsFor24Hours() {
        setPreference(Constants.Preferences.Keys.TIME_PRECISION, TimePrecisionPreference.SECOND.getValue());

        // Duration:
        //  10h 1m 35s
        TimeRegistration dummy1 = dummyRegistration(20, 7, 2006, 10, 0, 0, 20, 1, 35);
        // Duration:
        //  2h 42m 9s
        TimeRegistration dummy2 = dummyRegistration(16, 7, 1987, 6, 29, 58, 9, 12, 7);
        // Total duration:
        // 12h 43m 44s

        List<TimeRegistration> timeRegistrations = new ArrayList<TimeRegistration>();
        timeRegistrations.add(dummy1);
        timeRegistrations.add(dummy2);

        String expectedResult = "12h 43m 44s";

        String result = DateUtils.TimeCalculator.calculatePeriod(ctx, timeRegistrations,  ReportingDisplayDuration.DAYS_HOUR_MINUTES_SECONDS_24H);

        assertEquals(expectedResult, result);
    }

    public void testCalculatePeriodForMinutePrecisionForMultipleTimeRegistrationsFor24Hours() {
        setPreference(Constants.Preferences.Keys.TIME_PRECISION, TimePrecisionPreference.MINUTE.getValue());

        // Duration:
        //  10h 1m 35s
        TimeRegistration dummy1 = dummyRegistration(20, 7, 2006, 10, 0, 0, 20, 1, 35);
        // Duration:
        //  2h 42m 9s
        TimeRegistration dummy2 = dummyRegistration(16, 7, 1987, 6, 29, 58, 9, 12, 7);
        // Total duration:
        // 12h 43m 44s

        List<TimeRegistration> timeRegistrations = new ArrayList<TimeRegistration>();
        timeRegistrations.add(dummy1);
        timeRegistrations.add(dummy2);

        String expectedResult = "12h 44m";

        String result = DateUtils.TimeCalculator.calculatePeriod(ctx, timeRegistrations,  ReportingDisplayDuration.DAYS_HOUR_MINUTES_SECONDS_24H);

        assertEquals(expectedResult, result);
    }

    public void testCalculatePeriodForSecondPrecisionForMultipleTimeRegistrationsForMinutesSeconds() {
        setPreference(Constants.Preferences.Keys.TIME_PRECISION, TimePrecisionPreference.SECOND.getValue());

        // Duration:
        //  10h 1m 35s
        TimeRegistration dummy1 = dummyRegistration(20, 7, 2006, 10, 0, 0, 20, 1, 35);
        // Duration:
        //  2h 42m 9s
        TimeRegistration dummy2 = dummyRegistration(16, 7, 1987, 6, 29, 58, 9, 12, 7);
        // Total duration:
        // 12h 43m 44s

        List<TimeRegistration> timeRegistrations = new ArrayList<TimeRegistration>();
        timeRegistrations.add(dummy1);
        timeRegistrations.add(dummy2);

        String expectedResult = "12h 43m 44s";

        String result = DateUtils.TimeCalculator.calculatePeriod(ctx, timeRegistrations,  ReportingDisplayDuration.HOUR_MINUTES_SECONDS);

        assertEquals(expectedResult, result);
    }
}
