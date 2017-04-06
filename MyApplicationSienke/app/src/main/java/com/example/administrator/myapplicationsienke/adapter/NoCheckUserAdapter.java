package com.example.administrator.myapplicationsienke.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.model.NoCheckUserItem;
import com.example.administrator.myapplicationsienke.model.UserListviewItem;

import java.util.List;

/**
 * Created by Administrator on 2017/3/16.
 */
public class NoCheckUserAdapter extends BaseAdapter {
    private Context context;
    private List<UserListviewItem> itemList;
    private LayoutInflater layoutInflater;

    public NoCheckUserAdapter(Context context, List<UserListviewItem> itemList) {
        this.context = context;
        this.itemList = itemList;
        if (context != null) {
            layoutInflater = LayoutInflater.from(context);
        }
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
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
            viewHolder.if_edit = (ImageView) convertView.findViewById(R.id.if_edit);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        UserListviewItem item = itemList.get(position);
        viewHolder.security_number.setText(item.getSecurityNumber());
        Log.i("security_number=====>", "security_number=" + item.getSecurityNumber());
        viewHolder.user_name.setText(item.getUserName());
        viewHolder.number.setText(item.getNumber());
        viewHolder.phone_number.setText(item.getPhoneNumber());
        viewHolder.security_type.setText(item.getSecurityType());
        if(!item.getUserId().equals("null")){
            viewHolder.user_id.setText(item.getUserId());
        }else {
            viewHolder.user_id.setText("无");
        }
        viewHolder.address.setText(item.getAdress());
        viewHolder.if_edit.setImageResource(item.getIfEdit());

        return convertView;
    }

    public class ViewHolder {
        TextView security_number;  //安检编号
        TextView user_name;  //姓名
        TextView number;  //表编号
        TextView phone_number;  //电话号码
        TextView security_type;   //安检类型
        TextView user_id;  //用户编号
        TextView address;   //地址
        ImageView if_edit;   //是否编辑
    }
}
