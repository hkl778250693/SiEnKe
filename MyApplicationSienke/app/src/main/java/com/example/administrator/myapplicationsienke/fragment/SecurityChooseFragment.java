package com.example.administrator.myapplicationsienke.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.myapplicationsienke.R;

/**
 * Created by Administrator on 2017/3/16 0016.
 */
public class SecurityChooseFragment extends Fragment {
    private View view;
                        //继续安检  //用户列表 //未检用户 //新建任务   //安检统计     //任务选择
    private TextView continueSecurity,userList,noCheckUser,newTask,securityStatistics,taskChoose;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_security,null);
        //绑定控件ID
        bindView();
        return view;
    }

    //绑定控件
    public void bindView(){
        continueSecurity = (TextView) view.findViewById(R.id.continue_security);
        userList = (TextView) view.findViewById(R.id.user_list);
        noCheckUser = (TextView) view.findViewById(R.id.no_check_user);
        newTask = (TextView) view.findViewById(R.id.new_task);
        securityStatistics = (TextView) view.findViewById(R.id.security_statistics);
        taskChoose = (TextView) view.findViewById(R.id.task_choose);

        //点击事件
        continueSecurity.setOnClickListener(clickListener);
        userList.setOnClickListener(clickListener);
        noCheckUser.setOnClickListener(clickListener);
        newTask.setOnClickListener(clickListener);
        securityStatistics.setOnClickListener(clickListener);
        taskChoose.setOnClickListener(clickListener);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.continue_security:
                    break;
                case R.id.user_list:
                    break;
                case R.id.no_check_user:
                    break;
                case R.id.new_task:
                    break;
                case R.id.security_statistics:
                    break;
                case R.id.task_choose:
                    break;
            }
        }
    };
}
