package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.example.administrator.myapplicationsienke.R;

/**
 * Created by Administrator on 2017/3/14.
 */
public class SecurityChooseActivity extends Activity {
    private ImageView securityCheckBack;
    private ImageView security_check_back;
    private RadioButton optionRbt;
    private RadioButton dataTransferRbt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);

        //绑定控件
        bindView();
        //初始化设置
        defaultSetting();
    }

    //绑定控件
    private void bindView() {
        securityCheckBack = (ImageView) findViewById(R.id.security_check_back);
        security_check_back = (ImageView) findViewById(R.id.security_check_back);
        optionRbt = (RadioButton) findViewById(R.id.option_rbt);
        dataTransferRbt = (RadioButton) findViewById(R.id.data_transfer_rbt);

        //点击事件
        securityCheckBack.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.security_check_back:
                    Intent intent = new Intent(SecurityChooseActivity.this, MobileSecurityActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.option_rbt:

                    break;
                case R.id.data_transfer_rbt:

                    break;
            }
        }
    };

    //初始化设置
    private void defaultSetting() {
        optionRbt.setChecked(true);
    }

}
