package com.dulikaifa.zhitianweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dulikaifa.zhitianweather.service.AutoUpdateService;
import com.dulikaifa.zhitianweather.service.ServiceStateUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Author:李晓峰 on 2017/4/26 18:11
 * E-mail:chaate@163.com
 * Copyright(c)2017,All rights reserved.
 * Usage :
 */

public class SettingActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    @InjectView(R.id.myswitch_loading_photo)
    Switch myswitchLoadingPhoto;
    @InjectView(R.id.tv_version)
    TextView tvVersion;
    @InjectView(R.id.about)
    TextView about;
    @InjectView(R.id.about_layout)
    RelativeLayout aboutLayout;
    @InjectView(R.id.update)
    RelativeLayout update;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);
        ButterKnife.inject(this);

        myswitchLoadingPhoto.setOnCheckedChangeListener(this);

    }

    @OnClick({R.id.update,R.id.about_layout})
    public void onClick(View view) {
        switch (view.getId()) {
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
        Intent intent = new Intent(getApplicationContext(), AutoUpdateService.class);
        boolean isRunningService = true;
        if (isChecked) {
            isRunningService = ServiceStateUtils.isRunningService(getApplicationContext(), "com.dulikaifa.zhitianweather.service.AutoUpdateService");
            if (!isRunningService) {
                startService(intent);
                Toast.makeText(getApplicationContext(), "自动更新服务打开！", Toast.LENGTH_SHORT).show();
            }

        } else {
            isRunningService = ServiceStateUtils.isRunningService(getApplicationContext(), "com.dulikaifa.zhitianweather.service.AutoUpdateService");
            if (isRunningService) {
                stopService(intent);
                Toast.makeText(getApplicationContext(), "自动更新服务关闭！", Toast.LENGTH_SHORT).show();
            }

        }

    }
}
