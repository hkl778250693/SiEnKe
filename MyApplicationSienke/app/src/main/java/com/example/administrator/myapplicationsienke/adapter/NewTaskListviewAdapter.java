package com.example.administrator.myapplicationsienke.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.model.NewTaskListviewItem;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/4/5.
 */
public class NewTaskListviewAdapter extends BaseAdapter {
    private Context context;
    private List<NewTaskListviewItem> newTaskListviewItemList;
    private LayoutInflater layoutInflater;
    private static HashMap<Integer, Boolean> isCheck = new HashMap<Integer, Boolean>();

    public NewTaskListviewAdapter(Context context, List<NewTaskListviewItem> newTaskListviewItemList) {
        this.context = context;
        this.newTaskListviewItemList = newTaskListviewItemList;
        if (context != null) {
            layoutInflater = LayoutInflater.from(context);
        }
        isCheck.clear();
        // 默认为不选中
        initCheck(false);
    }

    // 初始化map集合
    public void initCheck(boolean flag) {
        for (int i = 0; i < newTaskListviewItemList.size(); i++) {  // map集合的数量和list的数量是一致的
            getIsCheck().put(i, flag);  // 设置默认的显示
        }
    }

    public HashMap<Integer, Boolean> getHashMap() {
        // 返回状态
        return isCheck;
    }

    @Override
    public int getCount() {
        return newTaskListviewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return newTaskListviewItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.newtask_listview_item, null);
            viewHolder.user_name = (TextView) convertView.findViewById(R.id.user_name);
            viewHolder.number = (TextView) convertView.findViewById(R.id.number);
            viewHolder.phone_number = (TextView) convertView.findViewById(R.id.phone_number);
            viewHolder.user_id = (TextView) convertView.findViewById(R.id.user_id);
            viewHolder.address = (TextView) convertView.findViewById(R.id.address);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        NewTaskListviewItem newTaskListviewItem = newTaskListviewItemList.get(position);
        Log.i("security_number=====>", "security_number=" + newTaskListviewItem.getUserName());
        viewHolder.user_name.setText(newTaskListviewItem.getUserName());
        viewHolder.number.setText(newTaskListviewItem.getNumber());
        viewHolder.phone_number.setText(newTaskListviewItem.getPhoneNumber());
        viewHolder.user_id.setText(newTaskListviewItem.getUserId());
        viewHolder.address.setText(newTaskListviewItem.getAdress());
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isCheck.put(position, true);
                    Log.i("NewTaskListviewAdapter", "当前的勾选框状态为："+isChecked);
                    setIsCheck(isCheck);
                } else {
                    isCheck.remove(position);
                    Log.i("NewTaskListviewAdapter", "当前的勾选框状态为："+isChecked);
                    setIsCheck(isCheck);
                }
                // 用map集合保存
                //isCheck.put(position, isChecked);
            }
        });
        // 设置状态
        viewHolder.checkBox.setChecked(getIsCheck().get(position));
        return convertView;
    }

    class ViewHolder {
        TextView user_name;  //姓名
        TextView number;  //表编号
        TextView phone_number;  //电话号码
        TextView user_id;  //用户编号
        TextView address;   //地址
        CheckBox checkBox;  //选择框
    }

    public static HashMap<Integer, Boolean> getIsCheck() {
        return isCheck;
    }

    public static void setIsCheck(HashMap<Integer, Boolean> isCheck) {
        NewTaskListviewAdapter.isCheck = isCheck;
        Log.i("setIsCheck", "setIsCheck进来了！"+isCheck);
    }
}
