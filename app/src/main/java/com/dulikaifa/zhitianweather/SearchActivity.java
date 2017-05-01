package com.dulikaifa.zhitianweather;

import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dulikaifa.zhitianweather.http.JsonRequestCallback;
import com.dulikaifa.zhitianweather.http.OkHttpUtil;
import com.dulikaifa.zhitianweather.http.Url;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Author:李晓峰 on 2017/4/24 23:51
 * E-mail:chaate@163.com
 * Copyright(c)2017,All rights reserved.
 * Usage :
 */
public class SearchActivity extends BaseActivity {
    private static final int RESULT_CODE = 4;
    @InjectView(R.id.edit_search)
    EditText editSearch;
    @InjectView(R.id.btn_search)
    Button btnSearch;
    @InjectView(R.id.tv_search)
    TextView tvSearch;
    @InjectView(R.id.ll_search)
    LinearLayout llSearch;
    @InjectView(R.id.btn_back2)
    Button btnBack2;

    private String searchWeatherId;
    private String searchCityName;
    private String searchProvinceName;
    private String searchCountryName;
    private SweetAlertDialog pDialog;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
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
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    @OnClick({R.id.btn_back2,R.id.btn_search, R.id.ll_search})
    public void onClick(View view) {
        String cityName = editSearch.getText().toString().trim();
        switch (view.getId()) {
            case R.id.btn_search:
                if (TextUtils.isEmpty(cityName)) {
                    Toast.makeText(SearchActivity.this, "城市名字不能为空，请重新输入", Toast.LENGTH_SHORT).show();

                } else {
                    String searchCityUrl = Url.SEARCH_CITY_URL + "?city=" + cityName + "&key=" + Url.APP_KEY;
                    searchCityWeacherId(searchCityUrl);
                }
                break;
            case R.id.ll_search:
                Intent intent = new Intent();
                if (searchCountryName != null && searchCityName != null) {
                    intent.putExtra("searchCountryName", searchCountryName);
                    intent.putExtra("searchProvinceName", searchProvinceName);
                    intent.putExtra("searchCityName", searchCityName);
                    SearchActivity.this.setResult(RESULT_CODE, intent);
                    SearchActivity.this.finish();
                }

                break;
            case R.id.btn_back2:
                finish();
                break;
            default:

                break;
        }

    }

    private void searchCityWeacherId(String searchCityUrl) {
        pDialog = new SweetAlertDialog(SearchActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("加载中...");
        pDialog.setCancelable(false);
        pDialog.show();
        OkHttpUtil.getInstance().getAsync(searchCityUrl, new JsonRequestCallback() {
            @Override
            public void onRequestSucess(String result) {
                pDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray heWeather5 = jsonObject.getJSONArray("HeWeather5");

                    if ("ok".equals(heWeather5.getJSONObject(0).getString("status"))) {
                        searchCityName = heWeather5.getJSONObject(0).getJSONObject("basic").getString("city");
                        searchProvinceName = heWeather5.getJSONObject(0).getJSONObject("basic").getString("prov");
                        searchCountryName = heWeather5.getJSONObject(0).getJSONObject("basic").getString("cnty");
                        tvSearch.setVisibility(View.VISIBLE);
                        tvSearch.setText(searchCityName + "-" + searchProvinceName + "-" + searchCountryName);
                    } else {
                        Toast.makeText(SearchActivity.this, "搜索的城市不存在" + "\n" + "或者不在服务范围", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onRequestFailure(String result) {
                pDialog.dismiss();
                Toast.makeText(SearchActivity.this, "获取城市信息失败,失败原因是：" + result, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
