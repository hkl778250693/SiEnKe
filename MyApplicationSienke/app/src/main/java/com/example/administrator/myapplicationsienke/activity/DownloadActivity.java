package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.mode.MySqliteHelper;
import com.example.administrator.myapplicationsienke.model.DownloadListvieItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/16.
 */
public class DownloadActivity extends Activity {
    private ImageView back;
    private ListView listView;
    private Button download;
    private List<DownloadListvieItem> downloadListvieItemList = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        bindView();  //绑定控件
        defaultSetting();//初始化设置
        setViewClickListener();  //点击事件
    }

    //绑定控件ID
    private void bindView() {
        back = (ImageView) findViewById(R.id.back);
        listView = (ListView) findViewById(R.id.listview);
        download= (Button) findViewById(R.id.download);
    }

    //点击事件
    private void setViewClickListener(){
        back.setOnClickListener(clickListener);
        download.setOnClickListener(clickListener);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:
                    finish();
                    break;
                case R.id.download:

                    break;
            }
        }
    };

    //初始化设置
    private void defaultSetting() {
        sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    //将服务器下载的数据存到本地数据库
    private void insertData(){
        MySqliteHelper helper = new MySqliteHelper(DownloadActivity.this,1);
        SQLiteDatabase db = helper.getWritableDatabase();
    }

}
