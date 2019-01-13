package michaelbrabec.bakalab.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPrefHandler {
    public static String getString(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences("cz.michaelbrabec.fossbakalari", Context.MODE_PRIVATE);

        return prefs.getString(key, "");
    }

    public static void setString(Context context, String key, String value) {
        SharedPreferences prefs = context.getSharedPreferences("cz.michaelbrabec.fossbakalari", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static Boolean getDefaultBool(Context context, String key) {
        return getDefaultBool(context, key, false);
    }

    public static Boolean getDefaultBool(Context context, String key, Boolean defValue) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getBoolean(key, defValue);
    }
}
