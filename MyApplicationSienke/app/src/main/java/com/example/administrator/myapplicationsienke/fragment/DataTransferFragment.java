package com.example.administrator.myapplicationsienke.fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.activity.UploadActivity;
import com.example.administrator.myapplicationsienke.mode.HttpUtils;
import com.example.administrator.myapplicationsienke.mode.MySqliteHelper;
import com.example.administrator.myapplicationsienke.model.TaskChoose;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/3/16 0016.
 */
public class DataTransferFragment extends Fragment {
    private View view, popupwindowView, uploadView;
    private TextView upload, download, progressName, progressPercent, tips;
    private LinearLayout rootLinearlayout, linearlayoutDown;
    private Button finishBtn;
    private RadioButton cancelRb, saveRb;
    private ImageView downFailed;
    private String taskResult, userResult, stateResult, contentResult, hiddenResult, reasonResult; //网络请求结果
    private SharedPreferences sharedPreferences, sharedPreferences_login;
    private SharedPreferences.Editor editor;
    private String ip, port;  //接口ip地址   端口
    public int responseCode = 0;
    public int asyncResponseCode = 0;
    private LayoutInflater layoutInflater;
    private PopupWindow popupWindow;
    private AnimationDrawable animationDrawable;
    private JSONObject taskObject, userObject, stateObject, contentObject, hiddenObject, reasonObject;
    private SQLiteDatabase db;  //数据库
    private int totalCount = 0;  //总户数
    private List<String> taskNumbList = new ArrayList<>();
    private ProgressBar downloadProgress;  //下载进度条
    private int currentProgress = 0;
    private int currentUserPercent = 0;
    private int currentPercent = 0;
    private int userProgress = 0;
    private JSONArray jsonArray;
    private long lastClickTime = 0;

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
                    upload.setClickable(false);
                    if(sharedPreferences.getBoolean("have_download",false)){
                        createSavePopupwindow();
                    }else {
                        Intent intent = new Intent(getActivity(), UploadActivity.class);
                        startActivity(intent);
                    }
                    break;
                case R.id.download:
                    if (!sharedPreferences.getBoolean("have_download", false)) {
                        download.setClickable(false);
                        /*if(isFastDoubleClick()){
                            Toast.makeText(getActivity(), "您点击太频繁了！", Toast.LENGTH_SHORT).show();
                        }*/
                        //开启支线程进行请求任务信息
                        new Thread() {
                            @Override
                            public void run() {
                                requireMyTask("SafeCheckPlan.do", "safePlanMember=");
                                requireSecurityState("findSecurityState.do "); //安检状态
                                requireSecurityContent("findSecurityContent.do");//安检内容
                                requireSafetyHidden("findSafetyHidden.do");//安检原因类型
                                requireSafetyReason("findSafetyReason.do");//安检原因
                                super.run();
                            }
                        }.start();
                    } else {
                        Toast.makeText(getActivity(), "上传数据之后才能再次下载任务哦！", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    //初始化设置
    private void defaultSetting() {
        sharedPreferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        sharedPreferences_login = getActivity().getSharedPreferences("login_info", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        MySqliteHelper helper = new MySqliteHelper(getActivity(), 1);
        db = helper.getWritableDatabase();
    }

    //弹出上传前提示popupwindow
    public void createSavePopupwindow() {
        layoutInflater = LayoutInflater.from(getActivity());
        uploadView = layoutInflater.inflate(R.layout.popupwindow_user_detail_info_save, null);
        popupWindow = new PopupWindow(uploadView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        //绑定控件ID
        tips = (TextView) uploadView.findViewById(R.id.tips);
        cancelRb = (RadioButton) uploadView.findViewById(R.id.cancel_rb);
        saveRb = (RadioButton) uploadView.findViewById(R.id.save_rb);
        //设置点击事件
        tips.setText("请确保你的任务完成哦！");
        saveRb.setText("确认完成");
        cancelRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                upload.setClickable(true);
            }
        });
        saveRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                Intent intent = new Intent(getActivity(), UploadActivity.class);
                startActivity(intent);
                upload.setClickable(true);
            }
        });
        popupWindow.update();
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
        popupWindow.setAnimationStyle(R.style.camera);
        backgroundAlpha(0.8F);   //背景变暗
        popupWindow.showAtLocation(rootLinearlayout, Gravity.CENTER, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                upload.setClickable(true);
                backgroundAlpha(1.0F);
            }
        });
    }

    //show下载popupwindow
    public void showPopupwindow() {
        layoutInflater = LayoutInflater.from(getActivity());
        popupwindowView = layoutInflater.inflate(R.layout.popupwindow_download_progressbar, null);
        popupWindow = new PopupWindow(popupwindowView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearlayoutDown = (LinearLayout) popupwindowView.findViewById(R.id.linearlayout_down);
        downFailed = (ImageView) popupwindowView.findViewById(R.id.down_failed);
        finishBtn = (Button) popupwindowView.findViewById(R.id.finish_btn);
        downloadProgress = (ProgressBar) popupwindowView.findViewById(R.id.download_progress);
        progressName = (TextView) popupwindowView.findViewById(R.id.progress_name);
        progressPercent = (TextView) popupwindowView.findViewById(R.id.progress_percent);
        progressName.setText("任务正在下载，请稍后...");
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadProgress.setProgress(0);
                download.setClickable(true);
                popupWindow.dismiss();
            }
        });
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
        popupWindow.showAtLocation(rootLinearlayout, Gravity.CENTER, 0, 0);
        backgroundAlpha(0.8F);   //背景变暗
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0F);
            }
        });
    }

    //设置背景透明度
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getActivity().getWindow().setAttributes(lp);
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
                        url = new URL(httpUrl + "?" + keyAndValue + URLEncoder.encode(sharedPreferences_login.getString("user_name", ""), "UTF-8"));
                        //没有参数传递
                    } else {
                        url = new URL(httpUrl);
                    }
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
                        taskResult = stringBuilder.toString();
                        Log.i("taskResult=====>", taskResult);
                        JSONObject jsonObject = new JSONObject(taskResult);
                        if (!jsonObject.optString("total", "").equals("0")) {
                            handler.sendEmptyMessage(1);
                        } else {
                            handler.sendEmptyMessage(2);
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
                            handler.sendEmptyMessage(10);
                        } else {
                            handler.sendEmptyMessage(11);
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
                            handler.sendEmptyMessage(12);
                        } else {
                            handler.sendEmptyMessage(13);
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
                            handler.sendEmptyMessage(14);
                        } else {
                            handler.sendEmptyMessage(15);
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
                            handler.sendEmptyMessage(16);
                        } else {
                            handler.sendEmptyMessage(17);
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
    public class MyAsyncTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            //在execute(Params... params)被调用后立即执行，一般用来在执行后台任务前对UI做一些标记。
            Log.i("onPreExecute===>", "onPreExecute进来了！");
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {    //所有的耗时操作都在此时进行，不能进行UI操作
            URL url = null;
            try {
                url = new URL(params[0]);
                Log.i("doInBackground===>", "url=" + url);
                HttpURLConnection httpURLConnection;
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(6000);
                httpURLConnection.setReadTimeout(6000);
                httpURLConnection.connect();
                //传回的数据解析成String
                Log.i("asyncResponseCode=>", httpURLConnection.getResponseCode() + "");
                if ((asyncResponseCode = httpURLConnection.getResponseCode()) == 200) {
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
                        if (url.toString().contains(taskNumbList.get(0))) {
                            //有相应用户数据
                            editor.putBoolean("user_data", true);
                            editor.commit();
                            for (int i = 0; i < taskNumbList.size(); i++) {
                                if (sharedPreferences.getBoolean("user_data", true)) {
                                    if (i == 0) {
                                        Log.i("jsonArray==========>", "jsonArray==" + jsonArray.length());
                                        for (int j = 0; j < jsonArray.length(); j++) {
                                            try {
                                                taskObject = jsonArray.getJSONObject(j);
                                                insertTaskDataBase();
                                                editor.putInt("totalCount", totalCount);
                                                editor.commit();
                                                Log.i("totalCount==========>", "总户数=" + totalCount);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    try {
                                        Thread.sleep(200);
                                        userProgress += 10 * taskNumbList.size() / taskNumbList.size();
                                        currentUserPercent = (1000 * (i + 1)) / (10 * taskNumbList.size());
                                        Message msg = new Message();
                                        msg.what = 9;
                                        msg.arg1 = userProgress;
                                        msg.arg2 = currentUserPercent;
                                        Log.i("down_user_progress=>", " 循环次数为" + taskNumbList.size());
                                        Log.i("down_user_progress=>", " 更新进度条" + userProgress);
                                        Log.i("down_user_progress=>", " 下载进度: " + currentUserPercent);
                                        handler.sendMessage(msg);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    handler.sendEmptyMessage(4);
                                    break;
                                }
                            }
                            /*if(sharedPreferences.getBoolean("user_data",true)){    //下载完之后做相应处理
                                handler.sendEmptyMessage(10);
                            }*/
                        }
                        return userResult;
                    } else {
                        editor.putBoolean("user_data", false);
                        editor.commit();
                        if (url.toString().contains(taskNumbList.get(taskNumbList.size() - 1))) {
                            //没有相应用户数据
                        }
                    }
                } else {
                    if (url.toString().contains(taskNumbList.get(taskNumbList.size() - 1))) {
                        handler.sendEmptyMessage(6);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Log.i("IOException==========>", "网络请求异常!");
                if (url.toString().contains(taskNumbList.get(taskNumbList.size() - 1))) {
                    handler.sendEmptyMessage(3);
                }
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {   //更新类似于进度条的控件的进度效果
            super.onProgressUpdate(values);
            if (isCancelled()) {
                return;
            }
            downloadProgress.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {  //执行UI操作
            if (s != null) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("rows");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        userObject = jsonArray.getJSONObject(i);
                        Log.i("onPostExecute========>", "更新UI！");
                        insertUserDataBase();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            super.onPostExecute(s);
        }
    }

    //开始异步任务
    public void startAsyncTask() {
        Log.i("startAsyncTask========>", "异步任务进来了！");
        if (!sharedPreferences.getString("security_ip", "").equals("")) {
            ip = sharedPreferences.getString("security_ip", "");
            Log.i("sharedPreferences=ip=>", ip);
        } else {
            ip = "88.88.88.66:";
        }
        if (!sharedPreferences.getString("security_port", "").equals("")) {
            port = sharedPreferences.getString("security_port", "");
            Log.i("sharedPreferences=ip=>", port);
        } else {
            port = "8088";
        }
        new Thread() {
            @Override
            public void run() {
                String url;
                String httpUrl = "http://" + ip + port + "/SMDemo/" + "getUserCheck.do?" + "safetyPlan=";
                Log.i("startAsyncTask========>", "任务编号个数为：" + taskNumbList.size());
                currentUserPercent = 0;
                for (int i = 0; i < taskNumbList.size(); i++) {
                    MyAsyncTask myAsyncTask = new MyAsyncTask();
                    url = httpUrl + taskNumbList.get(i);
                    Log.i("startAsyncTask========>", url);
                    myAsyncTask.execute(url);
                }
            }
        }.start();
    }

    public void setProgress() {
        new Thread() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Thread.sleep(200);
                        currentProgress += 10 * jsonArray.length() / jsonArray.length();
                        currentPercent = (1000 * (i + 1)) / (10 * jsonArray.length());
                        Message msg = new Message();
                        msg.what = 7;
                        msg.arg1 = currentProgress;
                        msg.arg2 = currentPercent;
                        handler.sendMessage(msg);
                        Log.i("down_task_progress=>", " 更新进度条" + currentProgress);
                        Log.i("down_task_progress=>", " 下载进度: " + currentPercent);
                    }
                    Thread.sleep(500);
                    handler.sendEmptyMessage(8);
                    startAsyncTask();//开启异步任务获取所有任务编号的用户数据
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 防止重复点击
     * @return
     */
    private boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeDistance = time - lastClickTime;
        if (timeDistance > 0 && timeDistance < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    try {
                        JSONObject jsonObject = new JSONObject(taskResult);
                        jsonArray = jsonObject.getJSONArray("rows");
                        showPopupwindow();
                        downloadProgress.setMax(10 * jsonArray.length());
                        taskNumbList.clear();
                        for (int j = 0; j < jsonArray.length(); j++) {             //获取到任务的个数，用于后面下载相应的用户数据
                            taskObject = jsonArray.getJSONObject(j);
                            taskNumbList.add(taskObject.optInt("safetyplanId", 0) + "");
                            Log.i("taskNumbList====>", "一共有" + taskNumbList.size() + "个任务");
                        }
                        setProgress();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    download.setClickable(true);
                    Toast.makeText(getActivity(), "没有任务下载哦！", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    download.setClickable(true);
                    Toast.makeText(getActivity(), "网络请求超时！", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    progressName.setText("没有相应的用户数据哦！");
                    linearlayoutDown.setVisibility(View.GONE);
                    downloadProgress.setVisibility(View.GONE);
                    downFailed.setVisibility(View.VISIBLE);
                    finishBtn.setVisibility(View.VISIBLE);
                    //Toast.makeText(getActivity(), "没有相应的用户数据！", Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    //有相应的用户数据
                    break;
                case 6:
                    popupWindow.dismiss();
                    download.setClickable(true);
                    Toast.makeText(getActivity(), "用户信息请求网络超时！", Toast.LENGTH_SHORT).show();
                    break;
                case 7:
                    progressPercent.setText(String.valueOf(msg.arg2));
                    downloadProgress.setProgress(msg.arg1);
                    Log.i("down_progress=>", " 任务进度为：" + downloadProgress.getProgress());
                    break;
                case 8:
                    downloadProgress.setProgress(0);
                    progressName.setText("用户信息正在下载，请稍等...");
                    progressPercent.setText("0");
                    currentProgress = 0;
                    currentPercent = 0;
                    break;
                case 9:
                    progressPercent.setText(String.valueOf(msg.arg2));
                    downloadProgress.setProgress(msg.arg1);
                    if (downloadProgress.getProgress() == downloadProgress.getMax()) {
                        Log.i("down_progress=>", " 用户信息下载完成进来了！");
                        progressName.setText("数据下载完成！");
                        linearlayoutDown.setVisibility(View.GONE);
                        finishBtn.setVisibility(View.VISIBLE);
                        downFailed.setVisibility(View.GONE);
                        editor.putBoolean("have_download", true);   //下载之后必须上传才能再次下载
                        editor.commit();
                        userProgress = 0;
                        currentUserPercent = 0;
                        download.setClickable(true);
                    }
                    break;
                case 10:
                    try {
                        JSONObject jsonObject = new JSONObject(stateResult);
                        JSONArray jsonArray = jsonObject.getJSONArray("rows");
                        for (int j = 0; j < jsonArray.length(); j++) {
                            stateObject = jsonArray.getJSONObject(j);
                            insertSecurityState();
                        }
                        download.setClickable(true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 11:
                    download.setClickable(true);
                    Toast.makeText(getActivity(), "没有任务状态信息！", Toast.LENGTH_SHORT).show();
                    break;
                case 12:
                    try {
                        JSONObject jsonObject = new JSONObject(contentResult);
                        JSONArray jsonArray = jsonObject.getJSONArray("rows");
                        for (int j = 0; j < jsonArray.length(); j++) {
                            contentObject = jsonArray.getJSONObject(j);
                            insertSecurityContent();
                        }
                        download.setClickable(true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 13:
                    download.setClickable(true);
                    Toast.makeText(getActivity(), "没有安检内容信息！", Toast.LENGTH_SHORT).show();
                    break;
                case 14:
                    try {
                        JSONObject jsonObject = new JSONObject(hiddenResult);
                        JSONArray jsonArray = jsonObject.getJSONArray("rows");
                        for (int j = 0; j < jsonArray.length(); j++) {
                            hiddenObject = jsonArray.getJSONObject(j);
                            insertSecurityHidden();
                        }
                        download.setClickable(true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 15:
                    download.setClickable(true);
                    Toast.makeText(getActivity(), "没有安全隐患信息！", Toast.LENGTH_SHORT).show();
                    break;
                case 16:
                    try {
                        JSONObject jsonObject = new JSONObject(reasonResult);
                        JSONArray jsonArray = jsonObject.getJSONArray("rows");
                        for (int j = 0; j < jsonArray.length(); j++) {
                            reasonObject = jsonArray.getJSONObject(j);
                            insertSecurityHiddenReason();
                        }
                        download.setClickable(true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 17:
                    download.setClickable(true);
                    Toast.makeText(getActivity(), "没有安全隐患原因信息！", Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    //读取下载到本地的任务数据
    public void getTaskData() {
        Cursor cursor = db.query("Task", null, null, null, null, null, null);//查询并获得游标
        //如果游标为空，则显示没有数据图片
        if (cursor.getCount() == 0) {
            return;
        }
        while (cursor.moveToNext()) {
            TaskChoose taskChoose = new TaskChoose();
            taskChoose.setTaskName(cursor.getString(1));
            taskChoose.setTaskNumber(cursor.getString(2));
            taskChoose.setCheckType(cursor.getString(3));
            taskChoose.setTotalUserNumber(cursor.getString(4));
            taskChoose.setEndTime(cursor.getString(5));
        }
        //cursor游标操作完成以后,一定要关闭
        cursor.close();
    }

    //任务数据存到本地数据库任务表
    private void insertTaskDataBase() {
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
        db.insert("Task", null, values);
    }

    //用户信息数据存到本地数据库用户表
    private void insertUserDataBase() {
        ContentValues values = new ContentValues();
        values.put("securityNumber", userObject.optString("safetyInspectionId", ""));
        values.put("userName", userObject.optString("userName", ""));
        values.put("meterNumber", userObject.optString("meterNumber", ""));
        values.put("userPhone", userObject.optString("userPhone", ""));
        values.put("securityType", userObject.optString("securityName", ""));
        values.put("oldUserId", userObject.optString("oldUserId", ""));
        Log.i("data_down_load", "下载的用户ID为：" + userObject.optString("oldUserId", ""));
        values.put("newUserId", userObject.optString("userId", ""));
        values.put("userAddress", userObject.optString("userAdress", ""));
        values.put("taskId", userObject.optInt("safetyPlan", 0) + "");
        values.put("ifChecked", "false");
        values.put("security_content","");
        values.put("newMeterNumber","");
        values.put("remarks","");
        values.put("security_hidden","");
        values.put("security_hidden_reason","");
        values.put("photoNumber","0");
        values.put("ifUpload","false");
        values.put("currentTime","");
        // 第一个参数:表名称
        // 第二个参数：SQl不允许一个空列，如果ContentValues是空的，那么这一列被明确的指明为NULL值
        // 第三个参数：ContentValues对象
        db.insert("User", null, values);
    }

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
