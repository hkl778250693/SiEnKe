package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.adapter.GridviewTypeAdapter;
import com.example.administrator.myapplicationsienke.mode.MySqliteHelper;
import com.example.administrator.myapplicationsienke.model.GridviewTypeItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2017/5/10.
 */
public class DownLoadDetailActivity extends Activity {
    private TextView startDate;//开始日期选择器
    private TextView endDate;//结束日期选择器
    private Calendar c; //日历
    private RadioButton cancelRb, saveRb;
    private GridviewTypeAdapter adapter;
    private List<GridviewTypeItem> gridviewTypeItemList = new ArrayList<>();
    private SQLiteDatabase db;  //数据库
    private Cursor cursor;
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popupwindow_download_detail);

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
        gridView = (GridView) findViewById(R.id.gridview);
    }

    //初始化设置
    private void defaultSetting(){
        MySqliteHelper helper = new MySqliteHelper(DownLoadDetailActivity.this, 1);
        db = helper.getWritableDatabase();
        c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        startDate.setText(year + "-" + (month + 1) + "-" + day);
        endDate.setText(year + "-" + (month + 1) + "-" + day);
        new Thread(){
            @Override
            public void run() {
                getSecurityState();
                if (cursor.getCount() != 0) {
                    handler.sendEmptyMessage(0);
                }else {
                    handler.sendEmptyMessage(1);
                }
            }
        }.start();

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
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    adapter = new GridviewTypeAdapter(DownLoadDetailActivity.this,gridviewTypeItemList);
                    gridView.setAdapter(adapter);
                    break;
                case 1:
                    gridviewTypeItemList.clear();
                    GridviewTypeItem item = new GridviewTypeItem();
                    item.setTypeName("无");
                    item.setTypeId("");
                    gridviewTypeItemList.add(item);
                    adapter = new GridviewTypeAdapter(DownLoadDetailActivity.this,gridviewTypeItemList);
                    gridView.setAdapter(adapter);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    //读取安检状态信息
    public void getSecurityState() {
        gridviewTypeItemList.clear();
        cursor = db.query("SecurityState", null, null, null, null, null, null);//查询并获得游标
        Log.i("getSecurityState=>", " 查询到的状态个数为：" + cursor.getCount());
        //如果游标为空，则显示默认数据
        if (cursor.getCount() == 0) {
            return;
        }
        while (cursor.moveToNext()) {
            GridviewTypeItem item = new GridviewTypeItem();
            item.setTypeName(cursor.getString(1));
            item.setTypeId(cursor.getString(2));
            gridviewTypeItemList.add(item);
        }
        Log.i("getSecurityState=>", " 安检状态个数为：" + gridviewTypeItemList.size());
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放和数据库的连接
        cursor.close(); //游标关闭
        db.close();
    }
}
