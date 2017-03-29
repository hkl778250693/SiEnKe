package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.adapter.TaskChooseAdapter;
import com.example.administrator.myapplicationsienke.mode.MySqliteHelper;
import com.example.administrator.myapplicationsienke.model.TaskChoose;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/3/15 0015.
 */
public class TaskChooseActivity extends Activity {
    private ImageView back;
    private TextView save, noData;
    private ListView listView;
    private List<TaskChoose> taskChooseList = new ArrayList<>();
    private HashMap<String, Object> map = new HashMap<String, Object>();
    private TaskChooseAdapter adapter;   //适配器
    private SQLiteDatabase db;  //数据库
    private MySqliteHelper helper; //数据库帮助类
    List<Integer> integerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_choose);

        defaultSetting();//初始化设置
        new Thread() {
            @Override
            public void run() {
                getTaskData();//读取下载到本地的任务数据
                super.run();
            }
        }.start();
        bindView();//绑定控件
        setViewClickListener();//点击事件
    }

    //绑定控件ID
    private void bindView() {
        back = (ImageView) findViewById(R.id.back);
        save = (TextView) findViewById(R.id.save);
        noData = (TextView) findViewById(R.id.no_data);
        listView = (ListView) findViewById(R.id.listview);
    }

    //初始化设置
    private void defaultSetting() {
        helper = new MySqliteHelper(TaskChooseActivity.this, 1);
        db = helper.getReadableDatabase();
    }

    //点击事件
    private void setViewClickListener() {
        back.setOnClickListener(onClickListener);
        save.setOnClickListener(onClickListener);
        adapter = new TaskChooseAdapter(TaskChooseActivity.this, taskChooseList);
        listView.setAdapter(adapter);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:
                    finish();
                    break;
                case R.id.save:
                    saveTaskInfo();
                    Toast.makeText(TaskChooseActivity.this, "保存成功！您可以到用户列表查看哦~", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(TaskChooseActivity.this, SecurityChooseActivity.class);
                    for (int j = 0; j < integerList.size(); j++) {
                        intent.putExtra("taskId" + integerList.get(j), map.get("taskId=" + integerList.get(j)) + "");
                        Log.i("intent.putExtra====>","传递的参数为："+map.get("taskId=" + integerList.get(j)));
                    }
                    intent.putExtra("task_total_numb",integerList.size());
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };

    //保存选中的任务编号信息
    public void saveTaskInfo() {
        HashMap<Integer, Boolean> state = adapter.state;
        for (int i = 0; i < adapter.getCount(); i++) {
            if (state.get(i) != null) {
                TaskChoose taskChoose = taskChooseList.get((int) adapter.getItemId(i));
                map.put("taskId=" + i, taskChoose.getTaskNumber());
                Log.i("taskId=========>","这次被勾选第"+i+"个，任务编号为："+taskChoose.getTaskNumber());
                integerList.add(i);
                Log.i("integerList====>","长度为："+integerList.size());
            }
        }
    }

    /**
     * 查询方法参数详解
     * <p/>
     * public Cursor query (boolean distinct, String table, String[] columns,
     * String selection, String[] selectionArgs,
     * String groupBy, String having,
     * String orderBy, String limit,
     * CancellationSignal cancellationSignal)
     * <p/>
     * 参数介绍 :
     * <p/>
     * -- 参数① distinct : 是否去重复, true 去重复;
     * <p/>
     * -- 参数② table : 要查询的表名;
     * <p/>
     * -- 参数③ columns : 要查询的列名, 如果为null, 就会查询所有的列;
     * <p/>
     * -- 参数④ whereClause : 条件查询子句, 在这里可以使用占位符 "?";
     * <p/>
     * -- 参数⑤ whereArgs : whereClause查询子句中的传入的参数值, 逐个替换 "?" 占位符;
     * <p/>
     * -- 参数⑥ groupBy: 控制分组, 如果为null 将不会分组;
     * <p/>
     * -- 参数⑦ having : 对分组进行过滤;
     * <p/>
     * -- 参数⑧ orderBy : 对记录进行排序;
     * <p/>
     * -- 参数⑨ limite : 用于分页, 如果为null, 就认为不进行分页查询;
     * <p/>
     * -- 参数⑩ cancellationSignal : 进程中取消操作的信号, 如果操作被取消, 当查询命令执行时会抛出 OperationCanceledException 异常;
     */

    //读取下载到本地的任务数据
    public void getTaskData() {
        Cursor cursor = db.query("Task", null, null, null, null, null, null);//查询并获得游标
        //如果游标为空，则显示没有数据图片
        if (cursor.getCount() == 0) {
            if (noData.getVisibility() == View.GONE) {
                noData.setVisibility(View.VISIBLE);
            }
            return;
        }
        /*if (!cursor.isFirst()) {  //是否在第一行
            cursor.moveToFirst();  //将游标移动到第一行
        } else {
            if (noData.getVisibility() == View.VISIBLE) {
                noData.setVisibility(View.GONE);
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
        }*/
        if (noData.getVisibility() == View.VISIBLE) {
            noData.setVisibility(View.GONE);
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

        /*int columnNumb = cursor.getColumnCount();
          for (int i=0;i < columnNumb;i++){  //循环读取每列的数据
                    String columnName = cursor.getColumnName(i);  //获得每列的列名
                    String taskName = cursor.getString(i);  //获取每列对应的值
                }*/
        //cursor游标操作完成以后,一定要关闭
        cursor.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //释放和数据库的连接
        db.close();
    }
}
