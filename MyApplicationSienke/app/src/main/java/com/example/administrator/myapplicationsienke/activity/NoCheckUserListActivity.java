package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.content.ContentValues;
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
    private UserListviewItem item;
    private int currentPosition;  //点击listview  当前item的位置
    private NoCheckUserAdapter noCheckUserAdapter;
    private int checkedNumber = 0;   //已检户数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_choose_nocheck_listview);

        defaultSetting();//初始化设置
        getTaskParams(); //获取任务编号参数
        bindView();//绑定控件
        setViewClickListener();//点击事件
        new Thread() {
            @Override
            public void run() {
                Log.i("NoCheckUserListActivity", "查询本地数据循环进来了！"+integers.size());
                if(integers.size() != 0){
                    if (noData.getVisibility() == View.VISIBLE) {
                        noData.setVisibility(View.GONE);
                    }
                    for (int i = 0; i < integers.size(); i++) {
                        getUserInfo(stringList.get(i));//读取未检任务数据
                        Log.i("NoCheckUserListActivity", "查询的任务编号是：" + stringList.get(i));
                    }
                }else {
                    if (noData.getVisibility() == View.GONE) {
                        Log.i("NoCheckUserListActivity", "显示没有用户数据照片！");
                        noData.setVisibility(View.VISIBLE);
                    }
                }
                super.run();
            }
        }.start();
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
        noCheckUserAdapter = new NoCheckUserAdapter(NoCheckUserListActivity.this, noCheckUserItemList);
        listView.setAdapter(noCheckUserAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                item = noCheckUserItemList.get((int) parent.getAdapter().getItemId(position));
                currentPosition = position;
                Intent intent = new Intent(NoCheckUserListActivity.this, UserDetailInfoActivity.class);
                intent.putExtra("position",position);
                startActivityForResult(intent,position);
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
        Log.i("NoCheckUserListActivity", "一共有"+cursor.getCount()+"个用户");
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

    //更新用户表是否安检状态
    public void updateUserCheckedState(){
        ContentValues values = new ContentValues();
        values.put("ifChecked","true");
        Log.i("UserList=update", "更新安检状态为true");
        db.update("User",values,"securityNumber=?",new String[]{item.getSecurityNumber()});
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == currentPosition){
                updateUserCheckedState(); //更新本地数据库用户表安检状态
                item.setIfEdit(R.mipmap.userlist_gray);
                noCheckUserItemList.remove(currentPosition);
                noCheckUserAdapter.notifyDataSetChanged();
                checkedNumber++;
                editor.putInt("checkedNumber",checkedNumber);
                editor.commit();
                Log.i("UserList=ActivityResult", "页面回调进来了");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //释放和数据库的连接
        db.close();
    }
}
