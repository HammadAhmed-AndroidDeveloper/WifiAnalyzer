package com.wifi_manager.wifi_analyzer.analyze_network_connection.qr.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class QRUtils {

    private QRUtils() {
        throw new AssertionError();
    }


    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }


    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

}
