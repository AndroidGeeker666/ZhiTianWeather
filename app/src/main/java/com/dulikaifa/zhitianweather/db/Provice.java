package com.dulikaifa.zhitianweather.db;

import org.litepal.crud.DataSupport;

/**
 * Author:李晓峰 on 2017/4/22 14:12
 * E-mail:chaate@163.com
 * Copyright(c)2017,All rights reserved.
 * Usage :
 */

public class Provice extends DataSupport {

    private int id;

    private String provinceName;

    private int provinceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
