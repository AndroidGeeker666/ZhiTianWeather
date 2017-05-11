package com.dulikaifa.zhitianweather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TimePicker;

import java.util.Calendar;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * Created by hasee on 2017/5/11.
 */

public class AlarmActivity extends BaseActivity {
    @InjectView(R.id.lv_alarm)
    ListView lvAlarm;
    @InjectView(R.id.btn_add_alarm)
    Button btnAddAlarm;
    private static final String KEY_ALARM_LIST = "alarmList";
    private ArrayAdapter<AlarmData> adapter;
    private AlarmManager alarmManager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_alarm;
    }

    @Override
    protected void initView() {

        //闹钟服务
        alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        adapter = new ArrayAdapter<AlarmActivity.AlarmData>(AlarmActivity.this, android.R.layout.simple_list_item_1);
        lvAlarm.setAdapter(adapter);
        //读取设置好的闹钟时间

        readSavedAlarmList();
    }

    @Override
    protected void initListener() {

        //item项点击事件
        lvAlarm.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {
                new SweetAlertDialog(AlarmActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("确定删除?")
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
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                deleteAlarm(position);
                            }
                        })
                        .show();

                return true;
            }
        });
    }

    //删除闹钟

    private void deleteAlarm(int position) {
        AlarmData ad = adapter.getItem(position);
        adapter.remove(ad);
        saveAlarmList();
        alarmManager.cancel(PendingIntent.getBroadcast(AlarmActivity.this, ad.getId(), new Intent("com.dulikaifa.zhitianweather"), 0));
    }


    private void addAlarm() {
        //TODO
        //弹出时间的选择框  TimerPickerDialog
        Calendar c = Calendar.getInstance();

        new TimePickerDialog(AlarmActivity.this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                //设置时间  calendar就是我们要设定的闹钟
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);


                //逻辑判断----设置的时间小于当前的时间，翻一天
                Calendar currentTime = Calendar.getInstance();

                if (calendar.getTimeInMillis() <= currentTime.getTimeInMillis()) {
                    //转化成毫秒
                    calendar.setTimeInMillis(calendar.getTimeInMillis() + 24 * 60 * 60 * 1000);
                }

                //添加到adapter中,测试中添加了两项，复制下面语句一次，list中添加了4项
                //不用华为手机测试，换个手机就OK了！！！！
                AlarmData ad = new AlarmData(calendar.getTimeInMillis());
                adapter.add(ad);
                //每添加一个闹钟，就设置一个系统闹钟
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, ad.getTime(), 24 * 60 * 60 * 1000,
                        //PendingIntent是一个挂起的intent，在以后某时刻启动
                        PendingIntent.getBroadcast(AlarmActivity.this, ad.getId(), new Intent(new Intent("com.dulikaifa.zhitianweather")), 0));
                //保存闹钟设置的时间
                saveAlarmList();

            }
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }

    //保存设置的闹钟数据
    private void saveAlarmList() {
        SharedPreferences.Editor editor = AlarmActivity.this.getSharedPreferences(AlarmActivity.class.getName(), Context.MODE_PRIVATE).edit();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < adapter.getCount(); i++) {
            sb.append(adapter.getItem(i).getTime()).append(",");
        }

        if (sb.length() > 1) {
            String content = sb.toString().substring(0, sb.length() - 1);

            editor.putString(KEY_ALARM_LIST, content);

            System.out.println(content);
        } else {
            editor.putString(KEY_ALARM_LIST, null);
        }

        editor.apply();
    }

    //读取已经存储的闹钟的时间
    private void readSavedAlarmList() {
        SharedPreferences sp = AlarmActivity.this.getSharedPreferences(AlarmActivity.class.getName(), Context.MODE_PRIVATE);
        String content = sp.getString(KEY_ALARM_LIST, null);

        if (content != null) {
            String[] timeStrings = content.split(",");
            for (String string : timeStrings) {
                adapter.add(new AlarmData(Long.parseLong(string)));
            }
        }
    }

    private static class AlarmData {
        private String timeLabel = "";//ListView中的item项
        private long time = 0;
        private Calendar date;

        public AlarmData(long time) {
            this.time = time;

            //使用time来创建date时间
            date = Calendar.getInstance();
            date.setTimeInMillis(time);


            //再使用date来创建时间
            timeLabel = String.format("%d月%d日 %d:%d",
                    date.get(Calendar.MONTH) + 1,
                    //month返回值从0开始
                    date.get(Calendar.DAY_OF_MONTH),
                    date.get(Calendar.HOUR_OF_DAY),
                    date.get(Calendar.MINUTE));
        }

        public long getTime() {
            return time;
        }

        public String getTimeLabel() {
            return timeLabel;
        }

        //返回时间的String
        @Override
        public String toString() {
            return getTimeLabel();
        }

        public int getId() {
            return (int) (getTime() / 1000 / 60);
        }


    }


    @Override
    protected void initData() {

    }

    @OnClick(R.id.btn_add_alarm)
    public void onViewClicked() {
        addAlarm();

    }
}
