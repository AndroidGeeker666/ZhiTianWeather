package com.dulikaifa.zhitianweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dulikaifa.zhitianweather.util.SpUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sp = SpUtil.getInstance(this).getSp();
        if (sp.getString("weather", null) != null) {
            Intent intent = new Intent(this,WeatherActivity.class);
            finish();
        }
    }
}
