package eu.vranckaert.worktime.dao.web.model.response.user;

import eu.vranckaert.worktime.dao.web.model.base.response.WorkTimeResponse;
import eu.vranckaert.worktime.dao.web.model.exception.FieldRequiredJSONException;
import eu.vranckaert.worktime.dao.web.model.exception.user.EmailOrPasswordIncorrectJSONException;
import eu.vranckaert.worktime.dao.web.model.exception.user.InvalidEmailJSONException;
import eu.vranckaert.worktime.dao.web.model.exception.user.RegisterEmailAlreadyInUseJSONException;

public class AuthenticationResponse extends WorkTimeResponse {
    private String sessionKey;

    private FieldRequiredJSONException fieldRequiredJSONException;
    private EmailOrPasswordIncorrectJSONException emailOrPasswordIncorrectJSONException;
    private RegisterEmailAlreadyInUseJSONException registerEmailAlreadyInUseJSONException;
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
}
