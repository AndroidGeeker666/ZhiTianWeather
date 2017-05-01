package com.dulikaifa.zhitianweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dulikaifa.zhitianweather.bean.CityBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:李晓峰 on 2017/4/28 20:10
 * E-mail:chaate@163.com
 * Copyright(c)2017,All rights reserved.
 * Usage :
 */

public class CityNameDbDao {

    private CityNameDbHelper helper;
    private List<CityBean> list=null;

    public CityNameDbDao(Context context) {

        helper = new CityNameDbHelper(context);

    }

    public List<CityBean> findAll() {
        SQLiteDatabase db = helper.getReadableDatabase();
        if(list==null){
            list= new ArrayList<>();
        }

        Cursor cursor = db.query("cityname", null, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String cityName = cursor.getString(1);
                String countryName = cursor.getString(2);
                CityBean bean = new CityBean(id,cityName,countryName);
                list.add(bean);
            }

        }
        db.close();
        return list;
    }

    public void add(String cityName,String countryName) {

        SQLiteDatabase db = helper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("cityname", cityName);
        values.put("countryname", countryName);
        long result = db.insert("cityname", null, values);  //原理底层 也是在组拼sql语句
        db.close();
    }

    public void delete(String cityName) {
        SQLiteDatabase db = helper.getReadableDatabase();
        long result =db.delete("cityname","cityname=?", new String[]{cityName});
        db.close();
    }
}
