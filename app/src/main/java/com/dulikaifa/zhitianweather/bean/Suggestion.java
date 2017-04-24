package com.dulikaifa.zhitianweather.bean;

import com.google.gson.annotations.SerializedName;

public class Suggestion {

    @SerializedName("air")
    public AirIndex airIndex;
    @SerializedName("comf")
    public ComfortIndex comfortIndex;
    @SerializedName("cw")
    public CarWashIndex carWashIndex;
    @SerializedName("drsg")
    public DressingIndex dressingIndex;
    @SerializedName("flu")
    public InfluenzaIndex influenzaIndex;
    @SerializedName("sport")
    public SportIndex sportIndex;
    @SerializedName("trav")
    public TravelIndex travelIndex;
    @SerializedName("uv")
    public UltravioletRayIndex ultravioletRayIndex;
    public class AirIndex {
        @SerializedName("brf")
        public String level;
        @SerializedName("txt")
        public String info;

    }

    public class ComfortIndex {

        @SerializedName("brf")
        public String level;
        @SerializedName("txt")
        public String info;

    }

    public class CarWashIndex {

        @SerializedName("brf")
        public String level;
        @SerializedName("txt")
        public String info;

    }

    public class DressingIndex {

        @SerializedName("brf")
        public String level;
        @SerializedName("txt")
        public String info;

    }
    public class InfluenzaIndex {

        @SerializedName("brf")
        public String level;
        @SerializedName("txt")
        public String info;

    }
    public class SportIndex {

        @SerializedName("brf")
        public String level;
        @SerializedName("txt")
        public String info;

    }
    public class TravelIndex {

        @SerializedName("brf")
        public String level;
        @SerializedName("txt")
        public String info;

    }
    public class UltravioletRayIndex {

        @SerializedName("brf")
        public String level;
        @SerializedName("txt")
        public String info;

    }

}
