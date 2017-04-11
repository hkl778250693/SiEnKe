package com.example.administrator.myapplicationsienke.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.activity.UploadActivity;
import com.example.administrator.myapplicationsienke.mode.MySqliteHelper;
import com.example.administrator.myapplicationsienke.model.DownloadListvieItem;

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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/16 0016.
 */
public class DataTransferFragment extends Fragment {
    private View view;
    private TextView upload, download;
    private LinearLayout rootLinearlayout;
    private String taskResult, userResult; //网络请求结果
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String ip, port;  //接口ip地址   端口
    public int responseCode = 0;
    private LayoutInflater layoutInflater;
    private PopupWindow popupWindow;
    private ImageView frameAnimation;
    private AnimationDrawable animationDrawable;
    private List<DownloadListvieItem> downloadListvieItemList = new ArrayList<>();
    private JSONObject taskObject, userObject;
    private SQLiteDatabase db;  //数据库
    private int totalCount = 0;  //总户数
    List<String> taskNumbList = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_data_transfer, null);

        bindView(); //绑定控件ID
        defaultSetting();//初始化设置
        setViewClickListener();//点击事件
        return view;
    }

    //绑定控件
    public void bindView() {
        upload = (TextView) view.findViewById(R.id.upload);
        download = (TextView) view.findViewById(R.id.download);
        rootLinearlayout = (LinearLayout) view.findViewById(R.id.root_linearlayout);
    }

    //点击事件
    private void setViewClickListener() {
        upload.setOnClickListener(clickListener);
        download.setOnClickListener(clickListener);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.upload:
                    Intent intent = new Intent(getActivity(), UploadActivity.class);
                    startActivity(intent);
                    break;
                case R.id.download:
                    showPopupwindow();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //开启支线程进行请求任务信息
                    new Thread() {
                        @Override
                        public void run() {
                            requireMyTask("SafeCheckPlan.do","safePlanMember=");
                            super.run();
                        }
                    }.start();
                    break;
            }
        }
    };

    //初始化设置
    private void defaultSetting() {
        sharedPreferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        MySqliteHelper helper = new MySqliteHelper(getActivity(), 1);
        db = helper.getWritableDatabase();
    }

    //show弹出框
    public void showPopupwindow() {
        layoutInflater = LayoutInflater.from(getActivity());
        view = layoutInflater.inflate(R.layout.popupwindow_query_loading, null);
        popupWindow = new PopupWindow(view, 250, 250);
        frameAnimation = (ImageView) view.findViewById(R.id.frame_animation);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.loading_shape));
        popupWindow.setAnimationStyle(R.style.dialog);
        popupWindow.update();
        popupWindow.showAtLocation(rootLinearlayout, Gravity.CENTER, 0, 0);
        backgroundAlpha(0.8F);   //背景变暗
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0F);
            }
        });
        //开始加载动画
        startFrameAnimation();
    }

    //设置背景透明度
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getActivity().getWindow().setAttributes(lp);
    }

    //开始帧动画
    public void startFrameAnimation() {
        frameAnimation.setBackgroundResource(R.drawable.frame_animation_list);
        animationDrawable = (AnimationDrawable) frameAnimation.getDrawable();
        animationDrawable.start();
    }

    //请求网络数据
    private void requireMyTask(final String method, final String keyAndValue) {
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
                    //有参数传递
                    if (!keyAndValue.equals("")) {
                        url = new URL(httpUrl + "?" + keyAndValue + URLEncoder.encode("杜述洪","UTF-8"));
                        //没有参数传递
                    } else {
                        url = new URL(httpUrl);
                    }
                    Log.i("url=============>", url + "");
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setConnectTimeout(6000);
                    httpURLConnection.setReadTimeout(6000);
                    httpURLConnection.connect();
                    //传回的数据解析成String
                    Log.i("responseCode====>", httpURLConnection.getResponseCode() + "");
                    if ((responseCode = httpURLConnection.getResponseCode()) == 200) {
                        InputStream inputStream = httpURLConnection.getInputStream();
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        StringBuilder stringBuilder = new StringBuilder();
                        String str;
                        while ((str = bufferedReader.readLine()) != null) {
                            stringBuilder.append(str);
                        }
                        taskResult = stringBuilder.toString();
                        Log.i("taskResult=====>",taskResult);
                        JSONObject jsonObject = new JSONObject(taskResult);
                        if (!jsonObject.optString("total", "").equals("0")) {
                            handler.sendEmptyMessage(1);
                        } else {
                            try {
                                Thread.sleep(3000);
                                handler.sendEmptyMessage(2);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            Thread.sleep(3000);
                            handler.sendEmptyMessage(3);
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
                    handler.sendEmptyMessage(3);
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //自定义异步任务
                                     //启动任务的参数，进度参数，结果参数
    public class MyAsyncTask extends AsyncTask<String,Integer,String>{
        @Override
        protected void onPreExecute() {
            //在execute(Params... params)被调用后立即执行，一般用来在执行后台任务前对UI做一些标记。
            Log.i("onPreExecute===>","onPreExecute进来了！");
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {    //所有的耗时操作都在此时进行，不能进行UI操作
            try {
                URL url = new URL(params[0]);
                Log.i("doInBackground===>","url="+url);
                HttpURLConnection httpURLConnection;
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(6000);
                httpURLConnection.setReadTimeout(6000);
                httpURLConnection.connect();
                //传回的数据解析成String
                Log.i("userResultCode=>", httpURLConnection.getResponseCode() + "");
                if ((responseCode = httpURLConnection.getResponseCode()) == 200) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuilder stringBuilder = new StringBuilder();
                    String str;
                    while ((str = bufferedReader.readLine()) != null) {
                        stringBuilder.append(str);
                    }
                    userResult = stringBuilder.toString();
                    Log.i("userResult==========>", userResult);
                    JSONObject jsonObject = new JSONObject(userResult);
                    if (!jsonObject.optString("total", "").equals("0")) {
                        handler.sendEmptyMessage(4);
                    } else {
                        try {
                            Thread.sleep(3000);
                            handler.sendEmptyMessage(2);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    return userResult;
                } else {
                    try {
                        Thread.sleep(3000);
                        handler.sendEmptyMessage(3);
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
                handler.sendEmptyMessage(3);
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {   //更新类似于进度条的控件的进度效果
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {  //执行UI操作
            if(s != null){
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("rows");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        userObject = jsonArray.getJSONObject(i);
                        Log.i("onPostExecute========>", "更新UI！");
                        insertUserInfo();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "用户信息下载失败！", Toast.LENGTH_SHORT).show();
                }
            }
            super.onPostExecute(s);
        }
    }

    //开始异步任务
    public void startAsyncTask(){
        Log.i("startAsyncTask========>", "异步任务进来了！");
        if (!sharedPreferences.getString("security_ip", "").equals("")) {
            ip = sharedPreferences.getString("security_ip", "");
            Log.i("sharedPreferences=ip=>",ip);
        }else {
            ip = "88.88.88.66:";
        }
        if (!sharedPreferences.getString("security_port", "").equals("")) {
            port = sharedPreferences.getString("security_port", "");
            Log.i("sharedPreferences=ip=>",port);
        } else {
            port = "8088";
        }
        String httpUrl = "http://" + ip + port + "/SMDemo/" + "getUserCheck.do?"+"safetyPlan=";
        String url;
        Log.i("startAsyncTask========>", "任务编号个数为："+taskNumbList.size());
        for(int i = 0;i<taskNumbList.size();i++){
            MyAsyncTask myAsyncTask = new MyAsyncTask();
            url = httpUrl + taskNumbList.get(i);
            Log.i("startAsyncTask========>", url);
            myAsyncTask.execute(url);
        }
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    try {
                        JSONObject jsonObject = new JSONObject(taskResult);
                        JSONArray jsonArray = jsonObject.getJSONArray("rows");
                        Log.i("jsonArray==========>", "jsonArray=="+jsonArray.length());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            taskObject = jsonArray.getJSONObject(i);
                            insertTaskData();
                            taskNumbList.add(taskObject.optInt("safetyplanId",0)+"");
                        }
                        Log.i("taskNumbList====>", "一共有"+taskNumbList.size()+"个任务");
                        Thread.sleep(1000);
                        editor.putInt("totalCount",totalCount);
                        editor.commit();
                        Log.i("totalCount==========>", "总户数="+totalCount);
                        Toast.makeText(getActivity(), "任务下载完成，用户信息正在下载，请稍等...", Toast.LENGTH_SHORT).show();
                        startAsyncTask();//开启异步任务获取所有任务编号的用户数据
                        popupWindow.dismiss();
                        Toast.makeText(getActivity(), "用户信息下载完成！", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    popupWindow.dismiss();
                    Toast.makeText(getActivity(), "没有任务下载！", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    popupWindow.dismiss();
                    Toast.makeText(getActivity(), "网络请求超时！", Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    //将服务器下载的任务数据存到本地数据库任务表
    private void insertTaskData() {
        ContentValues values = new ContentValues();
        values.put("taskName", taskObject.optString("safetyPlanName", ""));
        values.put("taskId", taskObject.optInt("safetyplanId", 0) + "");
        values.put("securityType", taskObject.optString("securityName", ""));
        values.put("totalCount", taskObject.optInt("countRs", 0) + "");
        totalCount += taskObject.optInt("countRs", 0);
        values.put("endTime", taskObject.optString("safetyEnd", ""));
        // 第一个参数:表名称
        // 第二个参数：SQl不允许一个空列，如果ContentValues是空的，那么这一列被明确的指明为NULL值
        // 第三个参数：ContentValues对象
        db.insert("Task",null,values);
        Log.i("db==========>","task_db!"+db);
    }

    //将服务器下载的用户信息数据存到本地数据库用户表
    private void insertUserInfo() {
        ContentValues values = new ContentValues();
        values.put("securityNumber", userObject.optString("safetyInspectionId", ""));
        values.put("userName", userObject.optString("userName", ""));
        values.put("meterNumber", userObject.optString("meterNumber", ""));
        values.put("userPhone", userObject.optString("userPhone", ""));
        values.put("securityType", userObject.optString("securityName", ""));
        values.put("oldUserId", userObject.optString("oldUserId", ""));
        Log.i("data_down_load","下载的用户ID为："+userObject.optString("oldUserId", ""));
        values.put("newUserId", userObject.optString("userId", ""));
        values.put("userAddress", userObject.optString("userAdress", ""));
        values.put("taskId", userObject.optInt("safetyPlan", 0) + "");
        values.put("ifChecked", "false");
        // 第一个参数:表名称
        // 第二个参数：SQl不允许一个空列，如果ContentValues是空的，那么这一列被明确的指明为NULL值
        // 第三个参数：ContentValues对象
        db.insert("User", null, values);
        Log.i("db==========>", "user_db!" + db);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //释放和数据库的连接
        db.close();
    }
}
