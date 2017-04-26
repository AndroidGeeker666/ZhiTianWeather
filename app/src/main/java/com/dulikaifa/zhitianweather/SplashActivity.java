package com.dulikaifa.zhitianweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends Activity {
    private final int SPLASH_DISPLAY_LENGHT = 2000; // 延迟两秒

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
                String weatherJson = prefs.getString("json", null);
                if(weatherJson!=null){
                    JSONObject jsonObject= null;
                    try {
                        jsonObject = new JSONObject(weatherJson);
                        JSONArray heWeather5 = jsonObject.getJSONArray("HeWeather5");
                        String status = heWeather5.getJSONObject(0).getString("status");
                        Toast.makeText(SplashActivity.this, "status:" + status, Toast.LENGTH_SHORT).show();
                        if ( "ok".equals(status)){
                            Intent intent = new Intent(SplashActivity.this, WeatherActivity.class);
                            SplashActivity.this.startActivity(intent);
                            SplashActivity.this.finish();
                        }else{
                            Toast.makeText(SplashActivity.this, "程序发生错误，请联系开发者！", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    SplashActivity.this.startActivity(mainIntent);//显示主窗口
                    SplashActivity.this.finish();//关闭主窗口
                }

            }
        }, SPLASH_DISPLAY_LENGHT);
    }
}
