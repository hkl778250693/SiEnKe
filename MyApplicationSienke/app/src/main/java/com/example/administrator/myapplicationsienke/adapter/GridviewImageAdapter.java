package com.example.administrator.myapplicationsienke.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.mode.PhotoBitmapUtil;
import com.example.administrator.myapplicationsienke.model.GridviewImage;
import com.example.administrator.myapplicationsienke.model.QueryListviewItem;

import java.util.List;

/**
 * Created by Administrator on 2017/3/21 0021.
 */
public class GridviewImageAdapter extends BaseAdapter {
    private Context context;
    private List<GridviewImage> gridviewImageList;
    private LayoutInflater layoutInflater;
    /**
     * 判断是否显示清除按钮 true=显示
     */
    private boolean showDelete = false;
    /**
     * 加号按钮
     */
    public Bitmap addBitmap;

    public GridviewImageAdapter(Context context, List<GridviewImage> gridviewImageList) {
        this.context = context;
        this.gridviewImageList = gridviewImageList;
        if (context != null) {
            layoutInflater = LayoutInflater.from(context);
            addBitmap = BitmapFactory.decodeResource(context.getResources(),R.mipmap.camera);
        }
    }

    @Override
    public int getCount() {
        // 数据集合加一，在该位置上添加加号
        return gridviewImageList == null ? 0 : gridviewImageList.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return gridviewImageList == null ? null : gridviewImageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.gridview_image_item, null);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image);
            viewHolder.delete = (ImageView) convertView.findViewById(R.id.delete);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (gridviewImageList != null && gridviewImageList.size() > position) {
            // 正常显示
            // 判断是否需要显示删除按钮
            if(getDeleteShow()){
                viewHolder.delete.setVisibility(View.VISIBLE);
            }else {
                viewHolder.delete.setVisibility(View.GONE);
            }
            viewHolder.imageView.setImageBitmap(gridviewImageList.get(position).getImage());
        }else {
            if(gridviewImageList.size() != 6){
                viewHolder.delete.setVisibility(View.GONE);
                viewHolder.imageView.setImageBitmap(addBitmap);
            }
        }

        //设置删除按钮的监听事件
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridviewImageList.remove(position);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
        ImageView delete;
    }

    /**
     * 设置图片显示状态
     *
     * @param clear 图片状态
     */
    public void setDeleteShow(boolean clear) {
        showDelete = clear;
    }

    /**
     * 图片显示状态
     *
     * @return 状态 true=显示
     */
    public boolean getDeleteShow() {
        return showDelete;
    }
}
