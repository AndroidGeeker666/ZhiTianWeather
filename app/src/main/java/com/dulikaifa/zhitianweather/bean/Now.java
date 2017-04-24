package com.dulikaifa.zhitianweather.bean;

import com.google.gson.annotations.SerializedName;

public class Now {

    @SerializedName("tmp")
    public String nowTemperature;
    @SerializedName("fl")
    public String nowFeelTemperature;
    @SerializedName("hum")
    public String nowHumidity;
    @SerializedName("pcpn")
    public String nowRainAmout;
    @SerializedName("pres")
    public String nowAtmosphericPressure;
    @SerializedName("vis")
    public String nowVisbility;
    @SerializedName("cond")
    public NowCondition nowCondition;
    public NowWind wind;
    public class NowCondition {

        public String code;
        @SerializedName("txt")
        public String weather;
    }
    public class NowWind {
        @SerializedName("deg")
        public String nowWindDegree;
        @SerializedName("dir")
        public String nowWindDirection;
        @SerializedName("sc")
        public String nowWindPower;
        @SerializedName("spd")
        public String nowWindSpeed;

    }
}
