package com.dulikaifa.zhitianweather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dulikaifa.zhitianweather.R;
import com.dulikaifa.zhitianweather.bean.CityBean;
import com.dulikaifa.zhitianweather.db.CityNameDbDao;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Author:李晓峰 on 2017/5/3 22:35
 * E-mail:chaate@163.com
 * Copyright(c)2017,All rights reserved.
 * Usage :
 */

@SuppressWarnings("ALL")
public class CityAdapter extends BaseAdapter {
    private int mResource;
    private Context mContext;
    private List<CityBean> mData;
    private CityNameDbDao dao;

    //构造方法
    public CityAdapter(int LayoutId, Context mContext, List<CityBean> mData, CityNameDbDao dao) {
        this.mContext = mContext;
        this.mResource = LayoutId;
        this.mData = mData;
        this.dao = dao;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public CityBean getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mResource, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvName.setText(mData.get(position).getCountryName() + "-" + mData.get(position).getCityName());

        return convertView;
    }

    public List<CityBean> getMdata() {

        if (mData != null) {
            return mData;
        }
        return null;
    }

    /**
     * 删除指定位置条目及数据
     *
     * @param i
     */
    public void remove(int i) {
        if (null != mData && mData.size() > 0) {
            CityBean bean = mData.get(i);
            dao.delete(bean);
            mData.remove(i);
            notifyDataSetChanged();
        }
    }

    /**
     * 加载更多数据，在原有数据基础上加入更多数据
     *
     * @param bean
     */
    public void add(CityBean bean) {
        if (null != bean) {
            mData.add(bean);
            dao.add(bean);
            notifyDataSetChanged();
        }
    }

    class ViewHolder {

        @InjectView(R.id.tv_name)
        TextView tvName;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
