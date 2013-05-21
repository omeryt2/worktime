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

package eu.vranckaert.worktime.dao.web.model.base.exception;

/**
 * This is the base class for all exception that can be returned when making a
 * JSON call.
 * @author dirkvranckaert
 */
public abstract class WorkTimeJSONException {
	private String requestUrl;
    private String message;
	
	public WorkTimeJSONException(String requestUrl, String message) {
		this.requestUrl = requestUrl;
        this.message = message;
	}
	
	public WorkTimeJSONException(String requestUrl) {
		super();
		this.requestUrl = requestUrl;
	}

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
