package com.dulikaifa.zhitianweather;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.dulikaifa.zhitianweather.adapter.CityAdapter;
import com.dulikaifa.zhitianweather.bean.CityBean;
import com.dulikaifa.zhitianweather.db.CityNameDbDao;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Author:李晓峰 on 2017/4/27 15:06
 * E-mail:chaate@163.com
 * Copyright(c)2017,All rights reserved.
 * Usage :
 */

public class CityManageActivity extends BaseActivity {

    private static final int REQUST_CODE_CITY_ADD = 3;
    private static final int RESULT_CODE = 2;

    @InjectView(R.id.btn_back1)
    Button backButton;
    @InjectView(R.id.city_manage_add)
    Button cityManageAdd;
    @InjectView(R.id.lv_list)
    ListView lvList;
    private CityAdapter mAdapter;
    private List<String> mCityList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_city_manage;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

        lvList.setOnItemClickListener(itemListener);
        lvList.setOnItemLongClickListener(itemLongListener);

    }


    @Override
    protected void initData() {
        mCityList = new ArrayList<>();
        CityNameDbDao dao = new CityNameDbDao(this);
        List<CityBean> mDataList = dao.findAll();
        mAdapter = new CityAdapter(R.layout.city_item, CityManageActivity.this, mDataList, dao);
        lvList.setAdapter(mAdapter);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUST_CODE_CITY_ADD:
                if (resultCode == 4) {
                    mCityList.clear();
                    for (CityBean cityBean : mAdapter.getMdata()) {
                        mCityList.add(cityBean.getCityName());

                    }
                    String searchCityName = data.getStringExtra("searchCityName");
                    String searchCountryName = data.getStringExtra("searchCountryName");
                    CityBean bean = new CityBean();
                    bean.setCityName(searchCityName);
                    bean.setCountryName(searchCountryName);
                    if (!(mCityList.contains(searchCityName))) {
                        mAdapter.add(bean);
                    } else {
                        Toast.makeText(CityManageActivity.this, "你已经添加该城市", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
    }

    private AdapterView.OnItemLongClickListener itemLongListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long id) {

            new SweetAlertDialog(CityManageActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("确定删除 " + mAdapter.getMdata().get(position).getCountryName() + "-" + mAdapter.getMdata().get(position).getCityName() + " 吗?")
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

                            sweetAlertDialog
                                    .setTitleText("删除成功!")
                                    .showCancelButton(false)
                                    .setContentText(mAdapter.getMdata().get(position).getCountryName() + "-" + mAdapter.getMdata().get(position).getCityName() + " 已删除!")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(null)
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            mAdapter.remove(position);
                        }
                    })
                    .show();
            return true;
        }
    };
    private AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int positon, long id) {
            CityBean bean = mAdapter.getMdata().get(positon);
            String name = bean.getCountryName() + "-" + bean.getCityName();
            Intent intent = new Intent();
            intent.putExtra("name", name);
            setResult(RESULT_CODE, intent);
            finish();
        }
    };

    @OnClick({R.id.btn_back1, R.id.city_manage_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back1:
                finish();
                break;
            case R.id.city_manage_add:
                Intent intent = new Intent(CityManageActivity.this, SearchActivity.class);
                startActivityForResult(intent, REQUST_CODE_CITY_ADD);
                break;
            default:
                break;
        }
    }

}