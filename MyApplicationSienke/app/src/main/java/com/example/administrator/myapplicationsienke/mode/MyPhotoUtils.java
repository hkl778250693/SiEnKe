package com.example.administrator.myapplicationsienke.mode;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/4/14 0014.
 */
public class MyPhotoUtils {
    private int TYPE_FILE_CROP_IMAGE;
    private String securityId;

    public MyPhotoUtils(int type,String securityId) {
        this.TYPE_FILE_CROP_IMAGE = type;
        this.securityId = securityId;
    }

    //得到输出文件的URI
    public Uri getOutFileUri(int fileType) {
        return Uri.fromFile(getOutFile(fileType));
    }

    //生成输出文件
    public File getOutFile(int fileType) {
        if (!Tools.hasSdcard()) {
            return null;
        }
        Log.i("MyPhotoUtils", "有SD卡");
        File mediaStorageDir = null;
        if(fileType == TYPE_FILE_CROP_IMAGE){
            mediaStorageDir = new File(Environment.getExternalStorageDirectory(),"SiEnKe_Crop");
            //mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"SiEnKe_Crop");
        }

        if (mediaStorageDir != null && !mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.i("MyPictures", "创建图片存储路径目录失败");
                Log.i("MyPictures", "mediaStorageDir : " + mediaStorageDir.getPath());
                return null;
            }
        }
        return new File(getFilePath(mediaStorageDir, fileType));
    }

    //生成输出文件路径
    public String getFilePath(File mediaStorageDir, int fileType) {
        String timeFlag = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filePath = mediaStorageDir.getPath() + File.separator;
        if(fileType == TYPE_FILE_CROP_IMAGE){
            filePath += ("Crop_IMG_" + timeFlag + "_" + securityId + ".jpg");
        } else {
            return null;
        }
        return filePath;
    }
}
