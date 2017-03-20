package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.administrator.myapplicationsienke.R;

/**
 * Created by Administrator on 2017/3/14.
 */
public class MobileSecurityActivity extends Activity {
    Button logonBtn;

    //强制竖屏
    @Override
    protected void onResume() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_security);

        bindView();//绑定控件
        setViewClickListener();//点击事件
    }

    //绑定控件
    private void bindView() {
        logonBtn = (Button) findViewById(R.id.logon_btn);
    }

    //点击事件
    private void setViewClickListener() {
        logonBtn.setOnClickListener(clickListener);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.logon_btn:
                    Intent intent = new Intent(MobileSecurityActivity.this, SecurityChooseActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };
}
