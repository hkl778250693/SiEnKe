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
import java.util.List;

/**
 * Created by Administrator on 2017/3/16.
 */
public class ContinueCheckUserActivity extends Activity {
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
    private int task_total_numb;
    private ArrayList<Integer> integers = new ArrayList<>();//保存选中任务的序号
    private SQLiteDatabase db;  //数据库
    private MySqliteHelper helper; //数据库帮助类
    private int currentPosition;  //点击listview  当前item的位置
    private UserListviewAdapter userListviewAdapter;
    private UserListviewItem item;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int checkedNumber = 0;   //已检户数
    private String continuePosition = "";  //继续安检位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlist);

        defaultSetting();//初始化设置
        getTaskParams();//获取任务编号参数
        new Thread() {
            @Override
            public void run() {
                if(integers.size() != 0){
                    if (noData.getVisibility() == View.VISIBLE) {
                        noData.setVisibility(View.GONE);
                    }
                    for (int i = 0; i < integers.size(); i++) {
                        getUserData(stringList.get(i));//读取继续安检用户数据
                        Log.i("ContinueCheckActivity", "查询的任务编号是：" + stringList.get(i));
                    }
                }else {
                    if (noData.getVisibility() == View.GONE) {
                        Log.i("ContinueCheckActivity", "显示没有用户数据照片！");
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
        Log.i("ContinueCheckActivity", "defaultSetting进来了");
        helper = new MySqliteHelper(ContinueCheckUserActivity.this, 1);
        db = helper.getWritableDatabase();
        sharedPreferences = this.getSharedPreferences("data", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        continuePosition = sharedPreferences.getString("continuePosition","");
    }

    //点击事件
    private void setOnClickListener() {
        back.setOnClickListener(onClickListener);
        tiaoZhuan.setOnClickListener(onClickListener);
        backBtn.setOnClickListener(onClickListener);
        nextBtn.setOnClickListener(onClickListener);
        securityCheckCase.setOnClickListener(onClickListener);
        userListviewAdapter = new UserListviewAdapter(ContinueCheckUserActivity.this, userListviewItemList);
        listView.setAdapter(userListviewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                item = userListviewItemList.get((int) parent.getAdapter().getItemId(position));
                currentPosition = position;
                Intent intent = new Intent(ContinueCheckUserActivity.this, UserDetailInfoActivity.class);
                intent.putExtra("position",position);
                startActivityForResult(intent,position);
            }
        });
        if(!continuePosition.equals("")){
            listView.setSelection(Integer.parseInt(continuePosition));  //让listview显示上次安检的位置
            Log.i("Continue_setSelection", "列表显示当前的位置是：" + continuePosition);
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:
                    ContinueCheckUserActivity.this.finish();
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
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                task_total_numb = bundle.getInt("task_total_numb", 0);
                Log.i("ContinueCheckActivity=", "task_total_numb=" + task_total_numb);
                integers = bundle.getIntegerArrayList("integerList");
                Log.i("ContinueCheckActivity=", "integers：" + integers.size());
                stringList = bundle.getStringArrayList("taskId");
                for (int i = 0; i < stringList.size(); i++) {
                    Log.i("ContinueCheckActivity=", "得到的参数为：" + stringList);
                }
            }
        }
    }

    //popupwindow
    public void createSecurityCasePopupwindow() {
        inflater = LayoutInflater.from(ContinueCheckUserActivity.this);
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
        Log.i("ContinueCheckActivity=", "查询用户数据进来了：！");
        Cursor cursor = db.rawQuery("select * from User where taskId=?", new String[]{taskId});
        Log.i("ContinueCheckActivity=", "数据库进来了：！");
        Log.i("ContinueCheckActivity=", "任务编号是：" + taskId);
        Log.i("ContinueCheckActivity=", "有" + cursor.getCount() + "条数据！");
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
                userListviewItemList.remove(currentPosition);
                userListviewAdapter.notifyDataSetChanged();
                checkedNumber++;
                editor.putInt("checkedNumber",checkedNumber);
                editor.commit();
                Log.i("ContinueCheckActivity", "页面回调进来了");
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
