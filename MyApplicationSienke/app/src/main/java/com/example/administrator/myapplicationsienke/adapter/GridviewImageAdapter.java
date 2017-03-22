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
    private boolean isShowDelete;//根据这个变量来判断是否显示删除图标，true是显示，false是不显示
    private int clickItemIndex=-1;//根据这个变量来辨识选中的current值

    public GridviewImageAdapter(Context context,List<GridviewImage> gridviewImageList){
        this.context = context;
        this.gridviewImageList = gridviewImageList;
        if(context != null){
            layoutInflater = LayoutInflater.from(context);
        }
    }

    @Override
    public int getCount() {
        if(gridviewImageList.size() == 0){
            return 1;
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

    public void setIsShowDelete(boolean isShowDelete){
        this.isShowDelete=isShowDelete;
        notifyDataSetChanged();
    }

    public void setClickItemIndex(int postion){
        this.clickItemIndex=postion;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.gridview_image_item,null);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image);
            viewHolder.delete = (ImageView) convertView.findViewById(R.id.delete);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(position < gridviewImageList.size()){
            GridviewImage image = gridviewImageList.get(position);
            viewHolder.imageView.setImageBitmap(image.getImage());
            Log.i("GridviewImageAdapter=>","image.getImage()");
        }else {
            viewHolder.imageView.setBackgroundResource(R.mipmap.camera);
            Log.i("GridviewImageAdapter=>","imageView");
            if (position == 6) {
                viewHolder.imageView.setVisibility(View.GONE);
            }
        }
        viewHolder.delete.setVisibility(isShowDelete?View.VISIBLE:View.GONE);//设置删除按钮是否显示
        if(viewHolder.delete.getVisibility() == View.VISIBLE){
            if(position == clickItemIndex){
                viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gridviewImageList.remove(position);
                        notifyDataSetChanged();
                    }
                });
            }
        }
        return convertView;
    }

    public class ViewHolder {
        ImageView imageView;
        ImageView delete;
    }
}
