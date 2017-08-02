package com.hxiong.camerademo.impl;

import android.content.Context;
import android.os.Environment;

import com.hxiong.camerademo.util.LogUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hxiong on 2017/8/1 22:03.
 * Email 2509477698@qq.com
 */

public class StorageManagerImpl {

    protected static final String DATE_FORMAT="yyyy-MM-dd_hh-mm-ss";
    protected static final String DCIM_DIR="/DCIM";
    protected static final String CAMERA_DEMO_DIR="/CameraDemo";
    protected static final String CAMERA_DATE_FORMAT="yyyy-MM";
    protected static final String PICTURE_PREFIX="/IMG_";
    protected static final String PICTURE_JPG=".jpg";


    private Context mContext;
    private SimpleDateFormat mDateFormat;
    private String mPictureDir;

    public StorageManagerImpl(Context context){
        this.mContext=context;
        mDateFormat=new SimpleDateFormat(DATE_FORMAT);
        SimpleDateFormat dateFormat=new SimpleDateFormat(CAMERA_DATE_FORMAT);
        mPictureDir="/"+dateFormat.format(new Date());
    }

    public String createPicturePath(){
        String pictureDir=getPicturePath();
        return pictureDir+getPictureName();
    }

    /////////////////////内部实现
    private String getDCIMPath(){
        File externalDir=Environment.getExternalStorageDirectory();
        File pictureDir=new File(externalDir.getPath()+DCIM_DIR);
        if(!pictureDir.exists()){
            LogUtils.logD("create  dir path: "+pictureDir.getPath());
            pictureDir.mkdir();
        }
        return pictureDir.getPath();
    }

    private String getCameraDemoPath(){
        File cameraDemoDir=new File(getDCIMPath()+CAMERA_DEMO_DIR);
        if(!cameraDemoDir.exists()){
            LogUtils.logD("create  dir path: "+cameraDemoDir.getPath());
            cameraDemoDir.mkdir();
        }
        return cameraDemoDir.getPath();
    }

    private String getPicturePath(){
        File pictureDir=new File(getCameraDemoPath()+mPictureDir);
        if(!pictureDir.exists()){
            LogUtils.logD("create  dir path: "+pictureDir.getPath());
            pictureDir.mkdir();
        }
        return pictureDir.getPath();
    }

    private String getPictureName(){
        return PICTURE_PREFIX+mDateFormat.format(new Date())+PICTURE_JPG;
    }
}
