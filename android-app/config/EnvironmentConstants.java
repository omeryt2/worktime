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
        public static final String ENDPOINT_URL = "@CONFIG.WORKTIME_ENDPOINT@";
        public static final String SERVICE_KEY = "@CONFIG.WORKTIME_ANDROID_SERVICE_KEY@";
    }
}