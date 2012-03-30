/*
 * Copyright 2012 Dirk Vranckaert
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

package eu.vranckaert.worktime.utils.context;

import eu.vranckaert.worktime.constants.OSContants;
import eu.vranckaert.worktime.test.cases.TestCase;

import java.util.Locale;

/**
 * User: DIRK VRANCKAERT
 * Date: 24/01/12
 * Time: 15:17
 */
public class ContextUtilsTest extends TestCase {
    public void testGetCurrentLocale() {
        Locale locale = ContextUtils.getCurrentLocale(ctx);
        assertNotNull(locale);
    }
    
    public void testGetCurrentApplicationVersionName() {
        String versionName = ContextUtils.getCurrentApplicationVersionName(ctx);
        assertNotNull(versionName);
        assertFalse("ERROR".equals(versionName));
    }
    
    public void testGetCurrentApplicationVersionCode() {
        int versionCode = ContextUtils.getCurrentApplicationVersionCode(ctx);
        assertTrue(versionCode >= 0);
    }
    
    public void testGetAndroidApiVersion() {
        int apiVersion = ContextUtils.getAndroidApiVersion();
        assertTrue(OSContants.API.ECLAIR <= apiVersion);
    }
    
    public void testGetApplicationPackage() {
        String applicationPackage = ContextUtils.getApplicationPackage(ctx);
        assertNotNull(applicationPackage);
        assertEquals("eu.vranckaert.worktime", applicationPackage);
    }
}
