package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.adapter.TaskChooseAdapter;
import com.example.administrator.myapplicationsienke.mode.MySqliteHelper;
import com.example.administrator.myapplicationsienke.model.TaskChoose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/15 0015.
 */
public class TaskChooseActivity extends Activity {
    private ImageView back;
    private TextView save;
    private ListView listView;
    private List<TaskChoose> taskChooseList = new ArrayList<>();
    private SQLiteDatabase db;  //数据库
    private MySqliteHelper helper; //数据库帮助类

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_choose);

        new Thread(){
            @Override
            public void run() {
                getTaskData();//读取下载到本地的任务数据
                super.run();
            }
        }.start();
        bindView();//绑定控件
        defaultSetting();//初始化设置
        setViewClickListener();//点击事件
    }

    //绑定控件ID
    private void bindView() {
        back = (ImageView) findViewById(R.id.back);
        save = (TextView) findViewById(R.id.save);
        listView = (ListView) findViewById(R.id.listview);
    }

    //初始化设置
    private void defaultSetting() {
        helper = new MySqliteHelper(TaskChooseActivity.this,1);
        db = helper.getReadableDatabase();
    }

    //点击事件
    private void setViewClickListener() {
        back.setOnClickListener(onClickListener);
        save.setOnClickListener(onClickListener);
        TaskChooseAdapter adapter = new TaskChooseAdapter(TaskChooseActivity.this, taskChooseList);
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
                    Intent intent = new Intent(TaskChooseActivity.this, SecurityChooseActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };

    /**
     * 查询方法参数详解
     *
     * public Cursor query (boolean distinct, String table, String[] columns,
     String selection, String[] selectionArgs,
     String groupBy, String having,
     String orderBy, String limit,
     CancellationSignal cancellationSignal)
     *
     * 参数介绍 :

     -- 参数① distinct : 是否去重复, true 去重复;

     -- 参数② table : 要查询的表名;

     -- 参数③ columns : 要查询的列名, 如果为null, 就会查询所有的列;

     -- 参数④ whereClause : 条件查询子句, 在这里可以使用占位符 "?";

     -- 参数⑤ whereArgs : whereClause查询子句中的传入的参数值, 逐个替换 "?" 占位符;

     -- 参数⑥ groupBy: 控制分组, 如果为null 将不会分组;

     -- 参数⑦ having : 对分组进行过滤;

     -- 参数⑧ orderBy : 对记录进行排序;

     -- 参数⑨ limite : 用于分页, 如果为null, 就认为不进行分页查询;

     -- 参数⑩ cancellationSignal : 进程中取消操作的信号, 如果操作被取消, 当查询命令执行时会抛出 OperationCanceledException 异常;
     */

    //读取下载到本地的任务数据
    public void getTaskData() {
        Cursor cursor = db.query("Task",null,null,null,null,null,null);//查询并获得游标
        if(!cursor.isFirst()){  //是否在第一行
            cursor.moveToFirst();  //将游标移动到第一行
            int columnNumb = cursor.getColumnCount();
            while (cursor.moveToNext()){
                TaskChoose taskChoose = new TaskChoose();
                for (int i=0;i < columnNumb;i++){  //循环读取每列的数据


                }
                taskChooseList.add(taskChoose);
            }
        }
        //cursor游标操作完成以后,一定要关闭
        cursor.close();
        for (int i = 0; i < 20; i++) {
            TaskChoose taskChoose = new TaskChoose();
            taskChooseList.add(taskChoose);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //释放和数据库的连接
        db.close();
    }
}
