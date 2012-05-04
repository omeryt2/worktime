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

import android.content.Context;

/**
 * This class is used for sending log output.<br/>
 * Generally used logging levels are LogLevel.INFO, LogLevel#DEBUG, LogLevel#WARN and LogLevel#ERROR.<br/>
 * A check is performed on execution time to check the phase of the current executed build. This is done in order to not
 * have logging enabled for certain (much-used) levels and thus to not spam the end-user's log. The logging levels that
 * are disabled when running a stable build (provided through the Android Play Store) are:<br/>
 * <li>LogLevel#VERBOSE</li>
 * <li>LogLevel#INFO</li>
 * <li>LogLevel#DEBUG</li>
 */
public class Logger {
    /**
     * Sending a log to the output.
     *
     * @param context  The context of the application
     * @param logLevel The {@link LogLevel} to use.
     * @param tag      The tag to be added to the log (most likely the activity, service or dao from which the log is
     *                 triggered.
     * @param message  The message to log.
     */
    public static void log(Context context, LogLevel logLevel, String tag, String message) {
        log(context, logLevel, tag, message, null);
    }

    /**
     * Sending a log to the output.
     *
     * @param context  The context of the application
     * @param logLevel The {@link LogLevel} to use.
     * @param tag      The tag to be added to the log (most likely the activity, service or dao from which the log is
     *                 triggered.
     * @param message  The message to log.
     * @param e        Optionally an exception can be provided to be logged.
     */
    public static void log(Context context, LogLevel logLevel, String tag, String message, Throwable e) {
        if (ContextUtils.isStableBuild(context) && (logLevel.equals(LogLevel.VERBOSE) || logLevel.equals(LogLevel.INFO)
                || logLevel.equals(LogLevel.DEBUG) || logLevel.equals(LogLevel.WTF))) {
            return;
        }

        switch (logLevel) {
            case VERBOSE:
                v(tag, message, e);
                break;
            case INFO:
                i(tag, message, e);
                break;
            case DEBUG:
                d(tag, message, e);
                break;
            case WARN:
                w(tag, message, e);
                break;
            case ERROR:
                e(tag, message, e);
                break;
            case WTF:
                wtf(tag, message, e);
                break;
            default:
                i(tag, message, e);
        }
    }

    private static void v(String tag, String message, Throwable e) {
        if (e == null) {
            android.util.Log.v(tag, message);
        } else {
            android.util.Log.v(tag, message, e);
        }
    }

    private static void i(String tag, String message, Throwable e) {
        if (e == null) {
            android.util.Log.i(tag, message);
        } else {
            android.util.Log.i(tag, message, e);
        }
    }

    private static void d(String tag, String message, Throwable e) {
        if (e == null) {
            android.util.Log.d(tag, message);
        } else {
            android.util.Log.d(tag, message, e);
        }
    }

    private static void w(String tag, String message, Throwable e) {
        if (e == null) {
            android.util.Log.w(tag, message);
        } else {
            android.util.Log.w(tag, message, e);
        }
    }

    private static void e(String tag, String message, Throwable e) {
        if (e == null) {
            android.util.Log.e(tag, message);
        } else {
            android.util.Log.e(tag, message, e);
        }
    }

    private static void wtf(String tag, String message, Throwable e) {
        if (e == null) {
            android.util.Log.wtf(tag, message);
        } else {
            android.util.Log.wtf(tag, message, e);
        }
    }

    /**
     * A logging level to be used for putting messages on the user's device log.
     */
    public enum LogLevel {
        VERBOSE, INFO, DEBUG, WARN, ERROR, WTF;
    }
}
