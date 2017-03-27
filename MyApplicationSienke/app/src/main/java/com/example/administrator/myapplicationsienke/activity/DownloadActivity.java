package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.adapter.DownloadListViewAdapter;
import com.example.administrator.myapplicationsienke.adapter.QueryAdapter;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/16.
 */
public class DownloadActivity extends Activity {
    private ImageView back;
    private ListView listView;
    private Button download;
    private List<DownloadListvieItem> downloadListvieItemList = new ArrayList<>();
    private Intent intent;
    private String safePlanMember;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String ip,port;  //接口ip地址   端口
    public int responseCode = 0;
    private String result; //网络请求结果
    private DownloadListvieItem item;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        bindView();  //绑定控件
        defaultSetting();//初始化设置
        setViewClickListener();  //点击事件
        new Thread(){   //开起一个支线程进行网络请求
            @Override
            public void run() {
                intent = getIntent();
                if(intent != null){
                    safePlanMember = intent.getStringExtra("safePlanMember");
                    if(safePlanMember != null){
                        requireMyWorks("SafeCheckPlan.do","safePlanMember="+"杜述洪");
                    }
                }
                super.run();
            }
        }.start();
    }

    //绑定控件ID
    private void bindView() {
        back = (ImageView) findViewById(R.id.back);
        listView = (ListView) findViewById(R.id.listview);
        download= (Button) findViewById(R.id.download);
    }

    //点击事件
    private void setViewClickListener(){
        back.setOnClickListener(clickListener);
        download.setOnClickListener(clickListener);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                item = downloadListvieItemList.get((int) parent.getAdapter().getItemId(position));  //获取当前被点击的item位置
            }
        });
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:
                    finish();
                    break;
                case R.id.download:

                    break;
            }
        }
    };

    //初始化设置
    private void defaultSetting() {
        sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    //请求网络数据
    private void requireMyWorks(final String method, final String keyAndValue) {
        new Thread() {
            @Override
            public void run() {
                try {
                    URL url;
                    HttpURLConnection httpURLConnection;
                    Log.i("sharedPreferences====>",sharedPreferences.getString("IP",""));
                    if(!sharedPreferences.getString("security_ip","").equals("")){
                        ip = sharedPreferences.getString("security_ip","");
                        //Log.i("sharedPreferences=ip=>",ip);
                    }else {
                        ip = "88.88.88.66:";
                    }
                    if(!sharedPreferences.getString("security_port","").equals("")){
                        port = sharedPreferences.getString("security_port","");
                        //Log.i("sharedPreferences=ip=>",ip);
                    }else {
                        port = "8088";
                    }
                    String httpUrl = "http://" + ip + port + "/SMDemo/" + method;
                    //有参数传递
                    if (!keyAndValue.equals("") ) {
                        url = new URL(httpUrl + "?" + keyAndValue);
                        //没有参数传递
                    } else {
                        url = new URL(httpUrl);
                    }
                    Log.i("DownloadActivity_url=>",url+"");
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setConnectTimeout(6000);
                    httpURLConnection.setReadTimeout(6000);
                    httpURLConnection.connect();
                    //传回的数据解析成String
                    Log.i("responseCode====>",httpURLConnection.getResponseCode()+"");
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
                        Log.i("result_DownloadActivity",result);
                        JSONObject jsonObject = new JSONObject(result);
                        if(!jsonObject.optString("total","").equals("0")){
                            handler.sendEmptyMessage(1);
                        }else{
                            try {
                                Thread.sleep(3000);
                                handler.sendEmptyMessage(2);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }else {
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
                    Log.i("IOException==========>","网络请求异常!");
                    handler.sendEmptyMessage(3);
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        JSONArray jsonArray = jsonObject.getJSONArray("rows");
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject object = jsonArray.getJSONObject(i);
                            DownloadListvieItem listvieItem = new DownloadListvieItem();
                            listvieItem.setTaskName(object.optString("safetyPlanName",""));
                            listvieItem.setTaskNumber(object.optInt("safetyplanId",0));
                            listvieItem.setCheckType(object.optString("securityName",""));
                            listvieItem.setTotalUserNumber(object.optInt("countRs",0));
                            listvieItem.setEndTime(object.optString("safetyEnd",""));
                            downloadListvieItemList.add(listvieItem);
                        }
                        if (downloadListvieItemList != null){
                            DownloadListViewAdapter adapter = new DownloadListViewAdapter(DownloadActivity.this, downloadListvieItemList);
                            listView.setAdapter(adapter);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    Toast.makeText(DownloadActivity.this,"网络请求异常！",Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(DownloadActivity.this,"网络请求超时！",Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    //将服务器下载的数据存到本地数据库
    private void insertData(){
        MySqliteHelper helper = new MySqliteHelper(DownloadActivity.this,1);
        SQLiteDatabase db = helper.getWritableDatabase();
    }

}
