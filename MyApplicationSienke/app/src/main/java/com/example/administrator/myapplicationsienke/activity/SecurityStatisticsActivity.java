package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.administrator.myapplicationsienke.R;

/**
 * Created by Administrator on 2017/3/16.
 */
public class SecurityStatisticsActivity extends Activity {
    private ImageView securityStatisticsBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_statistics);
        bindView();
    }
    //绑定控件
    private void bindView(){
        securityStatisticsBack= (ImageView) findViewById(R.id.security_statistics_back);
    }
    //点击事件
    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.security_statistics_back:
                    Intent intent=new Intent(SecurityStatisticsActivity.this,SecurityActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };
}
