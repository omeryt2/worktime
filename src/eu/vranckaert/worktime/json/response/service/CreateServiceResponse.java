package eu.vranckaert.worktime.json.response.service;

import javax.xml.bind.annotation.XmlRootElement;

import eu.vranckaert.worktime.json.base.response.WorkTimeResponse;

@XmlRootElement
public class CreateServiceResponse extends WorkTimeResponse {
	private String serviceKey;

	public String getServiceKey() {
		return serviceKey;
	}

	public void setServiceKey(String serviceKey) {
		this.serviceKey = serviceKey;
	}
}
