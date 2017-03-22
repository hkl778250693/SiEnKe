package com.example.administrator.myapplicationsienke.activity;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.adapter.UserListviewAdapter;
import com.example.administrator.myapplicationsienke.model.UserListviewItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/16.
 */
public class UserListActivity extends Activity {
    private ImageView back;
    private ListView listView;
    private TextView securityCheckCase;
    private LayoutInflater inflater;  //转换器
    private View securityCaseView;
    private RadioButton notSecurityCheck,passSecurityCheck,notPassSecurityCheck;
    private PopupWindow popupWindow;
    private List<UserListviewItem> userListviewItemList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlist);

        getData();//暂时获取假数据
        bindView();//绑定控件
        setOnClickListener();//点击事件
    }

    //强制竖屏
    @Override
    protected void onResume() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
    }

    //绑定控件ID
    private void bindView() {
        back = (ImageView) findViewById(R.id.back);
        listView = (ListView) findViewById(R.id.listview);
        securityCheckCase= (TextView) findViewById(R.id.security_check_case);
    }

    //点击事件
    private void setOnClickListener(){
        back.setOnClickListener(onClickListener);
        securityCheckCase.setOnClickListener(onClickListener);
        UserListviewAdapter userListviewAdapter = new UserListviewAdapter(UserListActivity.this, userListviewItemList);
        listView.setAdapter(userListviewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(UserListActivity.this,UserDetailInfoActivity.class);
                startActivity(intent);
            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:
                    UserListActivity.this.finish();
                    break;
                case R.id.security_check_case:
                    createSecurityCasePopupwindow();
                    break;
            }
        }
    };

    //暂时获取假数据
    public void getData() {
        for (int i = 0; i < 20; i++) {
            UserListviewItem userListviewItem = new UserListviewItem();
            userListviewItemList.add(userListviewItem);
        }
    }
    //popupwindow
    public void createSecurityCasePopupwindow(){
        inflater = LayoutInflater.from(UserListActivity.this);
        securityCaseView = inflater.inflate(R.layout.popupwindow_userlist_choose,null);
        popupWindow = new PopupWindow(securityCaseView, LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        //绑定控件ID
        notSecurityCheck = (RadioButton) securityCaseView.findViewById(R.id.not_security_check);
        passSecurityCheck = (RadioButton) securityCaseView.findViewById(R.id.pass_security_check);
        notPassSecurityCheck = (RadioButton) securityCaseView.findViewById(R.id.not_pass_security_check);
        //设置点击事件
        notSecurityCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityCheckCase.setText(notSecurityCheck.getText());
            }
        });
        passSecurityCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityCheckCase.setText(passSecurityCheck.getText());
            }
        });
        notPassSecurityCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityCheckCase.setText(notPassSecurityCheck.getText());
            }
        });
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
        popupWindow.setAnimationStyle(R.style.Popupwindow);
        backgroundAlpha(0.8F);   //背景变暗
        popupWindow.showAsDropDown(securityCheckCase,200,0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0F);
            }
        });
    }
    //设置背景透明度
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

}
