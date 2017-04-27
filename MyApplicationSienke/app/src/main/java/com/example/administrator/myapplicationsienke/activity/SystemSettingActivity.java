package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.mode.MySqliteHelper;

/**
 * Created by Administrator on 2017/3/21.
 */
public class SystemSettingActivity extends Activity {
    private ImageView back;
    private TextView ip,clearData;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private SQLiteDatabase db;  //数据库
    private MySqliteHelper helper; //数据库帮助类
    private LayoutInflater layoutInflater;
    private PopupWindow popupWindow;
    private View view;
    private RadioButton cancelRb, saveRb;
    private TextView tips;
    private LinearLayout rootLinearlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_settings);
        //绑定控件
        bindView();
        //点击事件
        setViewClickListener();
    }

    //绑定控件
    private void bindView() {
        back = (ImageView) findViewById(R.id.back);
        ip = (TextView) findViewById(R.id.ip);
        clearData = (TextView) findViewById(R.id.clear_data);
        rootLinearlayout = (LinearLayout)findViewById(R.id.root_linearlayout);
    }

    //点击事件
    private void setViewClickListener() {
        back.setOnClickListener(clickListener);
        ip.setOnClickListener(clickListener);
        clearData.setOnClickListener(clickListener);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:
                    finish();
                    break;
                case R.id.ip:
                    Intent intent = new Intent(SystemSettingActivity.this,IpSettingActivity.class);
                    startActivity(intent);
                    break;
                case R.id.clear_data:
                    createClearDataPopup();
                    break;
            }
        }
    };

    private void clearData(){
        sharedPreferences = this.getSharedPreferences("data", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        helper = new MySqliteHelper(SystemSettingActivity.this, 1);
        db = helper.getWritableDatabase();
        editor.clear();
        editor.apply();
        db.delete("User",null,null);  //删除User表中的所有数据（官方推荐方法）
        db.delete("Task",null,null);  //删除Task表中的所有数据
        db.delete("SecurityState",null,null);  //删除SecurityState表中的所有数据
        db.delete("security_content",null,null);  //删除security_content表中的所有数据
        db.delete("security_hidden",null,null);  //删除security_hidden表中的所有数据
        db.delete("security_hidden_reason",null,null);  //删除security_hidden_reason表中的所有数据
        //设置id从1开始（sqlite默认id从1开始），若没有这一句，id将会延续删除之前的id
        db.execSQL("update sqlite_sequence set seq=0 where name='User'");
        db.execSQL("update sqlite_sequence set seq=0 where name='Task'");
        db.execSQL("update sqlite_sequence set seq=0 where name='SecurityState'");
        db.execSQL("update sqlite_sequence set seq=0 where name='security_content'");
        db.execSQL("update sqlite_sequence set seq=0 where name='security_hidden'");
        db.execSQL("update sqlite_sequence set seq=0 where name='security_hidden_reason'");
    }

    //弹出清空数据前提示popupwindow
    public void createClearDataPopup() {
        layoutInflater = LayoutInflater.from(SystemSettingActivity.this);
        view = layoutInflater.inflate(R.layout.popupwindow_user_detail_info_save, null);
        popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        //绑定控件ID
        tips = (TextView) view.findViewById(R.id.tips);
        cancelRb = (RadioButton) view.findViewById(R.id.cancel_rb);
        saveRb = (RadioButton) view.findViewById(R.id.save_rb);
        //设置点击事件
        tips.setText("确定要清空本地数据吗！");
        saveRb.setText("确定");
        cancelRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        saveRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                clearData();
            }
        });
        popupWindow.update();
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
        popupWindow.setAnimationStyle(R.style.camera);
        backgroundAlpha(0.8F);   //背景变暗
        popupWindow.showAtLocation(rootLinearlayout, Gravity.CENTER, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0F);
            }
        });
    }

    //设置背景透明度
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = SystemSettingActivity.this.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        SystemSettingActivity.this.getWindow().setAttributes(lp);
    }

}
