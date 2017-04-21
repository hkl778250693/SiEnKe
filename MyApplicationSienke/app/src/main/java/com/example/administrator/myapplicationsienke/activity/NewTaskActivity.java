package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.mode.MySqliteHelper;
import com.example.administrator.myapplicationsienke.model.NewTaskListviewItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Administrator on 2017/3/15.
 */
public class NewTaskActivity extends Activity {
    private TextView securityType;// 安检类型
    private EditText taskName;//安检名称
    private TextView startTime;//开始日期选择器
    private TextView endTime;//结束日期选择器
    private String ip, port;  //接口ip地址   端口
    private String result; //网络请求结果
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private SQLiteDatabase db;  //数据库
    public int responseCode = 0;
    private int year;
    private int month;
    private int day;
    private int year1;
    private int month1;
    private int day1;
    private RadioButton commonSecurityCheck, yearPlan, reCheck, passGasSecurityCheck;
    private LayoutInflater inflater;  //转换器
    private ProgressBar progressBar;  //下载进度条
    private TextView progressName, progressPercent;
    private View securityHiddenreasonView;
    private LinearLayout linearlayoutDown;
    private PopupWindow popupWindow;
    private ImageView newTaskBack;
    private Button newPlanAddBtn;
    private Button save_btn, finishBtn;
    private View view;
    private ImageView frameAnimation;
    private AnimationDrawable animationDrawable;
    private LinearLayout rootLinearlayout;
    private ArrayList<NewTaskListviewItem> parclebleList = new ArrayList<>();
    private String resultTaskId;   //新增任务点保存时从服务器返回的任务编号
    private String userResult; //网络请求结果
    private JSONObject object;
    private int currentProgress = 0;
    private int currentPercent = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        bindView();//绑定控件
        defaultSetting();//初始化设置
        setViewClickListener();//点击事件
    }


    //强制竖屏
    @Override
    protected void onResume() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
    }


    //绑定控件
    private void bindView() {
        newTaskBack = (ImageView) findViewById(R.id.newtask_back);
        newPlanAddBtn = (Button) findViewById(R.id.newplan_add_btn);
        taskName = (EditText) findViewById(R.id.task_name);
        securityType = (TextView) findViewById(R.id.security_type);
        startTime = (TextView) findViewById(R.id.data);
        endTime = (TextView) findViewById(R.id.data1);
        save_btn = (Button) findViewById(R.id.save_btn);
        rootLinearlayout = (LinearLayout) findViewById(R.id.root_linearlayout);
    }

    //点击事件
    private void setViewClickListener() {
        newTaskBack.setOnClickListener(onClickListener);
        newPlanAddBtn.setOnClickListener(onClickListener);
        taskName.setOnClickListener(onClickListener);
        securityType.setOnClickListener(onClickListener);
        startTime.setOnClickListener(onClickListener);
        endTime.setOnClickListener(onClickListener);
        save_btn.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.newtask_back:
                    NewTaskActivity.this.finish();
                    break;
                case R.id.newplan_add_btn:
                    Intent intent1 = new Intent(NewTaskActivity.this, NewTaskDetailActivity.class);
                    startActivityForResult(intent1, 100);
                    break;
                case R.id.save_btn:
                    if (parclebleList.size() != 0) {
                        new Thread() {
                            @Override
                            public void run() {
                                postMyTask();
                            }
                        }.start();
                    } else {
                        Toast.makeText(NewTaskActivity.this, "请添加用户数据哦！", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.security_type:
                    createSecurityTypePopupwindow();
                    break;
                case R.id.data:
                    Calendar cale1 = Calendar.getInstance();
                    year = cale1.get(Calendar.YEAR);
                    month = cale1.get(Calendar.MONTH);
                    day = cale1.get(Calendar.DAY_OF_MONTH);
                    startTime.setText(year + "-" + (month + 1) + "-" + day);
                    //开始时间选择器
                    new DatePickerDialog(NewTaskActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int myyear, int monthOfYear,
                                              int dayOfMonth) {
                            year = myyear;
                            month = monthOfYear;
                            day = dayOfMonth;
                            updateDate();
                        }

                        private void updateDate() {
                            startTime.setText(year + "-" + (month + 1) + "-" + day);
                        }
                    }
                            , cale1.get(Calendar.YEAR)
                            , cale1.get(Calendar.MONTH)
                            , cale1.get(Calendar.DAY_OF_MONTH)).show();
                    break;
                case R.id.data1:
                    Calendar cale2 = Calendar.getInstance();
                    year1 = cale2.get(Calendar.YEAR);
                    month1 = cale2.get(Calendar.MONTH);
                    day1 = cale2.get(Calendar.DAY_OF_MONTH);
                    endTime.setText(year + "-" + (month + 1) + "-" + day);
                    //结束时间选择器
                    new DatePickerDialog(NewTaskActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int myyear, int monthOfYear,
                                              int dayOfMonth) {
                            year = myyear;
                            month = monthOfYear;
                            day = dayOfMonth;
                            updateDate();
                        }

                        private void updateDate() {
                            endTime.setText(year + "-" + (month + 1) + "-" + day);
                        }
                    }
                            , cale2.get(Calendar.YEAR)
                            , cale2.get(Calendar.MONTH)
                            , cale2.get(Calendar.DAY_OF_MONTH)).show();
                    break;
            }
        }
    };

    //初始化设置
    private void defaultSetting() {
        sharedPreferences = NewTaskActivity.this.getSharedPreferences("data", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        MySqliteHelper helper = new MySqliteHelper(NewTaskActivity.this, 1);
        db = helper.getWritableDatabase();
    }


    //弹出安检类型popupwindow
    public void createSecurityTypePopupwindow() {
        inflater = LayoutInflater.from(NewTaskActivity.this);
        securityHiddenreasonView = inflater.inflate(R.layout.popupwindow_security_type, null);
        popupWindow = new PopupWindow(securityHiddenreasonView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //绑定控件ID
        commonSecurityCheck = (RadioButton) securityHiddenreasonView.findViewById(R.id.common_security_check);
        yearPlan = (RadioButton) securityHiddenreasonView.findViewById(R.id.year_plan);
        reCheck = (RadioButton) securityHiddenreasonView.findViewById(R.id.recheck);
        passGasSecurityCheck = (RadioButton) securityHiddenreasonView.findViewById(R.id.pass_gas_security_check);
        //设置点击事件
        commonSecurityCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityType.setText(commonSecurityCheck.getText());
            }
        });
        yearPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityType.setText(yearPlan.getText());
            }
        });
        reCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityType.setText(reCheck.getText());
            }
        });
        passGasSecurityCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityType.setText(passGasSecurityCheck.getText());
            }
        });
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popupwindow_spinner_shape));
        popupWindow.setAnimationStyle(R.style.Popupwindow);
        backgroundAlpha(0.8F);   //背景变暗
        popupWindow.showAsDropDown(securityType, 260, 0);
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

    //请求网络数据
    private void postMyTask() {
        new Thread() {
            @Override
            public void run() {
                try {
                    //请求的地址
                    if (!sharedPreferences.getString("security_ip", "").equals("")) {
                        ip = sharedPreferences.getString("security_ip", "");
                    } else {
                        ip = "88.88.88.66:";
                    }
                    if (!sharedPreferences.getString("security_port", "").equals("")) {
                        port = sharedPreferences.getString("security_port", "");
                    } else {
                        port = "8088";
                    }
                    String httpUrl = "http://" + ip + port + "/SMDemo/addSafePlan.do";
                    Log.i("postMyTask_url====>", "" + httpUrl);
                    // 根据地址创建URL对象
                    URL url = new URL(httpUrl);
                    // 根据URL对象打开链接
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    // 发送POST请求必须设置允许输出
                    urlConnection.setDoOutput(true);
                    urlConnection.setDoInput(true);
                    urlConnection.setUseCaches(false);//不使用缓存
                    // 设置请求的方式
                    urlConnection.setRequestMethod("POST");
                    // 设置请求的超时时间
                    urlConnection.setReadTimeout(8000);
                    urlConnection.setConnectTimeout(8000);
                    // 传递的数据
                    String data = dataToJson();
                    Log.i("postMyTask_data=>", "data=" + data);
                    // 设置请求的头
                    urlConnection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
                    urlConnection.setRequestProperty("Content-Type", "applicaton/json;charset=UTF-8");
                    //urlConnection.setRequestProperty("Origin", "http://"+ ip + port);
                    //urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");
                    //获取输出流
                    OutputStream os = urlConnection.getOutputStream();
                    os.write(data.getBytes("UTF-8"));
                    os.flush();
                    os.close();
                    Log.i("getResponseCode====>", "" + urlConnection.getResponseCode());
                    if ((responseCode = urlConnection.getResponseCode()) == 200) {
                        InputStream inputStream = urlConnection.getInputStream();
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        StringBuilder stringBuilder = new StringBuilder();
                        String str;
                        while ((str = bufferedReader.readLine()) != null) {
                            stringBuilder.append(str);
                        }
                        // 释放资源
                        inputStream.close();
                        // 返回字符串
                        result = stringBuilder.toString();
                        Log.i("postMyTask_result====>", result);
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.optString("message", "").equals("保存成功！")) {
                            handler.sendEmptyMessage(1);
                        } else {
                            handler.sendEmptyMessage(2);
                        }
                    } else {
                        handler.sendEmptyMessage(3);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.i("IOException==========>", "网络请求异常!");
                    handler.sendEmptyMessage(2);
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.run();
            }
        }.start();
    }

    //将数据转换成Json格式
    public String dataToJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject object = new JSONObject();
            object.put("c_safety_plan_name", taskName.getText().toString());      //安检任务名称
            object.put("c_safety_plan_member", "杜述洪");    //操作员
            object.put("d_safety_start", startTime.getText().toString());       //开始时间
            object.put("d_safety_end", endTime.getText().toString());    //结束时间
            object.put("n_company_id", Integer.parseInt(sharedPreferences.getString("company_id", "")));       //公司id
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < parclebleList.size(); i++) {
                JSONObject object1 = new JSONObject();
                object1.put("c_user_id", parclebleList.get(i).getUserId());
                object1.put("n_data_state", 1);
                object1.put("n_safety_state", 1);
                object1.put("n_safety_date_type", 0);
                object1.put("c_safety_type", 2 + "");       //安检类型
                jsonArray.put(i, object1);
            }
            jsonObject.put("safetyInspection", jsonArray);
            jsonObject.put("safetyPlan", object);
            Log.i("dataToJson==========>", "封装的json数据为：" + jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    //将新增的任务数据存到本地数据库任务表
    private void insertTaskDataBase() {
        ContentValues values = new ContentValues();
        values.put("taskName", taskName.getText().toString());
        values.put("taskId", resultTaskId);
        values.put("securityType", securityType.getText().toString());
        values.put("totalCount", parclebleList.size());
        values.put("endTime", endTime.getText().toString());
        db.insert("Task", null, values);
    }

    //将添加的用户信息数据存到本地数据库用户表
    private void insertUserDataBase(String securityId) {
        ContentValues values = new ContentValues();
        for (int i = 0; i < parclebleList.size(); i++) {
            values.put("securityNumber", securityId);
            values.put("userName", parclebleList.get(i).getUserName());
            values.put("meterNumber", parclebleList.get(i).getNumber());
            values.put("userPhone", parclebleList.get(i).getPhoneNumber());
            values.put("securityType", securityType.getText().toString());
            values.put("oldUserId", object.optString("oldUserId", ""));
            values.put("newUserId", parclebleList.get(i).getUserId());
            values.put("userAddress", parclebleList.get(i).getAdress());
            values.put("taskId", resultTaskId);
            values.put("ifChecked", "false");
            db.insert("User", null, values);
        }
    }

    //根据数据库返回回来的任务编号去查询相应用户的安检编号
    private void requireSecurityId(final String method, final String keyAndValue) {
        new Thread() {
            @Override
            public void run() {
                try {
                    URL url;
                    HttpURLConnection httpURLConnection;
                    Log.i("sharedPreferences====>", sharedPreferences.getString("IP", ""));
                    if (!sharedPreferences.getString("security_ip", "").equals("")) {
                        ip = sharedPreferences.getString("security_ip", "");
                    } else {
                        ip = "88.88.88.66:";
                    }
                    if (!sharedPreferences.getString("security_port", "").equals("")) {
                        port = sharedPreferences.getString("security_port", "");
                    } else {
                        port = "8088";
                    }
                    String httpUrl = "http://" + ip + port + "/SMDemo/" + method;
                    //有参数传递
                    if (!keyAndValue.equals("")) {
                        url = new URL(httpUrl + "?" + keyAndValue);
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
                        userResult = stringBuilder.toString();
                        Log.i("taskResult=====>", userResult);
                        JSONObject jsonObject = new JSONObject(userResult);
                        if (!jsonObject.optString("total", "").equals("0")) {
                            handler.sendEmptyMessage(4);
                        } else {
                            handler.sendEmptyMessage(5);
                        }
                    } else {
                        try {
                            Thread.sleep(3000);
                            handler.sendEmptyMessage(6);
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
                    handler.sendEmptyMessage(6);
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //show弹出框
    public void showPopupwindow() {
        inflater = LayoutInflater.from(NewTaskActivity.this);
        view = inflater.inflate(R.layout.popupwindow_download_progressbar, null);
        popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearlayoutDown = (LinearLayout) view.findViewById(R.id.linearlayout_down);
        finishBtn = (Button) view.findViewById(R.id.finish_btn);
        progressBar = (ProgressBar) view.findViewById(R.id.download_progress);
        progressName = (TextView) view.findViewById(R.id.progress_name);
        progressPercent = (TextView) view.findViewById(R.id.progress_percent);
        progressBar.setMax(50);
        progressName.setText("任务正在保存，请稍后...");
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setProgress(0);
                popupWindow.dismiss();
                NewTaskActivity.this.finish();
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


    public void setProgress() {
        new Thread() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 5; i++) {
                        Thread.sleep(300);
                        currentProgress += 10 * 5 / 5;
                        currentPercent = (1000 * (i + 1)) / (10 * 5);
                        Message msg = new Message();
                        msg.what = 7;
                        msg.arg1 = currentProgress;
                        msg.arg2 = currentPercent;
                        handler.sendMessage(msg);
                        Log.i("upload_task_progress=>", " 更新进度条" + currentProgress);
                        Log.i("upload_task_progress=>", " 下载进度: " + currentPercent);
                    }
                    handler.sendEmptyMessage(8);
                } catch (InterruptedException e) {
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
                    try {
                        JSONObject taskObject = new JSONObject(result);
                        resultTaskId = taskObject.optInt("safetyPlanId", 0) + "";
                        requireSecurityId("getUserCheck.do", "safetyPlan=" + resultTaskId);
                        insertTaskDataBase();  //将新增的任务数据存到本地数据库任务表
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    Toast.makeText(NewTaskActivity.this, "任务新增失败！", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(NewTaskActivity.this, "网络错误！", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    try {
                        showPopupwindow();
                        setProgress();
                        JSONObject userObject = new JSONObject(userResult);
                        JSONArray array = userObject.getJSONArray("rows");
                        for (int i = 0; i < array.length(); i++) {
                            object = array.getJSONObject(i);
                            insertUserDataBase(object.optInt("safetyInspectionId", 0) + "");  //将添加的用户信息数据存到本地数据库用户表
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 5:
                    Toast.makeText(NewTaskActivity.this, "任务编号请求数据为空！", Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    Toast.makeText(NewTaskActivity.this, "任务编号请求数据错误！", Toast.LENGTH_SHORT).show();
                    break;
                case 7:
                    progressPercent.setText(String.valueOf(msg.arg2));
                    progressBar.setProgress(msg.arg1);
                    Log.i("down_progress=>", " 任务进度为：" + progressBar.getProgress());
                    break;
                case 8:
                    progressName.setText("任务新增成功！");
                    linearlayoutDown.setVisibility(View.GONE);
                    finishBtn.setVisibility(View.VISIBLE);
                    currentProgress = 0;
                    currentPercent = 0;
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                if (data != null) {
                    parclebleList = data.getParcelableArrayListExtra("parclebleList");
                }
            }
        }
    }
}
