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
import com.example.administrator.myapplicationsienke.model.TaskChooseViewHolder;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/3/15 0015.
 */
public class TaskChooseAdapter extends BaseAdapter {
    private Context context;
    private List<TaskChoose> taskChooseList;
    private LayoutInflater layoutInflater;
    private static HashMap<Integer, Boolean> isCheck = new HashMap<Integer, Boolean>();

    public TaskChooseAdapter(Context context, List<TaskChoose> taskChooseList) {
        this.context = context;
        this.taskChooseList = taskChooseList;
        if (context != null) {
            layoutInflater = LayoutInflater.from(context);
        }
        // 默认为不选中
        initCheck(false);
    }

    // 初始化map集合
    public void initCheck(boolean flag) {
        for (int i = 0; i < taskChooseList.size(); i++) {  // map集合的数量和list的数量是一致的
            getIsCheck().put(i, flag);  // 设置默认的显示
        }
    }

    // 删除一个数据
    public void removeData(int position) {
        taskChooseList.remove(position);
    }

    public HashMap<Integer, Boolean> getHashMap() {
        // 返回状态
        return isCheck;
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
        TaskChooseViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new TaskChooseViewHolder();
            convertView = layoutInflater.inflate(R.layout.task_choose_listview_item, null);
            viewHolder.taskName = (TextView) convertView.findViewById(R.id.task_name);
            viewHolder.taskNumber = (TextView) convertView.findViewById(R.id.task_number);
            viewHolder.checkType = (TextView) convertView.findViewById(R.id.check_type);
            viewHolder.totalUserNumber = (TextView) convertView.findViewById(R.id.total_user_number);
            viewHolder.endTime = (TextView) convertView.findViewById(R.id.end_time);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.is_checked);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (TaskChooseViewHolder) convertView.getTag();
        }
        TaskChoose taskChoose = taskChooseList.get(position);
        viewHolder.taskName.setText(taskChoose.getTaskName());
        viewHolder.taskNumber.setText(taskChoose.getTaskNumber());
        viewHolder.checkType.setText(taskChoose.getCheckType());
        viewHolder.totalUserNumber.setText(taskChoose.getTotalUserNumber());
        viewHolder.endTime.setText(taskChoose.getEndTime());
        viewHolder.checkBox.setChecked(getIsCheck().get(position));
        return convertView;
    }

    public static HashMap<Integer, Boolean> getIsCheck() {
        return isCheck;
    }

    public static void setIsCheck(HashMap<Integer, Boolean> isCheck) {
        TaskChooseAdapter.isCheck = isCheck;
    }
}
