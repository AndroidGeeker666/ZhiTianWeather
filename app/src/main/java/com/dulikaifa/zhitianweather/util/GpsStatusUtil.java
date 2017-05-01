package com.dulikaifa.zhitianweather.util;

import android.content.Context;
import android.location.LocationManager;

/**
 * Author:李晓峰 on 2017/4/30 23:15
 * E-mail:chaate@163.com
 * Copyright(c)2017,All rights reserved.
 * Usage :
 */

public class GPSStatusUtil {

    public static boolean isGpsAvailable(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (manager != null) {
            return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        return false;
    }
}
