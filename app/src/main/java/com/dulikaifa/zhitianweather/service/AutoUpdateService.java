package com.dulikaifa.zhitianweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.dulikaifa.zhitianweather.http.JsonRequestCallback;
import com.dulikaifa.zhitianweather.http.OkHttpUtil;
import com.dulikaifa.zhitianweather.http.Url;

/**
 * Author:李晓峰 on 2017/4/23 16:57
 * E-mail:chaate@163.com
 * Copyright(c)2017,All rights reserved.
 * Usage :
 */

public class AutoUpdateService extends Service {
    private static final long SPLASH_DISPLAY_LENGHT = 4 * 1000;

    private boolean isFirstTime = false;

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {

        Toast.makeText(AutoUpdateService.this, "天气自动更新服务开启成功", Toast.LENGTH_SHORT).show();
        if (!isFirstTime) {
            checkUpdate();
        }
        isFirstTime = true;
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 60 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }


    private void checkUpdate() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updateBingPic();
                updateWeather();
            }
        }, SPLASH_DISPLAY_LENGHT);
    }

    private void updateBingPic() {
        OkHttpUtil.getInstance().getAsync(Url.BINGPIC_URL, new JsonRequestCallback() {
            @Override
            public void onRequestSucess(final String result) {

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic", result);
                editor.apply();
            }

            @Override
            public void onRequestFailure(final String result) {
                Toast.makeText(AutoUpdateService.this, "获取背景图片失败，请检查网络设置！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateWeather() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String weatherId = prefs.getString("weatherId", null);
        //final String countryName = prefs.getString("countryName", null);
        if (weatherId != null) {
            String weatherUrl = Url.WEATHER_Url + "?city=" + weatherId + "&key=" + Url.APP_KEY;
            OkHttpUtil.getInstance().getAsync(weatherUrl, new JsonRequestCallback() {
                @Override
                public void onRequestSucess(final String result) {

                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                    editor.putString("json", result);
                    editor.apply();
                }

                @Override
                public void onRequestFailure(final String result) {
                    Toast.makeText(AutoUpdateService.this, "更新天气失败,请检查网络！", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}
