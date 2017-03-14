package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.administrator.myapplicationsienke.R;

/**
 * Created by Administrator on 2017/3/2 0002.
 */
public class BusinessManageLoginActivity extends Activity {
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_manage_login);

        bindView();

    }

    //绑定控件ID
    private void bindView(){
        loginBtn = (Button) findViewById(R.id.login_btn);

        //点击事件
        loginBtn.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.login_btn:
                    Intent intent = new Intent(BusinessManageLoginActivity.this,BusinessManageActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };
}
