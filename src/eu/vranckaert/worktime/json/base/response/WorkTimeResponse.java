package eu.vranckaert.worktime.json.base.response;

import eu.vranckaert.worktime.json.exception.security.ServiceNotAllowedJSONException;
import eu.vranckaert.worktime.json.exception.security.UserIncorrectRoleException;
import eu.vranckaert.worktime.json.exception.security.UserNotLoggedInJSONException;

public abstract class WorkTimeResponse {
    private ServiceNotAllowedJSONException serviceNotAllowedException;
    private UserNotLoggedInJSONException userNotLoggedInException;
    private UserIncorrectRoleException userIncorrectRoleException;
    private boolean resultOk = true;

    public ServiceNotAllowedJSONException getServiceNotAllowedException() {
        return serviceNotAllowedException;
    }

    public void setServiceNotAllowedException(ServiceNotAllowedJSONException serviceNotAllowedException) {
        this.serviceNotAllowedException = serviceNotAllowedException;
        resultOk = false;
    }

    public UserNotLoggedInJSONException getUserNotLoggedInException() {
        return userNotLoggedInException;
    }

    public void setUserNotLoggedInException(UserNotLoggedInJSONException userNotLoggedInException) {
        this.userNotLoggedInException = userNotLoggedInException;
        resultOk = false;
    }

    public UserIncorrectRoleException getUserIncorrectRoleException() {
        return userIncorrectRoleException;
    }

    public void setUserIncorrectRoleException(UserIncorrectRoleException userIncorrectRoleException) {
        this.userIncorrectRoleException = userIncorrectRoleException;
        resultOk = false;
    }

    public boolean isResultOk() {
        return resultOk;
    }
    
    public void setResultOk(boolean resultOk) {
    	this.resultOk = resultOk;
    }
}
