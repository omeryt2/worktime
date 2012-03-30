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

package eu.vranckaert.worktime.utils.string;

import android.test.AndroidTestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 22/01/12
 * Time: 11:48
 */
public class StringUtilsTest extends AndroidTestCase {
    public void testConvertListToArray() {
        List<String> list = new ArrayList<String>();
        list.add("item1");
        list.add("item2");
        list.add("item3");
        
        String[] array = StringUtils.convertListToArray(list);

        assertNotNull(array);
        assertEquals(list.size(), array.length);
        assertEquals(list.get(0), array[0]);
        assertEquals(list.get(1), array[1]);
        assertEquals(list.get(2), array[2]);
    }
    
    public void testIsBlank() {
        String test = null;
        boolean result1 = StringUtils.isBlank(test);
        assertTrue(result1);

        test = "";
        boolean result2 = StringUtils.isBlank(test);
        assertTrue(result2);

        test = "       ";
        boolean result3 = StringUtils.isBlank(test);
        assertTrue(result3);

        test = "     t       ";
        boolean result4 = StringUtils.isBlank(test);
        assertFalse(result4);
    }
    
    public void testIsNotBlank() {
        String test = null;
        boolean result1 = StringUtils.isNotBlank(test);
        assertFalse(result1);

        test = "";
        boolean result2 = StringUtils.isNotBlank(test);
        assertFalse(result2);

        test = "       ";
        boolean result3 = StringUtils.isNotBlank(test);
        assertFalse(result3);

        test = "     t       ";
        boolean result4 = StringUtils.isNotBlank(test);
        assertTrue(result4);
    }
    
    public void testOptimizeString() {
        String text = "  text  ";
        String expectedResult = "text";
        String result = StringUtils.optimizeString(text);
        
        assertEquals(expectedResult, result);
    }
    
    public void testLeftPad() {
        String text = "test";

        String expectedResult = "test";
        String result = StringUtils.leftPad(text,  "y", 4);
        assertEquals(expectedResult, result);

        expectedResult = "test";
        result = StringUtils.leftPad(text,  "y", -10);
        assertEquals(expectedResult, result);

        expectedResult = "yyytest";
        result = StringUtils.leftPad(text,  "y", 7);
        assertEquals(expectedResult, result);

        expectedResult = "Azertytest";
        result = StringUtils.leftPad(text,  "Azerty", 5);
        assertEquals(expectedResult, result);

        expectedResult = "Azertytest";
        result = StringUtils.leftPad(text,  "Azerty", 6);
        assertEquals(expectedResult, result);

        expectedResult = "AzertyAzertytest";
        result = StringUtils.leftPad(text,  "Azerty", 11);
        assertEquals(expectedResult, result);
    }
}
