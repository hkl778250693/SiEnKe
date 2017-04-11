package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.adapter.GridviewImageAdapter;
import com.example.administrator.myapplicationsienke.mode.Tools;
import com.example.administrator.myapplicationsienke.model.GridviewImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/16 0016.
 */
public class UserDetailInfoActivity extends Activity {
    private ImageView back, more;  //返回，更多
    private GridView gridView;
    private LinearLayout rootLinearlayout;  //添加图片
    private RelativeLayout hiddenTypeRoot, hiddenReasonRoot;
    private TextView securityCheckCase, securityHiddenType, securityHiddenReason;  //安全情况,安全隐患类型，安全隐患原因
    private Button saveBtn, takePhoto, photoAlbum, cancel;  //保存、拍照、相册、取消
    private RadioButton notSecurityCheck, passSecurityCheck, notPassSecurityCheck, overSecurityCheckTime, nobodyHere, refuseSecurityCheck;
    private RadioButton commonSecurityCheck, yearPlan, recheck, passGasSecurityCheck;
    private RadioButton indoorStandPipe, indoorBranchPipe, fuelGasMeter, burningAppliances, gasFacilitiesRoom, threeWayPipe;
    private RadioButton cancelRb, saveRb;
    private LayoutInflater inflater;  //转换器
    private View popupwindowView, securityCaseView, securityHiddenTypeView, securityHiddenreasonView, saveView;
    private PopupWindow popupWindow;
    private Bitmap bitmap;
    int sdkVersion = Build.VERSION.SDK_INT;  //当前SDK版本
    private String SD_CARD_TEMP_DIR;
    protected static Uri tempUri, albumUri, photoUri;
    protected static final int TAKE_PHOTO = 100;//选择本地照片
    protected static final int PHOTO_ALBUM = 200;//拍照
    protected static final int CROP_SMALL_PICTURE = 300;  //裁剪成小图片
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String result, path, uid;
    private List<GridviewImage> imageList = new ArrayList<>();
    private Bitmap addImageBitmap;  //添加照片
    private GridviewImage image;
    private GridviewImageAdapter adapter;
    private boolean isShowDelete;
    private List<Bitmap> bitmaps = new ArrayList<>();


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
        more = (ImageView) findViewById(R.id.more);
        securityCheckCase = (TextView) findViewById(R.id.security_check_case);
        securityHiddenType = (TextView) findViewById(R.id.security_hidden_type);
        securityHiddenReason = (TextView) findViewById(R.id.security_hidden_reason);
        hiddenTypeRoot = (RelativeLayout) findViewById(R.id.hidden_type_root);
        hiddenReasonRoot = (RelativeLayout) findViewById(R.id.hidden_reason_root);
        saveBtn = (Button) findViewById(R.id.save_btn);
        rootLinearlayout = (LinearLayout) findViewById(R.id.root_linearlayout);
        gridView = (GridView) findViewById(R.id.gridview);
    }

    //点击事件
    private void setViewClickListener() {
        back.setOnClickListener(onClickListener);
        more.setOnClickListener(onClickListener);
        securityCheckCase.setOnClickListener(onClickListener);
        securityHiddenType.setOnClickListener(onClickListener);
        securityHiddenReason.setOnClickListener(onClickListener);
        saveBtn.setOnClickListener(onClickListener);

        adapter = new GridviewImageAdapter(UserDetailInfoActivity.this, imageList);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 如果单击时删除按钮处在显示状态，则隐藏它
                if (adapter.getDeleteShow()) {
                    adapter.setDeleteShow(false);
                    adapter.notifyDataSetChanged();
                } else {
                    if (adapter.getCount() - 1 == position) {
                        // 判断是否达到了可添加图片最大数
                        if (imageList.size() != 6) {
                            createPhotoPopupwindow();
                        }
                    }
                }
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (!(position == imageList.size())) {
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
                    finish();
                    break;
                case R.id.more:
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
        securityCheckCase.setText("安检合格");
        securityHiddenType.setText("常规安检");
        securityHiddenReason.setText("户内立管内");
        if (hiddenTypeRoot.getVisibility() == View.VISIBLE && hiddenReasonRoot.getVisibility() == View.VISIBLE) {
            hiddenTypeRoot.setVisibility(View.GONE);
            hiddenReasonRoot.setVisibility(View.GONE);
        }

        //获取上一个页面传过来的用户ID
        Intent intent = getIntent();
        uid = intent.getStringExtra("user_id");
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
        popupWindow = new PopupWindow(saveView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
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
                popupWindow.dismiss();
                if (bitmaps.size() != 0) {
                    saveImage(bitmaps);
                }
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                if (!securityCheckCase.getText().equals("安检合格")) {
                    editor.putInt("problem_number", sharedPreferences.getInt("problem_number", 0) + 1);  //保存到sharedPreferences
                    editor.commit();
                }
                finish();
            }
        });
        popupWindow.update();
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.business_check_shape));
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
        securityCaseView = inflater.inflate(R.layout.popupwindow_security_case, null);
        popupWindow = new PopupWindow(securityCaseView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //绑定控件ID
        notSecurityCheck = (RadioButton) securityCaseView.findViewById(R.id.not_security_check);
        passSecurityCheck = (RadioButton) securityCaseView.findViewById(R.id.pass_security_check);
        notPassSecurityCheck = (RadioButton) securityCaseView.findViewById(R.id.not_pass_security_check);
        overSecurityCheckTime = (RadioButton) securityCaseView.findViewById(R.id.over_security_check_time);
        nobodyHere = (RadioButton) securityCaseView.findViewById(R.id.nobody_here);
        refuseSecurityCheck = (RadioButton) securityCaseView.findViewById(R.id.refuse_security_check);
        //设置点击事件
        notSecurityCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityCheckCase.setText(notSecurityCheck.getText());
                showHiddenTypeAndReason();
            }
        });
        passSecurityCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityCheckCase.setText(passSecurityCheck.getText());
                if (securityCheckCase.getText().equals("安检合格")) {
                    Log.i("securityCheckCase", "安检合格");
                    if (hiddenTypeRoot.getVisibility() == View.VISIBLE && hiddenReasonRoot.getVisibility() == View.VISIBLE) {
                        hiddenTypeRoot.setVisibility(View.GONE);
                        hiddenReasonRoot.setVisibility(View.GONE);
                    }
                }
            }
        });
        notPassSecurityCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityCheckCase.setText(notPassSecurityCheck.getText());
                showHiddenTypeAndReason();
            }
        });
        overSecurityCheckTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityCheckCase.setText(overSecurityCheckTime.getText());
                showHiddenTypeAndReason();
            }
        });
        nobodyHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityCheckCase.setText(nobodyHere.getText());
                showHiddenTypeAndReason();
            }
        });
        refuseSecurityCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityCheckCase.setText(refuseSecurityCheck.getText());
                showHiddenTypeAndReason();
            }
        });
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
        popupWindow.setAnimationStyle(R.style.Popupwindow);
        backgroundAlpha(0.8F);   //背景变暗
        popupWindow.showAsDropDown(securityCheckCase, 0, 0);
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

    //弹出安全隐患类型popupwindow
    public void createSecurityHiddenTypePopupwindow() {
        inflater = LayoutInflater.from(UserDetailInfoActivity.this);
        securityHiddenTypeView = inflater.inflate(R.layout.popupwindow_security_hidden_type, null);
        popupWindow = new PopupWindow(securityHiddenTypeView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //绑定控件ID
        commonSecurityCheck = (RadioButton) securityHiddenTypeView.findViewById(R.id.common_security_check);
        yearPlan = (RadioButton) securityHiddenTypeView.findViewById(R.id.year_plan);
        recheck = (RadioButton) securityHiddenTypeView.findViewById(R.id.recheck);
        passGasSecurityCheck = (RadioButton) securityHiddenTypeView.findViewById(R.id.pass_gas_security_check);
        //设置点击事件
        commonSecurityCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityHiddenType.setText(commonSecurityCheck.getText());
            }
        });
        yearPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityHiddenType.setText(yearPlan.getText());
            }
        });
        recheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityHiddenType.setText(passGasSecurityCheck.getText());
            }
        });
        passGasSecurityCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityHiddenType.setText(commonSecurityCheck.getText());
            }
        });
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
        popupWindow.setAnimationStyle(R.style.Popupwindow);
        backgroundAlpha(0.8F);   //背景变暗
        popupWindow.showAsDropDown(securityHiddenType, 0, 0);
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
        securityHiddenreasonView = inflater.inflate(R.layout.popupwindow_security_hidden_reason, null);
        popupWindow = new PopupWindow(securityHiddenreasonView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //绑定控件ID
        indoorStandPipe = (RadioButton) securityHiddenreasonView.findViewById(R.id.indoor_stand_pipe);
        indoorBranchPipe = (RadioButton) securityHiddenreasonView.findViewById(R.id.indoor_branch_pipe);
        fuelGasMeter = (RadioButton) securityHiddenreasonView.findViewById(R.id.fuel_gas_meter);
        burningAppliances = (RadioButton) securityHiddenreasonView.findViewById(R.id.burning_appliances);
        gasFacilitiesRoom = (RadioButton) securityHiddenreasonView.findViewById(R.id.gas_facilities_room);
        threeWayPipe = (RadioButton) securityHiddenreasonView.findViewById(R.id.three_way_pipe);
        //设置点击事件
        indoorStandPipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityHiddenReason.setText(indoorStandPipe.getText());
            }
        });
        indoorBranchPipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityHiddenReason.setText(indoorBranchPipe.getText());
            }
        });
        fuelGasMeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityHiddenReason.setText(fuelGasMeter.getText());
            }
        });
        burningAppliances.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityHiddenReason.setText(burningAppliances.getText());
            }
        });
        gasFacilitiesRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityHiddenReason.setText(gasFacilitiesRoom.getText());
            }
        });
        threeWayPipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityHiddenReason.setText(threeWayPipe.getText());
            }
        });
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
        popupWindow.setAnimationStyle(R.style.Popupwindow);
        backgroundAlpha(0.8F);   //背景变暗
        popupWindow.showAsDropDown(securityHiddenReason, 0, 0);
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
        // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
        SD_CARD_TEMP_DIR = Environment.getExternalStorageDirectory() + "image.jpg";
        tempUri = Uri.fromFile(new File(SD_CARD_TEMP_DIR));
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
        startActivityForResult(openCameraIntent, TAKE_PHOTO);
    }

    /*//调用本地相册
    public void openAlnum(){
        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
        openAlbumIntent.setType("image*//*");
        startActivityForResult(openAlbumIntent,PHOTO_ALBUM);
    }*/

    //页面回调方法


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("onActivityResult===>", "" + true);
        Log.i("onActivityResult===>", "" + requestCode);
        if (resultCode == RESULT_OK) {   //如果返回码是可用的
            Log.i("RESULT_OK===>", "" + true);
            switch (requestCode) {
                case TAKE_PHOTO:
                    Log.i("TAKE_PHOTO===>", "" + true);
                    startCropPhoto(tempUri);
                    break;
                case PHOTO_ALBUM:
                    Log.i("PHOTO_ALBUM===>", "" + true);
                    if (Tools.hasSdcard()) {
                        //startCropPhoto(albumUri);
                        if (data != null) {
                            albumUri = data.getData();
                            if (sdkVersion >= 19) {      //android 5.0以上直接返回的是图片的路径
                                Log.i("sdkVersion===>", "" + sdkVersion);
                                path = albumUri.getPath();
                                Log.i("sdkVersion=path=>", "" + path);
                                //path = getPath_above19(this,imgUri);    //或者直接使用path = imgUri.getPath();
                            } else {
                                path = getFilePath_below19(albumUri);
                            }
                            ContentResolver cr = this.getContentResolver();
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(cr, albumUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Log.i("sdkVersion=bitmap=>", "" + bitmap);
                            //photoOne.setImageBitmap(bitmap);
                            //saveImage(data);//保存图片
                        }
                    } else {
                        Toast.makeText(UserDetailInfoActivity.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_LONG).show();
                    }
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        Bundle bundle = data.getExtras();
                        bitmap = (Bitmap) bundle.get("data");
                        bitmaps.add(bitmap);
                        Log.i("CROP_SMALL_photoUri=>", "" + bitmap);
                        image = new GridviewImage();
                        image.setImage(bitmap);
                        Log.i("CROP_SMALL_photoUri=>", "" + image);
                        imageList.add(image);
                        Log.i("CROP_SMALL_photoUri=>", "" + imageList.size());
                        handler.sendEmptyMessage(1);
                    }
                    break;
            }
        }

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                adapter = new GridviewImageAdapter(UserDetailInfoActivity.this, imageList);
                gridView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            super.handleMessage(msg);
        }
    };

    /**
     * API19以下获取图片路径的方法
     *
     * @param uri
     */


    private String getFilePath_below19(Uri uri) {
        //这里开始的第二部分，获取图片的路径：低版本的是没问题的，但是sdk>19会获取不到
        String[] proj = {MediaStore.Images.Media.DATA};
        //好像是android多媒体数据库的封装接口，具体的看Android文档
        Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
        //获得用户选择的图片的索引值
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        Log.i("***************", "" + column_index);
        //将光标移至开头 ，这个很重要，不小心很容易引起越界
        cursor.moveToFirst();
        //最后根据索引值获取图片路径   结果类似：/mnt/sdcard/DCIM/Camera/IMG_20151124_013332.jpg
        String path = cursor.getString(column_index);
        Log.i("path:", path);
        return path;
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    protected void startCropPhoto(Uri uri) {
        if (uri != null) {
            tempUri = uri;
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(uri, "image/*");
            // 设置裁剪
            intent.putExtra("crop", "true");
            // aspectX aspectY 是宽高的比例
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            // outputX outputY 是裁剪图片宽高
            intent.putExtra("outputX", 40);
            intent.putExtra("outputY", 40);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, CROP_SMALL_PICTURE);
        }
    }

    /**
     * 压缩图片大小
     *
     * @param path 图片的路径
     * @return
     */
    private Bitmap decodeSampleBitmap(String path) {
        //photoOne是将要呈现图片的ImageView控件
        //int targetW = photoOne.getWidth();
        //int targetH = photoOne.getHeight();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int photoW = options.outWidth;
        int photoH = options.outHeight;
        //获取图片的最大压缩比
        //int scaleFactor = Math.max(photoW/targetW,photoH/targetH);
        options.inJustDecodeBounds = false;
        //options.inSampleSize = scaleFactor;
        options.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
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
                    Log.i("UserDetailInfoActivity", "有SD卡");
                    filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Sienke/files/img/" + uid + "_" + i + ".jpg";
                    file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
                } else {
                    Log.i("UserDetailInfoActivity", "没有SD卡");
                    filePath = "data/data/" + UserDetailInfoActivity.this.getPackageName() + "/Sienke/files/img/" + uid + "_" + i + ".jpg";
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
                //保存记录到本地数据库

                //上传到数据库
                //postImage();
                Log.i("UserDetailInfoActivity", "上传到数据库");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("UserDetailInfoActivity", "getImageToView 抛出异常");
        }
    }
}
