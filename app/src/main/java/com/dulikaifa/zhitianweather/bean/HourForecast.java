package com.dulikaifa.zhitianweather.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Author:李晓峰 on 2017/4/23 23:27
 * E-mail:chaate@163.com
 * Copyright(c)2017,All rights reserved.
 * Usage :
 */

public class HourForecast {
    @SerializedName("tmp")
    public String temperature;
    @SerializedName("hum")
    public String humidity;
    @SerializedName("pres")
    public String atmosphericPressure;
    @SerializedName("pop")
    public String rainProbability;
    @SerializedName("cond")
    public Condition condition;
    public String date;
    public Wind wind;
    public class Condition {

        public String code;
        @SerializedName("txt")
        public String weather;
    }
    public class Wind {
        @SerializedName("deg")
        public String windDegree;
        @SerializedName("dir")
        public String windDirection;
        @SerializedName("sc")
        public String windPower;
        @SerializedName("spd")
        public String windSpeed;

    }

}
