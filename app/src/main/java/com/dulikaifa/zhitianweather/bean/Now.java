package com.dulikaifa.zhitianweather.bean;

import com.google.gson.annotations.SerializedName;

public class Now {

    @SerializedName("tmp")
    public String temperature;
    @SerializedName("fl")
    public String feelTemperature;
    @SerializedName("hum")
    public String humidity;
    @SerializedName("pcpn")
    public String rainAmout;
    @SerializedName("pres")
    public String atmosphericPressure;
    @SerializedName("vis")
    public String visibility;
    @SerializedName("cond")
    public Condition condition;
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
