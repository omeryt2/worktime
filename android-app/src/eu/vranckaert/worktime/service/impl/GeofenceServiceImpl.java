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

package eu.vranckaert.worktime.service.impl;

import com.google.inject.Inject;
import eu.vranckaert.worktime.dao.GeofenceDao;
import eu.vranckaert.worktime.exceptions.worktime.trigger.geofence.DuplicateGeofenceNameException;
import eu.vranckaert.worktime.model.trigger.Geofence;
import eu.vranckaert.worktime.service.GeofenceService;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * User: DIRK VRANCKAERT
 * Date: 21/05/13
 * Time: 8:03
 */
public class GeofenceServiceImpl implements GeofenceService {
    private static final String LOG_TAG = GeofenceServiceImpl.class.getSimpleName();

    @Inject
    private GeofenceDao dao;

    @Override
    public Geofence storeGeofence(Geofence geofence) throws DuplicateGeofenceNameException {
        if (dao.getGeofencesByNameCount(geofence.getName()) > 0) {
            throw new DuplicateGeofenceNameException();
        }

        if (geofence.getExpirationDate() != null) {
            Calendar expiration = Calendar.getInstance();
            expiration.setTime(geofence.getExpirationDate());
            expiration.set(Calendar.HOUR_OF_DAY, expiration.getActualMaximum(Calendar.HOUR_OF_DAY));
            expiration.set(Calendar.MINUTE, expiration.getActualMaximum(Calendar.MINUTE));
            expiration.set(Calendar.SECOND, expiration.getActualMaximum(Calendar.SECOND));
            expiration.set(Calendar.MILLISECOND, expiration.getActualMaximum(Calendar.MILLISECOND));
            geofence.setExpirationDate(expiration.getTime());
        }

        geofence.setGeofenceRequestId(getNewUniqueGeofenceId());

        geofence = dao.save(geofence);
        return geofence;
    }

    /**
     * Get a new and always unique {@link Geofence#geofenceRequestId}.
     * @return The always unique {@link Geofence#geofenceRequestId}.
     */
    private String getNewUniqueGeofenceId() {
        return "WORKTIME_" + UUID.randomUUID().toString();
    }

    @Override
    public Geofence updatGeofence(Geofence geofence) throws DuplicateGeofenceNameException {
        Geofence oldGeofence = dao.findById(geofence.getId());

        int maxCount = 0;
        if (oldGeofence.getName().equals(geofence.getName())) {
            maxCount = 1;
        }

        if (dao.getGeofencesByNameCount(geofence.getName()) > maxCount) {
            throw new DuplicateGeofenceNameException();
        }

        geofence = dao.update(geofence);
        return geofence;
    }

    @Override
    public void deleteGeofence(Geofence geofence) {
        dao.delete(geofence);
    }

    @Override
    public List<Geofence> findAllNonExpired() {
        return dao.findAllNonExpired();
    }
}
