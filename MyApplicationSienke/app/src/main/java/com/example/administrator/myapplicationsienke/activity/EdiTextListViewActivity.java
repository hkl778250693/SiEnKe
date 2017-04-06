package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.adapter.UserListviewAdapter;
import com.example.administrator.myapplicationsienke.mode.MySqliteHelper;
import com.example.administrator.myapplicationsienke.model.UserListviewItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/6.
 */
public class EdiTextListViewActivity extends Activity{

    private EditText etSearch;
    private ListView listView;
    private UserListviewAdapter adapter;
    private SQLiteDatabase db;  //数据库
    private MySqliteHelper helper; //数据库帮助类
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private UserListviewAdapter userListviewAdapter;
    private List<UserListviewItem> userListviewItemList = new ArrayList<>();
    private UserListviewItem item;
    private int currentPosition;

    List<UserListviewItem> list = new ArrayList<UserListviewItem>();//所有数据的list
    List<UserListviewItem> newlist = new ArrayList<UserListviewItem>();//查询后的数据list

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlist);

        //绑定控件
        init();
        initDefaultLists();
        setOnClickListener();//点击事件

    }
    //初始化控件
    private void init(){
        etSearch = (EditText) findViewById(R.id.etSearch);
        //为输入添加TextWatcher监听文字的变化
        etSearch.addTextChangedListener(new TextWatcher_Enum());
        listView = (ListView) findViewById(R.id.listview);
    }
    //添加数据
    private void initDefaultLists() {
        helper = new MySqliteHelper(EdiTextListViewActivity.this, 1);
        db = helper.getWritableDatabase();
        sharedPreferences = this.getSharedPreferences("data", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt("problem_number", 0);
        editor.commit();
    }
    //当editetext变化时调用的方法，来判断所输入是否包含在所属数据中
    private List<UserListviewItem>getNewData(String input_info){
        for (int i = 0;i<list.size();i++){
            UserListviewItem viewItem = list.get(i);
            if (viewItem.getSecurityNumber().contains(input_info)){
                UserListviewItem viewItem2 = new UserListviewItem();

                viewItem2.setSecurityNumber(viewItem.getSecurityNumber());
                newlist.add(viewItem2);
            }
        }
        return newlist;
    }
    //点击事件
    private void setOnClickListener(){
        userListviewAdapter = new UserListviewAdapter(EdiTextListViewActivity.this, userListviewItemList);
        listView.setAdapter(userListviewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                item = userListviewItemList.get((int) parent.getAdapter().getItemId(position));
                currentPosition = position;
                Intent intent = new Intent(EdiTextListViewActivity.this, UserDetailInfoActivity.class);
                intent.putExtra("position", position);
                startActivityForResult(intent, position);
            }
        });
    }
    //TextWatcher接口
    class TextWatcher_Enum implements TextWatcher{
        //文字变化前
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        //文字变化时
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            newlist.clear();
            if (etSearch.getText() != null){
                String input_info =etSearch.getText().toString();
                newlist = getNewData(input_info);
                adapter = new UserListviewAdapter(EdiTextListViewActivity.this,newlist);
                listView.setAdapter(adapter);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
