package com.example.administrator.myapplicationsienke.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
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
import com.example.administrator.myapplicationsienke.activity.DownloadActivity;
import com.example.administrator.myapplicationsienke.activity.UploadActivity;

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

/**
 * Created by Administrator on 2017/3/16 0016.
 */
public class DataTransferFragment extends Fragment {
    private View view;
    private TextView upload, download;
    private LinearLayout rootLinearlayout;
    private String result; //网络请求结果
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String ip, port;  //接口ip地址   端口
    public int responseCode = 0;
    private LayoutInflater layoutInflater;
    private PopupWindow popupWindow;
    private ImageView frameAnimation;
    private AnimationDrawable animationDrawable;

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
                    requireMyWorks("UserSafeCheck.do", "safetyPlan=" + 11 + "&safetyState=" + 1 + "&page=" + 1 + "&rows" + 50);
                    break;
            }
        }
    };

    //初始化设置
    private void defaultSetting() {
        sharedPreferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    //show弹出框
    public void showPopupwindow() {
        layoutInflater = LayoutInflater.from(getActivity());
        view = layoutInflater.inflate(R.layout.popupwindow_query_loading, null);
        popupWindow = new PopupWindow(view, 250, 250);
        frameAnimation = (ImageView) view.findViewById(R.id.frame_animation);
        //popupWindow.setFocusable(true);
        //popupWindow.setOutsideTouchable(true);
        //popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#E6E6E6")));
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.loading_shape));
        popupWindow.setAnimationStyle(R.style.dialog);
        //popupWindow.update();
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
    private void requireMyWorks(final String method, final String keyAndValue) {
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
                        ip = "88.88.88.31:";
                    }
                    if (!sharedPreferences.getString("security_port", "").equals("")) {
                        port = sharedPreferences.getString("security_port", "");
                        //Log.i("sharedPreferences=ip=>",ip);
                    } else {
                        port = "8080";
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
                        result = stringBuilder.toString();
                        Log.i("result_query==========>", result);
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
                            handler.sendEmptyMessage(2);
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
                    Intent intent = new Intent(getActivity(), DownloadActivity.class);
                    intent.putExtra("", "");
                    startActivity(intent);
                    popupWindow.dismiss();
                    break;
                case 2:
                    popupWindow.dismiss();
                    Toast.makeText(getActivity(), "网络请求超时！", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    popupWindow.dismiss();
                    Toast.makeText(getActivity(), "网络请求超时！", Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
