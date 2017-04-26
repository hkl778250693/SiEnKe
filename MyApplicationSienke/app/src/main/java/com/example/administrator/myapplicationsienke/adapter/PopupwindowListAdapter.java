package com.example.administrator.myapplicationsienke.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.model.PopupwindowListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/25.
 */
public class PopupwindowListAdapter extends BaseAdapter {
    private Context context;
    private List<PopupwindowListItem> popupwindowListItemList = new ArrayList<>();
    private LayoutInflater layoutInflater;

    public PopupwindowListAdapter(Context context,List<PopupwindowListItem> lists) {
        this.context = context;
        this.popupwindowListItemList = lists;
        if(context != null){
            layoutInflater = LayoutInflater.from(context);
        }
    }

    @Override
    public int getCount() {
        return popupwindowListItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return popupwindowListItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.popupwindow_list_item, null);
            viewHolder.itemName = (Button) convertView.findViewById(R.id.item_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        PopupwindowListItem item = popupwindowListItemList.get(position);
        viewHolder.itemName.setText(item.getItemName());
        return convertView;
    }

    public class ViewHolder {
        Button itemName;
    }
}
