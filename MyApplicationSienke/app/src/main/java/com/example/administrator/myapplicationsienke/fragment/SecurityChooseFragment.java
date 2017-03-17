package com.example.administrator.myapplicationsienke.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.activity.NewTaskActivity;
import com.example.administrator.myapplicationsienke.activity.NoCheckUserListActivity;
import com.example.administrator.myapplicationsienke.activity.SecurityStatisticsActivity;
import com.example.administrator.myapplicationsienke.activity.TaskChooseActivity;
import com.example.administrator.myapplicationsienke.activity.UserListActivity;

/**
 * Created by Administrator on 2017/3/16 0016.
 */
public class SecurityChooseFragment extends Fragment {
    private View view;
                        //继续安检  //用户列表 //未检用户 //新建任务   //安检统计     //任务选择
    private LinearLayout continueSecurity,userList,noCheckUser,newTask,securityStatistics,taskChoose;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_security,null);

        bindView();//绑定控件ID
        setViewClickListener();//点击事件
        return view;
    }

    //绑定控件
    public void bindView(){
        continueSecurity = (LinearLayout) view.findViewById(R.id.continue_security);
        userList = (LinearLayout) view.findViewById(R.id.user_list);
        noCheckUser = (LinearLayout) view.findViewById(R.id.no_check_user);
        newTask = (LinearLayout) view.findViewById(R.id.new_task);
        securityStatistics = (LinearLayout) view.findViewById(R.id.security_statistics);
        taskChoose = (LinearLayout) view.findViewById(R.id.task_choose);
    }

    //点击事件
    private void setViewClickListener(){
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
                    Intent intent = new Intent(getActivity(),UserListActivity.class);
                    startActivity(intent);
                    break;
                case R.id.no_check_user:
                    Intent intent1 = new Intent(getActivity(), NoCheckUserListActivity.class);
                    startActivity(intent1);
                    break;
                case R.id.new_task:
                    Intent intent2 = new Intent(getActivity(), NewTaskActivity.class);
                    startActivity(intent2);
                    break;
                case R.id.security_statistics:
                    Intent intent3 = new Intent(getActivity(), SecurityStatisticsActivity.class);
                    startActivity(intent3);
                    break;
                case R.id.task_choose:
                    Intent intent4 = new Intent(getActivity(), TaskChooseActivity.class);
                    startActivity(intent4);
                    break;
            }
        }
    };
}
