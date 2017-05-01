package com.dulikaifa.zhitianweather;

import android.widget.Button;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Author:李晓峰 on 2017/4/26 19:09
 * E-mail:chaate@163.com
 * Copyright(c)2017,All rights reserved.
 * Usage :
 */

public class AboutActivity extends BaseActivity {

    @InjectView(R.id.btn_back)
    Button btnBack;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {

    }

    @OnClick(R.id.btn_back)
    public void onClick() {
        finish();
    }
}
