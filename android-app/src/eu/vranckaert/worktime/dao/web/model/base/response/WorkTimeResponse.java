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

package eu.vranckaert.worktime.dao.web.model.base.response;

import eu.vranckaert.worktime.dao.web.model.exception.security.ServiceNotAllowedJSONException;
import eu.vranckaert.worktime.dao.web.model.exception.security.UserIncorrectRoleException;
import eu.vranckaert.worktime.dao.web.model.exception.security.UserNotLoggedInJSONException;
import eu.vranckaert.worktime.web.json.model.JsonEntity;

public abstract class WorkTimeResponse extends JsonEntity {
    private ServiceNotAllowedJSONException serviceNotAllowedException;
    private UserNotLoggedInJSONException userNotLoggedInException;
    private UserIncorrectRoleException userIncorrectRoleException;
    private boolean resultOk = true;

    public ServiceNotAllowedJSONException getServiceNotAllowedException() {
        return serviceNotAllowedException;
    }

    public UserNotLoggedInJSONException getUserNotLoggedInException() {
        return userNotLoggedInException;
    }

    public UserIncorrectRoleException getUserIncorrectRoleException() {
        return userIncorrectRoleException;
    }

    public boolean isResultOk() {
        return resultOk;
    }
}

