package com.example.administrator.myapplicationsienke.adapter;

import android.content.Context;
import android.util.Log;
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
        if(context != null){
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
            convertView = layoutInflater.inflate(R.layout.userlist_listview_item, null);
            viewHolder.security_number = (TextView) convertView.findViewById(R.id.security_number);
            viewHolder.user_name = (TextView) convertView.findViewById(R.id.user_name);
            viewHolder.number = (TextView) convertView.findViewById(R.id.number);
            viewHolder.phone_number = (TextView) convertView.findViewById(R.id.phone_number);
            viewHolder.security_type = (TextView) convertView.findViewById(R.id.security_type);
            viewHolder.user_id = (TextView) convertView.findViewById(R.id.user_id);
            viewHolder.address = (TextView) convertView.findViewById(R.id.address);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        UserListviewItem userListviewItem = userListviewList.get(position);
        viewHolder.security_number.setText(userListviewItem.getSecurityNumber());
        Log.i("security_number=====>","security_number="+userListviewItem.getSecurityNumber());
        viewHolder.user_name.setText(userListviewItem.getUserName());
        viewHolder.number.setText(userListviewItem.getNumber());
        viewHolder.phone_number.setText(userListviewItem.getPhoneNumber());
        viewHolder.security_type.setText(userListviewItem.getSecurityType());
        viewHolder.user_id.setText(userListviewItem.getUserId());
        viewHolder.address.setText(userListviewItem.getAdress());

        return convertView;
    }

    class ViewHolder {
        TextView security_number;  //安检编号
        TextView user_name;  //姓名
        TextView number;  //表编号
        TextView phone_number;  //电话号码
        TextView security_type;   //安检类型
        TextView user_id;  //用户编号
        TextView address;   //地址
    }
}
