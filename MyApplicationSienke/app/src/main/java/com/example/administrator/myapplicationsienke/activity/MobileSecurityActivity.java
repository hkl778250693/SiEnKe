package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import com.example.administrator.myapplicationsienke.R;

/**
 * Created by Administrator on 2017/3/14.
 */
public class MobileSecurityActivity extends Activity {
    Button logon_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_security);


        bindView();
    }

    //绑定控件
    private void bindView() {
    }
}
