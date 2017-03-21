package com.example.administrator.myapplicationsienke.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.model.GridviewImage;
import com.example.administrator.myapplicationsienke.model.QueryListviewItem;

import java.util.List;

/**
 * Created by Administrator on 2017/3/21 0021.
 */
public class GridviewImageAdapter extends BaseAdapter{
    private Context context;
    private List<GridviewImage> gridviewImageList;
    private LayoutInflater layoutInflater;

    public GridviewImageAdapter(Context context,List<GridviewImage> gridviewImageList){
        this.context = context;
        this.gridviewImageList = gridviewImageList;
        if(context != null){
            layoutInflater = LayoutInflater.from(context);
        }
    }

    @Override
    public int getCount() {
        return gridviewImageList.size();
    }

    @Override
    public Object getItem(int position) {
        return gridviewImageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.gridview_image_item,null);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
            GridviewImage image = gridviewImageList.get(position);
            imageView.setImageBitmap(image.getImage());
        }
        return convertView;
    }
}
