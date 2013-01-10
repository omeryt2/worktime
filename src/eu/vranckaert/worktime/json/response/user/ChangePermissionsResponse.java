package eu.vranckaert.worktime.json.response.user;

import javax.xml.bind.annotation.XmlRootElement;

import eu.vranckaert.worktime.json.base.response.WorkTimeResponse;
import eu.vranckaert.worktime.json.exception.user.UserNotFoundJSONException;

@XmlRootElement
public class ChangePermissionsResponse extends WorkTimeResponse {
	private UserNotFoundJSONException userNotFoundJSONException;

	public UserNotFoundJSONException getUserNotFoundJSONException() {
		return userNotFoundJSONException;
	}

	public void setUserNotFoundJSONException(
			UserNotFoundJSONException userNotFoundJSONException) {
		this.userNotFoundJSONException = userNotFoundJSONException;
		setResultOk(false);
	}
}
