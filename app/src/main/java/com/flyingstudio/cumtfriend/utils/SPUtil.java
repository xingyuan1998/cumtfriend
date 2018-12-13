package com.flyingstudio.cumtfriend.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtil {
    private static SharedPreferences sharedPreferences;

    public static void setValue(Context context, String key, String value) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getValue(Context context, String key) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sharedPreferences.getString(key, null);
    }
}
