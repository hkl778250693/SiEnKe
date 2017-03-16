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
public class NoCheckUserListActivity extends Activity {
    private ImageView securityNoCheckBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_choose_nocheck_listview);
        bindView();
    }
   //绑定控件
    private void bindView() {
        securityNoCheckBack= (ImageView) findViewById(R.id.security_nocheck_back);
        //点击事件
        securityNoCheckBack.setOnClickListener(onClickListener);
    }
    View.OnClickListener onClickListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.security_nocheck_back:
                    Intent intent=new Intent(NoCheckUserListActivity.this,SecurityActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };
}
