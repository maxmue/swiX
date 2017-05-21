package de.fu_berlin.inf.mfm235.xswipin;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Debug {

    private static Boolean debugModeEnabled = null;

    public static boolean IsDebugModeEnabled(Context context) {

        if (debugModeEnabled == null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            debugModeEnabled = preferences.getBoolean("debugMode", false);
        }

        return debugModeEnabled.booleanValue();
    }

    public static void Invalidate() {
        debugModeEnabled = null;
    }
}
