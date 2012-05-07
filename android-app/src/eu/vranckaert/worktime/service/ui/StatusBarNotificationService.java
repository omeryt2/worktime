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

package eu.vranckaert.worktime.service.ui;

import eu.vranckaert.worktime.model.TimeRegistration;

public interface StatusBarNotificationService {
    /**
     * Removes an existing notification in the status bar.
     */
    void removeOngoingTimeRegistrationNotification();

    /**
     * Add a new notification in the status bar for the specified {@link TimeRegistration}, but only if the time
     * registrations is 'ongoing' and the preference has been enabled. If the provided {@link TimeRegistration} is null
     * the latest time registration will be resolved and used.
     * @param registration The registration to create a notification for.
     */
    void addOrUpdateNotification(TimeRegistration registration);

    /**
     * Add a new notification in the status bar to notify that a backup-restore is successful.
     */
    void addStatusBarNotificationForRestore();
}
