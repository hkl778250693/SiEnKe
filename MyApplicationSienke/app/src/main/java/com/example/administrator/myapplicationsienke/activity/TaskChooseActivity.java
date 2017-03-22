package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.adapter.TaskChooseAdapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_choose);

        getData();//暂时获取假数据
        bindView();//绑定控件
        setViewClickListener();//点击事件
    }

    //绑定控件ID
    private void bindView() {
        back = (ImageView) findViewById(R.id.back);
        save = (TextView) findViewById(R.id.save);
        listView = (ListView) findViewById(R.id.listview);
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

    //暂时获取假数据
    public void getData() {
        for (int i = 0; i < 20; i++) {
            TaskChoose taskChoose = new TaskChoose();
            taskChooseList.add(taskChoose);
        }
    }
}
