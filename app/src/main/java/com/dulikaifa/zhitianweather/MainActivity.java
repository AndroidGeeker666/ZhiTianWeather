package com.dulikaifa.zhitianweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.dulikaifa.zhitianweather.http.NetStatusUtil;
import com.dulikaifa.zhitianweather.util.GPSStatusUtil;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private SweetAlertDialog pDialog;
    private FrameLayout location_layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherJson = prefs.getString("json", null);
        boolean isAutoLocationOpen = prefs.getBoolean("isAutoLocationOpen", true);
        if (isAutoLocationOpen) {
            location();

        } else if(weatherJson!=null){
            JSONObject jsonObject= null;
            try {
                jsonObject = new JSONObject(weatherJson);
                JSONArray heWeather5 = jsonObject.getJSONArray("HeWeather5");
                String status = heWeather5.getJSONObject(0).getString("status");
                Toast.makeText(this, "status:" + status, Toast.LENGTH_SHORT).show();
                if ( "ok".equals(status)){
                    Intent intent = new Intent(this, WeatherActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(this, "程序发生错误，请联系开发者！", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            setContentView(R.layout.activity_main);
        }
    }

    private void location() {
        if (NetStatusUtil.isNetworkAvailable(this)&& GPSStatusUtil.isGpsAvailable(this)) {
            //初始化定位
            initLocation();
            dialog();
            startLocation();
        } else {
            if(!NetStatusUtil.isNetworkAvailable(this)){
                SweetAlertDialog sDialog=new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("网络未连接")
                        .setContentText("请打开网络，以便准确定位")
                        .setConfirmText("我知道了,去设置")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        });
                sDialog.setCancelable(false);
                sDialog.show();

                setContentView(R.layout.activity_fresh);
                FrameLayout location_layout =  (FrameLayout) findViewById(R.id.location_layout);
                Button location_btn = (Button) findViewById(R.id.location_btn);
                location_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        location();
                    }
                });
            }else if(!GPSStatusUtil.isGpsAvailable(this)){

                SweetAlertDialog sDialog=new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("GPS未打开")
                        .setContentText("请打开GPS，以便准确定位")
                        .setConfirmText("我知道了,去设置")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        });
                sDialog.setCancelable(false);
                sDialog.show();

                setContentView(R.layout.activity_fresh);
                FrameLayout location_layout =  (FrameLayout) findViewById(R.id.location_layout);
                Button location_btn = (Button) findViewById(R.id.location_btn);
                location_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        location();
                    }
                });
            }
        }
    }

    private void dialog() {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("自动定位中，请稍候...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    /**
     * 初始化定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            pDialog.dismiss();
            if (null != location) {
                stopLocation();
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.getErrorCode() == 0) {
                    String locationCity = location.getCountry() + "-" + location.getCity();
                    Toast.makeText(MainActivity.this, "定位成功，当前城市：" + locationCity, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                    intent.putExtra("locationCity", locationCity);
                    MainActivity.this.startActivity(intent);
                    MainActivity.this.finish();
                } else {


                    //定位失败
                    String errorMessage = location.getErrorCode() + "\n" + location.getErrorInfo();
                    Toast.makeText(MainActivity.this, "自动定位失败！失败原因：" + errorMessage, Toast.LENGTH_SHORT).show();
                    stopLocation();
                    setContentView(R.layout.activity_main);
                }

            } else {
                stopLocation();
                //定位失败
                Toast.makeText(MainActivity.this, "自动定位失败！", Toast.LENGTH_SHORT).show();
                setContentView(R.layout.activity_main);
            }
        }
    };

    /**
     * 开始定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    public void startLocation() {
        // 启动定位
        locationClient.startLocation();
    }

    /**
     * 停止定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void stopLocation() {
        // 停止定位
        locationClient.stopLocation();
    }

    /**
     * 销毁定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void destroyLocation() {
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }
}
