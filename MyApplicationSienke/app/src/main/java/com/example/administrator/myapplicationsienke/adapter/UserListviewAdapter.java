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
public class UserListviewAdapter extends BaseAdapter {
    private Context context;
    private List<UserListviewItem> userListviewList;
    private LayoutInflater layoutInflater;
    private List<UserListviewItem> mFilteredArrayList;//这个是过滤的时候记载满足要求的那些数据的集合
    private List<UserListviewItem> myPeopleList;//这个是要设置给adapter的

    public UserListviewAdapter(Context context, List<UserListviewItem> userListviewList) {
        this.context = context;
        this.userListviewList = userListviewList;
        //这个时候把我们的一些中间变量给初始化一下
        mFilteredArrayList = new ArrayList<UserListviewItem>();
        myPeopleList = new ArrayList<UserListviewItem>();
        //把我们传递过来的数据,弄到这个集合里面.保存原始集合的完整性
        myPeopleList.addAll(userListviewList);
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
        viewHolder.phone_number.setText(userListviewItem.getPhoneNumber());
        viewHolder.security_type.setText(userListviewItem.getSecurityType());
        if(!userListviewItem.getUserId().equals("null")){
            viewHolder.user_id.setText(userListviewItem.getUserId());
        }else {
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

    private NameFilter nameFilter;

    public NameFilter getFilter() {
        if (nameFilter == null) {
            nameFilter = new NameFilter();
        }
        return nameFilter;
    }

    //过滤数据
    class NameFilter extends Filter {
        //执行筛选
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults filterResults = new FilterResults();

            if (TextUtils.isEmpty(charSequence)) {
                mFilteredArrayList.clear();//清除一下
                filterResults.values = userListviewList;
                return filterResults;
            } else {
                mFilteredArrayList.clear();//清除一下
                for (Iterator<UserListviewItem> iterator = userListviewList.iterator(); iterator.hasNext(); ) {
                    UserListviewItem name = iterator.next();
                    if (name.getUserName().contains(charSequence)) {
                        mFilteredArrayList.add(name);
                    }
                }
                filterResults.values = mFilteredArrayList;
                return filterResults;
            }
        }

        //筛选结果
        @Override
        protected void publishResults(CharSequence arg0, FilterResults results) {
            myPeopleList = (ArrayList<UserListviewItem>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
