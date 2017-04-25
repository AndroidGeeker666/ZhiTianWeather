package com.dulikaifa.zhitianweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dulikaifa.zhitianweather.http.JsonRequestCallback;
import com.dulikaifa.zhitianweather.http.OkHttpUtil;
import com.dulikaifa.zhitianweather.http.Url;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Author:李晓峰 on 2017/4/24 23:51
 * E-mail:chaate@163.com
 * Copyright(c)2017,All rights reserved.
 * Usage :
 */
public class SearchActivity extends AppCompatActivity {
    @InjectView(R.id.edit_search)
    EditText editSearch;
    @InjectView(R.id.btn_search)
    Button btnSearch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.inject(this);

    }

    @OnClick(R.id.btn_search)
    public void onClick() {
        String cityName = editSearch.getText().toString().trim();
        String searchCityUrl = Url.SEARCH_CITY_URL + "?city=" + cityName + "&key=" + Url.APP_KEY;
        searchCityWeacherId(searchCityUrl);

    }

    private void searchCityWeacherId(String searchCityUrl) {
        OkHttpUtil.getInstance().getAsync(searchCityUrl, new JsonRequestCallback() {
            @Override
            public void onRequestSucess(String result) {

                try {
                    JSONObject jsonObject= new JSONObject(result);
                    JSONArray heWeather5 = jsonObject.getJSONArray("HeWeather5");

                    if("ok".equals(heWeather5.getJSONObject(0).getString("status"))){
                        String searchWeatherId = heWeather5.getJSONObject(0).getString("id");
                        Intent intent = new Intent(SearchActivity.this, WeatherActivity.class);
                        intent.putExtra("searchWeatherId", searchWeatherId);
                        SearchActivity.this.startActivity(intent);
                        SearchActivity.this.finish();

                    }else{
                        Toast.makeText(SearchActivity.this, "搜索的城市不存在" + "\n" + "或者不在服务范围", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onRequestFailure(String result) {
                Toast.makeText(SearchActivity.this, "获取城市信息失败,失败原因是：" + result, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
