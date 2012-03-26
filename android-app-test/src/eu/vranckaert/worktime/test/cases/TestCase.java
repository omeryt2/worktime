/*
 *  Copyright 2012 Dirk Vranckaert
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package eu.vranckaert.worktime.test.cases;

import android.content.Context;
import android.test.AndroidTestCase;
import eu.vranckaert.worktime.test.utils.TestUtil;

/**
 * User: DIRK VRANCKAERT
 * Date: 20/01/12
 * Time: 14:27
 */
public class TestCase extends AndroidTestCase {
    /**
     * The context that is used to execute the test.
     */
    public Context ctx;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        ctx = getContext();

        TestUtil.removeAllPreferences(getContext());
    }

    /**
     * Set a preference, defined by the key parameter, to a certain value.
     * @param key The key referring to the preference.
     * @param value The value of the preference to set.
     */
    public void setPreference(String key, Object value) {
        TestUtil.setPreference(getContext(), key, value);
    }
}
