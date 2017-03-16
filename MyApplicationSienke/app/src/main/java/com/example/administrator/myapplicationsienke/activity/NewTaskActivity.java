package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.administrator.myapplicationsienke.R;

/**
 * Created by Administrator on 2017/3/15.
 */
public class NewTaskActivity extends Activity {
    private ImageView newTaskBack;
    private Button newPlanAddBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        bindView();
    }

    //绑定控件
    private void bindView() {
        newTaskBack = (ImageView) findViewById(R.id.newtask_back);
        newPlanAddBtn = (Button) findViewById(R.id.newplan_add_btn);
        //点击事件
        newTaskBack.setOnClickListener(onClickListener);
        newPlanAddBtn.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.newtask_back:
                    Intent intent = new Intent(NewTaskActivity.this, SecurityChooseActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.newplan_add_btn:
                    Intent intent1 = new Intent(NewTaskActivity.this, SecurityChooseActivity.class);
                    startActivity(intent1);
                    finish();
                    break;
            }
        }
    };


}
