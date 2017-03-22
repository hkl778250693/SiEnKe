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
        if(gridviewImageList.size() == 6){
            return 6;
        }
        return (gridviewImageList.size() + 1);//返回listiview数目加1
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.gridview_image_item,null);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(position == gridviewImageList.size()){
            /*view = layoutInflater.inflate(R.layout.gridview_default_add_image_item,null);
            viewHolder.imageView.setImageBitmap(view.getDrawingCache());*/
            viewHolder.imageView.setBackgroundResource(R.mipmap.camera);
            Log.i("GridviewImageAdapter=>","imageView");
            if (position == 6) {
                viewHolder.imageView.setVisibility(View.GONE);
            }
        }else {
            GridviewImage image = gridviewImageList.get(position);
            viewHolder.imageView.setImageBitmap(image.getImage());
        }
        return convertView;
    }

    public class ViewHolder {
        public ImageView imageView;
    }
}
