package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2017/3/15.
 */
public class NewTaskActivity extends Activity {
    private TextView securityType;// 安检类型
    private EditText taskName;//安检名称
    private TextView date;//开始日期选择器
    private TextView date1;//结束日期选择器
    private String ip, port;  //接口ip地址   端口
    private String result; //网络请求结果
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private SQLiteDatabase db;  //数据库
    List<String> taskNumbList = new ArrayList<>();
    public int responseCode = 0;
    private int year;
    private int month;
    private int day;
    private int year1;
    private int month1;
    private int day1;
    private RadioButton notSecurityCheck, passSecurityCheck, notPassSecurityCheck, overSecurityCheckTime;
    private RadioButton indoorStandPipe, indoorBranchPipe, fuelGasMeter, burningAppliances, gasFacilitiesRoom, threeWayPipe;
    private LayoutInflater inflater;  //转换器
    private View securityCaseView, securityHiddenreasonView;
    private PopupWindow popupWindow;
    private ImageView newTaskBack;
    private Button newPlanAddBtn;
    private Button save_btn;
    private LayoutInflater layoutInflater;
    private View view;
    private ImageView frameAnimation;
    private AnimationDrawable animationDrawable;
    private LinearLayout rootLinearlayout;
    private ArrayList<NewTaskListviewItem> parclebleList =new ArrayList<>();

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
        date = (TextView) findViewById(R.id.data);
        date1 = (TextView) findViewById(R.id.data1);
        save_btn = (Button) findViewById(R.id.save_btn);
        rootLinearlayout = (LinearLayout) findViewById(R.id.root_linearlayout);

    }

    //点击事件
    private void setViewClickListener() {
        newTaskBack.setOnClickListener(onClickListener);
        newPlanAddBtn.setOnClickListener(onClickListener);
        taskName.setOnClickListener(onClickListener);
        securityType.setOnClickListener(onClickListener);
        date.setOnClickListener(onClickListener);
        date1.setOnClickListener(onClickListener);
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
                    startActivityForResult(intent1,100);
                    break;
                case R.id.save_btn:
                    new Thread(){
                        @Override
                        public void run() {
                            postMyTask();
                        }
                    }.start();
                    Toast.makeText(NewTaskActivity.this,"新增任务已保存",Toast.LENGTH_SHORT).show();
                    NewTaskActivity.this.finish();
                    break;
                case R.id.security_type:
                    createSecurityTypePopupwindow();
                    break;
                case R.id.data:
                    Calendar cale1 = Calendar.getInstance();
                    year = cale1.get(Calendar.YEAR);
                    month = cale1.get(Calendar.MONTH);
                    day = cale1.get(Calendar.DAY_OF_MONTH);
                    date.setText(year + "-" + (month + 1) + "-" + day);
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
                            date.setText(year + "-" + (month + 1) + "-" + day);
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
                    date1.setText(year + "-" + (month + 1) + "-" + day);
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
                            date1.setText(year + "-" + (month + 1) + "-" + day);
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


    //弹出安检区域popupwindow
    public void createSecurityCasePopupwindow() {
        inflater = LayoutInflater.from(NewTaskActivity.this);
        securityCaseView = inflater.inflate(R.layout.popupwidow_security_area, null);
        popupWindow = new PopupWindow(securityCaseView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //绑定控件ID
        notSecurityCheck = (RadioButton) securityCaseView.findViewById(R.id.not_security_check);
        passSecurityCheck = (RadioButton) securityCaseView.findViewById(R.id.pass_security_check);
        notPassSecurityCheck = (RadioButton) securityCaseView.findViewById(R.id.not_pass_security_check);
        overSecurityCheckTime = (RadioButton) securityCaseView.findViewById(R.id.over_security_check_time);
        //设置点击事件
        notSecurityCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                taskName.setText(notSecurityCheck.getText());
            }
        });
        passSecurityCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                taskName.setText(passSecurityCheck.getText());
            }
        });
        notPassSecurityCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                taskName.setText(notPassSecurityCheck.getText());
            }
        });
        overSecurityCheckTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                taskName.setText(overSecurityCheckTime.getText());
            }
        });
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
        popupWindow.setAnimationStyle(R.style.Popupwindow);
        backgroundAlpha(0.8F);   //背景变暗
        popupWindow.showAsDropDown(taskName, 600, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0F);
            }
        });
    }

    //弹出安全隐患类型popupwindow
    public void createSecurityTypePopupwindow() {
        inflater = LayoutInflater.from(NewTaskActivity.this);
        securityHiddenreasonView = inflater.inflate(R.layout.popupwindow_security_type, null);
        popupWindow = new PopupWindow(securityHiddenreasonView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //绑定控件ID
        indoorStandPipe = (RadioButton) securityHiddenreasonView.findViewById(R.id.indoor_stand_pipe);
        indoorBranchPipe = (RadioButton) securityHiddenreasonView.findViewById(R.id.indoor_branch_pipe);
        fuelGasMeter = (RadioButton) securityHiddenreasonView.findViewById(R.id.fuel_gas_meter);
        burningAppliances = (RadioButton) securityHiddenreasonView.findViewById(R.id.burning_appliances);
        gasFacilitiesRoom = (RadioButton) securityHiddenreasonView.findViewById(R.id.gas_facilities_room);
        threeWayPipe = (RadioButton) securityHiddenreasonView.findViewById(R.id.three_way_pipe);
        //设置点击事件
        indoorStandPipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityType.setText(indoorStandPipe.getText());
            }
        });
        indoorBranchPipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityType.setText(indoorBranchPipe.getText());
            }
        });
        fuelGasMeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityType.setText(fuelGasMeter.getText());
            }
        });
        burningAppliances.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityType.setText(burningAppliances.getText());
            }
        });
        gasFacilitiesRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityType.setText(gasFacilitiesRoom.getText());
            }
        });
        threeWayPipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityType.setText(threeWayPipe.getText());
            }
        });
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
        popupWindow.setAnimationStyle(R.style.Popupwindow);
        backgroundAlpha(0.8F);   //背景变暗
        popupWindow.showAsDropDown(securityType, 365, 0);
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
                        //Log.i("sharedPreferences=ip=>",ip);
                    } else {
                        ip = "88.88.88.31:";
                    }
                    if (!sharedPreferences.getString("security_port", "").equals("")) {
                        port = sharedPreferences.getString("security_port", "");
                        //Log.i("sharedPreferences=ip=>",port);
                    } else {
                        port = "8080";
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
                    if (urlConnection.getResponseCode() == 200) {
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
                        String result = stringBuilder.toString();
                        Log.i("postMyTask_result====>", result);
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.optInt("messg", 0) == 1) {
                            handler.sendEmptyMessage(1);
                        }
                        if (jsonObject.optInt("messg", 0) == 0) {
                            handler.sendEmptyMessage(2);
                        }
                    } else {
                        Log.i("login_state===>","登录失败");
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
                super.run();
            }
        }.start();
    }

    //将数据转换成Json格式
    public String dataToJson(){
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject object = new JSONObject();
            object.put("c_safety_plan_name",taskName.getText().toString());      //安检任务名称
            object.put("c_safety_plan_member","杜述洪");    //操作员
            object.put("d_safety_start",date.getText().toString());       //开始时间
            object.put("d_safety_end",date1.getText().toString());    //结束时间
            object.put("n_company_id",Integer.parseInt(sharedPreferences.getString("company_id","")));       //公司id
            JSONArray jsonArray = new JSONArray();
            for(int i = 0;i<parclebleList.size();i++){
                JSONObject object1 = new JSONObject();
                object1.put("c_user_id",parclebleList.get(i).getUserId());
                object1.put("n_data_state",1);
                object1.put("n_safety_state",1);
                object1.put("n_safety_date_type",0);
                object1.put("c_safety_type",securityType.getText().toString());       //安检类型
                jsonArray.put(i,object1);
            }
            jsonObject.put("safetyInspection",jsonArray);
            jsonObject.put("safetyPlan",object);
            Log.i("dataToJson==========>", "封装的json数据为："+jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    //show弹出框
    public void showPopupwindow() {
        layoutInflater = LayoutInflater.from(NewTaskActivity.this);
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

    //开始帧动画
    public void startFrameAnimation() {
        frameAnimation.setBackgroundResource(R.drawable.frame_animation_list);
        animationDrawable = (AnimationDrawable) frameAnimation.getDrawable();
        animationDrawable.start();
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    try {
                        Thread.sleep(1000);
                        Toast.makeText(NewTaskActivity.this, "用户信息下载完成！", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(NewTaskActivity.this, NewTaskDetailActivity.class);
                        startActivity(intent1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    Toast.makeText(NewTaskActivity.this, "没有用户信息下载！", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(NewTaskActivity.this, "网络请求超时！", Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 100){
                if (data != null ){
                    parclebleList = data.getParcelableArrayListExtra("parclebleList");
                    Log.i("NewTaskActivity","接收到的parclebleList长度为："+parclebleList.get(0).getUserId());
                }
            }
        }
    }
}
