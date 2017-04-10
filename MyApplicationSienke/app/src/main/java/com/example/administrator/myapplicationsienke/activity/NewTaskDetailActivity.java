package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.Toast;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.adapter.NewTaskListviewAdapter;
import com.example.administrator.myapplicationsienke.model.NewTaskListviewItem;

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
import java.util.List;

/**
 * Created by Administrator on 2017/4/5.
 */
public class NewTaskDetailActivity extends Activity {
    private View view;
    private ImageView back;
    private ListView listView;
    private TextView securityCheckCase, save, no_data;
    private EditText setEsearchTextChanged;//搜索框
    private Button searchBtn;
    private PopupWindow popupWindow;
    private View securityCaseView;
    private RadioButton notSecurityCheck, passSecurityCheck, notPassSecurityCheck;
    private LayoutInflater inflater;  //转换器
    private List<NewTaskListviewItem> newTaskListviewItemList = new ArrayList<>();
    private String ip, port;  //接口ip地址   端口
    private String result; //网络请求结果
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private LayoutInflater layoutInflater;
    private LinearLayout rootLinearlayout;
    private ImageView frameAnimation;
    private AnimationDrawable animationDrawable;
    public int responseCode = 0;
    private NewTaskListviewAdapter newTaskListviewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task_datail);


        /*//开启支线程进行请求任务信息
        new Thread() {
            @Override
            public void run() {
                requireMyTask("getCostomer.do", "safetyPlan=");
                super.run();
            }
        }.start();*/
        bindView();//绑定控件
        defaultSetting();//初始化设置
        setOnClickListener();//点击事件
    }

    //绑定控件ID
    private void bindView() {
        back = (ImageView) findViewById(R.id.back);
        listView = (ListView) findViewById(R.id.listview);
        securityCheckCase = (TextView) findViewById(R.id.security_check_case);
        setEsearchTextChanged = (EditText) findViewById(R.id.etSearch);
        searchBtn = (Button) findViewById(R.id.search_btn);
        save = (TextView) findViewById(R.id.save);
        no_data = (TextView) findViewById(R.id.no_data);
        rootLinearlayout = (LinearLayout)findViewById(R.id.root_linearlayout);
    }

    //点击事件
    private void setOnClickListener() {
        back.setOnClickListener(onClickListener);
        securityCheckCase.setOnClickListener(onClickListener);
        save.setOnClickListener(onClickListener);
        searchBtn.setOnClickListener(onClickListener);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NewTaskDetailActivity.this, UserDetailInfoActivity.class);
                startActivity(intent);
            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:
                    NewTaskDetailActivity.this.finish();
                    break;
                case R.id.security_check_case:
                    createSecurityCasePopupwindow();
                    break;
                case R.id.save:
                    Toast.makeText(NewTaskDetailActivity.this,"用户信息已保存",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(NewTaskDetailActivity.this, NewTaskActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.search_btn:
                    if (securityCheckCase.getText().equals("姓名")) {
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
                                requireMyTask("getCostomer.do", "userName="+setEsearchTextChanged.getText().toString());
                                super.run();
                            }
                        }.start();
                    } else if (securityCheckCase.getText().equals("表编号")) {
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
                                requireMyTask("getCostomer.do", "meterNumber="+setEsearchTextChanged.getText().toString());
                                super.run();
                            }
                        }.start();
                    } else if (securityCheckCase.getText().equals("地址")) {
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
                                requireMyTask("getCostomer.do", "userAdress="+setEsearchTextChanged.getText().toString());
                                super.run();
                            }
                        }.start();
                    }
                    break;
            }
        }
    };

    //初始化设置
    private void defaultSetting() {
        sharedPreferences = NewTaskDetailActivity.this.getSharedPreferences("data", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        securityCheckCase.setText("筛选");
        if (no_data.getVisibility() == View.GONE) {
            no_data.setVisibility(View.VISIBLE);
        }
    }

    //show弹出框
    public void showPopupwindow() {
        layoutInflater = LayoutInflater.from(NewTaskDetailActivity.this);
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

    //popupwindow
    public void createSecurityCasePopupwindow() {
        inflater = LayoutInflater.from(NewTaskDetailActivity.this);
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
                if (no_data.getVisibility() == View.VISIBLE) {
                    no_data.setVisibility(View.GONE);
                }
            }
        });
        passSecurityCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityCheckCase.setText(passSecurityCheck.getText());
                if (no_data.getVisibility() == View.VISIBLE) {
                    no_data.setVisibility(View.GONE);
                }
            }
        });
        notPassSecurityCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityCheckCase.setText(notPassSecurityCheck.getText());
                if (no_data.getVisibility() == View.VISIBLE) {
                    no_data.setVisibility(View.GONE);
                }
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
                        url = new URL(httpUrl + "?" + keyAndValue);
                        //没有参数传递
                    } else {
                        url = new URL(httpUrl);
                    }
                    Log.i("NewTaskDetailActivity", url + "");
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
                        result = stringBuilder.toString();
                        Log.i("NewTaskDetailActivity", result);
                        JSONObject jsonObject = new JSONObject(result);
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

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        JSONArray jsonArray = jsonObject.getJSONArray("rows");
                        Log.i("NewTaskDetailActivity", "jsonArray==" + jsonArray.length());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            NewTaskListviewItem item = new NewTaskListviewItem();
                            item.setUserName(object.optString("c_user_name", ""));
                            item.setNumber(object.optString("c_meter_number", ""));
                            item.setPhoneNumber(object.optString("c_user_phone", ""));
                            item.setUserId(object.optString("c_old_user_id", ""));
                            item.setAdress(object.optString("c_user_address", ""));
                            newTaskListviewItemList.add(item);
                        }
                        if (newTaskListviewItemList.size() != 0) {
                            Log.i("NewTaskDetailActivity", "传入的数据长度为：" + newTaskListviewItemList.size());
                            newTaskListviewAdapter = new NewTaskListviewAdapter(NewTaskDetailActivity.this, newTaskListviewItemList);
                            listView.setAdapter(newTaskListviewAdapter);
                        }
                        popupWindow.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    popupWindow.dismiss();
                    Toast.makeText(NewTaskDetailActivity.this, "没有相应的数据！", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    popupWindow.dismiss();
                    Toast.makeText(NewTaskDetailActivity.this, "网络请求超时！", Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
