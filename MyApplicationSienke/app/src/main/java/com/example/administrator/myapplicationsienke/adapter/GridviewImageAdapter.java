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
    private boolean isShowDelete = false;//根据这个变量来判断是否显示删除图标，true是显示，false是不显示
    private long index;

    public GridviewImageAdapter(Context context,List<GridviewImage> gridviewImageList){
        this.context = context;
        this.gridviewImageList = gridviewImageList;
        if(context != null){
            layoutInflater = LayoutInflater.from(context);
        }
    }

    @Override
    public int getCount() {
        return gridviewImageList.size() + 1;//返回listiview数目加1
    }

    @Override
    public Object getItem(int position) {
        return gridviewImageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setIsShowDelete(boolean isShowDelete){
        this.isShowDelete = isShowDelete;
        notifyDataSetChanged();
    }

    public void setIndex(long index){
        this.index = index;
    }


    @Override
    public View getView( int position, View convertView, ViewGroup parent) {
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

        if(position == gridviewImageList.size()){
            viewHolder.imageView.setBackgroundResource(R.mipmap.camera);
            viewHolder.imageView.setVisibility(View.VISIBLE);
            viewHolder.delete.setVisibility(View.GONE);
            Log.i("GridviewImageAdapter=>","position"+position);
            if (position == 6) {
                viewHolder.imageView.setVisibility(View.GONE);
            }
            return convertView;
        }else {
            Log.i("GridviewImageAdapter","imageList="+gridviewImageList.size());
            GridviewImage image = gridviewImageList.get(position);
            Log.i("GridviewImageAdapter=>","gridviewImageList"+position);
            viewHolder.imageView.setImageBitmap(image.getImage());
            viewHolder.delete.setVisibility(isShowDelete?View.VISIBLE:View.GONE);//设置删除按钮是否显示
            if(viewHolder.delete.getVisibility() == View.VISIBLE){
                if(index == position){
                    viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gridviewImageList.remove((int)index);
                            Log.i("GridviewImageAdapter=>","index"+index);
                            notifyDataSetChanged();
                        }
                    });
                }
            }
            return convertView;
        }

    }

    @Override
    public boolean isEnabled(int position) {
        return super.isEnabled(position);
    }

    class ViewHolder {
        ImageView imageView;
        ImageView delete;
    }
}
