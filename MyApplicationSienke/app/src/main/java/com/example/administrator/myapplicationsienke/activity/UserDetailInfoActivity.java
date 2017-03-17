package com.example.administrator.myapplicationsienke.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myapplicationsienke.R;
import com.example.administrator.myapplicationsienke.mode.Tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2017/3/16 0016.
 */
public class UserDetailInfoActivity extends Activity {
    private ImageView back,more;  //返回，更多，添加图片
    private LinearLayout rootLinearlayout,addImgs;
    private TextView securityCheckCase,securityHiddenType,securityHiddenReason;  //安全情况,安全隐患类型，安全隐患原因
    private Button saveBtn,takePhoto,photoAlbum,cancel;  //保存、拍照、相册、取消
    private RadioButton notSecurityCheck,passSecurityCheck,notPassSecurityCheck,overSecurityCheckTime,nobodyHere,refuseSecurityCheck;
    private RadioButton commonSecurityCheck,yearPlan,recheck,passGasSecurityCheck;
    private RadioButton indoorStandPipe,indoorBranchPipe,fuelGasMeter,burningAppliances,gasFacilitiesRoom,threeWayPipe;
    private LayoutInflater inflater;  //转换器
    private View popupwindowView,securityCaseView,securityHiddenTypeView,securityHiddenreasonView;
    private PopupWindow popupWindow;
    protected static Uri tempUri;
    protected static final int TAKE_PHOTO = 100;//选择本地照片
    protected static final int PHOTO_ALBUM = 200;//拍照
    protected static final int CROP_SMALL_PICTURE = 300;  //裁剪成小图片
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String result, path, uid;


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
        addImgs = (LinearLayout) findViewById(R.id.add_imgs);
        saveBtn = (Button) findViewById(R.id.save_btn);
        rootLinearlayout = (LinearLayout) findViewById(R.id.root_linearlayout);
    }

    //点击事件
    private void setViewClickListener(){
        back.setOnClickListener(onClickListener);
        more.setOnClickListener(onClickListener);
        securityCheckCase.setOnClickListener(onClickListener);
        securityHiddenType.setOnClickListener(onClickListener);
        securityHiddenReason.setOnClickListener(onClickListener);
        addImgs.setOnClickListener(onClickListener);
        saveBtn.setOnClickListener(onClickListener);
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
                case R.id.add_imgs:
                    createPhotoPopupwindow();
                    Log.i("createPopupwindow===>","true");
                    break;
                case R.id.save_btn:  //保存
                    break;
            }
        }
    };

    //初始化设置
    private void defaultSetting() {
        sharedPreferences = UserDetailInfoActivity.this.getSharedPreferences("data", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    //弹出照相或者选择照片popupwindow
    public void createPhotoPopupwindow(){
        inflater = LayoutInflater.from(UserDetailInfoActivity.this);
        popupwindowView = inflater.inflate(R.layout.popupwindow_security_userinfo_take_photo,null);
        popupWindow = new PopupWindow(popupwindowView, LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        //绑定控件ID
        takePhoto = (Button) popupwindowView.findViewById(R.id.take_photo);
        photoAlbum = (Button) popupwindowView.findViewById(R.id.photo_album);
        cancel = (Button) popupwindowView.findViewById(R.id.cancel);
        //设置点击事件
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();//拍照
            }
        });
        photoAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAlnum();//打开相册
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
        popupWindow.setAnimationStyle(R.style.Popupwindow);
        backgroundAlpha(0.8F);   //背景变暗
        popupWindow.showAtLocation(rootLinearlayout,Gravity.BOTTOM,0,0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0F);
            }
        });
    }

    //弹出安全情况popupwindow
    public void createSecurityCasePopupwindow(){
        inflater = LayoutInflater.from(UserDetailInfoActivity.this);
        securityCaseView = inflater.inflate(R.layout.popupwindow_security_case,null);
        popupWindow = new PopupWindow(securityCaseView, LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
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
            }
        });
        passSecurityCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityCheckCase.setText(passSecurityCheck.getText());
            }
        });
        notPassSecurityCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityCheckCase.setText(notPassSecurityCheck.getText());
            }
        });
        overSecurityCheckTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityCheckCase.setText(overSecurityCheckTime.getText());
            }
        });
        nobodyHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityCheckCase.setText(nobodyHere.getText());
            }
        });
        refuseSecurityCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                securityCheckCase.setText(refuseSecurityCheck.getText());
            }
        });
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
        popupWindow.setAnimationStyle(R.style.Popupwindow);
        backgroundAlpha(0.8F);   //背景变暗
        popupWindow.showAsDropDown(securityCheckCase,0,0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0F);
            }
        });
    }

    //弹出安全隐患类型popupwindow
    public void createSecurityHiddenTypePopupwindow(){
        inflater = LayoutInflater.from(UserDetailInfoActivity.this);
        securityHiddenTypeView = inflater.inflate(R.layout.popupwindow_security_hidden_type,null);
        popupWindow = new PopupWindow(securityHiddenTypeView, LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
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
        popupWindow.showAsDropDown(securityHiddenType,0,0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0F);
            }
        });
    }

    //弹出安全隐患原因popupwindow
    public void createSecurityHiddenReasonPopupwindow(){
        inflater = LayoutInflater.from(UserDetailInfoActivity.this);
        securityHiddenreasonView = inflater.inflate(R.layout.popupwindow_security_hidden_reason,null);
        popupWindow = new PopupWindow(securityHiddenreasonView, LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
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
        popupWindow.showAsDropDown(securityHiddenReason,0,0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0F);
            }
        });
    }

    //设置背景透明度
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    //调用相机
    public void openCamera(){
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
        tempUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"image.jpg"));
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,tempUri);
        startActivityForResult(openCameraIntent,TAKE_PHOTO);
    }

    //调用本地相册
    public void openAlnum(){
        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
        openAlbumIntent.setType("image/*");
        startActivityForResult(openAlbumIntent,PHOTO_ALBUM);
    }

    //页面回调方法


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RESULT_OK){   //如果返回码是可用的
            switch (requestCode){
                case TAKE_PHOTO:
                    startCropPhoto(tempUri);
                    break;
                case PHOTO_ALBUM:
                    if(Tools.hasSdcard()){
                        startCropPhoto(data.getData());
                    }else {
                        Toast.makeText(UserDetailInfoActivity.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_LONG).show();
                    }
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        saveImage(data);//保存图片
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     * @param uri
     */
    protected void startCropPhoto(Uri uri){
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
            intent.putExtra("outputX", 150);
            intent.putExtra("outputY", 150);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, CROP_SMALL_PICTURE);
        }
    }

    /**
     * 保存裁剪之后的图片数据
     * @param data
     */
    private void saveImage(Intent data) {
        String filePath;
        File file;
        Bundle extras = data.getExtras();
        try {
            if (extras != null) {
                Bitmap photo = extras.getParcelable("data");//第一步，将Drawable对象转化为Bitmap对象
                if (Tools.hasSdcard()) {
                    Log.i("UserDetailInfoActivity", "有SD卡");
                    filePath = Environment.getExternalStorageDirectory() + "/Sienke/files/img/" + uid + ".jpg";
                    file = new File(filePath);
                } else {
                    Log.i("UserDetailInfoActivity", "没有SD卡");
                    filePath = "data/data/" + UserDetailInfoActivity.this.getPackageName() + "/files/img/" + uid + ".jpg";
                    file = new File(filePath);
                }
                if (!file.exists()) {
                    file.createNewFile();
                }
                Log.i("UserDetailInfoActivity", "file=>" + filePath);
                FileOutputStream fileOutputStream = new FileOutputStream(file);//通过file打开输出流
                //将bitmap写入到文件
                photo.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);//图片压缩
                fileOutputStream.flush();
                fileOutputStream.close();
                Log.i("UserDetailInfoActivity", "bitmap写入到文件");
                //提交记录到本地数据库
                editor.putString(uid, filePath);
                editor.commit();
                Log.i("UserDetailInfoActivity", "提交记录");
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
