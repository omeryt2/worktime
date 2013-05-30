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

package eu.vranckaert.worktime.model.trigger;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import eu.vranckaert.worktime.enums.triggers.Recurrence;
import eu.vranckaert.worktime.model.Task;

import java.io.Serializable;
import java.util.Date;

/**
 * User: DIRK VRANCKAERT
 * Date: 30/05/13
 * Time: 10:55
 */
@DatabaseTable(tableName = "recurrence")
public class RecurrenceTrigger implements Serializable {
    @DatabaseField(generatedId = true, columnName = "id")
    private Integer id;
    @DatabaseField(columnName = "timeRegistrationStartTime", dataType = DataType.DATE_STRING, canBeNull = false)
    private Date timeRegistrationStartTime;
    @DatabaseField(columnName = "timeRegistrationEndTime", dataType = DataType.DATE_STRING, canBeNull = false)
    private Date timeRegistrationEndTime;
    @DatabaseField(foreign = true, columnName = "taskId", canBeNull = false)
    private Task task;

    @DatabaseField(columnName = "recurrence")
    private Recurrence recurrence;
    @DatabaseField(columnName = "monday")
    private boolean monday;
    @DatabaseField(columnName = "tuesday")
    private boolean tuesday;
    @DatabaseField(columnName = "wednesday")
    private boolean wednesday;
    @DatabaseField(columnName = "thursday")
    private boolean thursday;
    @DatabaseField(columnName = "friday")
    private boolean friday;
    @DatabaseField(columnName = "saturday")
    private boolean saturday;
    @DatabaseField(columnName = "sunday")
    private boolean sunday;

    @DatabaseField(columnName = "triggerStartDate", dataType = DataType.DATE_STRING, canBeNull = false)
    private Date triggerStartDate;
    @DatabaseField(columnName = "triggerEndDate", dataType = DataType.DATE_STRING, canBeNull = true)
    private Date triggerEndDate;
    @DatabaseField(columnName = "triggerEndTimes")
    private Integer triggerEndTimes;

    @DatabaseField(columnName = "timesTriggered")
    private int timesTriggered;
    @DatabaseField(columnName = "active", canBeNull = false, defaultValue = "1")
    private boolean active;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getTimeRegistrationStartTime() {
        return timeRegistrationStartTime;
    }

    public void setTimeRegistrationStartTime(Date timeRegistrationStartTime) {
        this.timeRegistrationStartTime = timeRegistrationStartTime;
    }

    public Date getTimeRegistrationEndTime() {
        return timeRegistrationEndTime;
    }

    public void setTimeRegistrationEndTime(Date timeRegistrationEndTime) {
        this.timeRegistrationEndTime = timeRegistrationEndTime;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Recurrence getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(Recurrence recurrence) {
        this.recurrence = recurrence;
    }

    public boolean isMonday() {
        return monday;
    }

    public void setMonday(boolean monday) {
        this.monday = monday;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public void setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public void setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
    }

    public boolean isThursday() {
        return thursday;
    }

    public void setThursday(boolean thursday) {
        this.thursday = thursday;
    }

    public boolean isFriday() {
        return friday;
    }

    public void setFriday(boolean friday) {
        this.friday = friday;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public void setSaturday(boolean saturday) {
        this.saturday = saturday;
    }

    public boolean isSunday() {
        return sunday;
    }

    public void setSunday(boolean sunday) {
        this.sunday = sunday;
    }

    public Date getTriggerStartDate() {
        return triggerStartDate;
    }

    public void setTriggerStartDate(Date triggerStartDate) {
        this.triggerStartDate = triggerStartDate;
    }

    public Date getTriggerEndDate() {
        return triggerEndDate;
    }

    public void setTriggerEndDate(Date triggerEndDate) {
        this.triggerEndDate = triggerEndDate;
    }

    public Integer getTriggerEndTimes() {
        return triggerEndTimes;
    }

    public void setTriggerEndTimes(Integer triggerEndTimes) {
        this.triggerEndTimes = triggerEndTimes;
    }

    public int getTimesTriggered() {
        return timesTriggered;
    }

    public void setTimesTriggered(int timesTriggered) {
        this.timesTriggered = timesTriggered;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
