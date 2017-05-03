package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.adapter.UploadListViewAdapter;
import com.example.administrator.myapplicationsienke.mode.HttpUtils;
import com.example.administrator.myapplicationsienke.mode.MySqliteHelper;
import com.example.administrator.myapplicationsienke.model.UploadListViewItem;
import com.example.administrator.myapplicationsienke.model.UploadViewHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/3/16.
 */
public class UploadActivity extends Activity {
    private ImageView back, uploadFailed;
    private TextView upLoad, noData;
    private ListView listView;
    private RelativeLayout uploadTaskSelectLayout;
    private List<UploadListViewItem> uploadListViewItemList = new ArrayList<>();
    private ArrayList<Integer> integers = new ArrayList<>();//保存选中任务的序号
    private HashMap<String, Object> map = new HashMap<String, Object>();
    private HashMap<String, Object> map1;
    private ArrayList<String> stringList = new ArrayList<>();//保存任务编号参数
    private Cursor cursor;
    private SQLiteDatabase db;  //数据库
    private UploadListViewAdapter adapter;   //适配器
    private MySqliteHelper helper; //数据库帮助类
    private SharedPreferences.Editor editor;
    private String checkState;
    private SharedPreferences sharedPreferences;
    private String defaul = "";//默认的全部不勾选
    private TextView selectAll, reverse, selectCancel;
    private HttpUtils httpUtils;
    private String securityNumber;
    private Map<String, File> fileMap;
    private LayoutInflater layoutInflater;
    private PopupWindow popupWindow;
    private View popupwindowView;
    private LinearLayout rootLinearlayout, linearlayoutUpload;
    private Button finishBtn;
    private ProgressBar uploadProgress;
    private TextView progressName, progressPercent;
    private int maxProgress;//最大进度
    private int currentProgress = 0;
    private int currentPercent = 0;
    private int uploadNumber = 0;//记录上传的总用户数
    private int noCheckNumber = 0;//记录未安检用户数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        defaultSetting();//初始化设置
        //绑定控件
        bindView();
        //获取数据
        new Thread() {
            @Override
            public void run() {
                getTaskData();//读取下载到本地的任务数据
                handler.sendEmptyMessage(1);
                super.run();
            }
        }.start();
        setViewClickListener();//点击事件
    }


    //初始化设置
    private void defaultSetting() {
        helper = new MySqliteHelper(UploadActivity.this, 1);
        db = helper.getReadableDatabase();
        sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        httpUtils = new HttpUtils();
        //初始化勾选框信息，默认都是以未勾选为单位
        for (int i = 0; i < uploadListViewItemList.size(); i++) {
            defaul = defaul + "0";
        }
    }

    //获得保存在这个activity中的选择框选中状态信息
    public void getCheckStateInfo() {
        Log.i("getCheckStateInfo==>", "读取上次保存的状态方法进来了！循环次数为：" + uploadListViewItemList.size());
        for (int i = 0; i < checkState.length(); i++) {
            Log.i("getCheckStateInfo==>", "checkState的长度为：" + checkState.length());
            if (checkState.charAt(i) == '1') {
                UploadListViewAdapter.getIsCheck().put(i, true);
                Log.i("getCheckStateInfo==>", "读取上次保存的状态进来了！");
            }
        }
    }

    //绑定控件ID
    private void bindView() {
        back = (ImageView) findViewById(R.id.back);
        listView = (ListView) findViewById(R.id.listview);
        upLoad = (TextView) findViewById(R.id.up_load);
        selectAll = (TextView) findViewById(R.id.select_all);
        reverse = (TextView) findViewById(R.id.reverse);
        selectCancel = (TextView) findViewById(R.id.select_cancel);
        noData = (TextView) findViewById(R.id.no_data);
        uploadTaskSelectLayout = (RelativeLayout) findViewById(R.id.upload_task_select_layout);
        rootLinearlayout = (LinearLayout) findViewById(R.id.root_linearlayout);
    }

    //点击事件
    private void setViewClickListener() {
        upLoad.setOnClickListener(onClickListener);
        back.setOnClickListener(onClickListener);
        selectAll.setOnClickListener(onClickListener);
        reverse.setOnClickListener(onClickListener);
        selectCancel.setOnClickListener(onClickListener);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UploadViewHolder holder = (UploadViewHolder) view.getTag();
                holder.checkBox.toggle();
                UploadListViewAdapter.getIsCheck().put(position, holder.checkBox.isChecked());
            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:
                    finish();
                    break;
                case R.id.up_load:
                    saveTaskInfo(); //保存选中的任务编号信息
                    Log.i("UploadActivity", "长度为：" + integers.size());
                    if (integers.size() != 0) {
                        upLoad.setClickable(false);
                        showPopupwindow();  // 显示上传进度提示
                        uploadProgress.setMax(maxProgress * 10);
                        for (int j = 0; j < integers.size(); j++) {
                            getUserDataAndPost(map.get("taskId" + integers.get(j)).toString());  //根据任务编号去查询用户信息
                            //stringList.add(map.get("taskId" + integers.get(j)).toString());
                            Log.i("UploadActivity", "得到的任务编号为：" + map.get("taskId" + integers.get(j)).toString());
                        }
                        if (uploadNumber != 0) {
                            if (uploadProgress.getProgress() == maxProgress * 10) {
                                Log.i("uploadProgress", "上传的进度为：" + uploadProgress.getProgress());
                                progressPercent.setText("100");
                                Message msg = new Message();
                                msg.what = 4;
                                msg.arg1 = uploadNumber;
                                msg.arg2 = noCheckNumber;
                                handler.sendMessage(msg);
                            }
                        } else {
                            if (noCheckNumber == 0) {
                                handler.sendEmptyMessage(6);
                            } else {
                                Message msg = new Message();
                                msg.what = 7;
                                msg.arg1 = noCheckNumber;
                                handler.sendMessage(msg);
                            }
                        }
                        saveCheckStateInfo();//保存选中状态，将信息写入preference保存以备下一次读取使用
                    } else {
                        Toast.makeText(UploadActivity.this, "您还没有选择任务哦~", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.select_all:
                    selectAll();
                    break;
                case R.id.reverse:
                    reverse();
                    break;
                case R.id.select_cancel:
                    selectCancle();
                    break;
            }
        }
    };

    //全选
    public void selectAll() {
        for (int i = 0; i < uploadListViewItemList.size(); i++) {
            adapter.getIsCheck().put(i, true);
        }
        adapter.notifyDataSetChanged();
    }

    //反选
    public void reverse() {
        for (int i = 0; i < uploadListViewItemList.size(); i++) {
            if (adapter.getIsCheck().get(i)) {
                adapter.getIsCheck().put(i, false);
            } else {
                adapter.getIsCheck().put(i, true);
            }
        }
        adapter.notifyDataSetChanged();
    }

    //取消选择
    public void selectCancle() {
        for (int i = 0; i < uploadListViewItemList.size(); i++) {
            if (adapter.getIsCheck().get(i)) {
                adapter.getIsCheck().put(i, false);
            }
        }
        adapter.notifyDataSetChanged();
    }

    //保存选中的任务编号信息
    public void saveTaskInfo() {
        integers.clear();
        int count = adapter.getCount();
        Log.i("saveTaskInfo====>", "长度为：" + count);
        for (int i = 0; i < count; i++) {
            if (UploadListViewAdapter.getIsCheck().get(i)) {
                UploadListViewItem item = uploadListViewItemList.get((int) adapter.getItemId(i));
                maxProgress += Integer.parseInt(item.getTotalUserNumber());
                Log.i("saveTaskInfo====>", "最大进度为：" + maxProgress);
                map.put("taskId" + i, item.getTaskNumber());
                Log.i("saveTaskInfo=========>", "这次被勾选第" + i + "个，任务编号为：" + item.getTaskNumber());
                integers.add(i);
                Log.i("saveTaskInfo====>", "integers长度为：" + integers.size());
            }
        }
        editor.commit();
    }


    //保存选中状态，将信息写入preference保存以备下一次读取使用
    public void saveCheckStateInfo() {
        String flag = "";
        int count = adapter.getCount();
        Log.i("count====>", "长度为：" + count);
        for (int i = 0; i < count; i++) {
            if (UploadListViewAdapter.getIsCheck().get(i)) {  //判断如果此时是选中状态就保存到SharedPreferences，“1”表示选中，0表示没选中
                flag = flag + '1';
            } else {
                flag = flag + '0';
            }
        }
        editor.putString("upload_check_state", flag);//将数据已字符串形式保存起来，下次读取再用
        Log.i("saveCheckStateInfo=>", "checkState状态为：" + flag);
        editor.commit();
    }

    //读取下载到本地的任务数据
    public void getTaskData() {
        cursor = db.query("Task", null, null, null, null, null, null);//查询并获得游标
        if (cursor.getCount() == 0) {
            if (noData.getVisibility() == View.GONE) {
                noData.setVisibility(View.VISIBLE);
            }
            uploadTaskSelectLayout.setVisibility(View.GONE);
            return;
        }
        if (noData.getVisibility() == View.VISIBLE) {
            noData.setVisibility(View.GONE);
        }
        if (uploadTaskSelectLayout.getVisibility() == View.GONE) {
            uploadTaskSelectLayout.setVisibility(View.VISIBLE);
        }
        while (cursor.moveToNext()) {
            UploadListViewItem item = new UploadListViewItem();
            item.setTaskName(cursor.getString(1));
            item.setTaskNumber(cursor.getString(2));
            item.setCheckType(cursor.getString(3));
            item.setTotalUserNumber(cursor.getString(4));
            item.setEndTime(cursor.getString(5));
            uploadListViewItemList.add(item);
        }
        //cursor游标操作完成以后,一定要关闭
        cursor.close();
    }

    //读取本地安检用户数据，并上传服务器
    public void getUserDataAndPost(final String taskId) {
        new Thread() {
            @Override
            public void run() {
                Cursor cursor = db.rawQuery("select * from User where taskId=?", new String[]{taskId});//查询并获得游标
                map1 = new HashMap<String, Object>();
                while (cursor.moveToNext()) {
                    if (cursor.getString(10).equals("true")) {  //判断是否为安检过的，未安检的不上传
                        if (cursor.getString(17).equals("false")) { //判断是否为未上传，上传的用户数据不再上传
                            map1.put("n_safety_inspection_id", cursor.getString(1));
                            Log.i("getUserData=>", "安检ID为：" + cursor.getString(1));
                            securityNumber = cursor.getString(1);
                            Log.i("getUserData=>", "上传的用户数为：" + cursor.getCount());
                            if (!cursor.getString(11).equals("")) {
                                map1.put("n_safety_state", cursor.getString(11));
                            } else {
                                map1.put("n_safety_state", null);
                            }
                            Log.i("getUserData=>", "安检状态是：" + cursor.getString(11));
                            if (!cursor.getString(13).equals("")) {
                                map1.put("c_safety_remark", cursor.getString(13));
                            } else {
                                map1.put("c_safety_remark", "");
                            }
                            Log.i("getUserData=>", "备注是：" + cursor.getString(13));
                            if (!cursor.getString(14).equals("")) {
                                map1.put("n_safety_hidden_id", cursor.getString(14));
                            } else {
                                map1.put("n_safety_hidden_id", null);
                            }
                            Log.i("getUserData=>", "隐患类型是：" + cursor.getString(14));
                            if (!cursor.getString(15).equals("")) {
                                map1.put("n_safety_hidden_reason_id", cursor.getString(15));
                            } else {
                                map1.put("n_safety_hidden_reason_id", null);
                            }
                            Log.i("getUserData=>", "隐患原因ID是：" + cursor.getString(15));
                            if (!cursor.getString(18).equals("")) {
                                map1.put("d_safety_inspection_date", cursor.getString(18));
                            }
                            Log.i("getUserData=>", "安检的时间是：" + cursor.getString(18));
                            getPhotoData(securityNumber);
                            httpUtils.postData("http://88.88.88.31:8080/SMDemo/updateInspection.do", map1, fileMap);
                            if ("保存成功".equals(httpUtils.result)) {
                                updateUserUploadState(securityNumber);   //如果返回保存成功则将用户表的上传状态改为true
                                uploadNumber++;
                                handler.sendEmptyMessage(3);
                            } else if ("保存失败".equals(httpUtils.result) || "".equals(httpUtils.result)) {
                                Log.i("UploadActivity=>", "保存失败！");
                                handler.sendEmptyMessage(5);
                                break;
                            }
                        }
                    } else {
                        noCheckNumber++;
                    }
                }
                cursor.close(); //游标关闭
            }
        }.start();
    }

    /**
     * 将拍的照片张数保存到本地数据库安检图片表
     */
    private void updateUserUploadState(String securityNumber) {
        ContentValues values = new ContentValues();
        values.put("ifUpload", "true");
        db.update("User", values, "securityNumber=?", new String[]{securityNumber});
    }

    //show下载popupwindow
    public void showPopupwindow() {
        layoutInflater = LayoutInflater.from(UploadActivity.this);
        popupwindowView = layoutInflater.inflate(R.layout.popupwindow_upload_progressbar, null);
        popupWindow = new PopupWindow(popupwindowView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearlayoutUpload = (LinearLayout) popupwindowView.findViewById(R.id.linearlayout_upload);
        uploadFailed = (ImageView) popupwindowView.findViewById(R.id.upload_failed);
        finishBtn = (Button) popupwindowView.findViewById(R.id.finish_btn);
        uploadProgress = (ProgressBar) popupwindowView.findViewById(R.id.upload_progress);
        progressName = (TextView) popupwindowView.findViewById(R.id.progress_name);
        progressPercent = (TextView) popupwindowView.findViewById(R.id.progress_percent);
        progressName.setText("正在上传用户数据，请耐心等待...");
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProgress.setProgress(0);
                upLoad.setClickable(true);
                popupWindow.dismiss();
            }
        });
        popupWindow.setFocusable(true);
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
        WindowManager.LayoutParams lp = UploadActivity.this.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        UploadActivity.this.getWindow().setAttributes(lp);
    }

    //读取下载到本地的图片数据，并上传服务器
    public void getPhotoData(String securityId) {
        Cursor cursor = db.rawQuery("select * from security_photo where securityNumber=?", new String[]{securityId});//查询并获得游标
        fileMap = new HashMap<String, File>();
        File file = null;
        while (cursor.moveToNext()) {
            file = new File(cursor.getString(1));
            fileMap.put("file" + cursor.getPosition(), file);
        }
        Log.i("getUserData=>", "上传的照片流为：" + fileMap.size());
        cursor.close(); //游标关闭
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    adapter = new UploadListViewAdapter(UploadActivity.this, uploadListViewItemList);
                    adapter.notifyDataSetChanged();
                    listView.setAdapter(adapter);
                    checkState = sharedPreferences.getString("upload_check_state", defaul); //如果没有获取到的话默认是0
                    if (!checkState.equals("")) {
                        getCheckStateInfo();//获得保存在这个activity中的选择框选中状态信息
                    }
                    break;
                case 2:
                    Toast.makeText(UploadActivity.this, "安检编号为" + securityNumber + "的用户上传失败，请重新上传！", Toast.LENGTH_LONG).show();
                    break;
                case 3:
                    //上传成功一个用户数据就修改一次上传进度
                    //计算进度值和百分比
                    currentProgress += 10 * maxProgress / maxProgress;  //当前进度
                    currentPercent += 1000 / (10 * maxProgress);  //当前进度百分比
                    //设置进度值和百分比
                    uploadProgress.setProgress(currentProgress);
                    progressPercent.setText(String.valueOf(currentPercent));
                    break;
                case 4:
                    String uploadResult;
                    if (msg.arg2 == 0) {
                        uploadResult = "数据上传完成！共上传了" + msg.arg1 + "个用户数据";
                    } else {
                        uploadResult = "数据上传完成！共上传了" + msg.arg1 + "个用户数据,还有" + msg.arg2 + "个用户未安检，请您安检以后继续上传！";
                    }
                    progressName.setText(uploadResult);
                    linearlayoutUpload.setVisibility(View.GONE);
                    finishBtn.setVisibility(View.VISIBLE);
                    uploadFailed.setVisibility(View.GONE);
                    currentProgress = 0;
                    currentPercent = 0;
                    noCheckNumber = 0;
                    uploadNumber = 0;
                    break;
                case 5:
                    progressName.setText("安检编号为" + securityNumber + "的用户上传失败，请重新上传！");
                    linearlayoutUpload.setVisibility(View.GONE);
                    uploadProgress.setVisibility(View.GONE);
                    uploadFailed.setImageResource(R.mipmap.defeated);
                    uploadFailed.setVisibility(View.VISIBLE);
                    finishBtn.setVisibility(View.VISIBLE);
                    noCheckNumber = 0;
                    uploadNumber = 0;
                    break;
                case 6:
                    progressName.setText("当前勾选任务用户已全部上传哦！");
                    linearlayoutUpload.setVisibility(View.GONE);
                    uploadProgress.setVisibility(View.GONE);
                    uploadFailed.setImageResource(R.mipmap.smile);
                    uploadFailed.setVisibility(View.VISIBLE);
                    finishBtn.setVisibility(View.VISIBLE);
                    noCheckNumber = 0;
                    uploadNumber = 0;
                    break;
                case 7:
                    String uploadResult1 = "当前勾选任务用户还有" + msg.arg1 + "个用户没安检哦！请您安检以后继续上传！";
                    progressName.setText(uploadResult1);
                    linearlayoutUpload.setVisibility(View.GONE);
                    uploadProgress.setVisibility(View.GONE);
                    uploadFailed.setImageResource(R.mipmap.smile);
                    uploadFailed.setVisibility(View.VISIBLE);
                    finishBtn.setVisibility(View.VISIBLE);
                    noCheckNumber = 0;
                    uploadNumber = 0;
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        //释放和数据库的连接
        db.close();
    }
}
