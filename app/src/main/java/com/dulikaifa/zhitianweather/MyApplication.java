package com.dulikaifa.zhitianweather;

import android.app.Application;
import android.content.Context;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.umeng.analytics.MobclickAgent;

import org.litepal.LitePal;

/**
 * Author:李晓峰 on 2017/4/29 23:41
 * E-mail:chaate@163.com
 * Copyright(c)2017,All rights reserved.
 * Usage :
 */

public class MyApplication extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        //LitePal数据库初始化
        LitePal.initialize(context);
        //Logger日志工具初始化
        Logger
                .init("我的TAG")                 // default PRETTYLOGGER or use just init()
                .methodCount(2)                 // default 2// default shown
                .logLevel(LogLevel.FULL)        // default LogLevel.FULL
                .methodOffset(0);               // default 0; //default AndroidLogAdapter
        //友盟统计初始化
        //启动间隔
        MobclickAgent.setSessionContinueMillis(60000);
        //对统计的日志进行加密
        MobclickAgent.enableEncrypt(true);
        //初始化
        MobclickAgent.startWithConfigure(new MobclickAgent.UMAnalyticsConfig(this,
                "59059b8e8f4a9d1b5f00145d", "Wandoujia", MobclickAgent.EScenarioType.E_UM_NORMAL));
        //打开调试模式
        MobclickAgent.setDebugMode( true );
    }

    public static Context getContext() {

        return context;
    }
}
