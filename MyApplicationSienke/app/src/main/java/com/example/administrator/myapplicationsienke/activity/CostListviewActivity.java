package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.adapter.QueryAdapter;
import com.example.administrator.myapplicationsienke.model.QueryListviewItem;

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
 * Created by Administrator on 2017/2/24 0024.
 */
public class CostListviewActivity extends Activity {
    private ImageView back;
    private ListView listView;
    private List<QueryListviewItem> queryListviewItemList = new ArrayList<>();
    public int responseCode = 0;
    private String result; //网络请求结果
    private String result1; //网络请求结果listview
    private TextView userNumber;
    private TextView userName;
    private TextView meterNumberTextview;
    private TextView waterType;
    private TextView meterModelNumber;
    private TextView meterType;
    private TextView address;
    private String meterNumber;
    private String userid;
    private Intent intent;
    QueryAdapter adapter=null;
    private String ip;  //接口ip地址
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cost_listview);

        bindView();//绑定控件
        defaultSetting();//初始化设置

        new Thread(){//开起一个支线程进行网络请求
            @Override
            public void run() {
                intent = getIntent();
                Log.i("CostListview_intent==>",intent+"");
                if(intent != null){
                    userid = intent.getStringExtra("userid");
                    if(userid != null){
                        requireMyWorks("getUser.do", "userid=" + userid);
                        requireMyWorksListview("getUser.do", "userid=" + userid);
                        //Log.i("CostListview_userid===>",userid);
                    }
                    meterNumber = intent.getStringExtra("meterNumber");
                   // Log.i("meterNumber===========>",meterNumber);
                    if(meterNumber != null){
                        requireMyWorks("getUser.do", "meterNumber=" + meterNumber);
                        requireMyWorksListview("getUser.do", "meterNumber=" + meterNumber);
                       // Log.i("CostListview_meterNumb=",meterNumber);
                    }
                }
            }
        }.start();

        setViewClickListener();//点击事件
    }


    //绑定控件ID
    private void bindView(){
        back = (ImageView) findViewById(R.id.back);
        userNumber = (TextView) findViewById(R.id.user_number);
        userName = (TextView) findViewById(R.id.user_name);
        meterNumberTextview = (TextView) findViewById(R.id.meter_number);
        waterType = (TextView) findViewById(R.id.water_type);
        meterModelNumber = (TextView) findViewById(R.id.meter_model_number);
        meterType = (TextView) findViewById(R.id.meter_type);
        address = (TextView) findViewById(R.id.address);
        listView = (ListView) findViewById(R.id.listview);
    }

    //点击事件
    private void setViewClickListener(){
        back.setOnClickListener(clickListener);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CostListviewActivity.this,CostDetailActivity.class);
                QueryListviewItem item = queryListviewItemList.get((int) parent.getAdapter().getItemId(position));
                if(!userNumber.getText().toString().equals("")){
                    intent.putExtra("userid",userNumber.getText().toString());
                    Log.i("QueryListviewItem_uid=>",userNumber.getText().toString());
                }
                if(!item.getDate().equals("")){
                    intent.putExtra("useMonth",item.getDate());
                    Log.i("month=============>",item.getDate());
                }
                startActivity(intent);
            }
        });
    }


    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.back:
                    CostListviewActivity.this.setResult(100);
                    CostListviewActivity.this.finish();
                    break;
            }
        }
    };

    //初始化设置
    private void defaultSetting() {
        sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
    }

    //请求网络数据
    private void requireMyWorks(final String method, final String keyAndValue) {
        new Thread() {
            @Override
            public void run() {
                try {
                    URL url;
                    ip = sharedPreferences.getString("IP","");
                    HttpURLConnection httpURLConnection;
                    String httpUrl = "http://"+ip+"/SMDemo/" + method;
                    //有参数传递
                    if (!keyAndValue.equals("") ) {
                        url = new URL(httpUrl + "?" + keyAndValue);
                        //没有参数传递
                    } else {
                        url = new URL(httpUrl);
                    }
                    Log.i("url=============>",url+"");
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setConnectTimeout(6000);
                    httpURLConnection.connect();
                    //传回的数据解析成String
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
                       // Log.i("result==========>",result);
                        handler.sendEmptyMessage(0);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Log.i("IOException==========>","网络请求异常!");
                    handler.sendEmptyMessage(2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //请求网络数据listview
    private void requireMyWorksListview(final String method, final String keyAndValue) {
        new Thread() {
            @Override
            public void run() {
                try {
                    URL url;
                    ip = sharedPreferences.getString("IP","");
                    HttpURLConnection httpURLConnection;
                    String httpUrl = "http://"+ip+"/SMDemo/" + method;
                    //有参数传递
                    if (!keyAndValue.equals("") ) {
                        url = new URL(httpUrl + "?" + keyAndValue);
                        //没有参数传递
                    } else {
                        url = new URL(httpUrl);
                    }
                    Log.i("url_listview=========>",url+"");
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setConnectTimeout(6000);
                    httpURLConnection.connect();
                    //传回的数据解析成String
                    if ((responseCode = httpURLConnection.getResponseCode()) == 200) {
                        InputStream inputStream = httpURLConnection.getInputStream();
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        StringBuilder stringBuilder = new StringBuilder();
                        String str;
                        while ((str = bufferedReader.readLine()) != null) {
                            stringBuilder.append(str);
                        }
                        result1 = stringBuilder.toString();
                       // Log.i("result1==========>",result1);
                        handler.sendEmptyMessage(1);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0){
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("rows");
                    JSONObject object = jsonArray.getJSONObject(0);
                    userNumber.setText(object.optString("c_old_user_id",""));
                    userName.setText(object.optString("c_user_name",""));
                    meterNumberTextview.setText(object.optString("c_meter_number",""));
                    waterType.setText(object.optString("c_properties_name",""));
                    meterModelNumber.setText(object.optString("c_model_name",""));
                    meterType.setText(object.optString("type",""));
                    address.setText(object.optString("c_user_address",""));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if(msg.what == 1){
                //解析listview的数据
                JSONArray jsonArray = null;
                try {
                    JSONObject jsonObject = new JSONObject(result1);
                    jsonArray = jsonObject.getJSONArray("rows");
                    Log.i("jsonArray_listview====",""+jsonArray.length());
                    for(int t=0;t<jsonArray.length();t++){
                        JSONObject object = jsonArray.getJSONObject(t);
                        QueryListviewItem queryListviewItem = new QueryListviewItem();
                        queryListviewItem.setDate(object.optString("c_situation_use_month",""));
                        queryListviewItem.setPay_state(object.optString("n_charge_state",""));
                        queryListviewItem.setStart_degree(object.optInt("minStart",0));
                        queryListviewItem.setEnd_degree(object.optInt("maxEnd",0));
                        queryListviewItem.setUnit_cost(object.optDouble("detailPrice",0.0));
                        queryListviewItem.setIntegrated_water(object.optDouble("totalAmount",0.0));
                        queryListviewItemList.add(queryListviewItem);
                    }
                    if (queryListviewItemList!=null){
                        adapter = new QueryAdapter(CostListviewActivity.this,queryListviewItemList);
                        listView.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if(msg.what == 2){
                Toast.makeText(CostListviewActivity.this,"网络请求超时！",Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };

}
