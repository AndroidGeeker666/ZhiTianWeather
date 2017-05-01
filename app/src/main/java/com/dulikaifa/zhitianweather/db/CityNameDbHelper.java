package com.dulikaifa.zhitianweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Author:李晓峰 on 2017/4/28 19:47
 * E-mail:chaate@163.com
 * Copyright(c)2017,All rights reserved.
 * Usage :
 */

public class CityNameDbHelper extends SQLiteOpenHelper {

    public CityNameDbHelper(Context context) {
        super(context, "cityname.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // 创建表结构
        String sql = "create table cityname (id integer primary key autoincrement,cityname varchar(20),countryname varchar(20))";
        // 执行sql
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
