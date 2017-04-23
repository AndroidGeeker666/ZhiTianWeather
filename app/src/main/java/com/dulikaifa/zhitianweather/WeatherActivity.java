package com.dulikaifa.zhitianweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dulikaifa.zhitianweather.bean.Forecast;
import com.dulikaifa.zhitianweather.bean.Weather;
import com.dulikaifa.zhitianweather.http.JsonRequestCallback;
import com.dulikaifa.zhitianweather.http.OkHttpUtil;
import com.dulikaifa.zhitianweather.http.Url;
import com.dulikaifa.zhitianweather.service.AutoUpdateService;
import com.dulikaifa.zhitianweather.util.HandleJsonUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Author:李晓峰 on 2017/4/22 22:14
 * E-mail:chaate@163.com
 * Copyright(c)2017,All rights reserved.
 * Usage :
 */

public class WeatherActivity extends AppCompatActivity {
    @InjectView(R.id.bing_pic_img)
    ImageView bingPicImg;
    @InjectView(R.id.nav_button)
    Button navButton;
    @InjectView(R.id.title_city)
    TextView titleCity;
    @InjectView(R.id.title_update_time)
    TextView titleUpdateTime;
    @InjectView(R.id.degree_text)
    TextView degreeText;
    @InjectView(R.id.weather_info_text)
    TextView weatherInfoText;
    @InjectView(R.id.forecast_layout)
    LinearLayout forecastLayout;
    @InjectView(R.id.aqi_text)
    TextView aqiText;
    @InjectView(R.id.pm25_text)
    TextView pm25Text;
    @InjectView(R.id.comfort_text)
    TextView comfortText;
    @InjectView(R.id.car_wash_text)
    TextView carWashText;
    @InjectView(R.id.sport_text)
    TextView sportText;
    @InjectView(R.id.weather_layout)
    ScrollView weatherLayout;
    @InjectView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    private String mWeatherId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transparentStatusBar();
        setContentView(R.layout.activity_weather);
        ButterKnife.inject(this);
        initView();
        initListener();
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    //使状态栏透明
    private void transparentStatusBar() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void initView() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {//本地有缓存的必应图片，直接从缓存中加载
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {            //本地没有缓存的必应图片，则从网络请求
            loadBingPic();
        }
        String weatherStr = prefs.getString("weather", null);

        if (weatherStr != null) {   //本地有天气缓存数据，则优先显示缓存数据
            Weather weather = HandleJsonUtil.handleWeatherResponse(weatherStr);
            if (weather != null) {
                mWeatherId = weather.basic.weatherId;
                showWeatherInfo(weather);
            }
        } else {                    //本地没有天气缓存数据，则从网络请求数据
            mWeatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requesWeather(mWeatherId);
        }
    }

    /*
    加载必应每日一图
     */
    private void loadBingPic() {
        OkHttpUtil.getInstance().getAsync(Url.BINGPIC_URL, new JsonRequestCallback() {
            @Override
            public void onRequestSucess(String result) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", result);
                editor.apply();
                Glide.with(WeatherActivity.this).load(result).into(bingPicImg);
            }

            @Override
            public void onRequestFailure(String result) {
                Toast.makeText(WeatherActivity.this, "获取必应图片失败！失败原因：" + result, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initListener() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requesWeather(mWeatherId);
            }
        });
    }

    /**
     * 根据天气id请求城市天气信息。
     */
    public void requesWeather(String weatherId) {
        String weatherUrl = Url.WEATHER_Url + "?cityid=" + weatherId + "&key=" + Url.APP_KEY;

        OkHttpUtil.getInstance().getAsync(weatherUrl, new JsonRequestCallback() {
            @Override
            public void onRequestSucess(String result) {
                swipeRefresh.setRefreshing(false);
                Weather weather = HandleJsonUtil.handleWeatherResponse(result);
                if(weather!=null&& "ok".equals(weather.status)){
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                    editor.putString("weather", result);
                    editor.apply();
                    mWeatherId=weather.basic.weatherId;
                    showWeatherInfo(weather);
                } else {
                    Toast.makeText(WeatherActivity.this, "获取天气信息失败,请检查网络", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onRequestFailure(String result) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(WeatherActivity.this, "获取天气信息失败！失败原因：" + result, Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 显示天气信息
     */
    private void showWeatherInfo(Weather weather) {
        titleCity.setText(weather.basic.cityName);
        titleUpdateTime.setText("更新于"+weather.basic.update.updateTime.split(" ")[1]);
        degreeText.setText(weather.now.temperature + "℃");
        weatherInfoText.setText(weather.now.more.info);
        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sport = "运动建议：" + weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);

    }

    @OnClick(R.id.nav_button)
    public void onClick() {
        drawerLayout.openDrawer(GravityCompat.START);
    }
}
