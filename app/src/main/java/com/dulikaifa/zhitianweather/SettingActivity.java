package com.dulikaifa.zhitianweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dulikaifa.zhitianweather.service.AutoUpdateService;
import com.dulikaifa.zhitianweather.service.ServiceStateUtils;
import com.umeng.analytics.MobclickAgent;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Author:李晓峰 on 2017/4/26 18:11
 * E-mail:chaate@163.com
 * Copyright(c)2017,All rights reserved.
 * Usage :
 */

public class SettingActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    @InjectView(R.id.tv_version)
    TextView tvVersion;
    @InjectView(R.id.about_layout)
    RelativeLayout aboutLayout;
    @InjectView(R.id.update)
    RelativeLayout update;
    @InjectView(R.id.btn_back3)
    Button btnBack3;
    @InjectView(R.id.switch_auto_update)
    Switch switchAutoUpdate;
    @InjectView(R.id.switch_auto_location)
    Switch switchAutoLocation;
    boolean isUpdateServiceOpen = true;
    boolean isAutoLocationOpen = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        isUpdateServiceOpen = prefs.getBoolean("isUpdateServiceOpen", true);
        if (isUpdateServiceOpen) {
            switchAutoUpdate.setChecked(true);
        } else {
            switchAutoUpdate.setChecked(false);
        }
        isAutoLocationOpen = prefs.getBoolean("isAutoLocationOpen", true);
        if (isAutoLocationOpen) {
            switchAutoLocation.setChecked(true);
        } else {
            switchAutoLocation.setChecked(false);
        }
    }

    @Override
    protected void initListener() {
        switchAutoUpdate.setOnCheckedChangeListener(this);
        switchAutoLocation.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initData() {

    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @OnClick({R.id.btn_back3, R.id.update, R.id.about_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back3:
                finish();
                break;
            case R.id.update:
                Toast.makeText(getApplicationContext(), "已是最新版本！", Toast.LENGTH_SHORT).show();
                break;

            case R.id.about_layout:
                Intent intent = new Intent(SettingActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {


        switch (compoundButton.getId()) {

            case R.id.switch_auto_update:
                Intent intent = new Intent(getApplicationContext(), AutoUpdateService.class);
                boolean isRunningService = true;
                if (isChecked) {

                    isRunningService = ServiceStateUtils.isRunningService(getApplicationContext(), "com.dulikaifa.zhitianweather.service.AutoUpdateService");
                    if (!isRunningService) {
                        startService(intent);
                        isUpdateServiceOpen = true;
                        Toast.makeText(getApplicationContext(), "自动更新服务打开！", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                        editor.putBoolean("isUpdateServiceOpen", true);
                        editor.apply();
                    }

                } else {
                    isRunningService = ServiceStateUtils.isRunningService(getApplicationContext(), "com.dulikaifa.zhitianweather.service.AutoUpdateService");
                    if (isRunningService) {
                        stopService(intent);
                        isUpdateServiceOpen = false;
                        Toast.makeText(getApplicationContext(), "自动更新服务关闭！", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                        editor.putBoolean("isUpdateServiceOpen", false);
                        editor.apply();
                    }

                }
                break;
            case R.id.switch_auto_location:
                if (isChecked) {
                    isAutoLocationOpen = true;
                    Toast.makeText(getApplicationContext(), "自动定位打开！", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                    editor.putBoolean("isAutoLocationOpen", true);
                    editor.apply();
                } else {
                    isAutoLocationOpen = false;
                    Toast.makeText(getApplicationContext(), "自动定位关闭！", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                    editor.putBoolean("isAutoLocationOpen", false);
                    editor.apply();
                }
                break;
            default:
                break;
        }


    }

}
