package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.adapter.GridviewImageAdapter;
import com.example.administrator.myapplicationsienke.adapter.PopupwindowListAdapter;
import com.example.administrator.myapplicationsienke.mode.MyPhotoUtils;
import com.example.administrator.myapplicationsienke.mode.MySqliteHelper;
import com.example.administrator.myapplicationsienke.mode.Tools;
import com.example.administrator.myapplicationsienke.model.PopupwindowListItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/16 0016.
 */
public class UserDetailInfoActivity extends Activity {
    private ImageView back;  //返回
    private GridView gridView;
    private LinearLayout rootLinearlayout;  //添加图片
    private RelativeLayout hiddenTypeRoot, hiddenReasonRoot;
    private TextView securityCheckCase, securityHiddenType, securityHiddenReason;  //安全情况,安全隐患类型，安全隐患原因
    private Button saveBtn, takePhoto, cancel;  //保存、拍照、相册、取消
    private ListView listView;
    private RadioButton cancelRb, saveRb;
    private LayoutInflater inflater;  //转换器
    private View popupwindowView, securityCaseView, securityHiddenTypeView, securityHiddenreasonView, saveView;
    private PopupWindow popupWindow;
    int sdkVersion = Build.VERSION.SDK_INT;  //当前SDK版本
    private int TYPE_FILE_CROP_IMAGE = 2;
    protected static Uri tempUri, cropPhotoUri;
    protected static final int TAKE_PHOTO = 100;//拍照
    protected static final int CROP_SMALL_PICTURE = 300;  //裁剪成小图片
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String securityId, UserNumber, UserName, MeterNumber, UserAddress, CheckType, UserPhoneNumber;
    private TextView userNumber, userName, meterNumber, userAddress, checkType, userPhoneNumber;
    private GridviewImageAdapter adapter;
    private PopupwindowListAdapter padapter;
    private List<Bitmap> bitmaps = new ArrayList<>();
    private String cropPhotoPath;  //裁剪的图片路径
    private ArrayList<String> cropPathLists = new ArrayList<>();  //裁剪的图片路径集合
    private ArrayList<String> cropPathLists_back = new ArrayList<>();  //大图页面返回的图片路径集合
    private int currentPosition = 0;
    private SQLiteDatabase db;  //数据库
    private List<PopupwindowListItem> popupwindowListItemList = new ArrayList<>();
    private Cursor cursor1, cursor2, cursor3, cursor4, cursor5;
    private List<PopupwindowListItem> defaultList = new ArrayList<>();
    private String securityContentItemId,securityHiddenItemId,hiddenReasonItemId;//安检情况类型id,安检隐患类型id,安检隐患原因id
    private EditText newMeterNumb, remarks;
    private String securityContent;
    private String newmeternumber;
    private String remarksContent;
    private String securityHidden;
    private String hiddenReason;
    private String photoNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail_info);

        bindView();//绑定控件
        defaultSetting();//初始化设置
        setViewClickListener();//点击事件
    }

    //绑定控件
    private void bindView() {
        back = (ImageView) findViewById(R.id.back);
        securityCheckCase = (TextView) findViewById(R.id.security_check_case);
        securityHiddenType = (TextView) findViewById(R.id.security_hidden_type);
        securityHiddenReason = (TextView) findViewById(R.id.security_hidden_reason);
        userNumber = (TextView) findViewById(R.id.user_number);
        userName = (TextView) findViewById(R.id.user_name);
        meterNumber = (TextView) findViewById(R.id.meter_number);
        userAddress = (TextView) findViewById(R.id.user_address);
        checkType = (TextView) findViewById(R.id.check_type);
        userPhoneNumber = (TextView) findViewById(R.id.user_phone_number);
        newMeterNumb = (EditText) findViewById(R.id.new_meter_numb);
        remarks = (EditText) findViewById(R.id.remarks);
        hiddenTypeRoot = (RelativeLayout) findViewById(R.id.hidden_type_root);
        hiddenReasonRoot = (RelativeLayout) findViewById(R.id.hidden_reason_root);
        saveBtn = (Button) findViewById(R.id.save_btn);
        rootLinearlayout = (LinearLayout) findViewById(R.id.root_linearlayout);
        gridView = (GridView) findViewById(R.id.gridview);
    }

    //点击事件
    private void setViewClickListener() {
        back.setOnClickListener(onClickListener);
        securityCheckCase.setOnClickListener(onClickListener);
        securityHiddenType.setOnClickListener(onClickListener);
        securityHiddenReason.setOnClickListener(onClickListener);
        saveBtn.setOnClickListener(onClickListener);

        adapter = new GridviewImageAdapter(UserDetailInfoActivity.this, cropPathLists);
        //gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentPosition = position;
                // 如果单击时删除按钮处在显示状态，则隐藏它
                if (adapter.getDeleteShow()) {
                    adapter.setDeleteShow(false);
                    adapter.notifyDataSetChanged();
                } else {
                    if (adapter.getCount() - 1 == position) {
                        // 判断是否达到了可添加图片最大数
                        if (cropPathLists.size() != 9) {
                            createPhotoPopupwindow();
                        }
                    }
                }
                if (!adapter.getDeleteShow() && adapter.getCount() - 1 != position) {
                    Intent intent = new Intent(UserDetailInfoActivity.this, MyPhotoGalleryActivity.class);
                    intent.putExtra("currentPosition", currentPosition);
                    intent.putStringArrayListExtra("cropPathLists", cropPathLists);
                    Log.i("UserDetailInfoActivity", "点击图片跳转进来到预览详情页面的图片数量为：" + cropPathLists.size());
                    startActivityForResult(intent, 500);
                }
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (!(position == cropPathLists.size())) {
                    // 如果删除按钮已经显示了则不再设置
                    if (!adapter.getDeleteShow()) {
                        adapter.setDeleteShow(true);
                        adapter.notifyDataSetChanged();
                    }
                }
                // 返回true，停止事件向下传播
                return true;
            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:
                    if (cropPathLists.size() != 0) {
                        for (int i = 0; i < cropPathLists.size(); i++) {
                            deletePicture(new File(cropPathLists.get(i)));
                        }
                    }
                    finish();
                    break;
                case R.id.security_check_case:
                    createSecurityCasePopupwindow();
                    break;
                case R.id.security_hidden_type:
                    createSecurityHiddenTypePopupwindow();
                    break;
                case R.id.security_hidden_reason:
                    createSecurityHiddenReasonPopupwindow();
                    break;
                case R.id.save_btn:  //保存
                    createSavePopupwindow();
                    break;
            }
        }
    };

    //初始化设置
    private void defaultSetting() {
        sharedPreferences = UserDetailInfoActivity.this.getSharedPreferences("data", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        MySqliteHelper helper = new MySqliteHelper(UserDetailInfoActivity.this, 1);
        db = helper.getWritableDatabase();

        securityCheckCase.setText("安检合格");
        securityHiddenType.setText("常规安检");
        securityHiddenReason.setText("户内立管内");
        if (hiddenTypeRoot.getVisibility() == View.VISIBLE && hiddenReasonRoot.getVisibility() == View.VISIBLE) {
            hiddenTypeRoot.setVisibility(View.GONE);
            hiddenReasonRoot.setVisibility(View.GONE);
        }

        //获取上一个页面传过来的用户ID
        Intent intent = getIntent();
        if (intent != null) {
            securityId = intent.getStringExtra("security_id");
            UserNumber = intent.getStringExtra("user_number");
            UserName = intent.getStringExtra("user_name");
            MeterNumber = intent.getStringExtra("meter_number");
            UserAddress = intent.getStringExtra("user_address");
            CheckType = intent.getStringExtra("check_type");
            UserPhoneNumber = intent.getStringExtra("user_phone_number");

            if (!UserNumber.equals("null")) {
                userNumber.setText(UserNumber);
            } else {
                userNumber.setText("无");
            }
            if (!UserName.equals("null")) {
                userName.setText(UserName);
            } else {
                userName.setText("无");
            }
            if (!MeterNumber.equals("null")) {
                meterNumber.setText(MeterNumber);
            } else {
                meterNumber.setText("无");
            }
            if (!UserAddress.equals("null")) {
                userAddress.setText(UserAddress);
            } else {
                userAddress.setText("无");
            }
            if (!CheckType.equals("null")) {
                checkType.setText(CheckType);
            } else {
                checkType.setText("无");
            }
            if (!UserPhoneNumber.equals("null")) {
                userPhoneNumber.setText(UserPhoneNumber);
            } else {
                userPhoneNumber.setText("无");
            }
        }

        new Thread() {
            @Override
            public void run() {
                if (querySecurityState(securityId)) {
                    querySecurityContent(securityId);
                    Log.i("UserDetailInfoActivity","显示上次数据！");
                    securityCheckCase.setText(securityContent);
                    if (!(securityCheckCase.getText().equals("合格") || securityCheckCase.getText().equals("复检合格"))) {
                        showHiddenTypeAndReason();
                    } else {
                        noShowHiddenTypeAndReason();
                    }
                    if (!newmeternumber.equals("")) {
                        newMeterNumb.setText(newmeternumber);
                    }
                    if (!remarksContent.equals("")) {
                        remarks.setText(remarksContent);
                    }
                    securityHiddenType.setText(securityHidden);
                    securityHiddenReason.setText(hiddenReason);
                    if (!photoNumber.equals("0")) {
                        new Thread(){
                            @Override
                            public void run() {
                                querySecurityPhoto(securityId);
                                handler.sendEmptyMessage(12);
                            }
                        }.start();
                    }
                } else {
                    Log.i("UserDetailInfoActivity","显示默认数据！");
                    //显示默认安全情况
                    new Thread() {
                        @Override
                        public void run() {
                            getSecurityCheckCase();
                            if (cursor1.getCount() != 0) {
                                handler.sendEmptyMessage(5);
                            } else {
                                handler.sendEmptyMessage(6);
                            }
                        }
                    }.start();
                    //显示默认安全隐患类型
                    new Thread() {
                        @Override
                        public void run() {
                            getSecurityHiddenType();
                            if (cursor2.getCount() != 0) {
                                handler.sendEmptyMessage(7);
                            } else {
                                handler.sendEmptyMessage(8);
                            }
                        }
                    }.start();
                    //显示默认安全隐患原因
                    new Thread() {
                        @Override
                        public void run() {
                            getSecurityHiddenReason("8");
                            if (cursor3.getCount() != 0) {
                                handler.sendEmptyMessage(9);
                            } else {
                                handler.sendEmptyMessage(10);
                            }
                        }
                    }.start();
                }
            }
        }.start();
    }

    //弹出拍照popupwindow
    public void createPhotoPopupwindow() {
        inflater = LayoutInflater.from(UserDetailInfoActivity.this);
        popupwindowView = inflater.inflate(R.layout.popupwindow_security_userinfo_take_photo, null);
        popupWindow = new PopupWindow(popupwindowView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //绑定控件ID
        takePhoto = (Button) popupwindowView.findViewById(R.id.take_photo);
        cancel = (Button) popupwindowView.findViewById(R.id.cancel);
        //设置点击事件
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                openCamera();//拍照
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
        popupWindow.setAnimationStyle(R.style.camera);
        backgroundAlpha(0.8F);   //背景变暗
        popupWindow.showAtLocation(rootLinearlayout, Gravity.BOTTOM, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0F);
            }
        });
    }

    //弹出是否保存popupwindow
    public void createSavePopupwindow() {
        inflater = LayoutInflater.from(UserDetailInfoActivity.this);
        saveView = inflater.inflate(R.layout.popupwindow_user_detail_info_save, null);
        popupWindow = new PopupWindow(saveView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        //绑定控件ID
        cancelRb = (RadioButton) saveView.findViewById(R.id.cancel_rb);
        saveRb = (RadioButton) saveView.findViewById(R.id.save_rb);
        //设置点击事件
        cancelRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        saveRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserInfo();
                if (cropPathLists.size() != 0) {
                    db.delete("security_photo",null,null);  //删除security_photo表中的所有数据
                    //设置id从1开始（sqlite默认id从1开始），若没有这一句，id将会延续删除之前的id
                    db.execSQL("update sqlite_sequence set seq=0 where name='security_photo'");
                    for (int i = 0; i < cropPathLists.size(); i++) {
                        insertSecurityPhoto(cropPathLists.get(i));
                    }
                    updateUserPhoto(String.valueOf(cropPathLists.size()));
                } else {
                    updateUserPhoto(String.valueOf(0));
                }
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                if (!(securityCheckCase.getText().equals("合格") || securityCheckCase.getText().equals("复检合格"))) {
                    editor.putInt("problem_number", sharedPreferences.getInt("problem_number", 0) + 1);  //保存到sharedPreferences
                    editor.commit();
                }
                popupWindow.dismiss();
                finish();
            }
        });
        popupWindow.update();
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
        popupWindow.setAnimationStyle(R.style.camera);
        backgroundAlpha(0.8F);   //背景变暗
        popupWindow.showAtLocation(rootLinearlayout, Gravity.CENTER, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0F);
            }
        });
    }

    //弹出安全情况popupwindow
    public void createSecurityCasePopupwindow() {
        inflater = LayoutInflater.from(UserDetailInfoActivity.this);
        securityCaseView = inflater.inflate(R.layout.popupwindow_security_type, null);
        popupWindow = new PopupWindow(securityCaseView, securityCheckCase.getWidth(), LinearLayout.LayoutParams.WRAP_CONTENT);
        //绑定控件ID
        listView = (ListView) securityCaseView.findViewById(R.id.listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PopupwindowListItem item = popupwindowListItemList.get((int) parent.getAdapter().getItemId(position));
                securityCheckCase.setText(item.getItemName());
                securityContentItemId = item.getItemId();
                if (!(securityCheckCase.getText().equals("合格") || securityCheckCase.getText().equals("复检合格"))) {
                    showHiddenTypeAndReason();
                } else {
                    noShowHiddenTypeAndReason();
                }
                popupWindow.dismiss();
            }
        });
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popupwindow_spinner_shape));
        popupWindow.setAnimationStyle(R.style.Popupwindow);
        backgroundAlpha(0.8F);   //背景变暗
        popupWindow.showAsDropDown(securityCheckCase, 0, 0);
        new Thread() {
            @Override
            public void run() {
                getSecurityCheckCase();
                if (cursor1.getCount() != 0) {
                    handler.sendEmptyMessage(3);
                } else {
                    handler.sendEmptyMessage(4);
                }

            }
        }.start();
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0F);
            }
        });
    }

    //当不是安检合格的时候，显示安全隐患和安全隐患原因
    public void showHiddenTypeAndReason() {
        if (hiddenTypeRoot.getVisibility() == View.GONE && hiddenReasonRoot.getVisibility() == View.GONE) {
            hiddenTypeRoot.setVisibility(View.VISIBLE);
            hiddenReasonRoot.setVisibility(View.VISIBLE);
        }
    }

    //当是安检合格的时候，不显示安全隐患和安全隐患原因
    public void noShowHiddenTypeAndReason() {
        if (hiddenTypeRoot.getVisibility() == View.VISIBLE && hiddenReasonRoot.getVisibility() == View.VISIBLE) {
            hiddenTypeRoot.setVisibility(View.GONE);
            hiddenReasonRoot.setVisibility(View.GONE);
        }
    }

    //弹出安全隐患类型popupwindow
    public void createSecurityHiddenTypePopupwindow() {
        inflater = LayoutInflater.from(UserDetailInfoActivity.this);
        securityHiddenTypeView = inflater.inflate(R.layout.popupwindow_security_type, null);
        popupWindow = new PopupWindow(securityHiddenTypeView, securityHiddenType.getWidth(), LinearLayout.LayoutParams.WRAP_CONTENT);
        //绑定控件ID
        listView = (ListView) securityHiddenTypeView.findViewById(R.id.listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PopupwindowListItem item = popupwindowListItemList.get((int) parent.getAdapter().getItemId(position));
                securityHiddenType.setText(item.getItemName());
                popupWindow.dismiss();
                securityHiddenItemId = item.getItemId();
                new Thread() {
                    @Override
                    public void run() {
                        Log.i("getSecurityState=>", " 调用了！");
                        if (securityHiddenItemId != null) {
                            getSecurityHiddenReasonDefault(securityHiddenItemId);
                            if (cursor4.getCount() != 0) {
                                handler.sendEmptyMessage(11);
                            } else {
                                handler.sendEmptyMessage(10);
                            }
                        }
                    }
                }.start();
            }
        });
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popupwindow_spinner_shape));
        popupWindow.setAnimationStyle(R.style.Popupwindow);
        backgroundAlpha(0.8F);   //背景变暗
        popupWindow.showAsDropDown(securityHiddenType, 0, 0);
        new Thread() {
            @Override
            public void run() {
                Log.i("getSecurityState=>", " 调用了！");
                getSecurityHiddenType();
                if (cursor2.getCount() != 0) {
                    handler.sendEmptyMessage(3);
                } else {
                    handler.sendEmptyMessage(4);
                }

            }
        }.start();
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0F);
            }
        });
    }

    //弹出安全隐患原因popupwindow
    public void createSecurityHiddenReasonPopupwindow() {
        inflater = LayoutInflater.from(UserDetailInfoActivity.this);
        securityHiddenreasonView = inflater.inflate(R.layout.popupwindow_security_type, null);
        popupWindow = new PopupWindow(securityHiddenreasonView, securityHiddenReason.getWidth(), LinearLayout.LayoutParams.WRAP_CONTENT);
        //绑定控件ID
        listView = (ListView) securityHiddenreasonView.findViewById(R.id.listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PopupwindowListItem item = popupwindowListItemList.get((int) parent.getAdapter().getItemId(position));
                securityHiddenReason.setText(item.getItemName());
                hiddenReasonItemId = item.getItemId();
                popupWindow.dismiss();
            }
        });
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popupwindow_spinner_shape));
        popupWindow.setAnimationStyle(R.style.Popupwindow);
        backgroundAlpha(0.8F);   //背景变暗
        popupWindow.showAsDropDown(securityHiddenReason, 0, 0);
        new Thread() {
            @Override
            public void run() {
                Log.i("getSecurityState=>", " 调用了！");
                if (securityHiddenItemId != null) {
                    getSecurityHiddenReason(securityHiddenItemId);
                    if (cursor3.getCount() != 0) {
                        handler.sendEmptyMessage(3);
                    } else {
                        handler.sendEmptyMessage(4);
                    }
                } else {
                    getSecurityHiddenReason("8");
                    if (cursor3.getCount() != 0) {
                        handler.sendEmptyMessage(3);
                    } else {
                        handler.sendEmptyMessage(4);
                    }
                }
            }
        }.start();
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0F);
            }
        });
    }

    //设置背景透明度
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    //调用相机
    public void openCamera() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 指定照片保存路径（SD卡），temp.jpg为一个临时文件，每次拍照后这个图片都会被替换
        tempUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "temp.jpg"));
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
        Log.i("openCamera===>", "保存的地址为" + tempUri);
        startActivityForResult(openCameraIntent, TAKE_PHOTO);
    }

    //页面回调方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {   //如果返回码是可用的
            switch (requestCode) {
                case TAKE_PHOTO:
                    startCropPhoto(tempUri);
                    Log.i("TAKE_PHOTO=====>", "没有数据返回！");
                    if (data != null) {
                        Log.i("TAKE_PHOTO=====>", "有数据返回！");
                    }
                    break;
                case CROP_SMALL_PICTURE:
                    Log.i("CROP_SMALL_PICTURE===>", "有数据返回！");
                    cropPathLists.add(cropPhotoPath);
                    handler.sendEmptyMessage(1);
                    break;
                case 500:
                    if (data != null) {
                        cropPathLists_back = data.getStringArrayListExtra("cropPathLists_back");
                        handler.sendEmptyMessage(2);
                    }
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            //Toast.makeText(UserDetailInfoActivity.this, "您取消了拍照哦", Toast.LENGTH_SHORT).show();
        }

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    adapter = new GridviewImageAdapter(UserDetailInfoActivity.this, cropPathLists);
                    adapter.notifyDataSetChanged();
                    gridView.setAdapter(adapter);
                    break;
                case 2:
                    adapter = new GridviewImageAdapter(UserDetailInfoActivity.this, cropPathLists_back);
                    cropPathLists = cropPathLists_back;
                    adapter.notifyDataSetChanged();
                    gridView.setAdapter(adapter);
                    break;
                case 3:
                    padapter = new PopupwindowListAdapter(UserDetailInfoActivity.this, popupwindowListItemList);
                    padapter.notifyDataSetChanged();
                    listView.setAdapter(padapter);
                    break;
                case 4:
                    defaultList.clear();
                    securityCheckCase.setText("无");
                    PopupwindowListItem item = new PopupwindowListItem();
                    item.setItemName("无");
                    defaultList.add(item);
                    padapter = new PopupwindowListAdapter(UserDetailInfoActivity.this, defaultList);
                    padapter.notifyDataSetChanged();
                    listView.setAdapter(padapter);
                    break;
                case 5:
                    cursor1.moveToPosition(0);
                    securityCheckCase.setText(cursor1.getString(2));
                    securityContentItemId = cursor1.getString(1);
                    if (!securityCheckCase.getText().equals("合格")) {
                        showHiddenTypeAndReason();
                    } else {
                        noShowHiddenTypeAndReason();
                    }
                    break;
                case 6:
                    securityCheckCase.setText("无");
                    break;
                case 7:
                    cursor2.moveToPosition(0);
                    securityHiddenType.setText(cursor2.getString(2));
                    securityHiddenItemId = cursor1.getString(1);
                    break;
                case 8:
                    securityHiddenType.setText("无");
                    break;
                case 9:
                    cursor3.moveToPosition(0);
                    securityHiddenReason.setText(cursor3.getString(3));
                    hiddenReasonItemId = cursor1.getString(1);
                    break;
                case 10:
                    securityHiddenReason.setText("无");
                    break;
                case 11:
                    cursor4.moveToPosition(0);
                    securityHiddenReason.setText(cursor4.getString(3));
                    break;
                case 12:
                    adapter = new GridviewImageAdapter(UserDetailInfoActivity.this, cropPathLists);
                    adapter.notifyDataSetChanged();
                    gridView.setAdapter(adapter);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    //读取安全情况状态信息
    public void getSecurityCheckCase() {
        popupwindowListItemList.clear();
        cursor1 = db.query("security_content", null, null, null, null, null, null);//查询并获得游标
        Log.i("getSecurityCheckCase=>", " 查询到的状态个数为：" + cursor1.getCount());
        //如果游标为空，则显示默认数据
        if (cursor1.getCount() == 0) {
            return;
        }
        while (cursor1.moveToNext()) {
            PopupwindowListItem item = new PopupwindowListItem();
            item.setItemName(cursor1.getString(2));
            popupwindowListItemList.add(item);
        }
        Log.i("getSecurityState=>", " 安检状态个数为：" + popupwindowListItemList.size());
    }

    //读取安全隐患类型信息
    public void getSecurityHiddenType() {
        popupwindowListItemList.clear();
        cursor2 = db.query("security_hidden", null, null, null, null, null, null);//查询并获得游标
        Log.i("getSecurityCheckCase=>", " 查询到的状态个数为：" + cursor2.getCount());
        //如果游标为空，则显示默认数据
        if (cursor2.getCount() == 0) {
            return;
        }
        while (cursor2.moveToNext()) {
            PopupwindowListItem item = new PopupwindowListItem();
            item.setItemName(cursor2.getString(2));
            item.setItemId(cursor2.getString(1));
            popupwindowListItemList.add(item);
        }
        Log.i("getSecurityState=>", " 安全隐患个数为：" + popupwindowListItemList.size());
    }

    //读取安全隐患原因信息
    public void getSecurityHiddenReason(String itemId) {
        popupwindowListItemList.clear();
        cursor3 = db.rawQuery("select * from security_hidden_reason where n_safety_hidden_id=?", new String[]{itemId});//查询并获得游标
        //如果游标为空，则显示默认数据
        if (cursor3.getCount() == 0) {
            return;
        }
        while (cursor3.moveToNext()) {
            PopupwindowListItem item = new PopupwindowListItem();
            item.setItemName(cursor3.getString(3));
            popupwindowListItemList.add(item);
        }

        Log.i("getSecurityState=>", " 安全隐患原因个数为：" + popupwindowListItemList.size());
    }

    //读取安全隐患原因默认信息
    public void getSecurityHiddenReasonDefault(String itemId) {
        cursor4 = db.rawQuery("select * from security_hidden_reason where n_safety_hidden_id=?", new String[]{itemId});//查询并获得游标
        //如果游标为空，则显示默认数据
        if (cursor4.getCount() == 0) {
            return;
        }
        while (cursor4.moveToNext()) {
            PopupwindowListItem item = new PopupwindowListItem();
            item.setItemName(cursor4.getString(3));
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    protected void startCropPhoto(Uri uri) {
        if (uri != null) {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(uri, "image/*");
            // 设置裁剪
            intent.putExtra("crop", "true");
            // aspectX aspectY 是宽高的比例
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1.3);
            // outputX outputY 是裁剪图片宽高
            intent.putExtra("outputX", 500);
            intent.putExtra("outputY", 650);
            intent.putExtra("return-data", false);
            // 当图片的宽高不足时，会出现黑边，去除黑边
            intent.putExtra("scale", true);
            intent.putExtra("scaleUpIfNeeded", true);
            MyPhotoUtils photoUtils = new MyPhotoUtils(TYPE_FILE_CROP_IMAGE, securityId);
            cropPhotoUri = photoUtils.getOutFileUri(TYPE_FILE_CROP_IMAGE);
            Log.i("startCropPhoto", "图片裁剪的uri = " + cropPhotoUri);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cropPhotoUri);
            cropPhotoPath = cropPhotoUri.getPath();
            startActivityForResult(intent, CROP_SMALL_PICTURE);
        }
    }

    /**
     * 返回的时候，删除所有拍照的图片
     */
    private void deletePicture(File file) {
        if (file.exists()) {
            file.delete();
        } else {
            Log.i("deletePicture", "没有相应的图片文件");
        }
    }

    /**
     * 将拍的照片路径保存到本地数据库安检图片表
     */
    private void insertSecurityPhoto(String photoPath) {
        ContentValues values = new ContentValues();
        values.put("securityNumber", securityId);
        values.put("photoPath", photoPath);
        db.insert("security_photo", null, values);
    }

    /**
     * 将拍的照片张数保存到本地数据库安检图片表
     */
    private void updateUserPhoto(String photoNumber) {
        ContentValues values = new ContentValues();
        values.put("photoNumber", photoNumber);
        db.update("User",values,"securityNumber=?",new String[]{securityId});
    }

    /**
     * 将安检的信息保存到本地数据库用户表
     */
    private void updateUserInfo() {
        ContentValues values = new ContentValues();
        values.put("security_content", securityContentItemId);
        if(!newMeterNumb.getText().toString().trim().equals("")){
            values.put("newMeterNumber",newMeterNumb.getText().toString().trim());
            Log.i("insertUserInfo","输入的新表编号是："+newMeterNumb.getText().toString().trim());
        }else {
            values.put("newMeterNumber","");
        }
        if(!remarks.getText().toString().trim().equals("")){
            values.put("remarks", remarks.getText().toString().trim());
        }else {
            values.put("remarks", "");
        }
        values.put("security_hidden", securityHiddenItemId);
        values.put("security_hidden_reason", hiddenReasonItemId);
        db.update("User",values,"securityNumber=?",new String[]{securityId});
    }

    /**
     * 根据安检ID查询用户是否处于安检状态，如果是安检状态，则显示上次安检所记录的内容，否则显示默认的内容
     */
    private boolean querySecurityState(String securityId) {
        Cursor cursor = db.rawQuery("select * from User where securityNumber=?", new String[]{securityId});//查询并获得游标
        while (cursor.moveToNext()) {
            if (cursor.getString(10).equals("true")) {
                return true;
            }
        }
        cursor.close();
        return false;
    }

    /**
     * 根据安检ID查询用户上次安检记录信息
     */
    private void querySecurityContent(String securityId) {
        Cursor cursor = db.rawQuery("select * from User where securityNumber=?", new String[]{securityId});//查询并获得游标
        while (cursor.moveToNext()) {
            Log.i("querySecurityContent","上次安检信息查询进来了，数据条数为："+cursor.getCount());
            Log.i("querySecurityContent","上次安检用户为："+cursor.getString(2));
            Log.i("querySecurityContent","上次安检情况为："+cursor.getString(11));
            securityContent = cursor.getString(11);
            Log.i("querySecurityContent","上次安检输入表编号为："+cursor.getString(12));
            if(!cursor.getString(12).equals("null")){
                newmeternumber = cursor.getString(12);
            }else {
                newmeternumber = "";
            }
            Log.i("querySecurityContent","上次安检备注信息为："+cursor.getString(13));
            if(!cursor.getString(13).equals("null")){
                remarksContent = cursor.getString(13);
            }else {
                remarksContent = "";
            }
            securityHidden = cursor.getString(14);
            hiddenReason = cursor.getString(15);
            photoNumber = cursor.getString(16);
        }
        cursor.close();
    }

    /**
     * 根据安检ID查询用户是否处于安检状态，如果是安检状态，则显示上次安检所记录的图片，否则不显示
     */
    private void querySecurityPhoto(String securityId) {
        cursor5 = db.rawQuery("select * from security_photo where securityNumber=?", new String[]{securityId});//查询并获得游标
        while (cursor5.moveToNext()) {
            cropPathLists.add(cursor5.getString(1));
        }
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param bitmaps
     */
    private void saveImage(List<Bitmap> bitmaps) {
        String filePath;
        File file;
        try {
            for (int i = 0; i < bitmaps.size(); i++) {
                Bitmap photo = bitmaps.get(i);
                if (Tools.hasSdcard()) {
                    filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Sienke/files/img/" + securityId + "_" + i + ".jpg";
                    file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
                } else {
                    filePath = "data/data/" + UserDetailInfoActivity.this.getPackageName() + "/Sienke/files/img/" + securityId + "_" + i + ".jpg";
                    file = new File("data/data/" + UserDetailInfoActivity.this.getPackageName());
                }
                if (!file.exists()) {
                    file.createNewFile();
                }
                Log.i("UserDetailInfoActivity", "file=>" + filePath);
                FileOutputStream fileOutputStream = new FileOutputStream(filePath);//通过file打开输出流
                //将bitmap写入到文件
                photo.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);//图片压缩
                fileOutputStream.flush();
                fileOutputStream.close();
                Log.i("UserDetailInfoActivity", "bitmap写入到文件");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor1 != null) { //游标关闭
            cursor1.close();
        }
        if (cursor2 != null) {
            cursor2.close();
        }
        if (cursor3 != null) {
            cursor3.close();
        }
        if (cursor4 != null) {
            cursor4.close();
        }
        if (cursor5 != null) {
            cursor5.close();
        }
        db.close();
    }
}
