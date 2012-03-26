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

import android.app.Activity;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import com.jayway.android.robotium.solo.Solo;
import eu.vranckaert.worktime.test.utils.ScreenshotUtil;
import eu.vranckaert.worktime.test.utils.TestUtil;

/**
 * User: DIRK VRANCKAERT
 * Date: 24/02/12
 * Time: 9:31
 */
public class ActivityTestCase<V extends Activity> extends ActivityInstrumentationTestCase2<V> {
    private final String LOG_TAG = ActivityTestCase.class.getSimpleName();
    private Class testClass;
    private int screenshotCountNumber;
    
    private Intent customIntent = null;

    protected Solo solo;

    protected ActivityTestCase(Class clazz) {
        super("eu.vranckaert.worktime", clazz);
        this.testClass = clazz;
    }

    /**
     * Gets you the full package and class name of this test.
     * @return The full package and class name of this test.
     */
    private String getFullTestClassName() {
        return this.getClass().getName();
    }

    @Override
    protected void setUp() throws Exception {
        beforeTestBeforeActivityLaunch();
        super.setUp();

        if (customIntent != null) {
            setActivityIntent(customIntent);
        }

        TestUtil.removeAllPreferences(getInstrumentation().getTargetContext());
        solo = new Solo(getInstrumentation(), getActivity());
        beforeTestAfterActivityLaunch();
    }

    @Override
    protected void runTest() throws Throwable {
        super.runTest();
        afterTest();
        getActivity().finish();
    }

    @Override
    protected void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    /**
     * Everything to be done before the test starts, AND before the activity is launched. In here no calls can be done
     * to the {@link android.test.ActivityInstrumentationTestCase2#getActivity()} method. 
     */
    private void beforeTestBeforeActivityLaunch() {
        TestUtil.cleanUpDatabase(getInstrumentation().getTargetContext());
    }

    /**
     * Everything to be done before the test starts, but after the activity is launched. In here you can call the 
     * {@link android.test.ActivityInstrumentationTestCase2#getActivity()} method.
     */
    private void beforeTestAfterActivityLaunch() {
        screenshotCountNumber = -1;
        takeScreenshot();
    }

    /**
     * Everything to be done after test
     */
    private void afterTest() {
        if (solo.getViews().size() > 0) {
            try {
                ScreenshotUtil.takeScreenShot(
                        getActivity(),
                        solo.getViews().get(0),
                        getFullTestClassName(),
                        getName(),
                        getLastScreenshotCountNumber()
                );
            } catch (Exception e) {
                Log.e(LOG_TAG, "Not creating a screenshot due to an exception!", e);
            }
        } else {
            Log.w(LOG_TAG, "Solo does not contain any views, not creating a screenshot!");
        }
    }

    /**
     * Get the next number for the next screenshot.
     * @return The number of the next screenshot in {@link String} format.
     * @throws Exception An exception if you are exceeding the limit of 9999 screenshots.
     */
    private String getNextScreenshotCountNumber() throws Exception {
        screenshotCountNumber++;

        if (screenshotCountNumber < 10) {
            return "000" + screenshotCountNumber;
        } else if (screenshotCountNumber >= 10 && screenshotCountNumber < 100) {
            return "00" + screenshotCountNumber;
        } else if (screenshotCountNumber >= 100 && screenshotCountNumber < 1000) {
            return "0" + screenshotCountNumber;
        } else if (screenshotCountNumber >= 1000 && screenshotCountNumber < 9999) {
            return "" + screenshotCountNumber;
        }

        throw new Exception("No more than 9999 screenshots are allowed!");
    }

    /**
     * Get the number of the last screenshot to be taken of the app.
     * @return The number of the last screenshot (9999).
     */
    private String getLastScreenshotCountNumber() {
        return "9999";
    }
    
    /*
     * PROTECTED METHODS
     */

    /**
     * Takes a screenshot of the active screen/dialog/...
     */
    protected void takeScreenshot() {
        if (solo.getViews().size() > 0) {
            try {
                ScreenshotUtil.takeScreenShot(
                        getInstrumentation().getTargetContext(),
                        solo.getViews().get(0),
                        getFullTestClassName(),
                        getName(),
                        getNextScreenshotCountNumber()
                );
            } catch (Exception e) {
                Log.e(LOG_TAG, "Not creating a screenshot due to an exception!", e);
            }
        } else {
            Log.w(LOG_TAG, "Solo does not contain any views, not creating a screenshot!");
        }
    }

    /**
     * Set a preference, defined by the key parameter, to a certain value.
     * @param key The key referring to the preference.
     * @param value The value of the preference to set.
     */
    protected void setPreference(String key, Object value) {
        TestUtil.setPreference(getActivity(), key, value);
    }

    /**
     * Set a custom intent to be launched.
     * @param customIntent The custom {@link Intent} that will be launched for the test-suite.
     */
    protected void setCustomIntent(Intent customIntent) {
        this.customIntent = customIntent;
    }
}
