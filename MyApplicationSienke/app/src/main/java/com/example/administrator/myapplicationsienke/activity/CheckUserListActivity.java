package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.administrator.myapplicationsienke.R;

/**
 * Created by Administrator on 2017/3/15.
 */
public class CheckUserListActivity extends Activity {
    private ImageView securityCheckBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlist);

        bindView();//绑定控件
        setViewClickListener();//点击事件
    }

    //绑定控件
    private void bindView() {
        securityCheckBack= (ImageView) findViewById(R.id.back);
    }

    //点击事件
    private void setViewClickListener(){
        securityCheckBack.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener=new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.back:
                    Intent intent=new Intent(CheckUserListActivity.this,SecurityChooseActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }

        }
    };


}
