package com.dulikaifa.zhitianweather.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Author:李晓峰 on 2017/4/30 14:25
 * E-mail:chaate@163.com
 * Copyright(c)2017,All rights reserved.
 * Usage :
 */

public class NetStatusUtil {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if(networkInfo!=null){
                String typeName = networkInfo.getTypeName();
                Toast.makeText(context, "typeName:"+typeName, Toast.LENGTH_SHORT).show();
            }
            return networkInfo != null && networkInfo.isAvailable();
        }
        return false;
    }
}
