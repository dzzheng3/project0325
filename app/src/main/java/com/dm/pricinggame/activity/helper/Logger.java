package com.dm.pricinggame.activity.helper;

import android.util.Log;

/**
 * Created by User on 2015-11-02.
 */
public class Logger {
    static boolean debugMode = true;

    public static void e(String tag, String message) {
        if (debugMode) {
            Log.e(tag, message);
        }
    }
}
