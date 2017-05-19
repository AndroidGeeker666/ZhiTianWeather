package com.dulikaifa.zhitianweather;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
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
import com.dulikaifa.zhitianweather.service.ServiceStateUtils;
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

import static com.dulikaifa.zhitianweather.R.id.today_temp;
import static com.dulikaifa.zhitianweather.R.id.today_weather;


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

    private static String TAG = WeatherActivity.class.getSimpleName();
    private Weather mWeather;
    private SpeechSynthesizer mTts;
    //默认天气预报员
    private static final String SPEAKER_NAME = "xiaoyan";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private AutoVoiceCastReceiver receiver;

    /**
     * 初始化
     *
     * @param savedInstanceState 存储界面关闭前存储的数据的Bundle对象
     */

    @Override
    public void onCreate(final Bundle savedInstanceState) {

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
        registerAutoVoiceCastReceiver();

    }

    /**
     * 注册自动播报的广播
     */
    private void registerAutoVoiceCastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.dulikaifa.zhitianweather");
        receiver = new AutoVoiceCastReceiver();
        registerReceiver(receiver, filter);

    }


    class AutoVoiceCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (mWeatherId != null && mCountryName != null) {
                requesWeather(mWeatherId, mCountryName);
                Toast.makeText(WeatherActivity.this, "定时任务执行了！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 初始化讯飞语音参数配置
     */
    private void initIflyVoice() {
        //1.创建SpeechSynthesizer 对象, 第二个参数：本地合成时传InitListener
        mTts = SpeechSynthesizer.createSynthesizer(this, null);
        mTts.setParameter(SpeechConstant.SPEED, "50"); //设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80"); //设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端

    }

    /**
     * app异常关闭重新启动后恢复状态和数据
     *
     * @param savedInstanceState 存储界面关闭前存储的数据的Bundle对象
     */

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mWeatherId = (String) savedInstanceState.get("weather_id");
            mCountryName = (String) savedInstanceState.get("countryName");

        }
    }

    /**
     * app异常关闭前保存状态和数据
     *
     * @param outState 存储界面关闭前存储的数据的Bundle对象
     */
    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("weather_id", mWeatherId);
        outState.putString("countryName", mCountryName);
    }

    /**
     * Activity的生命周期onResume()
     */
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        //移动数据统计分析
        FlowerCollector.onResume(WeatherActivity.this);
        FlowerCollector.onPageStart(TAG);
    }

    /**
     * Activity的生命周期onPause()
     */
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        //移动数据统计分析
        FlowerCollector.onPageEnd(TAG);
        FlowerCollector.onPause(WeatherActivity.this);
    }

    /**
     * Activity的生命周期onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        sDialog = null;
        if (null != mTts) {
            mTts.stopSpeaking();
            // 退出时释放连接
            mTts.destroy();
        }
        unregisterReceiver(receiver);

    }

    /**
     * 使状态栏透明
     */
    private void transparentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 初始化界面
     */
    private void initView() {
        initIflyVoice();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) { //本地有缓存的必应图片，直接从缓存中加载
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
            String country = getIntent().getStringExtra("COUNTRY");
            String fragmentCountyName = getIntent().getStringExtra("fragmentCountyName");
            if (country != null && fragmentCountyName != null) {
                swipeRefresh.setRefreshing(true);
                requesWeather(fragmentCountyName, country);
            } else {
                String weatherStr = prefs.getString("json", null);
                String weatherId = prefs.getString("weatherId", null);
                String countryName = prefs.getString("countryName", null);
                if (weatherStr != null && countryName != null) {
                    Toast.makeText(WeatherActivity.this, "我是缓存中的数据：" + weatherId, Toast.LENGTH_SHORT).show();
                    mWeatherId = weatherId;
                    mCountryName = countryName;
                    Weather weather = HandleJsonUtil.handleWeatherResponse(weatherStr);
                    mWeather = weather;
                    showView(countryName, weather);

                }
            }
        }
    }

    /**
     * 接受山上个关闭界面回传的数据
     *
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        包含回传数据的Intent对象
     */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

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

    /**
     * 加载必应每日一图
     */
    private void loadBingPic() {
        OkHttpUtil.getInstance().getAsync(Url.BINGPIC_URL, new JsonRequestCallback() {
            @Override
            public void onRequestSucess(final String result) {
                editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", result);
                editor.apply();
                Glide.with(WeatherActivity.this).load(result).into(bingPicImg);
            }

            @Override
            public void onRequestFailure(final String result) {
                Toast.makeText(WeatherActivity.this, "获取必应图片失败！失败原因：" + result, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 初始化监听器
     */
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
     *
     * @param weatherId   城市名称
     * @param countryName 国家名称
     */
    public void requesWeather(final String weatherId, final String countryName) {
        String weatherUrl = Url.WEATHER_Url + "?city=" + weatherId + "&key=" + Url.APP_KEY;
        OkHttpUtil.getInstance().getAsync(weatherUrl, new JsonRequestCallback() {
            @Override
            public void onRequestSucess(final String result) {
                swipeRefresh.setRefreshing(false);
                mWeatherId = weatherId;
                mCountryName = countryName;
                editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("countryName", countryName);
                editor.putString("weatherId", weatherId);
                editor.putString("json", result);
                editor.apply();
                Weather weather = HandleJsonUtil.handleWeatherResponse(result);
                mWeather = weather;
                showView(countryName, weather);

            }

            @Override
            public void onRequestFailure(final String result) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(WeatherActivity.this, "网络异常,请检查网络设置!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 显示界面
     *
     * @param countryName 国家名称
     * @param weather     天气
     */
    private void showView(final String countryName, final Weather weather) {
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
            prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
            boolean isAutoSpeak = prefs.getBoolean("isAutoSpeak", true);
            if (isAutoSpeak && mWeather != null) {
                voiceWeatherForecast();
            }
            boolean isUpdateServiceOpen = prefs.getBoolean("isUpdateServiceOpen", true);
            if (isUpdateServiceOpen) {
                if (ServiceStateUtils.isRunningService(WeatherActivity.this, "com.dulikaifa.zhitianweather.service.AutoUpdateService")) {
                    Toast.makeText(WeatherActivity.this, "AutoUpdateService服务正在运行", Toast.LENGTH_SHORT).show();

                } else {
                    Intent intent = new Intent(this, AutoUpdateService.class);
                    startService(intent);
                }
            }
            sendNotification();

        } else {
            Toast.makeText(WeatherActivity.this, "存取json发生错误，请检查！", Toast.LENGTH_SHORT).show();

        }
    }

    private void sendNotification() {
        Intent intent = new Intent(this, WeatherActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String condition = mWeather.now.nowCondition.weather + "    ";
        String temp = mWeather.now.nowTemperature + "°    ";
        String windDir = mWeather.now.wind.nowWindDirection + "    ";
        String windPower = mWeather.now.wind.nowWindPower;
        Notification noti = new NotificationCompat.Builder(this)
                .setContentTitle(mWeatherId + "当前天气")
                .setContentText(condition + temp + windDir + windPower)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(getResources(),R.mipmap.logo666)))
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.logo666)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.logo666))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build();
        manager.notify(1, noti);


    }

    /**
     * 显示全部天气信息
     *
     * @param weather 天气
     */
    private void showAllInfo(final Weather weather) {
        showPartInfo(weather);
        showCommonInfo(weather);

    }

    /**
     * 显示部分天气信息
     *
     * @param weather 天气
     */
    @SuppressLint("SetTextI18n")
    private void showPartInfo(final Weather weather) {

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
     *
     * @param weather 天气信息
     */

    @SuppressLint("SetTextI18n")
    private void showCommonInfo(final Weather weather) {
        if (weather != null) {
            basicCity.setText(weather.basic.cityName);
            basicUpdatetime.setText(weather.basic.update.updateTime);
            nowDegreeText.setText(weather.now.nowTemperature + "°");
            nowWeatherText.setText(weather.now.nowCondition.weather);
            nowWinddirText.setText(weather.now.wind.nowWindDirection);
            nowFeelTemp.setText("体感温度" + weather.now.nowFeelTemperature + "°");
            nowWindpowerText.setText(getWindPower());
            dynamicLayout.removeAllViews();
            for (int i = 0; i < weather.forecastList.size(); i++) {
                Forecast forecast = weather.forecastList.get(i);
                View view = LayoutInflater.from(this).inflate(R.layout.dynamic_item, dynamicLayout, false);
                TextView today = (TextView) view.findViewById(R.id.today);
                TextView todayWeather = (TextView) view.findViewById(today_weather);
                TextView todayTemp = (TextView) view.findViewById(today_temp);
                today.setText(forecast.date.substring(5));
                if (forecast.condition.weatherDay.equals(forecast.condition.weatherNight)) {
                    todayWeather.setText(forecast.condition.weatherDay);
                } else {
                    todayWeather.setText(forecast.condition.weatherDay + "转" + forecast.condition.weatherNight);
                }
                todayTemp.setText(forecast.temperature.min + "~" + forecast.temperature.max + "°");
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
                windpowerText.setText(getWindPower());
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

    /**
     * 处理点击事件
     *
     * @param view 用户点击到的控件
     */
    @OnClick({R.id.nav_button, R.id.city_add, R.id.iv_voice})
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.nav_button:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.city_add:
                Intent intent = new Intent(WeatherActivity.this, CityManageActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.iv_voice:
                if (mWeather != null) {
                    voiceWeatherForecast();
                }
                break;
            default:
                break;
        }

    }

    /**
     * 播报天气
     */
    public void voiceWeatherForecast() {
        if (NetStatusUtil.isNetworkAvailable(WeatherActivity.this)) {
            prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
            String speaker = prefs.getString("speakerName", SPEAKER_NAME);
            //设置发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME, speaker); //设置发音人
            String forecastContent = getForecastContent();
            ivVoice.setBackgroundResource(R.drawable.voice);
            final AnimationDrawable imageAnim = (AnimationDrawable) ivVoice.getBackground();
            if (!mTts.isSpeaking() && (forecastContent != null)) {

                mTts.startSpeaking(forecastContent, new SynthesizerListener() {
                    @Override
                    public void onSpeakBegin() {
                        imageAnim.start();
                    }

                    @Override
                    public void onBufferProgress(final int i, final int i1, final int i2, final String s) {

                    }

                    @Override
                    public void onSpeakPaused() {

                    }

                    @Override
                    public void onSpeakResumed() {

                    }

                    @Override
                    public void onSpeakProgress(final int i, final int i1, final int i2) {

                    }

                    @Override
                    public void onCompleted(final SpeechError speechError) {

                        imageAnim.stop();
                        ivVoice.setBackgroundResource(R.drawable.voice_selector);
                    }

                    @Override
                    public void onEvent(final int i, final int i1, final int i2, final Bundle bundle) {

                    }
                });
            } else {
                mTts.stopSpeaking();
                imageAnim.stop();
                ivVoice.setBackgroundResource(R.drawable.voice_selector);

            }

        } else {
            Toast.makeText(WeatherActivity.this, "网络未打开，请打开网络后重试", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取天气播报内容
     *
     * @return getTimeContent() + getWeatherContent();
     */
    public String getForecastContent() {

        return getTimeContent() + getWeatherContent();

    }

    /**
     * 获取天气内容
     *
     * @return
     */
    private String getWeatherContent() {
        if (mWeather != null) {

            String todayWeather;
            String airQulity = null;
            String airAqi = null;
            if (mWeather.forecastList.get(0).condition.weatherDay.equals(mWeather.forecastList.get(0).condition.weatherNight)) {
                todayWeather = mWeather.forecastList.get(0).condition.weatherDay;
            } else {
                todayWeather = mWeather.forecastList.get(0).condition.weatherDay + "转" + mWeather.forecastList.get(0).condition.weatherNight;
            }
            String todayTemp = mWeather.forecastList.get(0).temperature.min + "至" + mWeather.forecastList.get(0).temperature.max + "度";
            String todayWind = mWeather.forecastList.get(0).wind.windDirection + "," + getWindPower();
            if (mCountryName.equals("中国") && (!mWeatherId.equals("西安")) && (!mWeatherId.equals("河北"))) {
                airQulity = mWeather.aqi.city.qlty;
                airAqi = mWeather.aqi.city.aqi;
            }

            if (mCountryName.equals("中国")) {

                return mWeatherId + ",今天天气," + todayWeather + "," + todayTemp + "," + todayWind + ",空气质量指数," + airAqi + ",空气质量," + airQulity;
            } else {
                return mWeatherId + ",今天天气," + todayWeather + "," + todayTemp + "," + todayWind;
            }
        }
        return null;
    }

    /**
     * 获取格式化风力
     *
     * @return (windPower.equals("微风")) ? windPower : (windPower + "级");
     */
    private String getWindPower() {

        String windPower = mWeather.forecastList.get(0).wind.windPower;
        return (windPower.equals("微风")) ? windPower : (windPower + "级");
    }

    /**
     * 获取播报的时间内容
     *
     * @return "主人好，现在是" + mYear + "年" + mMonth + "月" + mDay + "日," + getWeek(mWeek) + "," + mHour + "点" + mMinute + "分,";
     */
    private String getTimeContent() {
        Calendar c = Calendar.getInstance(); //首先要获取日历对象
        int mYear = c.get(Calendar.YEAR); // 获取当前年份
        int mMonth = c.get(Calendar.MONTH) + 1; // 获取当前月份
        int mDay = c.get(Calendar.DAY_OF_MONTH); // 获取当日期
        int mWeek = c.get(Calendar.DAY_OF_WEEK); // 获取当前日期的星期
        int mHour = c.get(Calendar.HOUR_OF_DAY); //时
        int mMinute = c.get(Calendar.MINUTE); //分

        return "主人好，现在是" + mYear + "年" + mMonth + "月" + mDay + "日," + getWeek(mWeek) + "," + mHour + "点" + mMinute + "分,";
    }

    /**
     * 获取星期
     *
     * @param week 星期
     * @return realWeek
     */
    private String getWeek(final int week) {
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

    /**
     * 处理返回键点击事件
     */
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
                        sDialog = null;
                    }
                });
        sDialog.show();

    }

}
