package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.mode.MySqliteHelper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Administrator on 2017/3/16.
 */
public class SecurityStatisticsActivity extends Activity {
    private ImageView securityStatisticsBack;
    private TextView checkedNumber, totalNumber, notCheckedNumber, finishRate, problemCheckedNumber;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private RadioButton userStatisticsBtn,taskStatisticsBtn;
    private int notChecked = 0;  //未安检的户数
    private SQLiteDatabase db;  //数据库
    private MySqliteHelper helper; //数据库帮助类
    private int task_total_numb = 0;
    private ArrayList<String> stringList = new ArrayList<>();//保存字符串参数
    private int checkedUserNumber = 0;  //已检户数

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
        userStatisticsBtn= (RadioButton) findViewById(R.id.user_statistics_btn);
        taskStatisticsBtn= (RadioButton) findViewById(R.id.task_statistics_btn);
    }

    //初始化设置
    private void defaultSetting() {
        helper = new MySqliteHelper(SecurityStatisticsActivity.this, 1);
        db = helper.getWritableDatabase();
        sharedPreferences = SecurityStatisticsActivity.this.getSharedPreferences("data", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userStatisticsBtn.setChecked(true);
        getTaskParams();
        for (int i = 0; i < task_total_numb; i++) {
            getCheckedNumber(stringList.get(i)); //获取已检用户户数
        }
        getTotalUserNumber();
    }

    //点击事件
    private void setViewClickListener() {
        securityStatisticsBack.setOnClickListener(onClickListener);
        userStatisticsBtn.setOnClickListener(onClickListener);
        taskStatisticsBtn.setOnClickListener(onClickListener);
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

    //获取任务编号参数
    public void getTaskParams() {
        if (sharedPreferences.getStringSet("stringSet", null) != null && sharedPreferences.getInt("task_total_numb", 0) != 0) {
            Iterator iterator = sharedPreferences.getStringSet("stringSet", null).iterator();
            while (iterator.hasNext()) {
                stringList.add(iterator.next().toString());
            }
            task_total_numb = sharedPreferences.getInt("task_total_numb", 0);
        }
    }

    public void getCheckedNumber(String taskId){
        Cursor cursor = db.rawQuery("select * from User where taskId=?", new String[]{taskId});//查询并获得游标
        //在页面finish之前，从上到下查询本地数据库没有安检的用户，相对应的item位置，查询到一个就break
        if (cursor.getCount() == 0) {
            return;
        }
        while (cursor.moveToNext()) {
            if (cursor.getString(10).equals("true")) {
                checkedUserNumber++;
                Log.i("getCheckedNumber===>", "已安检" + checkedUserNumber + "户");
            }
        }
        cursor.close(); //游标关闭
    }

    //总用户统计
    public void getTotalUserNumber (){
        if (sharedPreferences.getInt("totalCount", 0) != 0) {
            totalNumber.setText(String.valueOf(sharedPreferences.getInt("totalCount", 0)));
        } else {
            totalNumber.setText("0");
        }
        if (checkedUserNumber != 0) {
            checkedNumber.setText(String.valueOf(checkedUserNumber));
        } else {
            checkedNumber.setText("0");
        }
        if (sharedPreferences.getInt("totalCount", 0) != 0 ) {
            notChecked = sharedPreferences.getInt("totalCount", 0) - checkedUserNumber;
            notCheckedNumber.setText(String.valueOf(notChecked));
        } else {
            notCheckedNumber.setText("0");
        }
        if (sharedPreferences.getInt("totalCount", 0) != 0) {
            double checkedNumber = (double) checkedUserNumber* 100;
            double totalCount = (double) sharedPreferences.getInt("totalCount", 0);
            double finishingRate = checkedNumber/totalCount;  //完成率
            Log.i("getTotalUserNumber===>", "完成率=" + finishingRate + "%");
            DecimalFormat df = new DecimalFormat("0.0");
            String result = df.format(finishingRate);
            Log.i("getTotalUserNumber===>", "完成率=" + result + "%");
            finishRate.setText(result);
        } else {
            finishRate.setText("0.0");
        }
        if (sharedPreferences.getInt("problem_number", 0) != 0) {
            Log.i("getTotalUserNumber===>", "存在问题的户数=" + sharedPreferences.getInt("problem_number", 0) + "户");
            problemCheckedNumber.setText(String.valueOf(sharedPreferences.getInt("problem_number", 0)));
        } else {
            problemCheckedNumber.setText("0");
        }
    }

    //任务分区统计
    public void getTaskUserNumber (){
        if (sharedPreferences.getInt("taskTotalUserNumber", 0) != 0) {
            Log.i("getTaskUserNumber===>", "任务总户数=" + sharedPreferences.getInt("taskTotalUserNumber", 0) + "户");
            totalNumber.setText(String.valueOf(sharedPreferences.getInt("taskTotalUserNumber", 0)));
        } else {
            totalNumber.setText("0");
        }
        if (checkedUserNumber != 0) {
            checkedNumber.setText(String.valueOf(checkedUserNumber));
        } else {
            checkedNumber.setText("0");
        }
        if (sharedPreferences.getInt("taskTotalUserNumber", 0) != 0 ) {
            notChecked = sharedPreferences.getInt("taskTotalUserNumber", 0) - checkedUserNumber;
            notCheckedNumber.setText(String.valueOf(notChecked));
        } else {
            notCheckedNumber.setText("0");
        }
        if (sharedPreferences.getInt("taskTotalUserNumber", 0) != 0) {
            double checkedNumber = (double) checkedUserNumber* 100;
            double totalCount = (double) sharedPreferences.getInt("taskTotalUserNumber", 0);
            double finishingRate = checkedNumber/totalCount;  //完成率
            Log.i("getTaskUserNumber===>", "完成率=" + finishingRate + "%");
            DecimalFormat df = new DecimalFormat("0.0");
            String result = df.format(finishingRate);
            Log.i("getTaskUserNumber===>", "完成率=" + result + "%");
            finishRate.setText(result);
        } else {
            finishRate.setText("0.0");
        }
        if (sharedPreferences.getInt("problem_number", 0) != 0) {
            Log.i("getTaskUserNumber===>", "存在问题的户数=" + sharedPreferences.getInt("problem_number", 0) + "户");
            problemCheckedNumber.setText(String.valueOf(sharedPreferences.getInt("problem_number", 0)));
        } else {
            problemCheckedNumber.setText("0");
        }
    }
}
