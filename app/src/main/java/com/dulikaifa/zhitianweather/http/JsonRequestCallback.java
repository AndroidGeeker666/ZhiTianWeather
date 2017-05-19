package com.dulikaifa.zhitianweather.http;
/**
 * 网络请求结果的回调接口
 * @author lixf21
 *
 */
@SuppressWarnings("ALL")
public interface JsonRequestCallback {
	void onRequestSucess(String result);
	void onRequestFailure(String result);
}
