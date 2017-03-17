package com.example.administrator.myapplicationsienke.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
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
    private ImageView security_check_back;
    private RadioButton optionRbt;  //选项按钮
    private RadioButton dataTransferRbt;  //数据传输按钮
    private List<Fragment> fragmentList;
    private ViewPager viewPager;
    private SecurityCheckViewPagerAdapter adapter;

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
        optionRbt = (RadioButton) findViewById(R.id.option_rbt);
        dataTransferRbt = (RadioButton) findViewById(R.id.data_transfer_rbt);
        viewPager = (ViewPager) findViewById(R.id.security_viewpager);
    }

    //点击事件
    public void setViewClickListener(){
        security_check_back.setOnClickListener(onClickListener);
        optionRbt.setOnClickListener(onClickListener);
        dataTransferRbt.setOnClickListener(onClickListener);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
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
                case R.id.option_rbt:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.data_transfer_rbt:
                    viewPager.setCurrentItem(1);
                    break;
            }
        }
    };

    //初始化设置
    private void defaultSetting() {
        optionRbt.setChecked(true);
    }

    //设置viewPager
    private void setViewPager(){
        fragmentList = new ArrayList<>();
        //添加fragment到list
        fragmentList.add(new SecurityChooseFragment());
        fragmentList.add(new DataTransferFragment());
        //避免报空指针
        if(fragmentList != null){
            adapter = new SecurityCheckViewPagerAdapter(getSupportFragmentManager(),fragmentList);
        }
        viewPager.setAdapter(adapter);
    }

}
