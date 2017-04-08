package com.example.administrator.myapplicationsienke.adapter;

import android.content.Context;
import android.provider.Contacts;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.model.UserListviewItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2017/3/16.
 */
public class UserListviewAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private List<UserListviewItem> userListviewList;  //传递过来的数据源   这个数据是会改变的，所以要有个变量来备份一下原始数据
    private List<UserListviewItem> backList;  //备用数据源
    private LayoutInflater layoutInflater;
    private MyFilter myFilter;

    public UserListviewAdapter(Context context, List<UserListviewItem> userListviewList) {
        this.context = context;
        this.userListviewList = userListviewList;
        backList = userListviewList;
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
        UserListviewItem userListviewItem = userListviewList.get(position);
        viewHolder.security_number.setText(userListviewItem.getSecurityNumber());
        Log.i("security_number=====>", "security_number=" + userListviewItem.getSecurityNumber());
        viewHolder.user_name.setText(userListviewItem.getUserName());
        viewHolder.number.setText(userListviewItem.getNumber());
        if (!userListviewItem.getPhoneNumber().equals("null")) {
            viewHolder.phone_number.setText(userListviewItem.getPhoneNumber());
        } else {
            viewHolder.phone_number.setText("无");
        }
        viewHolder.security_type.setText(userListviewItem.getSecurityType());
        if (!userListviewItem.getUserId().equals("null")) {
            viewHolder.user_id.setText(userListviewItem.getUserId());
        } else {
            viewHolder.user_id.setText("无");
        }
        viewHolder.address.setText(userListviewItem.getAdress());
        viewHolder.if_edit.setImageResource(userListviewItem.getIfEdit());

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

    //当ListView调用setTextFilter()方法的时候，便会调用该方法
    @Override
    public Filter getFilter() {
        if (myFilter == null) {
            myFilter = new MyFilter();
        }
        return myFilter;
    }

    class MyFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<UserListviewItem> list;
            if (TextUtils.isEmpty(constraint)) {  //当过滤的关键字为空的时候，则显示所有的数据
                list = backList;
            } else {    //否则把符合条件的数据对象添加到集合中
                list = new ArrayList<>();
                for (UserListviewItem item : backList) {
                    if (item.getPhoneNumber().contains(constraint)) {
                        list.add(item);
                    }
                }
            }
            results.values = list;    //将得到的集合保存到FilterResults的value变量中
            results.count = list.size();   //将集合的大小保存到FilterResults的count变量中
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            userListviewList = (List<UserListviewItem>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();  //通知数据发生了改变
            }else {
                notifyDataSetInvalidated();  //通知数据失效
            }
        }
    }
}
