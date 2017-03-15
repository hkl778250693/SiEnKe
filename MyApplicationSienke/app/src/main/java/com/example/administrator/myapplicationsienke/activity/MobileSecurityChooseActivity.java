package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.administrator.myapplicationsienke.R;

/**
 * Created by Administrator on 2017/3/14.
 */
public class MobileSecurityChooseActivity extends Activity {
    private ImageView security_check_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_choose);

        bindView();
    }
    //绑定控件
    private void bindView() {
        security_check_back= (ImageView) findViewById(R.id.security_check_back);
        //点击事件
        security_check_back.setOnClickListener(onClickListener);
    }
    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.security_check_back:
                    Intent intent=new Intent(MobileSecurityChooseActivity.this,MobileSecurityActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };

}
