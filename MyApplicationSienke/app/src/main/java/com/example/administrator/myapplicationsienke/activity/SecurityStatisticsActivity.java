package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.myapplicationsienke.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by Administrator on 2017/3/16.
 */
public class SecurityStatisticsActivity extends Activity {
    private ImageView securityStatisticsBack;
    private TextView checkedNumber, totalNumber, notCheckedNumber, finishRate, problemCheckedNumber;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Button userStatisticsBtn,taskStatisticsBtn;
    private int notChecked = 0;  //未安检的户数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_statistics);

        bindView();//绑定控件
        defaultSetting();//初始化设置
        setViewClickListener();//点击事件
    }

    //绑定控件
    private void bindView() {
        securityStatisticsBack = (ImageView) findViewById(R.id.security_statistics_back);
        checkedNumber = (TextView) findViewById(R.id.checked_number);
        totalNumber = (TextView) findViewById(R.id.total_number);
        notCheckedNumber = (TextView) findViewById(R.id.not_checked_number);
        finishRate = (TextView) findViewById(R.id.finish_rate);
        problemCheckedNumber = (TextView) findViewById(R.id.problem_checked_number);
        userStatisticsBtn= (Button) findViewById(R.id.user_statistics_btn);
        taskStatisticsBtn= (Button) findViewById(R.id.task_statistics_btn);
    }

    //初始化设置
    private void defaultSetting() {
        sharedPreferences = SecurityStatisticsActivity.this.getSharedPreferences("data", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        getTotalUserNumber();
    }

    //点击事件
    private void setViewClickListener() {
        securityStatisticsBack.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.security_statistics_back:
                    SecurityStatisticsActivity.this.finish();
                    break;
                case R.id.user_statistics_btn:
                    getTotalUserNumber();
                    break;
                case R.id.task_statistics_btn:
                    getTaskUserNumber();
                    break;
            }
        }
    };

    //总用户统计
    public void getTotalUserNumber (){
        if (sharedPreferences.getInt("totalCount", 0) != 0) {
            totalNumber.setText(String.valueOf(sharedPreferences.getInt("totalCount", 0)));
        } else {
            totalNumber.setText("0");
        }
        if (sharedPreferences.getInt("checkedNumber", 0) != 0) {
            checkedNumber.setText(String.valueOf(sharedPreferences.getInt("checkedNumber", 0)));
        } else {
            checkedNumber.setText("0");
        }
        if (sharedPreferences.getInt("totalCount", 0) != 0 ) {
            notChecked = sharedPreferences.getInt("totalCount", 0) - sharedPreferences.getInt("checkedNumber", 0);
            notCheckedNumber.setText(String.valueOf(notChecked));
        } else {
            notCheckedNumber.setText("0");
        }
        if (sharedPreferences.getInt("totalCount", 0) != 0) {
            double checkedNumber = (double) sharedPreferences.getInt("checkedNumber", 0)* 100;
            double totalCount = (double) sharedPreferences.getInt("totalCount", 0);
            double finishingRate = checkedNumber/totalCount;  //完成率
            Log.i("StatisticsActivity===>", "完成率=" + finishingRate + "%");
            DecimalFormat df = new DecimalFormat("#.0");
            String result = df.format(finishingRate);
            Log.i("StatisticsActivity===>", "完成率=" + result + "%");
            finishRate.setText(result);
        } else {
            finishRate.setText("0.0");
        }
        if (sharedPreferences.getInt("problem_number", 0) != 0) {
            Log.i("StatisticsActivity===>", "存在问题的户数=" + sharedPreferences.getInt("problem_number", 0) + "户");
            problemCheckedNumber.setText(String.valueOf(sharedPreferences.getInt("problem_number", 0)));
        } else {
            problemCheckedNumber.setText("0");
        }
    }

    //任务分区统计
    public void getTaskUserNumber (){
        if (sharedPreferences.getInt("taskTotalUserNumber", 0) != 0) {
            totalNumber.setText(String.valueOf(sharedPreferences.getInt("taskTotalUserNumber", 0)));
        } else {
            totalNumber.setText("0");
        }
        if (sharedPreferences.getInt("checkedNumber", 0) != 0) {
            checkedNumber.setText(String.valueOf(sharedPreferences.getInt("checkedNumber", 0)));
        } else {
            checkedNumber.setText("0");
        }
        if (sharedPreferences.getInt("taskTotalUserNumber", 0) != 0 ) {
            notChecked = sharedPreferences.getInt("taskTotalUserNumber", 0) - sharedPreferences.getInt("checkedNumber", 0);
            notCheckedNumber.setText(String.valueOf(notChecked));
        } else {
            notCheckedNumber.setText("0");
        }
        if (sharedPreferences.getInt("taskTotalUserNumber", 0) != 0) {
            double checkedNumber = (double) sharedPreferences.getInt("checkedNumber", 0)* 100;
            double totalCount = (double) sharedPreferences.getInt("taskTotalUserNumber", 0);
            double finishingRate = checkedNumber/totalCount;  //完成率
            Log.i("StatisticsActivity===>", "完成率=" + finishingRate + "%");
            DecimalFormat df = new DecimalFormat("#.0");
            String result = df.format(finishingRate);
            Log.i("StatisticsActivity===>", "完成率=" + result + "%");
            finishRate.setText(result);
        } else {
            finishRate.setText("0.0");
        }
        if (sharedPreferences.getInt("problem_number", 0) != 0) {
            Log.i("StatisticsActivity===>", "存在问题的户数=" + sharedPreferences.getInt("problem_number", 0) + "户");
            problemCheckedNumber.setText(String.valueOf(sharedPreferences.getInt("problem_number", 0)));
        } else {
            problemCheckedNumber.setText("0");
        }
    }
}
