package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myapplicationsienke.R;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.util.List;

import javax.net.ssl.SSLSocketFactory;

import static android.app.PendingIntent.getActivity;

/**
 * Created by Administrator on 2017/3/14.
 */
public class MobileSecurityActivity extends Activity {
    Button logonBtn;
    Button cancel_btn;
    private EditText editMobileUser, editmobilePsw;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String ip, port;  //接口ip地址   端口

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
        setContentView(R.layout.activity_mobile_security);

        bindView();//绑定控件
        defaultSetting();
        setViewClickListener();//点击事件
    }

    //绑定控件
    private void bindView() {
        logonBtn = (Button) findViewById(R.id.logon_btn);
        cancel_btn = (Button) findViewById(R.id.cancel_btn);
        editMobileUser = (EditText) findViewById(R.id.edit_mobile_user);
        editmobilePsw = (EditText) findViewById(R.id.edit_mobile_psw);
    }

    //初始化设置
    private void defaultSetting() {
        sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    //点击事件
    private void setViewClickListener() {
        logonBtn.setOnClickListener(clickListener);
        cancel_btn.setOnClickListener(clickListener);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.logon_btn:
                    if (editMobileUser.getText().toString().equals("") && editmobilePsw.getText().toString().equals("")) {
                        Toast.makeText(MobileSecurityActivity.this, "用户名或密码不能为空", Toast.LENGTH_LONG).show();
                    } else if (editMobileUser.getText().toString().equals("")) {
                        Toast.makeText(MobileSecurityActivity.this, "请输入用户名", Toast.LENGTH_LONG).show();
                    } else if (editmobilePsw.getText().toString().equals("")) {
                        Toast.makeText(MobileSecurityActivity.this, "请输入密码", Toast.LENGTH_LONG).show();
                    }
                    if (!editMobileUser.getText().toString().equals("") && !editmobilePsw.getText().toString().equals("")) {
                        //开启子线程
                        new Thread() {
                            public void run() {
                                loginByPost(editMobileUser.getText().toString(), editmobilePsw.getText().toString());
                            }
                        }.start();
                    }
                    break;
                case R.id.cancel_btn:
                    MobileSecurityActivity.this.finish();
                    break;
            }
        }

        //post请求
        public void loginByPost(final String userName, final String userPass) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        //请求的地址
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
                        String httpUrl = "http://" + ip + port + "/SMDemo/login.do";
                        Log.i("httpUrl==========>", "" + httpUrl);
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
                        urlConnection.setReadTimeout(5000);
                        urlConnection.setConnectTimeout(5000);
                        // 传递的数据
                        String data = "username=" + URLEncoder.encode(userName, "UTF-8") + "&password=" + URLEncoder.encode(userPass, "UTF-8");
                        Log.i("data==========>", "data=" + data);
                        // 设置请求的头
                        //urlConnection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
                        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        //urlConnection.setRequestProperty("Origin", "http://"+ ip + port);
                        urlConnection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
                        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");
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
                            Log.i("login_result=========>", result);
                            JSONObject jsonObject = new JSONObject(result);
                            if (jsonObject.optInt("messg", 0) == 1) {
                                handler.sendEmptyMessage(1);
                            }
                            if (jsonObject.optInt("messg", 0) == 0) {
                                handler.sendEmptyMessage(2);
                            }
                        } else {
                            Log.i("login_state===>", "登录失败");
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
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(MobileSecurityActivity.this, "登录成功！!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MobileSecurityActivity.this, SecurityChooseActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case 2:
                    Toast.makeText(MobileSecurityActivity.this, "密码错误!", Toast.LENGTH_LONG).show();
                    break;
                case 3:
                    Toast.makeText(MobileSecurityActivity.this, "网络请求异常!", Toast.LENGTH_LONG).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
