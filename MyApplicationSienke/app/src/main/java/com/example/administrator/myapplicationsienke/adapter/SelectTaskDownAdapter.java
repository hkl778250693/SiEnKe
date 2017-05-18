package com.example.administrator.myapplicationsienke.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.model.SelectTaskDownViewHolder;
import com.example.administrator.myapplicationsienke.model.SelectTaskItem;
import com.example.administrator.myapplicationsienke.model.TaskChoose;
import com.example.administrator.myapplicationsienke.model.TaskChooseViewHolder;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017-05-17.
 */
public class SelectTaskDownAdapter extends BaseAdapter {
    private Context context;
    private List<SelectTaskItem> selectTaskItemList;
    private LayoutInflater layoutInflater;
    private static HashMap<Integer, Boolean> isCheck = new HashMap<Integer, Boolean>();

    public SelectTaskDownAdapter(Context context, List<SelectTaskItem> selectTaskItemList) {
        this.context = context;
        this.selectTaskItemList = selectTaskItemList;
        if (context != null) {
            layoutInflater = LayoutInflater.from(context);
        }
        // 默认为不选中
        initCheck(false);
    }
    // 初始化map集合
    public void initCheck(boolean flag) {
        for (int i = 0; i < selectTaskItemList.size(); i++) {  // map集合的数量和list的数量是一致的
            getIsCheck().put(i, flag);  // 设置默认的显示
        }
    }


    @Override
    public int getCount() {
        return selectTaskItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return selectTaskItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SelectTaskDownViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new SelectTaskDownViewHolder();
            convertView = layoutInflater.inflate(R.layout.select_task_listview_item, null);
            viewHolder.taskId = (TextView) convertView.findViewById(R.id.task_id);
            viewHolder.taskName = (TextView) convertView.findViewById(R.id.task_name);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.is_checked);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (SelectTaskDownViewHolder) convertView.getTag();
        }
        SelectTaskItem item = selectTaskItemList.get(position);
        viewHolder.taskId.setText(item.getTaskId());
        viewHolder.taskName.setText(item.getTaskName());
        viewHolder.checkBox.setChecked(getIsCheck().get(position));
        return convertView;
    }

    public static HashMap<Integer, Boolean> getIsCheck() {
        return isCheck;
    }

    public static void setIsCheck(HashMap<Integer, Boolean> isCheck) {
        SelectTaskDownAdapter.isCheck = isCheck;
    }
}
