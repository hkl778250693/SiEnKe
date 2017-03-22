package com.example.administrator.myapplicationsienke.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.adapter.SecurityCheckViewPagerAdapter;
import com.example.administrator.myapplicationsienke.fragment.DataTransferFragment;
import com.example.administrator.myapplicationsienke.fragment.SecurityChooseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/14.
 */
public class SecurityChooseActivity extends FragmentActivity {
    private Button file, settings, quite; //文件管理 系统设置 退出应用
    private LayoutInflater inflater; //转换器
    private View popupwindowView;
    private PopupWindow popupWindow;
    private ImageView security_check_go;
    private ImageView security_check_back;
    private RadioButton optionRbt;  //选项按钮
    private RadioButton dataTransferRbt;  //数据传输按钮
    private List<Fragment> fragmentList;
    private ViewPager viewPager;
    private SecurityCheckViewPagerAdapter adapter;


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
        setContentView(R.layout.activity_security);

        bindView();//绑定控件
        defaultSetting();//初始化设置
        setViewPager();//设置viewPager
        setViewClickListener();//点击事件
    }

    //绑定控件
    private void bindView() {
        security_check_back = (ImageView) findViewById(R.id.security_check_back);
        security_check_go = (ImageView) findViewById(R.id.security_check_go);
        optionRbt = (RadioButton) findViewById(R.id.option_rbt);
        dataTransferRbt = (RadioButton) findViewById(R.id.data_transfer_rbt);
        viewPager = (ViewPager) findViewById(R.id.security_viewpager);
    }

    //点击事件
    public void setViewClickListener() {
        security_check_back.setOnClickListener(onClickListener);
        security_check_go.setOnClickListener(onClickListener);
        optionRbt.setOnClickListener(onClickListener);
        dataTransferRbt.setOnClickListener(onClickListener);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        optionRbt.setChecked(true);
                        dataTransferRbt.setChecked(false);
                        break;
                    case 1:
                        optionRbt.setChecked(false);
                        dataTransferRbt.setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
                case R.id.security_check_go:
                    createPopupwindow();
                    Log.i("createPopupwindow===>", "true");
                    break;
                case R.id.option_rbt:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.data_transfer_rbt:
                    viewPager.setCurrentItem(1);
                    break;
            }
        }
    };

    //popupwindow
    public void createPopupwindow() {
        inflater = LayoutInflater.from(SecurityChooseActivity.this);
        popupwindowView = inflater.inflate(R.layout.popup_window_security, null);
        popupWindow = new PopupWindow(popupwindowView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //绑定控件ID
        file = (Button) popupwindowView.findViewById(R.id.file);//文件管理
        settings = (Button) popupwindowView.findViewById(R.id.settings);//系统设置
        quite = (Button) popupwindowView.findViewById(R.id.quite);//安全退出
        //设置点击事件
        file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.file:
                        Intent intent = new Intent(SecurityChooseActivity.this, FileManageActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.settings:
                        Intent intent = new Intent(SecurityChooseActivity.this, SystemSettingActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });

        quite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });

        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        backgroundAlpha(0.8F);   //背景变暗
        popupWindow.showAsDropDown(security_check_go, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0F);
            }
        });

    }

    //设置背景透明度
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    //初始化设置
    private void defaultSetting() {
        optionRbt.setChecked(true);
    }

    //设置viewPager
    private void setViewPager() {
        fragmentList = new ArrayList<>();
        //添加fragment到list
        fragmentList.add(new SecurityChooseFragment());
        fragmentList.add(new DataTransferFragment());
        //避免报空指针
        if (fragmentList != null) {
            adapter = new SecurityCheckViewPagerAdapter(getSupportFragmentManager(), fragmentList);
        }
        viewPager.setAdapter(adapter);
    }

}
