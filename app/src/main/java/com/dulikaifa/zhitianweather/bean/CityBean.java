package com.dulikaifa.zhitianweather.bean;

/**
 * Author:李晓峰 on 2017/4/28 20:25
 * E-mail:chaate@163.com
 * Copyright(c)2017,All rights reserved.
 * Usage :
 */

public class CityBean {

    public String id ;
    public String cityName;
    public String countryName;

    public CityBean(String id, String cityName, String countryName) {
        this.id = id;
        this.cityName = cityName;
        this.countryName = countryName;
    }

}
