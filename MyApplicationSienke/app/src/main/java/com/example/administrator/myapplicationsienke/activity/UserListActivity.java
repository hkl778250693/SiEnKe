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
import android.text.Editable;
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
public class UserListActivity extends Activity {
    private ImageView back, tiaoZhuan;
    private ListView listView;
    private TextView securityCheckCase, noData;
    private EditText setEsearchTextChanged;//搜索框
    private Button backBtn, nextBtn, searchBtn;
    private LayoutInflater inflater;  //转换器
    private View securityCaseView;
    private RadioButton notSecurityCheck, passSecurityCheck, notPassSecurityCheck;
    private PopupWindow popupWindow;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlist);

        defaultSetting();//初始化设置
        getTaskParams();//获取任务编号参数
        new Thread() {
            @Override
            public void run() {
                if(task_total_numb != 0){
                    if (noData.getVisibility() == View.VISIBLE) {
                        noData.setVisibility(View.GONE);
                    }
                    for (int i = 0; i < task_total_numb; i++) {
                        getUserData(stringList.get(i));//读取所有安检用户数据
                        Log.i("UserListActivity", "查询的任务编号是：" + stringList.get(i));
                    }
                }else {
                    if (noData.getVisibility() == View.GONE) {
                        Log.i("UserListActivity", "显示没有用户数据照片！");
                        noData.setVisibility(View.VISIBLE);
                    }
                }
                super.run();
            }
        }.start();
        bindView();//绑定控件
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
        listView = (ListView) findViewById(R.id.listview);
        securityCheckCase = (TextView) findViewById(R.id.security_check_case);
        tiaoZhuan = (ImageView) findViewById(R.id.tiaozhuan);
        backBtn = (Button) findViewById(R.id.back_btn);
        nextBtn = (Button) findViewById(R.id.next_btn);
        noData = (TextView) findViewById(R.id.no_data);
        setEsearchTextChanged = (EditText) findViewById(R.id.etSearch);
        searchBtn = (Button) findViewById(R.id.search_btn);
    }

    //初始化设置
    private void defaultSetting() {
        helper = new MySqliteHelper(UserListActivity.this, 1);
        db = helper.getWritableDatabase();
        sharedPreferences = this.getSharedPreferences("data", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    //点击事件
    private void setOnClickListener() {
        back.setOnClickListener(onClickListener);
        tiaoZhuan.setOnClickListener(onClickListener);
        backBtn.setOnClickListener(onClickListener);
        nextBtn.setOnClickListener(onClickListener);
        securityCheckCase.setOnClickListener(onClickListener);
        userListviewAdapter = new UserListviewAdapter(UserListActivity.this, userListviewItemList);
        listView.setAdapter(userListviewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                item = userListviewItemList.get((int) parent.getAdapter().getItemId(position));
                currentPosition = position;
                Intent intent = new Intent(UserListActivity.this, UserDetailInfoActivity.class);
                intent.putExtra("position",position);
                startActivityForResult(intent,position);
            }
        });


        /*查询*/
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEsearchTextChanged.setText("");
            }
        });
        setEsearchTextChanged.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userListviewAdapter.getFilter();
                if (s.length() > 0) {
                    searchBtn.setVisibility(View.VISIBLE);
                } else {
                    searchBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:
                    for (int i = 0; i < task_total_numb; i++) {
                        getContinueCheckPosition(stringList.get(i)); //获取继续安检的item位置
                        if(!sharedPreferences.getString("continuePosition","").equals("")){
                            break;
                        }
                    }
                    UserListActivity.this.finish();
                    break;
                case R.id.security_check_case:
                    createSecurityCasePopupwindow();
                    break;
                case R.id.tiaozhuan:
                    break;
            }
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

    //popupwindow
    public void createSecurityCasePopupwindow() {
        inflater = LayoutInflater.from(UserListActivity.this);
        securityCaseView = inflater.inflate(R.layout.popupwindow_userlist_choose, null);
        popupWindow = new PopupWindow(securityCaseView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //绑定控件ID
        notSecurityCheck = (RadioButton) securityCaseView.findViewById(R.id.not_security_check);
        passSecurityCheck = (RadioButton) securityCaseView.findViewById(R.id.pass_security_check);
        notPassSecurityCheck = (RadioButton) securityCaseView.findViewById(R.id.not_pass_security_check);
        //设置点击事件
        notSecurityCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityCheckCase.setText(notSecurityCheck.getText());
            }
        });
        passSecurityCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityCheckCase.setText(passSecurityCheck.getText());
            }
        });
        notPassSecurityCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityCheckCase.setText(notPassSecurityCheck.getText());
            }
        });
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
        popupWindow.setAnimationStyle(R.style.Popupwindow);
        backgroundAlpha(0.8F);   //背景变暗
        popupWindow.showAsDropDown(securityCheckCase, 200, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0F);
            }
        });
    }

    //设置背景透明度
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
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
        Log.i("UserListActivityget=", "查询用户数据进来了：！");
        Cursor cursor = db.rawQuery("select * from User where taskId=?", new String[]{taskId});//查询并获得游标
        Log.i("UserListActivityget=", "数据库进来了：！");
        Log.i("UserListActivityget=", "任务编号是：" + taskId);
        Log.i("UserListActivityget=", "有" + cursor.getCount() + "条数据！");
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
            Log.i("UserList=cursor", "安检状态为 = "+cursor.getString(10));
            if(cursor.getString(10).equals("true")){
                Log.i("UserList=cursor", "安检状态为true");
                userListviewItem.setIfEdit(R.mipmap.userlist_gray);
            }else {
                Log.i("UserList=cursor", "安检状态为false");
                userListviewItem.setIfEdit(R.mipmap.userlist_red);
            }
            userListviewItemList.add(userListviewItem);
            Log.i("UserListActivityget=", "用户列表的长度为：" + userListviewItemList.size());
        }
        cursor.close(); //游标关闭
    }

    //获取继续安检的item位置
    public void getContinueCheckPosition(String taskId) {
        Log.i("ContinueCheckPosition", "获取继续安检位置进来了！");
        Cursor cursor = db.rawQuery("select * from User where taskId=?", new String[]{taskId});//查询并获得游标
        //在页面finish之前，从上到下查询本地数据库没有安检的用户，相对应的item位置，查询到一个就break
        if(cursor.getCount() == 0){
            return;
        }
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
                userListviewAdapter.notifyDataSetChanged();
                editor.putInt("checkedNumber",sharedPreferences.getInt("checkedNumber",0)+1);
                editor.commit();
                Log.i("UserList=ActivityResult", "页面回调进来了");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close(); //释放和数据库的连接
    }
    //查询


}
