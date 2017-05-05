package com.example.administrator.myapplicationsienke.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.adapter.SecurityCheckViewPagerAdapter;
import com.example.administrator.myapplicationsienke.fragment.DataTransferFragment;
import com.example.administrator.myapplicationsienke.fragment.SecurityChooseFragment;
import com.example.administrator.myapplicationsienke.mode.MySqliteHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/3/14.
 */
public class SecurityChooseActivity extends FragmentActivity {
    private RadioButton file, settings, quite; //文件管理 系统设置 退出应用
    private LayoutInflater inflater; //转换器
    private View popupwindowView;
    private PopupWindow popupWindow;
    private ImageView security_check_go;
    private RadioButton optionRbt;  //选项按钮
    private TextView name, userName;
    private RadioButton dataTransferRbt;  //数据传输按钮
    private List<Fragment> fragmentList;
    private ViewPager viewPager;
    private SecurityCheckViewPagerAdapter adapter;
    private long exitTime = 0;//退出程序
    private SharedPreferences sharedPreferences, sharedPreferences_login;
    private SharedPreferences.Editor editor;
    private Bundle params;
    private Set<String> stringSet = new HashSet<>();//保存字符串参数
    private int task_total_numb;
    private ArrayList<String> stringList = new ArrayList<>();//得到的字符串集合
    private SQLiteDatabase db;  //数据库
    private MySqliteHelper helper; //数据库帮助类
    private String ip, port;  //接口ip地址   端口
    public int responseCode = 0;
    private String stateResult, contentResult, hiddenResult, reasonResult; //网络请求结果
    private JSONObject stateObject, contentObject, hiddenObject, reasonObject;

    //强制竖屏
    @Override
    protected void onResume() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);

        bindView();//绑定控件
        defaultSetting();//初始化设置
        setViewPager();//设置viewPager
        setViewClickListener();//点击事件
    }

    //绑定控件
    private void bindView() {
        security_check_go = (ImageView) findViewById(R.id.security_check_go);
        optionRbt = (RadioButton) findViewById(R.id.option_rbt);
        dataTransferRbt = (RadioButton) findViewById(R.id.data_transfer_rbt);
        viewPager = (ViewPager) findViewById(R.id.security_viewpager);
        name = (TextView) findViewById(R.id.name);
        userName = (TextView) findViewById(R.id.user_name);
    }

    //点击事件
    public void setViewClickListener() {
        security_check_go.setOnClickListener(onClickListener);
        optionRbt.setOnClickListener(onClickListener);
        dataTransferRbt.setOnClickListener(onClickListener);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        optionRbt.setChecked(true);
                        dataTransferRbt.setChecked(false);
                        name.setText("安检选项");
                        break;
                    case 1:
                        optionRbt.setChecked(false);
                        dataTransferRbt.setChecked(true);
                        name.setText("数据传输");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.security_check_go:
                    security_check_go.setClickable(false);
                    createPopupwindow();
                    Log.i("createPopupwindow===>", "true");
                    break;
                case R.id.option_rbt:
                    viewPager.setCurrentItem(0);
                    name.setText("安检选项");
                    break;
                case R.id.data_transfer_rbt:
                    viewPager.setCurrentItem(1);
                    name.setText("数据传输");
                    break;
            }
        }
    };

    //popupwindow
    public void createPopupwindow() {
        inflater = LayoutInflater.from(SecurityChooseActivity.this);
        popupwindowView = inflater.inflate(R.layout.popup_window_security, null);
        popupWindow = new PopupWindow(popupwindowView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //绑定控件ID
        //file = (RadioButton) popupwindowView.findViewById(R.id.file);//文件管理
        settings = (RadioButton) popupwindowView.findViewById(R.id.settings);//系统设置
        quite = (RadioButton) popupwindowView.findViewById(R.id.quite);//安全退出
        //设置点击事件
        /*file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.file:
                        Intent intent = new Intent(SecurityChooseActivity.this, FileManageActivity.class);
                        startActivity(intent);
                        popupWindow.dismiss();
                        break;
                }
            }
        });*/
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.settings:
                        Intent intent = new Intent(SecurityChooseActivity.this, SystemSettingActivity.class);
                        startActivity(intent);
                        popupWindow.dismiss();
                        security_check_go.setClickable(true);
                        break;
                }
            }
        });

        quite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                security_check_go.setClickable(true);
                System.exit(0);
            }
        });
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popupwindow_spinner_shape));
        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        backgroundAlpha(0.8F);   //背景变暗
        popupWindow.showAsDropDown(security_check_go, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                security_check_go.setClickable(true);
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

    //初始化设置
    private void defaultSetting() {
        optionRbt.setChecked(true);
        helper = new MySqliteHelper(SecurityChooseActivity.this, 1);
        db = helper.getWritableDatabase();
        sharedPreferences = this.getSharedPreferences("data", Context.MODE_PRIVATE);
        sharedPreferences_login = this.getSharedPreferences("login_info", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userName.setText(sharedPreferences_login.getString("user_name", "")); //设置登录用户的名称
        editor.putInt("problem_number", sharedPreferences.getInt("problem_number", 0));
        editor.apply();
        Log.i("user_exchanged", "用户是否改变" + sharedPreferences_login.getBoolean("user_exchanged", false));
        if (sharedPreferences_login.getBoolean("user_exchanged", false)) {
            editor.clear();
            editor.apply();
            db.delete("User", null, null);  //删除User表中的所有数据（官方推荐方法）
            db.delete("Task", null, null);  //删除Task表中的所有数据
            db.delete("SecurityState", null, null);  //删除SecurityState表中的所有数据
            db.delete("security_content", null, null);  //删除security_content表中的所有数据
            db.delete("security_hidden", null, null);  //删除security_hidden表中的所有数据
            db.delete("security_hidden_reason", null, null);  //删除security_hidden_reason表中的所有数据
            //设置id从1开始（sqlite默认id从1开始），若没有这一句，id将会延续删除之前的id
            db.execSQL("update sqlite_sequence set seq=0 where name='User'");
            db.execSQL("update sqlite_sequence set seq=0 where name='Task'");
            db.execSQL("update sqlite_sequence set seq=0 where name='SecurityState'");
            db.execSQL("update sqlite_sequence set seq=0 where name='security_content'");
            db.execSQL("update sqlite_sequence set seq=0 where name='security_hidden'");
            db.execSQL("update sqlite_sequence set seq=0 where name='security_hidden_reason'");
        }

        //开启支线程进行请求任务信息
        new Thread() {
            @Override
            public void run() {
                requireSecurityState("findSecurityState.do "); //安检状态
                requireSecurityContent("findSecurityContent.do");//安检内容
                requireSafetyHidden("findSafetyHidden.do");//安检原因类型
                requireSafetyReason("findSafetyReason.do");//安检原因
                super.run();
            }
        }.start();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            setIntent(intent);
            int number = getIntent().getIntExtra("down", 0);
            Log.i("onNewIntent", "onNewIntent进来了！返回的结果=" + number);
            if (number == 1) {     //获取任务选择页面传过来的参数，如果参数为 1 ，则让viewpager显示数据传输fragment
                viewPager.setCurrentItem(1);
                dataTransferRbt.setChecked(true);
            }
            params = getIntent().getExtras();
            if (params != null) {
                Log.i("SecurityChooseFragment=", "bundle不为空");
                task_total_numb = params.getInt("task_total_numb", 0);
                Log.i("SecurityChooseFragment=", "task_total_numb=" + task_total_numb);
                stringList = params.getStringArrayList("taskId");
                if (task_total_numb != 0) {
                    stringSet.clear();
                    for (int i = 0; i < task_total_numb; i++) {
                        stringSet.add(stringList.get(i));
                        Log.i("onNewIntent====>", "得到的参数为：" + stringList.get(i));
                    }
                }
                editor.putInt("task_total_numb", task_total_numb);
                editor.putStringSet("stringSet", stringSet);
                editor.apply();
            }
        }
    }

    //设置viewPager
    private void setViewPager() {
        fragmentList = new ArrayList<>();
        //添加fragment到list
        fragmentList.add(new SecurityChooseFragment());
        fragmentList.add(new DataTransferFragment());
        //避免报空指针
        if (fragmentList != null) {
            adapter = new SecurityCheckViewPagerAdapter(getSupportFragmentManager(), fragmentList);
        }
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 捕捉返回事件按钮
     * 因为此 Activity继承 TabActivity,用 onKeyDown无响应，
     * 所以改用 dispatchKeyEvent
     * <p/>
     * 一般的 Activity 用 onKeyDown就可以了
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                this.exitApp();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    /**
     * 退出程序
     */
    private void exitApp() {
        // 判断2次点击事件时间
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Log.i("exitTime==========>", System.currentTimeMillis() - exitTime + "");
            //-------------Activity.this的context 返回当前activity的上下文，属于activity，activity 摧毁他就摧毁
            //-------------getApplicationContext() 返回应用的上下文，生命周期是整个应用，应用摧毁它才摧毁
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }


    //请求安检状态网络数据
    private void requireSecurityState(final String method) {
        new Thread() {
            @Override
            public void run() {
                try {
                    URL url;
                    HttpURLConnection httpURLConnection;
                    Log.i("sharedPreferences====>", sharedPreferences.getString("IP", ""));
                    if (!sharedPreferences.getString("security_ip", "").equals("")) {
                        ip = sharedPreferences.getString("security_ip", "");
                        //Log.i("sharedPreferences=ip=>",ip);
                    } else {
                        ip = "88.88.88.66:";
                    }
                    if (!sharedPreferences.getString("security_port", "").equals("")) {
                        port = sharedPreferences.getString("security_port", "");
                        //Log.i("sharedPreferences=ip=>",ip);
                    } else {
                        port = "8088";
                    }
                    String httpUrl = "http://" + ip + port + "/SMDemo/" + method;
                    url = new URL(httpUrl);
                    Log.i("url=============>", url + "");
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setRequestProperty("Accept-Encoding", "identity");
                    httpURLConnection.setRequestProperty("Content-Type", "applicaton/json;charset=UTF-8");
                    httpURLConnection.setConnectTimeout(6000);
                    httpURLConnection.setReadTimeout(6000);
                    httpURLConnection.connect();
                    //传回的数据解析成String
                    if ((responseCode = httpURLConnection.getResponseCode()) == 200) {
                        InputStream inputStream = httpURLConnection.getInputStream();
                        Log.i("start_inputStream==>", "" + inputStream);
                        Log.i("mid_inputStream==>", "" + inputStream);
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        StringBuilder stringBuilder = new StringBuilder();
                        String str;
                        while ((str = bufferedReader.readLine()) != null) {
                            stringBuilder.append(str);
                        }
                        Log.i("end_inputStream==>", "" + inputStream);
                        stateResult = stringBuilder.toString();
                        Log.i("taskResult=====>", stateResult);
                        JSONObject jsonObject = new JSONObject(stateResult);
                        if (!jsonObject.optString("total", "").equals("0")) {
                            handler.sendEmptyMessage(2);
                        } else {
                            handler.sendEmptyMessage(3);
                        }
                    } else {
                        try {
                            Thread.sleep(3000);
                            handler.sendEmptyMessage(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.i("IOException==========>", "网络请求异常!");
                    handler.sendEmptyMessage(1);
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //请求安检内容网络数据
    private void requireSecurityContent(final String method) {
        new Thread() {
            @Override
            public void run() {
                try {
                    URL url;
                    HttpURLConnection httpURLConnection;
                    Log.i("sharedPreferences====>", sharedPreferences.getString("IP", ""));
                    if (!sharedPreferences.getString("security_ip", "").equals("")) {
                        ip = sharedPreferences.getString("security_ip", "");
                        //Log.i("sharedPreferences=ip=>",ip);
                    } else {
                        ip = "88.88.88.66:";
                    }
                    if (!sharedPreferences.getString("security_port", "").equals("")) {
                        port = sharedPreferences.getString("security_port", "");
                        //Log.i("sharedPreferences=ip=>",ip);
                    } else {
                        port = "8088";
                    }
                    String httpUrl = "http://" + ip + port + "/SMDemo/" + method;
                    url = new URL(httpUrl);
                    Log.i("url=============>", url + "");
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setRequestProperty("Accept-Encoding", "identity");
                    httpURLConnection.setRequestProperty("Content-Type", "applicaton/json;charset=UTF-8");
                    httpURLConnection.setConnectTimeout(6000);
                    httpURLConnection.setReadTimeout(6000);
                    httpURLConnection.connect();
                    //传回的数据解析成String
                    if ((responseCode = httpURLConnection.getResponseCode()) == 200) {
                        InputStream inputStream = httpURLConnection.getInputStream();
                        Log.i("start_inputStream==>", "" + inputStream);
                        Log.i("mid_inputStream==>", "" + inputStream);
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        StringBuilder stringBuilder = new StringBuilder();
                        String str;
                        while ((str = bufferedReader.readLine()) != null) {
                            stringBuilder.append(str);
                        }
                        Log.i("end_inputStream==>", "" + inputStream);
                        contentResult = stringBuilder.toString();
                        Log.i("taskResult=====>", contentResult);
                        JSONObject jsonObject = new JSONObject(contentResult);
                        if (!jsonObject.optString("total", "").equals("0")) {
                            handler.sendEmptyMessage(4);
                        } else {
                            handler.sendEmptyMessage(5);
                        }
                    } else {
                        try {
                            Thread.sleep(3000);
                            handler.sendEmptyMessage(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.i("IOException==========>", "网络请求异常!");
                    handler.sendEmptyMessage(1);
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //请求安检隐患类型网络数据
    private void requireSafetyHidden(final String method) {
        new Thread() {
            @Override
            public void run() {
                try {
                    URL url;
                    HttpURLConnection httpURLConnection;
                    Log.i("sharedPreferences====>", sharedPreferences.getString("IP", ""));
                    if (!sharedPreferences.getString("security_ip", "").equals("")) {
                        ip = sharedPreferences.getString("security_ip", "");
                        //Log.i("sharedPreferences=ip=>",ip);
                    } else {
                        ip = "88.88.88.66:";
                    }
                    if (!sharedPreferences.getString("security_port", "").equals("")) {
                        port = sharedPreferences.getString("security_port", "");
                        //Log.i("sharedPreferences=ip=>",ip);
                    } else {
                        port = "8088";
                    }
                    String httpUrl = "http://" + ip + port + "/SMDemo/" + method;
                    url = new URL(httpUrl);
                    Log.i("url=============>", url + "");
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setRequestProperty("Accept-Encoding", "identity");
                    httpURLConnection.setRequestProperty("Content-Type", "applicaton/json;charset=UTF-8");
                    httpURLConnection.setConnectTimeout(6000);
                    httpURLConnection.setReadTimeout(6000);
                    httpURLConnection.connect();
                    //传回的数据解析成String
                    if ((responseCode = httpURLConnection.getResponseCode()) == 200) {
                        InputStream inputStream = httpURLConnection.getInputStream();
                        Log.i("start_inputStream==>", "" + inputStream);
                        Log.i("mid_inputStream==>", "" + inputStream);
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        StringBuilder stringBuilder = new StringBuilder();
                        String str;
                        while ((str = bufferedReader.readLine()) != null) {
                            stringBuilder.append(str);
                        }
                        Log.i("end_inputStream==>", "" + inputStream);
                        hiddenResult = stringBuilder.toString();
                        Log.i("taskResult=====>", hiddenResult);
                        JSONObject jsonObject = new JSONObject(hiddenResult);
                        if (!jsonObject.optString("total", "").equals("0")) {
                            handler.sendEmptyMessage(6);
                        } else {
                            handler.sendEmptyMessage(7);
                        }
                    } else {
                        try {
                            Thread.sleep(3000);
                            handler.sendEmptyMessage(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.i("IOException==========>", "网络请求异常!");
                    handler.sendEmptyMessage(1);
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //请求安检隐患原因网络数据
    private void requireSafetyReason(final String method) {
        new Thread() {
            @Override
            public void run() {
                try {
                    URL url;
                    HttpURLConnection httpURLConnection;
                    Log.i("sharedPreferences====>", sharedPreferences.getString("IP", ""));
                    if (!sharedPreferences.getString("security_ip", "").equals("")) {
                        ip = sharedPreferences.getString("security_ip", "");
                        //Log.i("sharedPreferences=ip=>",ip);
                    } else {
                        ip = "88.88.88.66:";
                    }
                    if (!sharedPreferences.getString("security_port", "").equals("")) {
                        port = sharedPreferences.getString("security_port", "");
                        //Log.i("sharedPreferences=ip=>",ip);
                    } else {
                        port = "8088";
                    }
                    String httpUrl = "http://" + ip + port + "/SMDemo/" + method;
                    url = new URL(httpUrl);
                    Log.i("url=============>", url + "");
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setRequestProperty("Accept-Encoding", "identity");
                    httpURLConnection.setRequestProperty("Content-Type", "applicaton/json;charset=UTF-8");
                    httpURLConnection.setConnectTimeout(6000);
                    httpURLConnection.setReadTimeout(6000);
                    httpURLConnection.connect();
                    //传回的数据解析成String
                    if ((responseCode = httpURLConnection.getResponseCode()) == 200) {
                        InputStream inputStream = httpURLConnection.getInputStream();
                        Log.i("start_inputStream==>", "" + inputStream);
                        Log.i("mid_inputStream==>", "" + inputStream);
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        StringBuilder stringBuilder = new StringBuilder();
                        String str;
                        while ((str = bufferedReader.readLine()) != null) {
                            stringBuilder.append(str);
                        }
                        Log.i("end_inputStream==>", "" + inputStream);
                        reasonResult = stringBuilder.toString();
                        Log.i("taskResult=====>", reasonResult);
                        JSONObject jsonObject = new JSONObject(reasonResult);
                        if (!jsonObject.optString("total", "").equals("0")) {
                            handler.sendEmptyMessage(8);
                        } else {
                            handler.sendEmptyMessage(9);
                        }
                    } else {
                        try {
                            Thread.sleep(3000);
                            handler.sendEmptyMessage(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.i("IOException==========>", "网络请求异常!");
                    handler.sendEmptyMessage(1);
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(SecurityChooseActivity.this, "网络请求超时！", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    try {
                        JSONObject jsonObject = new JSONObject(stateResult);
                        JSONArray jsonArray = jsonObject.getJSONArray("rows");
                        for (int j = 0; j < jsonArray.length(); j++) {
                            stateObject = jsonArray.getJSONObject(j);
                            insertSecurityState();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    Toast.makeText(SecurityChooseActivity.this, "没有任务状态信息！", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    try {
                        JSONObject jsonObject = new JSONObject(contentResult);
                        JSONArray jsonArray = jsonObject.getJSONArray("rows");
                        for (int j = 0; j < jsonArray.length(); j++) {
                            contentObject = jsonArray.getJSONObject(j);
                            insertSecurityContent();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 5:
                    Toast.makeText(SecurityChooseActivity.this, "没有安检内容信息！", Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    try {
                        JSONObject jsonObject = new JSONObject(hiddenResult);
                        JSONArray jsonArray = jsonObject.getJSONArray("rows");
                        for (int j = 0; j < jsonArray.length(); j++) {
                            hiddenObject = jsonArray.getJSONObject(j);
                            insertSecurityHidden();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 7:
                    Toast.makeText(SecurityChooseActivity.this, "没有安全隐患信息！", Toast.LENGTH_SHORT).show();
                    break;
                case 8:
                    try {
                        JSONObject jsonObject = new JSONObject(reasonResult);
                        JSONArray jsonArray = jsonObject.getJSONArray("rows");
                        for (int j = 0; j < jsonArray.length(); j++) {
                            reasonObject = jsonArray.getJSONObject(j);
                            insertSecurityHiddenReason();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 9:
                    Toast.makeText(SecurityChooseActivity.this, "没有安全隐患原因信息！", Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    //安检状态数据存到本地数据库安检状态表
    private void insertSecurityState() {
        ContentValues values = new ContentValues();
        values.put("securityId", stateObject.optInt("securityId", 0) + "");
        values.put("securityName", stateObject.optString("securityName", ""));
        db.insert("SecurityState", null, values);
    }

    //安检内容数据存到本地数据库安检内容表
    private void insertSecurityContent() {
        ContentValues values = new ContentValues();
        values.put("securityId", contentObject.optInt("securityId", 0) + "");
        values.put("securityName", contentObject.optString("securityName", ""));
        db.insert("security_content", null, values);
    }

    //安检隐患类型数据存到本地数据库安检内容表
    private void insertSecurityHidden() {
        ContentValues values = new ContentValues();
        values.put("n_safety_hidden_id", hiddenObject.optInt("n_safety_hidden_id", 0) + "");
        values.put("n_safety_hidden_name", hiddenObject.optString("n_safety_hidden_name", ""));
        db.insert("security_hidden", null, values);
    }

    //安检隐患原因数据存到本地数据库安检内容表
    private void insertSecurityHiddenReason() {
        ContentValues values = new ContentValues();
        values.put("n_safety_hidden_reason_id", reasonObject.optInt("n_safety_hidden_reason_id", 0) + "");
        values.put("n_safety_hidden_id", reasonObject.optInt("n_safety_hidden_id", 0) + "");
        values.put("n_safety_hidden_reason_name", reasonObject.optString("n_safety_hidden_reason_name", ""));
        db.insert("security_hidden_reason", null, values);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //释放和数据库的连接
        db.close();
    }

}
