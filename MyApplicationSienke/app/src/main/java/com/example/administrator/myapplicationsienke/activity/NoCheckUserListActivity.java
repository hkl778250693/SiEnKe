package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.adapter.NoCheckUserAdapter;
import com.example.administrator.myapplicationsienke.mode.MySqliteHelper;
import com.example.administrator.myapplicationsienke.model.UserListviewItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/15.
 */
public class NoCheckUserListActivity extends Activity {
    private ImageView securityNoCheckBack;
    private ListView listView;
    private List<UserListviewItem> noCheckUserItemList = new ArrayList<>();
    private SQLiteDatabase db;  //数据库
    private MySqliteHelper helper; //数据库帮助类
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Cursor cursor;
    private TextView noData;
    private ArrayList<String> stringList = new ArrayList<>();//保存字符串参数
    private int task_total_numb;
    private ArrayList<Integer> integers = new ArrayList<>();//保存选中任务的序号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_choose_nocheck_listview);

        defaultSetting();//初始化设置
        getTaskParams(); //获取任务编号参数
        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < integers.size(); i++) {
                    getUserInfo(stringList.get(i));//读取下载到本地的未检任务数据
                    Log.i("UserListActivity=", "查询的任务编号是：" + stringList.get(i));
                }
                super.run();
            }
        }.start();
        bindView();//绑定控件
        setViewClickListener();//点击事件
    }

    //绑定控件ID
    private void bindView() {
        securityNoCheckBack = (ImageView) findViewById(R.id.security_nocheck_back);
        listView = (ListView) findViewById(R.id.listview);
        noData = (TextView) findViewById(R.id.no_data);
    }

    //初始化设置
    private void defaultSetting() {
        helper = new MySqliteHelper(NoCheckUserListActivity.this, 1);
        db = helper.getReadableDatabase();
        sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    //点击事件
    private void setViewClickListener(){
        securityNoCheckBack.setOnClickListener(onClickListener);
        NoCheckUserAdapter noCheckUserAdapter = new NoCheckUserAdapter(NoCheckUserListActivity.this, noCheckUserItemList);
        listView.setAdapter(noCheckUserAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NoCheckUserListActivity.this,UserDetailInfoActivity.class);
                startActivity(intent);
            }
        });
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.security_nocheck_back:
                    NoCheckUserListActivity.this.finish();
                    break;
            }
        }
    };

    //获取任务编号参数
    public void getTaskParams() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                task_total_numb = bundle.getInt("task_total_numb", 0);
                Log.i("UserListActivity=", "task_total_numb=" + task_total_numb);
                integers = bundle.getIntegerArrayList("integerList");
                Log.i("UserListActivity=", "integers：" + integers.size());
                stringList = bundle.getStringArrayList("taskId");
                for (int i = 0; i < stringList.size(); i++) {
                    Log.i("UserListActivitygetS=", "得到的参数为：" + stringList);
                }
            }
        }
    }

    //读取下载到本地的任务数据
    public void getUserInfo(String taskId) {
        Cursor cursor = db.rawQuery("select * from User where taskId=?", new String[]{taskId});
        //如果游标为空，则显示没有数据图片
        if (cursor.getCount() == 0) {
            if (noData.getVisibility() == View.GONE) {
                noData.setVisibility(View.VISIBLE);
            }
            return;
        }
        if (noData.getVisibility() == View.VISIBLE) {
            noData.setVisibility(View.GONE);
        }
        while (cursor.moveToNext()) {
            if(cursor.getString(10).equals("false")){
                UserListviewItem item = new UserListviewItem();
                item.setSecurityNumber(cursor.getString(1));
                item.setUserName(cursor.getString(2));
                item.setNumber(cursor.getString(3));
                item.setPhoneNumber(cursor.getString(4));
                item.setSecurityType(cursor.getString(5));
                item.setUserId(cursor.getString(6));
                item.setAdress(cursor.getString(8));
                Log.i("NoCheckUserListActivity", "安检状态为false");
                item.setIfEdit(R.mipmap.userlist_red);
                noCheckUserItemList.add(item);
            }
        }
        //cursor游标操作完成以后,一定要关闭
        cursor.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //释放和数据库的连接
        db.close();
    }
}
