package com.dulikaifa.zhitianweather;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dulikaifa.zhitianweather.http.JsonRequestCallback;
import com.dulikaifa.zhitianweather.http.OkHttpUtil;
import com.dulikaifa.zhitianweather.http.Url;
import com.dulikaifa.zhitianweather.util.JsonParser;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.sunflower.FlowerCollector;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Author:李晓峰 on 2017/4/24 23:51
 * E-mail:chaate@163.com
 * Copyright(c)2017,All rights reserved.
 * Usage :
 */
public class SearchActivity extends BaseActivity {

    private static String TAG = SearchActivity.class.getSimpleName();

    // 语音听写对象
    private SpeechRecognizer mIat;
    // 语音听写UI
    private RecognizerDialog mIatDialog;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<>();

    // 引擎类型
    private String mEngineType;
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
    @InjectView(R.id.iv_search_clear)
    ImageView clear;
    @InjectView(R.id.voice2_img)
    CircleImageView voice2Img;
    @InjectView(R.id.tv_voice)
    TextView tvVoice;
    private String searchWeatherId;
    private String searchCityName;
    private String searchProvinceName;
    private String searchCountryName;
    private SweetAlertDialog pDialog;
    private String cityName;


    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        // 开放统计 移动数据统计分析
        FlowerCollector.onResume(this);
        FlowerCollector.onPageStart(TAG);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        // 开放统计 移动数据统计分析
        FlowerCollector.onPageEnd(TAG);
        FlowerCollector.onPause(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initView() {
        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        //mIat = SpeechRecognizer.createRecognizer(this, mInitListener);
        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
        mIatDialog = new RecognizerDialog(SearchActivity.this, mInitListener);
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Toast.makeText(SearchActivity.this, "初始化失败，错误码：" + code, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void initListener() {
        editSearch.addTextChangedListener(textWatcher);

    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String keyword = s.toString();
            if (TextUtils.isEmpty(keyword)) {
                clear.setVisibility(View.GONE);
            } else {
                clear.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    protected void initData() {

    }

//    private void setParams() {
//        mEngineType=SpeechConstant.TYPE_CLOUD;
//        // 清空参数
//        //mIat.setParameter(SpeechConstant.PARAMS, null);
//
//        // 设置听写引擎
//        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
//        // 设置返回结果格式
//        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
//
//        //2.设置听写参数，详见《MSC Reference Manual》SpeechConstant类
//        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
//        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
//        mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");
////3.开始听写
//        mIat.startListening(mRecognizerListener);
//
//        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
//        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");
//
//        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
//        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");
//
//        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
//        mIat.setParameter(SpeechConstant.ASR_PTT,"0");
//        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
//        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
//        mIat.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
//        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/iat.wav");
//
//    }
//    /**
//     * 听写监听器。
//     */
//    private RecognizerListener mRecognizerListener = new RecognizerListener() {
//
//        @Override
//        public void onBeginOfSpeech() {
//            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
//            Toast.makeText(SearchActivity.this, "开始说话", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onError(SpeechError error) {
//            // Tips：
//            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
//            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
//
//            Toast.makeText(SearchActivity.this, error.getPlainDescription(true), Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onEndOfSpeech() {
//            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
//
//            Toast.makeText(SearchActivity.this, "结束说话", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onResult(RecognizerResult results, boolean isLast) {
//            Log.d(TAG, results.getResultString());
//            printResult(results);
//
//            if (isLast && cityName != null) {
//                searchCityWeacherId(getUrl(cityName));
//            }
//        }

//        @Override
//        public void onVolumeChanged(int volume, byte[] data) {
//            Toast.makeText(SearchActivity.this, "当前正在说话，音量大小：" + volume, Toast.LENGTH_SHORT).show();
//
//        }
//
//        @Override
//        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
//            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
//            // 若使用本地能力，会话id为null
//            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
//            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
//            //		Log.d(TAG, "session id =" + sid);
//            //	}
//        }
//    };

    @OnClick({R.id.btn_back2, R.id.btn_search, R.id.ll_search, R.id.iv_search_clear, R.id.voice2_img})
    public void onClick(View view) {
        String cityName = editSearch.getText().toString().trim();
        switch (view.getId()) {
            case R.id.btn_search:
                if (TextUtils.isEmpty(cityName)) {
                    Toast.makeText(SearchActivity.this, "城市名字不能为空，请重新输入", Toast.LENGTH_SHORT).show();

                } else {
                    String searchCityUrl = getUrl(cityName);
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
                    finish();
                }
                break;
            case R.id.iv_search_clear:
                editSearch.getText().clear();
                clear.setVisibility(View.GONE);
                break;
            case R.id.btn_back2:
                finish();
                break;
            case R.id.voice2_img:
                if (ContextCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SearchActivity.this,
                            new String[]{Manifest.permission.RECORD_AUDIO}, 1);

                } else {
                    startListenToWrite();
                }
                break;
            default:
                break;
        }

    }

    private void startListenToWrite() {
        // 移动数据分析，收集开始听写事件
        FlowerCollector.onEvent(this, "iat_recognize");
        mIatResults.clear();
        //setParams();
        // 显示听写对话框
        mIatDialog.setListener(mRecognizerDialogListener);
        mIatDialog.show();
        Toast.makeText(this, "请开始说话...", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 移动数据分析，收集开始听写事件
                    startListenToWrite();
                } else {
                    Toast.makeText(this, "你拒绝授予录音权限,将不能使用语音搜索功能", Toast.LENGTH_SHORT).show();
                    setContentView(R.layout.activity_main);
                }
                break;
        }
    }

    /**
     *
     * @param cityName
     * @return
     */
    @NonNull
    private String getUrl(final String cityName) {
        return Url.SEARCH_CITY_URL + "?city=" + cityName + "&key=" + Url.APP_KEY;
    }

    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results);
            if (isLast && cityName != null) {
                searchCityWeacherId(getUrl(cityName));
            }
        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            String plainDescription = error.getPlainDescription(true);

            Toast.makeText(SearchActivity.this, plainDescription, Toast.LENGTH_LONG).show();
        }

    };

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        String result = resultBuffer.toString();
        cityName = result.substring(0, result.length() - 1);

//        tvVoice.setText(result);
        //searchCityWeacherId(result);
    }


    private void searchCityWeacherId(String searchCityUrl) {
        pDialog = new SweetAlertDialog(SearchActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(R.color.widget);
        pDialog.setTitleText("搜索中...");
        pDialog.setCancelable(true);
        pDialog.show();
        OkHttpUtil.getInstance().getAsync(searchCityUrl, new JsonRequestCallback() {
            @Override
            public void onRequestSucess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray heWeather5 = jsonObject.getJSONArray("HeWeather5");

                    if ("ok".equals(heWeather5.getJSONObject(0).getString("status"))) {
                        searchCityName = heWeather5.getJSONObject(0).getJSONObject("basic").getString("city");
                        searchProvinceName = heWeather5.getJSONObject(0).getJSONObject("basic").getString("prov");
                        searchCountryName = heWeather5.getJSONObject(0).getJSONObject("basic").getString("cnty");
                        tvSearch.setVisibility(View.VISIBLE);
                        tvSearch.setText(searchCityName + "-" + searchProvinceName + "-" + searchCountryName);
                        pDialog.dismiss();
                    } else {
                        pDialog.dismiss();
                        Toast.makeText(SearchActivity.this, "搜索的城市不存在" + "\n" + "或者不在服务范围", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    pDialog.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onRequestFailure(String result) {
                pDialog.dismiss();
                Toast.makeText(SearchActivity.this, "获取城市信息失败,请检查网络！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mIat) {
            // 退出时释放连接
            mIat.cancel();
            mIat.destroy();
            pDialog = null;
        }
    }
}
