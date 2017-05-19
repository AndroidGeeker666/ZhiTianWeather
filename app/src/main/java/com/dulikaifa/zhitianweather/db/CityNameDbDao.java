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
    /**
     * 数据库创建帮助类
     */
    private CityNameDbHelper helper;
    /**
     * 实体类集合
     */
    private List<CityBean> list = null;

    /**
     * 构造方法
     * @param context 传入的上下文
     */
    public CityNameDbDao(final Context context) {
        helper = new CityNameDbHelper(context);
    }

    /**
     * 查询所有数据
     */
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
        if (cursor != null) {

            cursor.close();
        }
        db.close();
        return list;
    }

    /**
     * 向数据库中添加一条数据
     * @param bean 实体类对象
     */
    public void add(final CityBean bean) {

        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("cityname", bean.getCityName());
        values.put("countryname", bean.getCountryName());
        db.insert("cityname", null, values);  //原理底层 也是在组拼sql语句
        db.close();
    }

    /**
     *
     * @param bean 实体类对象
     */
    public void delete(final CityBean bean) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("cityname", "cityname=?", new String[]{bean.getCityName()});
        db.close();
    }
}
