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

package eu.vranckaert.worktime.constants;

/**
 * Do not modify this file. Instead look for the file with the same name under the root of the project, in directory
 * 'config'. This file will be automatically overriden!
 */
public class EnvironmentConstants {
    public class WorkTimeWeb {
        public static final String ENDPOINT_URL = "http://10.0.2.2:8080/worktime/"; // LOCAL DEV
        //public static final String ENDPOINT_URL = "http://31.25.101.141:8080/worktime-web/"; // ACCEPTANCE
        //public static final String ENDPOINT_URL = "https://worktime-web.appspot.com/"; // PRODUCTION
        public static final String SERVICE_KEY = "1e9c3da5-a869-4b9d-9a52-3bd40f3abf23";
    }
}