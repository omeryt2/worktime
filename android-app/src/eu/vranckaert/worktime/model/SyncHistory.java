/*
 * Copyright 2013 Dirk Vranckaert
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

package eu.vranckaert.worktime.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * User: Dirk Vranckaert
 * Date: 11/01/13
 * Time: 09:42
 */
@DatabaseTable
public class SyncHistory {
    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(dataType = DataType.DATE_STRING)
    private Date started;
    @DatabaseField(dataType = DataType.DATE_STRING)
    private Date ended;
    @DatabaseField(dataType = DataType.ENUM_STRING)
    private SyncHistoryStatus status;
    @DatabaseField(dataType = DataType.ENUM_STRING)
    private SyncHistoryAction action;
    @DatabaseField
    private String failureReason;

    public SyncHistory() {
        this.started = new Date();
        this.status = SyncHistoryStatus.BUSY;
        this.action = SyncHistoryAction.CHECK_DEVICE;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getStarted() {
        return started;
    }

    public void setStarted(Date started) {
        this.started = started;
    }

    public Date getEnded() {
        return ended;
    }

    public void setEnded(Date ended) {
        this.ended = ended;
    }

    public SyncHistoryStatus getStatus() {
        return status;
    }

    public void setStatus(SyncHistoryStatus status) {
        this.status = status;
    }

    public SyncHistoryAction getAction() {
        return action;
    }

    public void setAction(SyncHistoryAction action) {
        this.action = action;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
}
