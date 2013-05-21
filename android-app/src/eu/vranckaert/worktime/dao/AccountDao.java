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

package eu.vranckaert.worktime.dao;

import eu.vranckaert.worktime.dao.generic.GenericDao;
import eu.vranckaert.worktime.model.User;

/**
 * User: Dirk Vranckaert
 * Date: 13/12/12
 * Time: 11:34
 */
public interface AccountDao extends GenericDao<User, String> {
    /**
     * Store a newly logged in user in the database. Make sure no other user is available in the database.
     * @param user The user to be stored.
     */
    void storeLoggedInUser(User user);

    /**
     * Get the logged in user. If no user is logged in null will be returned.
     * @return The logged in user or null if no user is logged in at the moment.
     */
    User getLoggedInUser();
}
