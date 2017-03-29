package com.example.administrator.myapplicationsienke.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.model.TaskChoose;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/3/15 0015.
 */
public class TaskChooseAdapter extends BaseAdapter {
    private Context context;
    private List<TaskChoose> taskChooseList;
    private LayoutInflater layoutInflater;
    public HashMap<Integer,Boolean> state = new HashMap<Integer,Boolean>();

    public TaskChooseAdapter(Context context,List<TaskChoose> taskChooseList){
        this.context = context;
        this.taskChooseList = taskChooseList;
        if(context != null){
            layoutInflater = LayoutInflater.from(context);
        }
    }

    @Override
    public int getCount() {
        return taskChooseList.size();
    }

    @Override
    public Object getItem(int position) {
        return taskChooseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.task_choose_listview_item,null);
            viewHolder.taskName = (TextView) convertView.findViewById(R.id.task_name);
            viewHolder.taskNumber = (TextView) convertView.findViewById(R.id.task_number);
            viewHolder.checkType = (TextView) convertView.findViewById(R.id.check_type);
            viewHolder.totalUserNumber = (TextView) convertView.findViewById(R.id.total_user_number);
            viewHolder.endTime = (TextView) convertView.findViewById(R.id.end_time);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.is_checked);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        TaskChoose taskChoose = taskChooseList.get(position);
        viewHolder.taskName.setText(taskChoose.getTaskName());
        viewHolder.taskNumber.setText(taskChoose.getTaskNumber());
        viewHolder.checkType.setText(taskChoose.getCheckType());
        viewHolder.totalUserNumber.setText(taskChoose.getTotalUserNumber());
        viewHolder.endTime.setText(taskChoose.getEndTime());
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    state.put(position,isChecked);
                }else {
                    state.remove(position);
                }
            }
        });
        viewHolder.checkBox.setChecked((state.get(position) == null ? false : true));
        return convertView;
    }

    class ViewHolder{
        TextView taskName;  //任务名称
        TextView taskNumber;  //任务编号
        TextView checkType;  //安检类型
        TextView totalUserNumber;   //总用户数
        TextView endTime;  //结束时间
        CheckBox checkBox;  //选择框
    }
}
