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

package eu.vranckaert.worktime.dao.web.model.response.user;

import eu.vranckaert.worktime.dao.web.model.base.response.WorkTimeResponse;
import eu.vranckaert.worktime.dao.web.model.exception.FieldRequiredJSONException;
import eu.vranckaert.worktime.dao.web.model.exception.user.EmailOrPasswordIncorrectJSONException;
import eu.vranckaert.worktime.dao.web.model.exception.user.InvalidEmailJSONException;
import eu.vranckaert.worktime.dao.web.model.exception.user.PasswordLengthInvalidJSONException;
import eu.vranckaert.worktime.dao.web.model.exception.user.RegisterEmailAlreadyInUseJSONException;

public class AuthenticationResponse extends WorkTimeResponse {
    private String sessionKey;

    private FieldRequiredJSONException fieldRequiredJSONException;
    private EmailOrPasswordIncorrectJSONException emailOrPasswordIncorrectJSONException;
    private RegisterEmailAlreadyInUseJSONException registerEmailAlreadyInUseJSONException;
    private PasswordLengthInvalidJSONException passwordLengthInvalidJSONException;
    private InvalidEmailJSONException invalidEmailJSONException;

    public String getSessionKey() {
        return sessionKey;
    }

    public FieldRequiredJSONException getFieldRequiredJSONException() {
        return fieldRequiredJSONException;
    }

    public EmailOrPasswordIncorrectJSONException getEmailOrPasswordIncorrectJSONException() {
        return emailOrPasswordIncorrectJSONException;
    }

    public RegisterEmailAlreadyInUseJSONException getRegisterEmailAlreadyInUseJSONException() {
        return registerEmailAlreadyInUseJSONException;
    }

    public InvalidEmailJSONException getInvalidEmailJSONException() {
        return invalidEmailJSONException;
    }

    public PasswordLengthInvalidJSONException getPasswordLengthInvalidJSONException() {
        return passwordLengthInvalidJSONException;
    }

    public void setPasswordLengthInvalidJSONException(PasswordLengthInvalidJSONException passwordLengthInvalidJSONException) {
        this.passwordLengthInvalidJSONException = passwordLengthInvalidJSONException;
    }
}
