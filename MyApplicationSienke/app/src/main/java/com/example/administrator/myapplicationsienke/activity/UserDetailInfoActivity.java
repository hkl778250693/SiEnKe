package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.myapplicationsienke.R;

/**
 * Created by Administrator on 2017/3/16 0016.
 */
public class UserDetailInfoActivity extends Activity {
    private ImageView back,more,addImgs;  //返回，更多，添加图片
    private TextView securityCheckCase,securityHiddenType,securityHiddenReason;  //安全情况,安全隐患类型，安全隐患原因
    private Button saveBtn;  //保存

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail_info);

        //绑定控件
        bindView();
        //初始化设置
        defaultSetting();
    }

    //绑定控件
    private void bindView() {
        back = (ImageView) findViewById(R.id.back);
        more = (ImageView) findViewById(R.id.more);
        securityCheckCase = (TextView) findViewById(R.id.security_check_case);
        securityHiddenType = (TextView) findViewById(R.id.security_hidden_type);
        securityHiddenReason = (TextView) findViewById(R.id.security_hidden_reason);
        addImgs = (ImageView) findViewById(R.id.add_imgs);
        saveBtn = (Button) findViewById(R.id.save_btn);

        //点击事件
        back.setOnClickListener(onClickListener);
        more.setOnClickListener(onClickListener);
        securityCheckCase.setOnClickListener(onClickListener);
        securityHiddenType.setOnClickListener(onClickListener);
        securityHiddenReason.setOnClickListener(onClickListener);
        addImgs.setOnClickListener(onClickListener);
        saveBtn.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:
                    finish();
                    break;
                case R.id.more:
                    break;
                case R.id.security_check_case:
                    break;
                case R.id.security_hidden_type:
                    break;
                case R.id.security_hidden_reason:
                    break;
                case R.id.add_imgs:
                    break;
                case R.id.save_btn:
                    break;
            }
        }
    };

    //初始化设置
    private void defaultSetting() {

    }
}
