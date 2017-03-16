package com.example.administrator.myapplicationsienke.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.model.UserListviewItem;

import java.util.List;

/**
 * Created by Administrator on 2017/3/16.
 */
public class UserListviewAdapter extends BaseAdapter {
    private Context context;
    private List<UserListviewItem> userListviewList;
    private LayoutInflater layoutInflater;

    public UserListviewAdapter(Context context, List<UserListviewItem> userListviewList) {
        this.context = context;
        this.userListviewList = userListviewList;
        if (context != null) {
            layoutInflater = LayoutInflater.from(context);
        }
    }

    @Override
    public int getCount() {
        return userListviewList.size();
    }

    @Override
    public Object getItem(int position) {
        return userListviewList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.task_choose_listview_item, null);
            viewHolder.user_name = (TextView) convertView.findViewById(R.id.user_name);
            viewHolder.number = (TextView) convertView.findViewById(R.id.number);
            viewHolder.phone_number = (TextView) convertView.findViewById(R.id.phone_number);
            viewHolder.security_type = (TextView) convertView.findViewById(R.id.security_type);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.adress = (TextView) convertView.findViewById(R.id.adress);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        UserListviewItem userListviewItem = userListviewList.get(position);

        return null;
    }

    class ViewHolder {
        TextView user_name;  //姓名
        TextView number;  //表编号
        TextView phone_number;  //电话号码
        TextView security_type;   //安检类型
        TextView time;  //时间
        TextView adress;   //地址
    }
}
