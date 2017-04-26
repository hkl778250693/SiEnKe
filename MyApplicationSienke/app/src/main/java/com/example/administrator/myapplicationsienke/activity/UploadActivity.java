package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.adapter.TaskChooseAdapter;
import com.example.administrator.myapplicationsienke.adapter.UploadListViewAdapter;
import com.example.administrator.myapplicationsienke.mode.MySqliteHelper;
import com.example.administrator.myapplicationsienke.model.TaskChoose;
import com.example.administrator.myapplicationsienke.model.TaskChooseViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/3/16.
 */
public class UploadActivity extends Activity {
    private ImageView back;
    private TextView upLoad,noData;
    private ListView listView;
    private RelativeLayout uploadTaskSelectLayout;
    private List<TaskChoose> taskChooseList = new ArrayList<>();
    private ArrayList<Integer> integers = new ArrayList<>();//保存选中任务的序号
    private HashMap<String, Object> map = new HashMap<String, Object>();
    private Cursor cursor;
    private SQLiteDatabase db;  //数据库
    private TaskChooseAdapter adapter;   //适配器
    private MySqliteHelper helper; //数据库帮助类
    private SharedPreferences.Editor editor;
    private String checkState;
    private SharedPreferences sharedPreferences;
    private String defaul = "";//默认的全部不勾选
    private int taskTotalUserNumber = 0;
    private TextView selectAll,reverse,selectCancel;

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
        //初始化勾选框信息，默认都是以未勾选为单位
        for (int i = 0; i < taskChooseList.size(); i++) {
            defaul = defaul + "0";
        }
    }

    //获得保存在这个activity中的选择框选中状态信息
    public void getCheckStateInfo() {
        Log.i("getCheckStateInfo==>", "读取上次保存的状态方法进来了！循环次数为：" + taskChooseList.size());
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
                TaskChooseViewHolder holder = (TaskChooseViewHolder) view.getTag();
                holder.checkBox.toggle();
                TaskChooseAdapter.getIsCheck().put(position, holder.checkBox.isChecked());
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
                    Log.i("integers====>", "长度为：" + integers.size());
                    if (integers.size() !=0) {
                        saveCheckStateInfo();//保存选中状态，将信息写入preference保存以备下一次读取使用
                        Toast.makeText(UploadActivity.this, "上传成功！~", Toast.LENGTH_SHORT).show();
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
    public void selectAll(){
        for (int i = 0; i < taskChooseList.size(); i++) {
            adapter.getIsCheck().put(i, true);
        }
        adapter.notifyDataSetChanged();
    }

    //反选
    public void reverse(){
        for (int i = 0; i < taskChooseList.size(); i++) {
            if (adapter.getIsCheck().get(i)) {
                adapter.getIsCheck().put(i, false);
            } else {
                adapter.getIsCheck().put(i, true);
            }
        }
        adapter.notifyDataSetChanged();
    }

    //取消选择
    public void selectCancle(){
        for (int i = 0; i < taskChooseList.size(); i++) {
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
        Log.i("count====>", "长度为：" + count);
        for (int i = 0; i < count; i++) {
            if (TaskChooseAdapter.getIsCheck().get(i)) {
                TaskChoose taskChoose = taskChooseList.get((int) adapter.getItemId(i));
                map.put("taskId" + i, taskChoose.getTaskNumber());
                Log.i("taskId=========>", "这次被勾选第" + i + "个，任务编号为：" + taskChoose.getTaskNumber());
                integers.add(i);
                Log.i("integers====>", "长度为：" + integers.size());
            }
        }
        editor.putInt("taskTotalUserNumber", taskTotalUserNumber);
        Log.i("taskTotalUserNumber=>", "任务总户数为：" + taskTotalUserNumber);
        editor.commit();
    }


    //保存选中状态，将信息写入preference保存以备下一次读取使用
    public void saveCheckStateInfo() {
        String flag = "";
        int count = adapter.getCount();
        Log.i("count====>", "长度为：" + count);
        for (int i = 0; i < count; i++) {
            if (TaskChooseAdapter.getIsCheck().get(i)) {  //判断如果此时是选中状态就保存到SharedPreferences，“1”表示选中，0表示没选中
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
        if(uploadTaskSelectLayout.getVisibility() == View.GONE){
            uploadTaskSelectLayout.setVisibility(View.VISIBLE);
        }
        while (cursor.moveToNext()) {
            TaskChoose taskChoose = new TaskChoose();
            taskChoose.setTaskName(cursor.getString(1));
            taskChoose.setTaskNumber(cursor.getString(2));
            taskChoose.setCheckType(cursor.getString(3));
            taskChoose.setTotalUserNumber(cursor.getString(4));
            taskChoose.setEndTime(cursor.getString(5));
            taskChooseList.add(taskChoose);
        }
        //cursor游标操作完成以后,一定要关闭
        cursor.close();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    adapter = new TaskChooseAdapter(UploadActivity.this, taskChooseList);
                    adapter.notifyDataSetChanged();
                    listView.setAdapter(adapter);
                    checkState = sharedPreferences.getString("upload_check_state", defaul); //如果没有获取到的话默认是0
                    if (!checkState.equals("")) {
                        getCheckStateInfo();//获得保存在这个activity中的选择框选中状态信息
                    }
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
