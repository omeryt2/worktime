package eu.vranckaert.worktime.json.exception;

import javax.xml.bind.annotation.XmlRootElement;

import eu.vranckaert.worktime.json.base.exception.WorkTimeJSONException;
import eu.vranckaert.worktime.json.base.request.WorkTimeJSONRequest;

@XmlRootElement
public class FieldRequiredJSONException extends WorkTimeJSONException {
	private String fieldName;
	
	public FieldRequiredJSONException(String requestUrl, WorkTimeJSONRequest requestObject, String fieldName) {
		super(requestUrl);
		this.fieldName = fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
}
