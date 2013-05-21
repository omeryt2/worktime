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

package eu.vranckaert.worktime.dao.web.model.exception.security;

import eu.vranckaert.worktime.dao.web.model.base.exception.WorkTimeJSONException;
import eu.vranckaert.worktime.dao.web.model.entities.Role;

/**
 * User: Dirk Vranckaert
 * Date: 13/12/12
 * Time: 15:25
 */
public class UserIncorrectRoleException extends WorkTimeJSONException {
    Role requiredRole;

    public UserIncorrectRoleException(String requestUrl, Role requiredRole) {
        super(requestUrl);
        this.requiredRole = requiredRole;
    }

    public Role getRequiredRole() {
        return requiredRole;
    }

    public void setRequiredRole(Role requiredRole) {
        this.requiredRole = requiredRole;
    }
}
