package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.adapter.DownloadListViewAdapter;
import com.example.administrator.myapplicationsienke.model.DownloadListvieItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/16.
 */
public class DownloadActivity extends Activity {
    private ImageView back;
    private ListView listView;
    private List<DownloadListvieItem> downloadListvieItemList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        //绑定控件
        bindView();
        //暂时获取假数据
        getData();
    }

    //绑定控件ID
    private void bindView() {
        back = (ImageView) findViewById(R.id.back);
        listView = (ListView) findViewById(R.id.listview);

        //点击事件
        back.setOnClickListener(clickListener);
        DownloadListViewAdapter adapter = new DownloadListViewAdapter(DownloadActivity.this, downloadListvieItemList);
        listView.setAdapter(adapter);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:
                    finish();
                    break;
            }
        }
    };

    //暂时获取假数据
    public void getData() {
        for (int i = 0; i < 20; i++) {
            DownloadListvieItem downloadListvieItem = new DownloadListvieItem();
            downloadListvieItemList.add(downloadListvieItem);
        }
    }
}
