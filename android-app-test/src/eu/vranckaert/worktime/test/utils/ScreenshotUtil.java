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

package eu.vranckaert.worktime.test.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;

/**
 * User: DIRK VRANCKAERT
 * Date: 12/03/12
 * Time: 11:18
 */
public class ScreenshotUtil {
    private static final String LOG_TAG = ScreenshotUtil.class.getSimpleName();
    private static final String SCREENSHOT_FOLDER = "screenshots";
    
    public static void takeScreenShot(Context ctx, View view, String testClass, String testName, String seqNumber) throws Exception {
        Log.i(LOG_TAG, "Creating screenshot for test-class " + testClass + " and test " + testName + "(Screenshot number " + seqNumber + ")");
        String name = testClass + "." + testName + "_" + seqNumber;
        
        File screensPath = new File("/data/data/" + ctx.getPackageName() + "/" + SCREENSHOT_FOLDER);
        Log.i(LOG_TAG, "Saving screenshot to " + screensPath.getAbsolutePath());
        
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b = view.getDrawingCache();
        FileOutputStream fos = null;
        try {
            if (!screensPath.exists()) {
                screensPath.mkdirs();
            }
            File file = new File(screensPath, name + ".png");
            fos = new FileOutputStream(file);
            if (fos != null) {
                b.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
            }
        } catch (Exception e) {
        }
        Log.i(LOG_TAG, "Screenshot saved in PNG-format!");
    }
}
