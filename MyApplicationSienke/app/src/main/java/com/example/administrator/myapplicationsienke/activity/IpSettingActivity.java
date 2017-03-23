package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.myapplicationsienke.R;

/**
 * Created by Administrator on 2017/3/17.
 */
public class IpSettingActivity extends Activity {
    private ImageView back;
    private EditText ipEdit,portEdit;
    private Button confirmBtn;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String ip,port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_settings);

        bindView();//绑定控件
        defaultSetting();//初始化设置
        setViewClickListener();//点击事件
    }
    //绑定控件
    private void bindView(){
        back= (ImageView) findViewById(R.id.back);
        ipEdit = (EditText) findViewById(R.id.ip_edit);
        confirmBtn = (Button) findViewById(R.id.confirm_btn);
        portEdit = (EditText) findViewById(R.id.port_edit);
    }
    //点击事件
    private void setViewClickListener(){
        back.setOnClickListener(clickListener);
        confirmBtn.setOnClickListener(clickListener);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.back:
                    IpSettingActivity.this.finish();
                    break;
                case R.id.confirm_btn:
                    ip = ipEdit.getText().toString().trim();
                    port = portEdit.getText().toString().trim();
                    editor.putString("security_ip",ip);
                    editor.putString("security_port",port);
                    editor.commit();
                    Toast.makeText(IpSettingActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    //初始化设置
    private void defaultSetting() {
        sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
}

