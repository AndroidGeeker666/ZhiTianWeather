package com.dulikaifa.zhitianweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dulikaifa.zhitianweather.service.AutoUpdateService;
import com.dulikaifa.zhitianweather.service.ServiceStateUtils;
import com.umeng.analytics.MobclickAgent;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Author:李晓峰 on 2017/4/26 18:11
 * E-mail:chaate@163.com
 * Copyright(c)2017,All rights reserved.
 * Usage :
 */

public class SettingActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    private static final long UPDATE_TIME = 1500;
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
    @InjectView(R.id.speaker_layout)
    RelativeLayout speakerLayout;
    @InjectView(R.id.speaker)
    TextView speaker;
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

    @OnClick({R.id.btn_back3, R.id.update, R.id.about_layout,R.id.speaker_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back3:
                finish();
                break;
            case R.id.update:
                final SweetAlertDialog sDialog = new SweetAlertDialog(SettingActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                sDialog.getProgressHelper().setBarColor(R.color.widget);
                sDialog.setTitleText("检测中，请稍候...");
                sDialog.setCancelable(false);
                sDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sDialog.dismiss();
                        new SweetAlertDialog(SettingActivity.this)
                                .setTitleText("已是最新版本")
                                .show();
//                        Toast.makeText(getApplicationContext(), "已是最新版本", Toast.LENGTH_SHORT).show();
                    }
                }, UPDATE_TIME);

                break;

            case R.id.about_layout:
                Intent intent = new Intent(SettingActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.speaker_layout:
                new MaterialDialog.Builder(this)
                        .title(R.string.speaker_names)
                        .items(R.array.speakers)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                /**
                                 * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                                 * returning false here won't allow the newly selected radio button to actually be selected.
                                 **/
                                int beginIndex = text.toString().indexOf("x");
                                String speakerName=text.toString().substring(beginIndex);
                                speaker.setText(speakerName);
                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(SettingActivity.this).edit();
                                editor.putString("speakerName", speakerName);
                                editor.apply();
                                return true;
                            }
                        })
                        .positiveText("确定")
                        .show();

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
