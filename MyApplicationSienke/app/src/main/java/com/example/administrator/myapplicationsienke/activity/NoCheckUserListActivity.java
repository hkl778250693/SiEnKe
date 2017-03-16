package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.adapter.NoCheckUserAdapter;
import com.example.administrator.myapplicationsienke.model.NoCheckUserItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/15.
 */
public class NoCheckUserListActivity extends Activity {
    private ImageView securityNoCheckBack;
    private ListView listView;
    private List<NoCheckUserItem> noCheckUserItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_choose_nocheck_listview);
        //绑定控件
        bindView();
        //暂时获取假数据
        getData();
    }

    //绑定控件ID
    private void bindView() {
        securityNoCheckBack = (ImageView) findViewById(R.id.security_nocheck_back);
        listView = (ListView) findViewById(R.id.listview);
        //点击事件
        securityNoCheckBack.setOnClickListener(onClickListener);
        NoCheckUserAdapter noCheckUserAdapter = new NoCheckUserAdapter(NoCheckUserListActivity.this, noCheckUserItemList);
        listView.setAdapter(noCheckUserAdapter);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.security_nocheck_back:
                    Intent intent = new Intent(NoCheckUserListActivity.this, SecurityChooseActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };

    //暂时获取假数据
    public void getData() {
        for (int i = 0; i < 20; i++) {
            NoCheckUserItem noCheckUserItem = new NoCheckUserItem();
            noCheckUserItemList.add(noCheckUserItem);
        }
    }

}
