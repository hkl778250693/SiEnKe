package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.adapter.MyGalleryAdapter;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/4/14 0014.
 */
public class MyPhotoGalleryActivity extends Activity {
    private Gallery myGallery;
    private TextView back;
    private ImageView delete;
    private MyGalleryAdapter adapter;
    private ArrayList<String> cropPathLists = new ArrayList<>();  //原始的图片路径集合
    private LayoutInflater inflater;  //转换器
    private View popupwindowView;
    private PopupWindow popupWindow;
    private TextView photoCurrentNumber,photoNumber;  //当前的照片位置，照片总数
    private RelativeLayout rootLinearlayout;
    private int currentPosition = 0;
    private int getCurrentPosition = 0; //上个页面穿过来的位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail_gallery);

        bindView();
        defaultSetting();
        setOnClickListener();
    }

    //绑定控件
    private void bindView() {
        myGallery = (Gallery) findViewById(R.id.gallery);
        rootLinearlayout = (RelativeLayout) findViewById(R.id.root_linearlayout);
    }

    //初始化设置
    private void defaultSetting() {
        Intent intent = getIntent();
        getCurrentPosition = intent.getIntExtra("currentPosition",0);
        cropPathLists = intent.getStringArrayListExtra("cropPathLists");
        Log.i("MyPhotoGalleryActivity", "获取到的图片路径为："+cropPathLists);
    }

    //点击事件
    private void setOnClickListener() {
        adapter = new MyGalleryAdapter(MyPhotoGalleryActivity.this,cropPathLists);
        myGallery.setAdapter(adapter);
        myGallery.setSelection(getCurrentPosition);
        myGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentPosition = position;
                createPopupwindow();
            }
        });
    }

    //弹出详细操作的popupwindow
    public void createPopupwindow() {
        inflater = LayoutInflater.from(MyPhotoGalleryActivity.this);
        popupwindowView = inflater.inflate(R.layout.popupwindow_photo_detail, null);
        popupWindow = new PopupWindow(popupwindowView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //绑定控件ID
        back = (TextView) popupwindowView.findViewById(R.id.back);
        delete = (ImageView) popupwindowView.findViewById(R.id.delete);
        photoCurrentNumber = (TextView) popupwindowView.findViewById(R.id.photo_current_number);
        photoNumber = (TextView) popupwindowView.findViewById(R.id.photo_number);
        //设置点击事件
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                Intent intent = new Intent();
                intent.putStringArrayListExtra("cropPathLists_back",cropPathLists);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        photoCurrentNumber.setText(String.valueOf(currentPosition+1));
        photoNumber.setText(String.valueOf(cropPathLists.size()));
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cropPathLists.size() == 1){   //如果还有一张图片，则删除了直接返回上个页面
                    popupWindow.dismiss();
                    cropPathLists.remove(currentPosition);
                    adapter.removePhoto();
                    Log.i("MyPhotoGalleryActivity", "大图页面删除本地图片成功！");
                    adapter.notifyDataSetChanged();
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra("cropPathLists_back",cropPathLists);
                    setResult(RESULT_OK,intent);
                    finish();
                }else {  //删除刷新
                    popupWindow.dismiss();
                    cropPathLists.remove(currentPosition);
                    adapter.removePhoto();
                    adapter.notifyDataSetChanged();
                }

            }
        });
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        popupWindow.showAtLocation(rootLinearlayout, Gravity.TOP, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
    }
}
