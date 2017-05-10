package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.administrator.myapplicationsienke.R;

import java.util.Calendar;

/**
 * Created by Administrator on 2017/5/10.
 */
public class DownLoadDetailActivity extends Activity {
    private TextView startDate;//开始日期选择器
    private TextView endDate;//结束日期选择器
    private Calendar c; //日历
    private RadioButton cancelRb, saveRb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_detail);

        bindView();//绑定控件
        defaultSetting();//初始化设置
        setViewClickListener();//点击事件
    }

    //绑定控件
    public void bindView(){
        startDate = (TextView) findViewById(R.id.start_date);
        endDate = (TextView) findViewById(R.id.end_date);
        cancelRb = (RadioButton) findViewById(R.id.cancel_rb);
        saveRb = (RadioButton) findViewById(R.id.save_rb);
    }

    //初始化设置
    private void defaultSetting(){
        c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        startDate.setText(year + "-" + (month + 1) + "-" + day);
        endDate.setText(year + "-" + (month + 1) + "-" + day);
    }

    //点击事件
    private void setViewClickListener(){
        startDate.setOnClickListener(onClickListener);
        endDate.setOnClickListener(onClickListener);
        cancelRb.setOnClickListener(onClickListener);
        saveRb.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.start_date:
                    //开始时间选择器
                    new DatePickerDialog(DownLoadDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            startDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        }
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
                    break;
                case R.id.end_date:
                    //结束时间选择器
                    new DatePickerDialog(DownLoadDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            endDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        }
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
                    break;
                case R.id.cancel_rb:
                    break;
                case R.id.save_rb:
                    break;
            }
        }
    };
}
