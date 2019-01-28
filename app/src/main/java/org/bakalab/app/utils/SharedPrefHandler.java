package org.bakalab.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.bakalab.app.R;

public class SharedPrefHandler {
    public static String getString(Context context, String key) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(key, "");
    }

    public static void setString(Context context, String key, String value) {
        SharedPreferences.Editor preferenceManager = PreferenceManager.getDefaultSharedPreferences(context).edit();
        preferenceManager.putString(key, value);
        preferenceManager.apply();
    }

    public static Boolean getDefaultBool(Context context, String key) {
        return getDefaultBool(context, key, false);
    }

    public static Boolean getDefaultBool(Context context, String key, Boolean defValue) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getBoolean(key, defValue);
    }

    public static int getUserTheme(Context context){
        String theme = SharedPrefHandler.getString(context, "theme");
        switch(theme){
            case "0":
                return R.style.Bakalab_NoActionBar;
            case "1":
                return R.style.Bakalab_NoActionBar_Dark;
            default:
                return R.style.Bakalab_NoActionBar;
        }
    }
}
