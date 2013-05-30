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

package eu.vranckaert.worktime.service;

import eu.vranckaert.worktime.model.trigger.RecurrenceTrigger;

/**
 * User: DIRK VRANCKAERT
 * Date: 30/05/13
 * Time: 18:06
 */
public interface RecurrenceService {
    /**
     * Create a new {@link RecurrenceTrigger}.
     * @param trigger The {@link RecurrenceTrigger} to be saved.
     * @return The {@link RecurrenceTrigger} that has been saved.
     */
    RecurrenceTrigger save(RecurrenceTrigger trigger);
}
