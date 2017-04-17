package com.example.administrator.myapplicationsienke.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.adapter.SecurityCheckViewPagerAdapter;
import com.example.administrator.myapplicationsienke.fragment.DataTransferFragment;
import com.example.administrator.myapplicationsienke.fragment.SecurityChooseFragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/3/14.
 */
public class SecurityChooseActivity extends FragmentActivity {
    private RadioButton file, settings, quite; //文件管理 系统设置 退出应用
    private LayoutInflater inflater; //转换器
    private View popupwindowView;
    private PopupWindow popupWindow;
    private ImageView security_check_go;
    private RadioButton optionRbt;  //选项按钮
    private RadioButton dataTransferRbt;  //数据传输按钮
    private List<Fragment> fragmentList;
    private ViewPager viewPager;
    private SecurityCheckViewPagerAdapter adapter;
    private long exitTime = 0;//退出程序
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Bundle params;
    private Set<String> stringSet = new HashSet<>();//保存字符串参数
    private int task_total_numb;
    private ArrayList<String> stringList = new ArrayList<>();//得到的字符串集合

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
        security_check_go = (ImageView) findViewById(R.id.security_check_go);
        optionRbt = (RadioButton) findViewById(R.id.option_rbt);
        dataTransferRbt = (RadioButton) findViewById(R.id.data_transfer_rbt);
        viewPager = (ViewPager) findViewById(R.id.security_viewpager);
    }

    //点击事件
    public void setViewClickListener() {
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
        file = (RadioButton) popupwindowView.findViewById(R.id.file);//文件管理
        settings = (RadioButton) popupwindowView.findViewById(R.id.settings);//系统设置
        quite = (RadioButton) popupwindowView.findViewById(R.id.quite);//安全退出
        //设置点击事件
        file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.file:
                        Intent intent = new Intent(SecurityChooseActivity.this, FileManageActivity.class);
                        startActivity(intent);
                        popupWindow.dismiss();
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
                        popupWindow.dismiss();
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
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.check_hidden_danger_down_shape));
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
        sharedPreferences = this.getSharedPreferences("data", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt("problem_number",sharedPreferences.getInt("problem_number",0));
        editor.commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent != null){
            setIntent(intent);
            int number = getIntent().getIntExtra("down",0);
            Log.i("onNewIntent", "onNewIntent进来了！返回的结果="+number);
            if( number == 1){     //获取任务选择页面传过来的参数，如果参数为 1 ，则让viewpager显示数据传输fragment
                viewPager.setCurrentItem(1);
                dataTransferRbt.setChecked(true);
            }
            params = getIntent().getExtras();
            if (params != null) {
                Log.i("SecurityChooseFragment=", "bundle不为空");
                task_total_numb = params.getInt("task_total_numb", 0);
                Log.i("SecurityChooseFragment=", "task_total_numb=" + task_total_numb);
                stringList = params.getStringArrayList("taskId");
                if (task_total_numb != 0) {
                    stringSet.clear();
                    for (int i = 0; i < task_total_numb; i++) {
                        stringSet.add(stringList.get(i));
                        Log.i("onNewIntent====>", "得到的参数为：" +stringList.get(i));
                    }
                }
                editor.putInt("task_total_numb", task_total_numb);
                editor.putStringSet("stringSet", stringSet);
                editor.commit();
            }
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 捕捉返回事件按钮
     * 因为此 Activity继承 TabActivity,用 onKeyDown无响应，
     * 所以改用 dispatchKeyEvent
     * <p/>
     * 一般的 Activity 用 onKeyDown就可以了
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                this.exitApp();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    /**
     * 退出程序
     */
    private void exitApp() {
        // 判断2次点击事件时间
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Log.i("exitTime==========>", System.currentTimeMillis() - exitTime + "");
            //-------------Activity.this的context 返回当前activity的上下文，属于activity，activity 摧毁他就摧毁
            //-------------getApplicationContext() 返回应用的上下文，生命周期是整个应用，应用摧毁它才摧毁
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

}
