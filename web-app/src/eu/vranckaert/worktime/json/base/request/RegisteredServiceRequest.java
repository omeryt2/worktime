package eu.vranckaert.worktime.json.base.request;

public abstract class RegisteredServiceRequest extends WorkTimeJSONRequest {
	private String serviceKey;

	public String getServiceKey() {
		return serviceKey;
	}

	public void setServiceKey(String serviceKey) {
		this.serviceKey = serviceKey;
	}
}
