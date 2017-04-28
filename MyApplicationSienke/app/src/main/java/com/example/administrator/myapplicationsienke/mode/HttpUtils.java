package com.example.administrator.myapplicationsienke.mode;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class HttpUtils {
    public String token;
    public String uid;

    public void uploadImage(String httpUrl, final Map<String, Object> map,
                            final InputStream inputStream) {
        // TODO Auto-generated method stub
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader in = null;
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(10000); // 连接超时为10秒
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + boundary);// 设置请求数据类型并设置boundary部分；
            connection.connect();
            //获取输出流
            DataOutputStream ds = new DataOutputStream(
                    connection.getOutputStream());
            Set<Map.Entry<String, Object>> paramEntrySet = map.entrySet();
            Iterator paramIterator = paramEntrySet.iterator();
            while (paramIterator.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry<String, String>) paramIterator.next();
                String key = entry.getKey();
                String value = entry.getValue();
                ds.writeBytes(twoHyphens + boundary + end);
                ds.writeBytes("Content-Disposition: form-data; " + "name=\""
                        + key + "\"" + end + end + value);
                ds.writeBytes(end);
                ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
                ds.flush();
            }
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data; " + "name=\"file"
                    + "\";filename=\"" + "image1.png" + "\"" + end);
            // ds.writeBytes("Content-type:application/octet-stream");
            ds.writeBytes(end);
            // * 取得文件的FileInputStream *//*
//			FileInputStream fStream = new FileInputStream(
//					file.getAbsolutePath());
            // * 设置每次写入1024bytes *//*
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int length = -1;
            // * 从文件读取数据至缓冲区 *//*
            while ((length = inputStream.read(buffer)) != -1) {
                // * 将资料写入DataOutputStream中 *//*
                ds.write(buffer, 0, length);
                ds.flush();//刷新数据
            }
            ds.writeBytes(end);
            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
            ds.flush();
            ds.close();
            inputStream.close();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            int statusCode = connection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                char[] buf = new char[1024];
                int len = -1;
                while ((len = in.read(buf, 0, buf.length)) != -1) {
                    stringBuilder.append(buf, 0, len);
                }
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i("Upload", "result===========>" + stringBuilder.toString());
        try {
            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
            String message = jsonObject.optString("message", "");
            Log.i("Upload", "message=" + message);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //post提交form-data类型的数据name=value
    public void postData(String httpUrl, Map<String, Object> map) {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader in = null;
        String result = "";
        String end = "\r\n";//结束符【换行】
        String twoHyphens = "--";//分隔符开头字符
        String boundary = "SJDASJODAODASSD";//
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();//打开连接对象
            connection.setDoOutput(true);//设置允许输出
            connection.setUseCaches(false);//设置不缓存
            connection.setConnectTimeout(10000); // 连接超时为10秒
            connection.setRequestMethod("POST");//设置请求方式
            connection.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + boundary);// 设置请求数据类型并设置boundary部分；
            connection.connect();//开启连接
            DataOutputStream ds = new DataOutputStream(
                    connection.getOutputStream());//获得输出流的对象
            Set<Map.Entry<String, Object>> paramEntrySet = map.entrySet();//将map转换成set集合
            Iterator paramIterator = paramEntrySet.iterator();//set集合的迭代器
            while (paramIterator.hasNext()) {//循环取出key和value
                Map.Entry<String, String> entry = (Map.Entry<String, String>) paramIterator
                        .next();
                String key = entry.getKey();//取得key值
                String value = entry.getValue();//取得value的值
                ds.writeBytes(twoHyphens + boundary + end);//--SJDASJODAODASSD
                ds.writeBytes("Content-Disposition: form-data; " + "name=\""
                        + key + "\"" + end + end + value);//Content-Disposition: form-data; name="key"
                ds.writeBytes(end);
                ds.writeBytes(twoHyphens + boundary + twoHyphens + end);//--SJDASJODAODASSD
                ds.flush();//刷新数据
            }
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            int statusCode = connection.getResponseCode();//获得响应状态码
            if (statusCode == HttpURLConnection.HTTP_OK) {
                char[] buf = new char[1024];
                int len = -1;
                while ((len = in.read(buf, 0, buf.length)) != -1) {
                    stringBuilder.append(buf, 0, len);
                }
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i("Post", "result===========>" + stringBuilder.toString());
        try {
            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
            String message = jsonObject.optString("message", "");
            token = jsonObject.optString("token", "");
            uid = jsonObject.optString("uid", "");
            Log.i("Post", "message=" + message);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}