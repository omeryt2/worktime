package eu.vranckaert.worktime.json.base.request;

public abstract class AuthenticatedUserRequest extends RegisteredServiceRequest {
	private String email;
	private String sessionKey;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}
}
