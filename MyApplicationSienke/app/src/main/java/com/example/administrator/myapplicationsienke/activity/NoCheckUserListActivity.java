package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.adapter.NoCheckUserAdapter;
import com.example.administrator.myapplicationsienke.mode.MySqliteHelper;
import com.example.administrator.myapplicationsienke.model.UserListviewItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2017/3/15.
 */
public class NoCheckUserListActivity extends Activity {
    private ImageView back,editDelete;
    private ListView listView;
    private TextView name;
    private List<UserListviewItem> noCheckUserItemList = new ArrayList<>();
    private SQLiteDatabase db;  //数据库
    private MySqliteHelper helper; //数据库帮助类
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private EditText etSearch;//搜索框
    private TextView noData;
    private ArrayList<String> stringList = new ArrayList<>();//保存字符串参数
    private int task_total_numb = 0;
    private ArrayList<Integer> integers = new ArrayList<>();//保存选中任务的序号
    private UserListviewItem item;
    private int currentPosition;  //点击listview  当前item的位置
    private NoCheckUserAdapter noCheckUserAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlist);

        defaultSetting();//初始化设置
        getTaskParams(); //获取任务编号参数
        bindView();//绑定控件
        setViewClickListener();//点击事件
        new Thread() {
            @Override
            public void run() {
                if(task_total_numb != 0){
                    for (int i = 0; i < task_total_numb; i++) {
                        getUserInfo(stringList.get(i));//读取未检任务数据
                    }
                    handler.sendEmptyMessage(0);
                }else {
                    handler.sendEmptyMessage(1);
                }
                super.run();
            }
        }.start();
    }

    //绑定控件ID
    private void bindView() {
        back = (ImageView) findViewById(R.id.back);
        listView = (ListView) findViewById(R.id.listview);
        noData = (TextView) findViewById(R.id.no_data);
        name = (TextView) findViewById(R.id.name);
        etSearch = (EditText) findViewById(R.id.etSearch);
        editDelete = (ImageView) findViewById(R.id.edit_delete);
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
        name.setText("未检用户");
        back.setOnClickListener(onClickListener);
        editDelete.setOnClickListener(onClickListener);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                item = noCheckUserItemList.get((int) parent.getAdapter().getItemId(position));
                currentPosition = position;
                Intent intent = new Intent(NoCheckUserListActivity.this, UserDetailInfoActivity.class);
                intent.putExtra("position",position);
                intent.putExtra("user_number",item.getUserId());
                intent.putExtra("user_name",item.getUserName());
                intent.putExtra("security_id",item.getSecurityNumber());
                intent.putExtra("meter_number",item.getNumber());
                intent.putExtra("user_phone_number",item.getPhoneNumber());
                intent.putExtra("user_address",item.getAdress());
                intent.putExtra("check_type",item.getSecurityType());
                startActivityForResult(intent,position);
            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("UserListActivity", "onTextChanged进来了" );
                if(TextUtils.isEmpty(s.toString().trim())){
                    listView.clearTextFilter();    //搜索文本为空时，清除ListView的过滤
                    if(noCheckUserAdapter != null){
                        noCheckUserAdapter.getFilter().filter("");
                    }
                    new Thread(){
                        @Override
                        public void run() {
                            handler.sendEmptyMessage(0);
                        }
                    }.start();
                    if (editDelete.getVisibility() == View.VISIBLE) {
                        editDelete.setVisibility(View.GONE);  //当输入框为空时，叉叉消失
                    }
                }else {
                    if(noCheckUserAdapter != null){
                        noCheckUserAdapter.getFilter().filter(s);
                    }
                    if (editDelete.getVisibility() == View.GONE) {
                        editDelete.setVisibility(View.VISIBLE);  //反之则显示
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i("UserListActivity_after", "afterTextChanged进来了" );
            }
        });
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:
                    for (int i = 0; i < task_total_numb; i++) {
                        getContinueCheckPosition(stringList.get(i));//读取下载到本地的任务数据
                        if(!sharedPreferences.getString("continuePosition","").equals("")){
                            break;
                        }
                    }
                    NoCheckUserListActivity.this.finish();
                    break;
                case R.id.edit_delete:
                    etSearch.setText("");
                    editDelete.setVisibility(View.GONE);
                    if(noCheckUserAdapter != null){
                        noCheckUserAdapter.getFilter().filter("");
                    }
                    break;
            }
        }
    };


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    noCheckUserAdapter = new NoCheckUserAdapter(NoCheckUserListActivity.this, noCheckUserItemList);
                    noCheckUserAdapter.notifyDataSetChanged();
                    listView.setAdapter(noCheckUserAdapter);
                    break;
                case 1:
                    if (noData.getVisibility() == View.GONE) {
                        Log.i("ContinueCheckActivity", "显示没有用户数据照片！");
                        noData.setVisibility(View.VISIBLE);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    //获取任务编号参数
    public void getTaskParams() {
        if (sharedPreferences.getStringSet("stringSet",null) != null && sharedPreferences.getInt("task_total_numb",0) != 0) {
            Iterator iterator = sharedPreferences.getStringSet("stringSet",null).iterator();
            while (iterator.hasNext()){
                stringList.add(iterator.next().toString());
            }
            task_total_numb = sharedPreferences.getInt("task_total_numb",0);
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

    //获取继续安检的item位置
    public void getContinueCheckPosition(String taskId) {
        Log.i("ContinueCheckPosition", "获取继续安检位置进来了！");
        Cursor cursor = db.rawQuery("select * from User where taskId=?", new String[]{taskId});//查询并获得游标
        if(cursor.getCount() == 0){
            return;
        }
        //在页面finish之前，从上到下查询本地数据库没有安检的用户，相对应的item位置，查询到一个就break
        while (cursor.moveToNext()) {
            Log.i("ContinueCheckPosition", "游标进来了");
            Log.i("ContinueCheckPosition", "安检状态为 = "+cursor.getString(10));
            if(cursor.getString(10).equals("false")){
                Log.i("ContinueCheckPosition", "安检状态为 = "+cursor.getString(10));
                Log.i("ContinueCheckPosition", "安检状态为false,此时的item位置为："+cursor.getPosition());
                Log.i("ContinueCheckPosition", "安检状态为false,此时的item的用户名为："+cursor.getColumnName(2));
                editor.putString("continuePosition",cursor.getPosition()+"");
                editor.commit();
                break;
            }
        }
        cursor.close(); //游标关闭
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
