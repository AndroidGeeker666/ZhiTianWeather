package com.dulikaifa.zhitianweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dulikaifa.zhitianweather.bean.Forecast;
import com.dulikaifa.zhitianweather.bean.Weather;
import com.dulikaifa.zhitianweather.http.JsonRequestCallback;
import com.dulikaifa.zhitianweather.http.NetStatusUtil;
import com.dulikaifa.zhitianweather.http.OkHttpUtil;
import com.dulikaifa.zhitianweather.http.Url;
import com.dulikaifa.zhitianweather.service.AutoUpdateService;
import com.dulikaifa.zhitianweather.util.HandleJsonUtil;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.sunflower.FlowerCollector;
import com.umeng.analytics.MobclickAgent;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.dulikaifa.zhitianweather.MyApplication.context;


/**
 * Author:李晓峰 on 2017/4/22 22:14
 * E-mail:chaate@163.com
 * Copyright(c)2017,All rights reserved.
 * Usage :
 */

public class WeatherActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;
    @InjectView(R.id.aqi_layout)
    LinearLayout aqiLayout;
    @InjectView(R.id.suggestion_layout)
    LinearLayout suggestionLayout;
    @InjectView(R.id.forecast_layout)
    LinearLayout forecastLayout;
    @InjectView(R.id.now_layout)
    LinearLayout nowLayout;
    @InjectView(R.id.dynamic_layout)
    LinearLayout dynamicLayout;
    @InjectView(R.id.basic_layout)
    LinearLayout basicLayout;
    @InjectView(R.id.forecast_all_layout)
    LinearLayout forecastAllLayout;
    @InjectView(R.id.bing_pic_img)
    ImageView bingPicImg;
    @InjectView(R.id.nav_button)
    Button navButton;
    @InjectView(R.id.basic_city)
    TextView basicCity;
    @InjectView(R.id.basic_updatetime)
    TextView basicUpdatetime;
    @InjectView(R.id.now_degree_text)
    TextView nowDegreeText;
    @InjectView(R.id.now_weather_text)
    TextView nowWeatherText;
    @InjectView(R.id.now_winddir_text)
    TextView nowWinddirText;
    @InjectView(R.id.now_windpower_text)
    TextView nowWindpowerText;
    @InjectView(R.id.now_feel_temp)
    TextView nowFeelTemp;
    @InjectView(R.id.air_text)
    TextView airText;
    @InjectView(R.id.aqi_text)
    TextView aqiText;
    @InjectView(R.id.co_text)
    TextView coText;
    @InjectView(R.id.no2_text)
    TextView no2Text;
    @InjectView(R.id.o3_text)
    TextView o3Text;
    @InjectView(R.id.so2_text)
    TextView so2Text;
    @InjectView(R.id.pm10_text)
    TextView pm10Text;
    @InjectView(R.id.pm25_text)
    TextView pm25Text;
    @InjectView(R.id.air_dirty_text)
    TextView airDirtyText;
    @InjectView(R.id.comfort_text)
    TextView comfortText;
    @InjectView(R.id.car_wash_text)
    TextView carWashText;
    @InjectView(R.id.dressing_text)
    TextView dressingText;
    @InjectView(R.id.influenza_text)
    TextView influenzaText;
    @InjectView(R.id.sport_text)
    TextView sportText;
    @InjectView(R.id.travel_text)
    TextView travelText;
    @InjectView(R.id.ultraviolet_suggestion_text)
    TextView ultravioletSuggestionText;
    @InjectView(R.id.weather_layout)
    ScrollView weatherLayout;
    @InjectView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.air_info)
    TextView airInfo;
    @InjectView(R.id.comfort_info)
    TextView comfortInfo;
    @InjectView(R.id.car_wash_info)
    TextView carWashInfo;
    @InjectView(R.id.dressing_info)
    TextView dressingInfo;
    @InjectView(R.id.influenza_info)
    TextView influenzaInfo;
    @InjectView(R.id.sport_info)
    TextView sportInfo;
    @InjectView(R.id.travel_info)
    TextView travelInfo;
    @InjectView(R.id.ultraviolet_suggestion_info)
    TextView ultravioletSuggestionInfo;
    @InjectView(R.id.iv_voice)
    ImageButton ivVoice;
    private String mWeatherId;
    private String mCountryName;
    private SweetAlertDialog sDialog;
    private SpeechSynthesizer mTts;
    private static String TAG = WeatherActivity.class.getSimpleName();
    private Weather mWeather;
    public static final String PREFER_NAME = "com.iflytek.setting";
    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        transparentStatusBar();
        setContentView(R.layout.activity_weather);
        if (savedInstanceState != null) {
            mWeatherId = (String) savedInstanceState.get("weather_id");
            mCountryName = (String) savedInstanceState.get("countryName");
        }
        ButterKnife.inject(this);
        initView();
        initListener();
        initIflyVoice();

    }

    private void startSpeakingWeather(String weatherContent) {
        //3.开始合成
        mTts.startSpeaking(weatherContent, null);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mWeatherId = (String) savedInstanceState.get("weather_id");
            mCountryName = (String) savedInstanceState.get("countryName");

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("weather_id", mWeatherId);
        outState.putString("countryName", mCountryName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sDialog = null;
        if (null != mTts) {
            mTts.stopSpeaking();
            // 退出时释放连接
            mTts.destroy();
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        //移动数据统计分析
        FlowerCollector.onResume(WeatherActivity.this);
        FlowerCollector.onPageStart(TAG);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        //移动数据统计分析
        FlowerCollector.onPageEnd(TAG);
        FlowerCollector.onPause(WeatherActivity.this);
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

        String locationCity = getIntent().getStringExtra("locationCity");

        if (locationCity != null) {
            String[] location = locationCity.split("-");
            swipeRefresh.setRefreshing(true);
            requesWeather(location[1], location[0]);
        } else {
            String COUNTRY = getIntent().getStringExtra("COUNTRY");
            String fragmentCountyName = getIntent().getStringExtra("fragmentCountyName");
            if (COUNTRY != null && fragmentCountyName != null) {
                swipeRefresh.setRefreshing(true);
                requesWeather(fragmentCountyName, COUNTRY);
            } else {
                String weatherStr = prefs.getString("json", null);
                String weatherId = prefs.getString("weatherId", null);
                String countryName = prefs.getString("countryName", null);
                if (weatherStr != null && countryName != null) {
                    mWeatherId = weatherId;
                    mCountryName = countryName;
                    Weather weather = HandleJsonUtil.handleWeatherResponse(weatherStr);
                    showView(countryName, weather);
                }
            }
        }
    }

    private void initIflyVoice() {
        //1.创建SpeechSynthesizer 对象, 第二个参数：本地合成时传InitListener
        mTts = SpeechSynthesizer.createSynthesizer(context, null);
        mSharedPreferences = getSharedPreferences(PREFER_NAME, MODE_PRIVATE);
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, mSharedPreferences.getString("stream_preference", "3"));
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_CODE:
                if (resultCode == 2) {
                    String name = data.getStringExtra("name");
                    String[] names = name.split("-");
                    requesWeather(names[1], names[0]);
                }
                break;
            default:
                break;
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
                if (mWeatherId != null && mCountryName != null) {
                    requesWeather(mWeatherId, mCountryName);
                    Toast.makeText(WeatherActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 根据天气id请求城市天气信息。
     */
    public void requesWeather(final String weatherId, final String countryName) {
        String weatherUrl = Url.WEATHER_Url + "?city=" + weatherId + "&key=" + Url.APP_KEY;
        OkHttpUtil.getInstance().getAsync(weatherUrl, new JsonRequestCallback() {
            @Override
            public void onRequestSucess(String result) {
                swipeRefresh.setRefreshing(false);
                mWeatherId = weatherId;
                mCountryName = countryName;
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("countryName", countryName);
                editor.putString("weatherId", weatherId);
                editor.putString("json", result);
                editor.apply();
                Weather weather = HandleJsonUtil.handleWeatherResponse(result);
                mWeather = weather;
                showView(countryName, weather);
            }

            @Override
            public void onRequestFailure(String result) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(WeatherActivity.this, "获取天气信息失败,请检查网络设置!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showView(String countryName, Weather weather) {
        if (weather != null && "ok".equals(weather.status)) {
            if (countryName.equals("中国")) {
                basicLayout.setVisibility(View.VISIBLE);
                nowLayout.setVisibility(View.VISIBLE);
                forecastAllLayout.setVisibility(View.VISIBLE);
                aqiLayout.setVisibility(View.VISIBLE);
                suggestionLayout.setVisibility(View.VISIBLE);
                showAllInfo(weather);
            } else {
                basicLayout.setVisibility(View.VISIBLE);
                nowLayout.setVisibility(View.VISIBLE);
                forecastAllLayout.setVisibility(View.VISIBLE);
                aqiLayout.setVisibility(View.GONE);
                suggestionLayout.setVisibility(View.GONE);
                showCommonInfo(weather);
            }

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            boolean isUpdateServiceOpen = prefs.getBoolean("isUpdateServiceOpen", true);
            if (isUpdateServiceOpen) {
                Intent intent = new Intent(WeatherActivity.this, AutoUpdateService.class);
                startService(intent);
            }

        } else {
            Toast.makeText(WeatherActivity.this, "存取json发生错误，请检查！", Toast.LENGTH_SHORT).show();

        }
    }

    private void showAllInfo(Weather weather) {
        showPartInfo(weather);
        showCommonInfo(weather);

    }

    private void showPartInfo(Weather weather) {

        if (weather.aqi != null) {
            airText.setText(weather.aqi.city.qlty);
            aqiText.setText(weather.aqi.city.aqi);
            coText.setText(weather.aqi.city.co);
            no2Text.setText(weather.aqi.city.no2);
            o3Text.setText(weather.aqi.city.o3);
            so2Text.setText(weather.aqi.city.so2);
            pm10Text.setText(weather.aqi.city.pm10);
            pm25Text.setText(weather.aqi.city.pm25);

        }
        if (weather.suggestion != null) {
            if (!(weather.basic.cityName.equals("西安")) && (!(weather.basic.cityName.equals("河北")))) {
                airDirtyText.setText(weather.suggestion.airIndex.level);
            } else {
                airDirtyText.setText("无数据");
            }
            comfortText.setText(weather.suggestion.comfortIndex.level);
            carWashText.setText(weather.suggestion.carWashIndex.level);
            dressingText.setText(weather.suggestion.dressingIndex.level);
            influenzaText.setText(weather.suggestion.influenzaIndex.level);
            sportText.setText(weather.suggestion.sportIndex.level);
            travelText.setText(weather.suggestion.travelIndex.level);
            ultravioletSuggestionText.setText(weather.suggestion.ultravioletRayIndex.level);
            if (!(weather.basic.cityName.equals("西安")) && (!(weather.basic.cityName.equals("河北")))) {
                airInfo.setText("       " + weather.suggestion.airIndex.info);
            } else {
                airInfo.setText("无数据");
            }
            comfortInfo.setText("       " + weather.suggestion.comfortIndex.info);
            carWashInfo.setText("       " + weather.suggestion.carWashIndex.info);
            dressingInfo.setText("       " + weather.suggestion.dressingIndex.info);
            influenzaInfo.setText("       " + weather.suggestion.influenzaIndex.info);
            sportInfo.setText("       " + weather.suggestion.sportIndex.info);
            travelInfo.setText("       " + weather.suggestion.travelIndex.info);
            ultravioletSuggestionInfo.setText("       " + weather.suggestion.ultravioletRayIndex.info);

        }

    }

    /**
     * 显示天气信息
     */
    private void showCommonInfo(Weather weather) {
        if (weather != null) {
            basicCity.setText(weather.basic.cityName);
            basicUpdatetime.setText(weather.basic.update.updateTime);
            nowDegreeText.setText(weather.now.nowTemperature + "°");
            nowWeatherText.setText(weather.now.nowCondition.weather);
            nowWinddirText.setText(weather.now.wind.nowWindDirection);
            nowFeelTemp.setText("体感温度" + weather.now.nowFeelTemperature + "°");
            nowWindpowerText.setText(weather.now.wind.nowWindPower + "级");
            dynamicLayout.removeAllViews();
            for (int i = 0; i < weather.forecastList.size(); i++) {
                Forecast forecast = weather.forecastList.get(i);
                View view = LayoutInflater.from(this).inflate(R.layout.dynamic_item, dynamicLayout, false);
                TextView today = (TextView) view.findViewById(R.id.today);
                TextView today_weather = (TextView) view.findViewById(R.id.today_weather);
                TextView today_temp = (TextView) view.findViewById(R.id.today_temp);
                today.setText(forecast.date.substring(5));
                if (forecast.condition.weatherDay.equals(forecast.condition.weatherNight)) {
                    today_weather.setText(forecast.condition.weatherDay);
                } else {
                    today_weather.setText(forecast.condition.weatherDay + "转" + forecast.condition.weatherNight);
                }
                today_temp.setText(forecast.temperature.min + "~" + forecast.temperature.max + "°");
                dynamicLayout.addView(view);
                if (i != 2) {
                    View view1 = LayoutInflater.from(this).inflate(R.layout.line, dynamicLayout, false);
                    dynamicLayout.addView(view1);
                }
            }
            forecastLayout.removeAllViews();
            for (Forecast forecast : weather.forecastList) {
                View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
                TextView date = (TextView) view.findViewById(R.id.date);
                TextView skyText = (TextView) view.findViewById(R.id.sky_text);
                TextView tempText = (TextView) view.findViewById(R.id.temp_text);
                TextView winddegText = (TextView) view.findViewById(R.id.winddeg_text);
                TextView winddirText = (TextView) view.findViewById(R.id.winddir_text);
                TextView windpowerText = (TextView) view.findViewById(R.id.windpower_text);
                TextView windspeedText = (TextView) view.findViewById(R.id.windspeed_text);
                TextView humidityText = (TextView) view.findViewById(R.id.humidity_text);
                TextView rainProbabilityText = (TextView) view.findViewById(R.id.rainProbability_text);
                TextView rainAmoutText = (TextView) view.findViewById(R.id.rainAmout_text);
                TextView atmosphericText = (TextView) view.findViewById(R.id.atmospheric_text);
                TextView ultravioletText = (TextView) view.findViewById(R.id.ultraviolet_Ray_text);
                TextView visibilityText = (TextView) view.findViewById(R.id.visibility_text);
                TextView sunriseText = (TextView) view.findViewById(R.id.sunrise_text);
                TextView sunsetText = (TextView) view.findViewById(R.id.sunset_text);
                TextView moonriseText = (TextView) view.findViewById(R.id.moonrise_text);
                TextView moonsetText = (TextView) view.findViewById(R.id.moonset_text);
                date.setText(forecast.date);
                if (forecast.condition.weatherDay.equals(forecast.condition.weatherNight)) {
                    skyText.setText(forecast.condition.weatherDay);
                } else {
                    skyText.setText(forecast.condition.weatherDay + "转" + forecast.condition.weatherNight);
                }
                tempText.setText(forecast.temperature.min + "~" + forecast.temperature.max + "°");
                winddegText.setText(forecast.wind.windDegree + "°");
                winddirText.setText(forecast.wind.windDirection);
                windpowerText.setText(forecast.wind.windPower);
                windspeedText.setText(forecast.wind.windSpeed + "kmph");
                humidityText.setText(forecast.humidity + "%");
                rainProbabilityText.setText(forecast.rainProbability);
                rainAmoutText.setText(forecast.rainAmout + "mm");
                atmosphericText.setText(forecast.atmosphericPressure + "兆帕");
                ultravioletText.setText(forecast.ultravioletRay + "级");
                visibilityText.setText(forecast.visibility + "km");
                sunriseText.setText(forecast.astrology.sunrise);
                sunsetText.setText(forecast.astrology.sunset);
                moonriseText.setText(forecast.astrology.moonrise);
                moonsetText.setText(forecast.astrology.moonset);
                forecastLayout.addView(view);

            }
        }
    }

    @OnClick({R.id.nav_button, R.id.city_add, R.id.iv_voice})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_button:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.city_add:
                Intent intent = new Intent(WeatherActivity.this, CityManageActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.iv_voice:
                if (NetStatusUtil.isNetworkAvailable(this)) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    String speaker = prefs.getString("speakerName", "xiaoyan");
                    //设置发音人
                    mTts.setParameter(SpeechConstant.VOICE_NAME, speaker); //设置发音人
                    String forecastContent = getForecastContent();
                    ivVoice.setBackgroundResource(R.drawable.voice);
                    final AnimationDrawable imageAnim = (AnimationDrawable) ivVoice.getBackground();
                    if (!mTts.isSpeaking()) {
                        imageAnim.start();
                        mTts.startSpeaking(forecastContent, new SynthesizerListener() {
                            @Override
                            public void onSpeakBegin() {

                            }

                            @Override
                            public void onBufferProgress(int i, int i1, int i2, String s) {

                            }

                            @Override
                            public void onSpeakPaused() {

                            }

                            @Override
                            public void onSpeakResumed() {

                            }

                            @Override
                            public void onSpeakProgress(int i, int i1, int i2) {

                            }

                            @Override
                            public void onCompleted(SpeechError speechError) {
                                imageAnim.stop();
                                ivVoice.setBackgroundResource(R.drawable.voice_selector);
                            }

                            @Override
                            public void onEvent(int i, int i1, int i2, Bundle bundle) {

                            }
                        });
                    } else {
                        mTts.stopSpeaking();
                        imageAnim.stop();
                        ivVoice.setBackgroundResource(R.drawable.voice_selector);
                    }

                } else {
                    Toast.makeText(this, "网络未打开，请打开网络后重试", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onBackPressed() {

        sDialog = new SweetAlertDialog(WeatherActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("确定退出知天天气?")
                .setCancelText("取消")
                .setConfirmText("确定")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        WeatherActivity.super.onBackPressed();
                    }
                });
        sDialog.show();

    }

    public String getForecastContent() {

        return getTimeContent() + getWeatherContent();

    }

    private String getWeatherContent() {
        if (mWeather!=null) {
            String todayWeather;
            if (mWeather.forecastList.get(0).condition.weatherDay.equals(mWeather.forecastList.get(0).condition.weatherNight)) {
                todayWeather = mWeather.forecastList.get(0).condition.weatherDay;
            } else {
                todayWeather = mWeather.forecastList.get(0).condition.weatherDay + "转" + mWeather.forecastList.get(0).condition.weatherNight;
            }
            String todayTemp = mWeather.forecastList.get(0).temperature.min + "至" + mWeather.forecastList.get(0).temperature.max + "度";
            String todayWind = mWeather.forecastList.get(0).wind.windDirection + mWeather.forecastList.get(0).wind.windPower + "级";
            String airQulity=mWeather.aqi.city.qlty;
            String airAqi=mWeather.aqi.city.aqi;

            if (mWeatherId!=null&&mCountryName!=null&&mCountryName.equals("中国")){

                return mWeatherId+",今天天气," + todayWeather +","+todayTemp +","+todayWind+ ",空气质量指数," +airAqi+ ",空气质量,"+airQulity ;
            }else {
                return mWeatherId+",今天天气," + todayWeather + "," + todayTemp + ","+todayWind;
            }
        }
        return null;
    }

    private String getTimeContent() {
        Calendar c = Calendar.getInstance();//首先要获取日历对象
        int mYear = c.get(Calendar.YEAR); // 获取当前年份
        int mMonth = c.get(Calendar.MONTH) + 1;// 获取当前月份
        int mDay = c.get(Calendar.DAY_OF_MONTH);// 获取当日期
        int mWeek = c.get(Calendar.DAY_OF_WEEK);// 获取当前日期的星期
        int mHour = c.get(Calendar.HOUR_OF_DAY);//时
        int mMinute = c.get(Calendar.MINUTE);//分

        return "今天是" + mYear + "年" + mMonth + "月" + mDay + "日," + getWeek(mWeek) + ","+mHour + "点" + mMinute + "分,";
    }

    private String getWeek(int week) {
        String realWeek = null;
        switch (week) {
            case 1:
                realWeek = "星期日";
                break;
            case 2:
                realWeek = "星期一";
                break;
            case 3:
                realWeek = "星期二";
                break;
            case 4:
                realWeek = "星期三";
                break;
            case 5:
                realWeek = "星期四";
                break;
            case 6:
                realWeek = "星期五";
                break;
            case 7:
                realWeek = "星期六";
                break;
        }
        return realWeek;
    }
}
