package com.flyingstudio.cumtfriend.utils;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class UiUtil {

    public static void setImmerseLayout(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR );
            }
        }
    }

}
