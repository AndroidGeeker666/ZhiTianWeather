package com.dulikaifa.zhitianweather.bean;

import com.google.gson.annotations.SerializedName;

public class Basic {

    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;
    @SerializedName("lat")
    public String latitude;
    @SerializedName("lon")
    public String longitude;
    public Update update;

    public class Update {

        @SerializedName("loc")
        public String updateTime;
        @SerializedName("utc")
        public String utcTime;
    }

}
