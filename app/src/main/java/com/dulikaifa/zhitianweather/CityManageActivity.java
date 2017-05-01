package com.dulikaifa.zhitianweather;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dulikaifa.zhitianweather.bean.CityBean;
import com.dulikaifa.zhitianweather.db.CityNameDbDao;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
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
    @InjectView(R.id.rv_list)
    ListView rvList;

    private List<String> mDatas = null;
    private CityManageAdapter adapter;
    private CityNameDbDao dao;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_city_manage;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {
        rvList.setOnItemClickListener(itemListener);
        rvList.setOnItemLongClickListener(itemLongListener);
    }

    @Override
    protected void initData() {
        mDatas = new ArrayList<>();
        dao = new CityNameDbDao(this);
        List<CityBean> all = dao.findAll();

        for (CityBean bean : all) {
            if (bean.countryName != null && bean.cityName != null) {
                String cityAndCountryName = bean.countryName + "-" + bean.cityName;

                if (!mDatas.contains(cityAndCountryName)) {
                    mDatas.add(cityAndCountryName);
                }
            }
        }
        adapter = new CityManageAdapter();
        rvList.setAdapter(adapter);
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
                    String searchCityName = data.getStringExtra("searchCityName");
                    String searchCountryName = data.getStringExtra("searchCountryName");
                    String saveCity = searchCountryName + "-" + searchCityName;
                    if (!mDatas.contains(saveCity)) {
                        mDatas.add(saveCity);
                        dao.add(searchCityName, searchCountryName);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(CityManageActivity.this, "你已经添加该城市，无需重复添加", Toast.LENGTH_SHORT).show();
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
                    .setTitleText("你要删除 " + mDatas.get(position) + " 吗?")
                    .setCancelText("不，谢谢！")
                    .setConfirmText("对，就删它！")
                    .showCancelButton(true)
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.cancel();
                        }
                    })
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            adapter.remove(position);
                            sweetAlertDialog
                                    .setTitleText("删除成功!")
                                    .setContentText(mDatas.get(position)+"已删除!")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(null)
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        }
                    })
                    .show();

            return true;
        }
    };
    private AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int positon, long id) {

            String name = mDatas.get(positon);
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
        }
    }

    public class CityManageAdapter extends BaseAdapter {

        public CityManageAdapter() {
        }

        @Override
        public int getCount() {
            return mDatas != null ? mDatas.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(parent.getContext(), R.layout.city_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            String name = mDatas.get(position);
            holder.tvName.setText(name);
            return convertView;
        }

        /**
         * 刷新数据,清除原有数据，放入全新数据
         *
         * @param list
         */
        public void refresh(List<String> list) {
            mDatas.clear();
            mDatas.addAll(list);
            notifyDataSetChanged();
        }

        /**
         * 删除指定位置条目及数据
         *
         * @param i
         */
        public void remove(int i) {
            if (null != mDatas && mDatas.size() > i && i > -1) {
                String name = mDatas.get(i);
                String[] names = name.split("-");
                dao.delete(names[1]);
                mDatas.remove(i);
                notifyDataSetChanged();
            }
        }

        /**
         * 加载更多数据，在原有数据基础上加入更多数据
         *
         * @param list
         */
        public void addDatas(List<String> list) {
            if (null != list) {
                List<String> temp = new ArrayList<>();
                temp.addAll(list);
                if (mDatas != null) {
                    mDatas.addAll(temp);
                } else {
                    mDatas = temp;
                }
                notifyDataSetChanged();
            }
        }

        class ViewHolder {
            @InjectView(R.id.cb_item_check)
            CheckBox cbItemCheck;
            @InjectView(R.id.tv_name)
            TextView tvName;
            @InjectView(R.id.iv_touch)
            ImageView ivTouch;

            ViewHolder(View view) {
                ButterKnife.inject(this, view);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}