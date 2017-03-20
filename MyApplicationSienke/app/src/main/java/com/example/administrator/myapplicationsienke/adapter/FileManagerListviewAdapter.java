package com.example.administrator.myapplicationsienke.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.model.FileManagerListviewItem;
import com.example.administrator.myapplicationsienke.model.UserListviewItem;

import java.util.List;

/**
 * Created by Administrator on 2017/3/20.
 */
public class FileManagerListviewAdapter extends BaseAdapter {
    private Context context;
    private List<FileManagerListviewItem> fileManagerListviewItemList;
    private LayoutInflater layoutInflater;

    public FileManagerListviewAdapter(Context context, List<FileManagerListviewItem> fileManagerListviewItemList) {
        this.context = context;
        this.fileManagerListviewItemList = fileManagerListviewItemList;
        if (context != null) {
            layoutInflater = LayoutInflater.from(context);
        }
    }


    @Override
    public int getCount() {
        return fileManagerListviewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return fileManagerListviewItemList.get(position);
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
            convertView = layoutInflater.inflate(R.layout.file_manager_listview_item, null);
            viewHolder.list_item = (TextView) convertView.findViewById(R.id.list_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        FileManagerListviewItem fileManagerListviewItem = fileManagerListviewItemList.get(position);
        return convertView;
    }

    class ViewHolder {
        TextView list_item;
    }
}
