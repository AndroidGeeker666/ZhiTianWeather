package com.dulikaifa.zhitianweather.bean;

import com.google.gson.annotations.SerializedName;

public class Forecast {


    @SerializedName("astro")
    public Astrology astrology;
    @SerializedName("cond")
    public Condition condition;
    public String date;
    @SerializedName("hum")
    public String humidity;
    @SerializedName("pcpn")
    public String rainAmout;
    @SerializedName("pop")
    public String rainProbability;
    @SerializedName("pres")
    public String atmosphericPressure;
    @SerializedName("tmp")
    public Temperature temperature;
    @SerializedName("uv")
    public String ultravioletRay;
    @SerializedName("vis")
    public String visibility;
    public Wind wind;

    public class Astrology {
        @SerializedName("mr")
        public String moonrise;
        @SerializedName("ms")
        public String moonset;
        @SerializedName("sr")
        public String sunrise;
        @SerializedName("ss")
        public String sunset;

    }

    public class Temperature {

        public String max;

        public String min;

    }

    public class Condition {

        @SerializedName("code_d")
        public String codeDay;
        @SerializedName("code_n")
        public String codeNight;
        @SerializedName("txt_d")
        public String weatherDay;
        @SerializedName("txt_n")
        public String weatherNight;
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
