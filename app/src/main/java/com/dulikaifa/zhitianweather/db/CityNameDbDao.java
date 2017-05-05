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
    private List<CityBean> list = null;

    public CityNameDbDao(Context context) {
        helper = new CityNameDbHelper(context);
    }

    public List<CityBean> findAll() {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (list == null) {
            list = new ArrayList<>();
        }

        Cursor cursor = db.query("cityname", null, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String cityName = cursor.getString(1);
                String countryName = cursor.getString(2);
                CityBean bean = new CityBean();
                bean.setCityName(cityName);
                bean.setCountryName(countryName);
                list.add(bean);
            }
        }
        if(cursor!=null){
            cursor.close();
        }
        db.close();
        return list;
    }

    public void add(CityBean bean) {

        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("cityname", bean.getCityName());
        values.put("countryname", bean.getCountryName());
        long result = db.insert("cityname", null, values);  //原理底层 也是在组拼sql语句
        db.close();
    }

    public void delete(CityBean bean) {
        SQLiteDatabase db = helper.getWritableDatabase();
        long result = db.delete("cityname", "cityname=?", new String[]{bean.getCityName()});
        db.close();
    }
}
