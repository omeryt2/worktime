package eu.vranckaert.worktime.json.response.service;

import javax.xml.bind.annotation.XmlRootElement;

import eu.vranckaert.worktime.json.base.response.WorkTimeResponse;
import eu.vranckaert.worktime.json.exception.service.ServiceRemovesItselfJSONException;

@XmlRootElement
public class RemoveServiceResponse extends WorkTimeResponse {
	private ServiceRemovesItselfJSONException serviceRemovesItselfJSONException;

	public ServiceRemovesItselfJSONException getServiceRemovesItselfJSONException() {
		return serviceRemovesItselfJSONException;
	}

	public void setServiceRemovesItselfJSONException(
			ServiceRemovesItselfJSONException serviceRemovesItselfJSONException) {
		this.serviceRemovesItselfJSONException = serviceRemovesItselfJSONException;
		setResultOk(false);
	}	
}
