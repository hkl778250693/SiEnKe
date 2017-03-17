package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.administrator.myapplicationsienke.R;

/**
 * Created by Administrator on 2017/3/17.
 */
public class IpSettingActivity extends Activity {
    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_settings);
        //绑定控件
        bindView();
        //点击事件
        setViewClickListener();
    }
    //绑定控件
    private void bindView(){
        back= (ImageView) findViewById(R.id.back);

    }
    //点击事件
    private void setViewClickListener(){
        back.setOnClickListener(clickListener);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.back:
                    IpSettingActivity.this.finish();
                    break;
            }
        }
    };
}

