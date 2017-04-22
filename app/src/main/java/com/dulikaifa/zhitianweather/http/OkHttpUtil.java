package com.dulikaifa.zhitianweather.http;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class OkHttpUtil {
    private final String TAG = OkHttpUtil.class.getSimpleName();
    private static OkHttpUtil instance;
    private OkHttpClient client = new OkHttpClient();
    /***
     * 创建handler 用于将数据发往主线程  因为okhttp的请求是在子线程  我们需要将结果发往主线程  子线程是不能更新ui的
     */
    private Handler handler = new Handler(Looper.getMainLooper());

    private OkHttpUtil() {
    }

    public static OkHttpUtil getInstance() {
        if (instance == null) {
            instance = new OkHttpUtil();
        }
        return instance;
    }

    /**
     * 异步get请求
     *
     * @param url          网络地址
     * @param jsonCallback 回调接口的引用
     */
    public void getAsync(String url,final JsonRequestCallback jsonCallback) {

        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "text/plain; charset=utf-8")
                .build();
        getResult(request, jsonCallback);
    }

    /**
     * 执行网络请求，获取请求结果
     *
     * @param jsonCallback 回调接口的引用
     * @param request      网络请求
     */
    private void getResult(Request request, final JsonRequestCallback jsonCallback) {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(new Runnable() {
                    public void run() {
                        jsonCallback.onRequestFailure("网络不给力，请检查网络设置");
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                handler.post(new Runnable() {
                    public void run() {
                        jsonCallback.onRequestSucess(result);
                    }
                });
            }
        });
    }
}