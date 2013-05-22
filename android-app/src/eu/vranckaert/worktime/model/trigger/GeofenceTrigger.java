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

import com.google.android.gms.maps.model.LatLng;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import eu.vranckaert.worktime.model.Task;

import java.io.Serializable;
import java.util.Date;

/**
 * Date: 17/05/13
 * Time: 15:50
 *
 * @author Dirk Vranckaert
 */
@DatabaseTable
public class GeofenceTrigger implements Serializable {
    @DatabaseField(generatedId = true, columnName = "id")
    private Integer id;
    @DatabaseField(columnName = "geofenceRequestId", unique = true, canBeNull = false)
    private String geofenceRequestId;
    @DatabaseField(columnName = "name", unique = true, canBeNull = false)
    private String name;
    @DatabaseField(columnName = "expirationDate", dataType = DataType.DATE_STRING)
    private Date expirationDate;
    @DatabaseField(columnName = "latitude", canBeNull = false)
    private double latitude;
    @DatabaseField(columnName = "longitude", canBeNull = false)
    private double longitude;
    @DatabaseField(columnName = "radius", canBeNull = false)
    private double radius;
    @DatabaseField(columnName = "entered", canBeNull = false)
    private boolean entered;
    @DatabaseField(foreign = true, columnName = "taskId", canBeNull = false)
    private Task task;

    public GeofenceTrigger() {}

    public GeofenceTrigger(String name, Date expirationDate, LatLng latLng, double radius, Task task) {
        this.name = name;
        this.expirationDate = expirationDate;
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
        this.radius = radius;
        this.entered = false;
        this.task = task;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGeofenceRequestId() {
        return geofenceRequestId;
    }

    public void setGeofenceRequestId(String geofenceRequestId) {
        this.geofenceRequestId = geofenceRequestId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public boolean isEntered() {
        return entered;
    }

    public void setEntered(boolean entered) {
        this.entered = entered;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
