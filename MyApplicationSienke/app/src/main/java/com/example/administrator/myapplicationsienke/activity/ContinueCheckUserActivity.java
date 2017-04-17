package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.adapter.UserListviewAdapter;
import com.example.administrator.myapplicationsienke.mode.MySqliteHelper;
import com.example.administrator.myapplicationsienke.model.UserListviewItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2017/3/16.
 */
public class ContinueCheckUserActivity extends Activity {
    private ImageView back,editDelete;
    private ListView listView;
    private TextView noData,name;
    private EditText etSearch;//搜索框
    private List<UserListviewItem> userListviewItemList = new ArrayList<>();
    private ArrayList<String> stringList = new ArrayList<>();//保存字符串参数
    private int task_total_numb = 0;
    private SQLiteDatabase db;  //数据库
    private MySqliteHelper helper; //数据库帮助类
    private int currentPosition;  //点击listview  当前item的位置
    private UserListviewAdapter userListviewAdapter;
    private UserListviewItem item;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String continuePosition = "";  //继续安检位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlist);

        bindView();//绑定控件
        defaultSetting();//初始化设置
        getTaskParams();//获取任务编号参数
        new Thread() {
            @Override
            public void run() {
                if(task_total_numb != 0){
                    for (int i = 0; i < task_total_numb; i++) {
                        getUserData(stringList.get(i));//读取继续安检用户数据
                    }
                    handler.sendEmptyMessage(1);
                    handler.sendEmptyMessage(0);
                }else {
                    handler.sendEmptyMessage(2);
                }
                super.run();
            }
        }.start();
        setOnClickListener();//点击事件
    }

    //强制竖屏
    @Override
    protected void onResume() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
    }

    //绑定控件ID
    private void bindView() {
        back = (ImageView) findViewById(R.id.back);
        name = (TextView) findViewById(R.id.name);
        listView = (ListView) findViewById(R.id.listview);
        noData = (TextView) findViewById(R.id.no_data);
        etSearch = (EditText) findViewById(R.id.etSearch);
        editDelete = (ImageView) findViewById(R.id.edit_delete);
    }

    //初始化设置
    private void defaultSetting() {
        name.setText("继续安检");
        helper = new MySqliteHelper(ContinueCheckUserActivity.this, 1);
        db = helper.getWritableDatabase();
        sharedPreferences = this.getSharedPreferences("data", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        continuePosition = sharedPreferences.getString("continuePosition","");
    }

    //点击事件
    private void setOnClickListener() {
        back.setOnClickListener(onClickListener);
        editDelete.setOnClickListener(onClickListener);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                item = userListviewItemList.get((int) parent.getAdapter().getItemId(position));
                currentPosition = position;
                Intent intent = new Intent(ContinueCheckUserActivity.this, UserDetailInfoActivity.class);
                intent.putExtra("position",position);
                startActivityForResult(intent,currentPosition);
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
                    if(userListviewAdapter != null){
                        userListviewAdapter.getFilter().filter("");
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
                    userListviewAdapter.getFilter().filter(s);
                    //listView.setFilterText(s.toString().trim());  //设置过滤关键字
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
                    ContinueCheckUserActivity.this.finish();
                    break;
                case R.id.edit_delete:
                    etSearch.setText("");
                    editDelete.setVisibility(View.GONE);
                    userListviewAdapter.getFilter().filter("");
                    new Thread(){
                        @Override
                        public void run() {
                            handler.sendEmptyMessage(3);
                        }
                    }.start();
                    break;
            }
        }
    };

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    if(!sharedPreferences.getString("continuePosition","").equals("")){
                        listView.setSelection(Integer.parseInt(continuePosition));  //让listview显示上次安检的位置
                        Log.i("ContinueCheckActivity", "列表显示当前的位置是：" + continuePosition);
                    }
                    break;
                case 1:
                    userListviewAdapter = new UserListviewAdapter(ContinueCheckUserActivity.this, userListviewItemList);
                    userListviewAdapter.notifyDataSetChanged();
                    listView.setAdapter(userListviewAdapter);
                    break;
                case 2:
                    if (noData.getVisibility() == View.GONE) {
                        Log.i("ContinueCheckActivity", "显示没有用户数据照片！");
                        noData.setVisibility(View.VISIBLE);
                    }
                    break;
                case 3:
                    if(!sharedPreferences.getString("continuePosition","").equals("")){
                        listView.setSelection(Integer.parseInt(sharedPreferences.getString("continuePosition","")));  //让listview显示上次安检的位置
                        Log.i("Continue_edit_delete", "列表显示当前的位置是：" + sharedPreferences.getString("continuePosition",""));
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

    //获取继续安检的item位置
    public void getContinueCheckPosition(String taskId) {
        Log.i("ContinueCheckPosition", "获取继续安检位置进来了！");
        Cursor cursor = db.rawQuery("select * from User where taskId=?", new String[]{taskId});//查询并获得游标
        //在页面finish之前，从上到下查询本地数据库没有安检的用户，相对应的item位置，查询到一个就break
        if (cursor.getCount() == 0) {
            return;
        }
        while (cursor.moveToNext()) {
            if (cursor.getString(10).equals("false")) {
                editor.putString("continuePosition", cursor.getPosition() + "");
                editor.commit();
                break;
            }
        }
        cursor.close(); //游标关闭
    }

    /**
     * 查询方法参数详解
     * <p/>
     * public Cursor query (boolean distinct, String table, String[] columns,
     * String selection, String[] selectionArgs,
     * String groupBy, String having,
     * String orderBy, String limit,
     * CancellationSignal cancellationSignal)
     * <p/>
     * 参数介绍 :
     * <p/>
     * -- 参数① distinct : 是否去重复, true 去重复;
     * <p/>
     * -- 参数② table : 要查询的表名;
     * <p/>
     * -- 参数③ columns : 要查询的列名, 如果为null, 就会查询所有的列;
     * <p/>
     * -- 参数④ whereClause : 条件查询子句, 在这里可以使用占位符 "?";
     * <p/>
     * -- 参数⑤ whereArgs : whereClause查询子句中的传入的参数值, 逐个替换 "?" 占位符;
     * <p/>
     * -- 参数⑥ groupBy: 控制分组, 如果为null 将不会分组;
     * <p/>
     * -- 参数⑦ having : 对分组进行过滤;
     * <p/>
     * -- 参数⑧ orderBy : 对记录进行排序;
     * <p/>
     * -- 参数⑨ limite : 用于分页, 如果为null, 就认为不进行分页查询;
     * <p/>
     * -- 参数⑩ cancellationSignal : 进程中取消操作的信号, 如果操作被取消, 当查询命令执行时会抛出 OperationCanceledException 异常;
     */

    //读取下载到本地的任务数据
    public void getUserData(String taskId) {
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
            UserListviewItem userListviewItem = new UserListviewItem();
            userListviewItem.setSecurityNumber(cursor.getString(1));
            userListviewItem.setUserName(cursor.getString(2));
            userListviewItem.setNumber(cursor.getString(3));
            userListviewItem.setPhoneNumber(cursor.getString(4));
            userListviewItem.setSecurityType(cursor.getString(5));
            userListviewItem.setUserId(cursor.getString(6));
            userListviewItem.setAdress(cursor.getString(8));
            Log.i("ContinueCheckActivity", "安检状态为 = "+cursor.getString(10));
            if(cursor.getString(10).equals("true")){
                Log.i("ContinueCheckActivity", "安检状态为true");
                userListviewItem.setIfEdit(R.mipmap.userlist_gray);
            }else {
                Log.i("ContinueCheckActivity", "安检状态为false");
                userListviewItem.setIfEdit(R.mipmap.userlist_red);
            }
            userListviewItemList.add(userListviewItem);
            Log.i("ContinueCheckActivity", "用户列表的长度为：" + userListviewItemList.size());
        }
        cursor.close();
    }

    //更新用户表是否安检状态
    public void updateUserCheckedState(){
        ContentValues values = new ContentValues();
        values.put("ifChecked","true");
        Log.i("ContinueCheckActivity", "更新安检状态为true");
        db.update("User",values,"securityNumber=?",new String[]{item.getSecurityNumber()});
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == currentPosition){
                updateUserCheckedState(); //更新本地数据库用户表安检状态
                item.setIfEdit(R.mipmap.userlist_gray);
                userListviewAdapter.notifyDataSetChanged();
                for (int i = 0; i < task_total_numb; i++) {
                    getContinueCheckPosition(stringList.get(i)); //获取继续安检的item位置
                    if (!sharedPreferences.getString("continuePosition", "").equals("")) {
                        listView.setSelection(Integer.parseInt(sharedPreferences.getString("continuePosition", "")));
                        Log.i("ContinueCheckActivity", "页面回调进来了,显示的位置是："+sharedPreferences.getString("continuePosition", ""));
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //释放和数据库的连接
        db.close();
    }
    //查询


}
