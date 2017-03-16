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
    private ImageView back,more,addImgs;  //返回，更多，添加图片
    private LinearLayout rootLinearlayout;
    private TextView securityCheckCase,securityHiddenType,securityHiddenReason;  //安全情况,安全隐患类型，安全隐患原因
    private Button saveBtn,takePhoto,photoAlbum,cancel;  //保存、拍照、相册、取消
    private LayoutInflater inflater;  //转换器
    private View popupwindowView;
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

        //绑定控件
        bindView();
        //初始化设置
        defaultSetting();
    }

    //绑定控件
    private void bindView() {
        back = (ImageView) findViewById(R.id.back);
        more = (ImageView) findViewById(R.id.more);
        securityCheckCase = (TextView) findViewById(R.id.security_check_case);
        securityHiddenType = (TextView) findViewById(R.id.security_hidden_type);
        securityHiddenReason = (TextView) findViewById(R.id.security_hidden_reason);
        addImgs = (ImageView) findViewById(R.id.add_imgs);
        saveBtn = (Button) findViewById(R.id.save_btn);
        takePhoto = (Button) popupwindowView.findViewById(R.id.take_photo);
        photoAlbum = (Button) popupwindowView.findViewById(R.id.photo_album);
        cancel = (Button) popupwindowView.findViewById(R.id.cancel);
        rootLinearlayout = (LinearLayout) findViewById(R.id.root_linearlayout);
    }

    //点击事件
    public void setOnClickListener(){
        back.setOnClickListener(onClickListener);
        more.setOnClickListener(onClickListener);
        securityCheckCase.setOnClickListener(onClickListener);
        securityHiddenType.setOnClickListener(onClickListener);
        securityHiddenReason.setOnClickListener(onClickListener);
        addImgs.setOnClickListener(onClickListener);
        saveBtn.setOnClickListener(onClickListener);
        takePhoto.setOnClickListener(onClickListener);
        photoAlbum.setOnClickListener(onClickListener);
        cancel.setOnClickListener(onClickListener);
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
                    break;
                case R.id.security_hidden_type:
                    break;
                case R.id.security_hidden_reason:
                    break;
                case R.id.add_imgs:
                    createPopupwindow();
                    break;
                case R.id.save_btn:  //保存
                    break;
                case R.id.take_photo:  //拍照
                    openCamera();
                    break;
                case R.id.photo_album:  //打开相册
                    openAlnum();
                    break;
                case R.id.cancel:
                    popupWindow.dismiss();
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
    public void createPopupwindow(){
        inflater = LayoutInflater.from(UserDetailInfoActivity.this);
        popupwindowView = inflater.inflate(R.layout.popupwindow_security_userinfo,null);
        popupWindow = new PopupWindow(popupwindowView, LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.white));
        popupWindow.setAnimationStyle(R.style.dialog);
        popupWindow.showAtLocation(rootLinearlayout,Gravity.BOTTOM,0,0);
        backgroundAlpha(0.8F);   //背景变暗
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
