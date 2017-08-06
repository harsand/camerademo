package com.hxiong.camerademo.impl;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.hxiong.camerademo.CameraDemo;
import com.hxiong.camerademo.util.LogUtils;

import java.util.ArrayList;

/**
 * Created by hxiong on 2017/6/25 18:49.
 * Email 2509477698@qq.com
 */

public class CameraDemoManagerImpl {

    private Context mContext;
    private CameraManager mCameraManager;
    private RecorderManagerImpl mRecorderManagerImpl;
    private ArrayList<CameraDemoImpl> mCameraImpls;


    public CameraDemoManagerImpl(Context context) {
        this.mContext = context;
        this.mCameraImpls=new ArrayList<CameraDemoImpl>();
        this.mRecorderManagerImpl=new RecorderManagerImpl();
        checkCameraManager();
    }

    private synchronized boolean checkCameraManager() {
        if (mCameraManager == null) {
            try {
                mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
                LogUtils.logI("init CameraManager.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mCameraManager != null;
    }

    private boolean checkPermission(){
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            LogUtils.logE("checkSelfPermission fail.");
            return false;
        }
        return true;
    }

    public synchronized void openCamera(final String cameraId,final CameraDemo.CameraCallback callback) {
        if(isCameraOpened(cameraId)){
            LogUtils.logD("cameraDevice had opened. cameraId="+cameraId);

            return;
        }
        try {
            if(checkCameraManager()&&checkPermission()) {
                mCameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {

                    private boolean firstCallback = true;
                    @Override
                    public void onOpened(@NonNull CameraDevice camera) {
                        LogUtils.logD("StateCallback onOpened()");
                        if (firstCallback) {
                            firstCallback = false;
                            CameraDemoImpl cameraDemo=new CameraDemoImpl(mRecorderManagerImpl,camera,getCameraCharacteristics(cameraId));
                            mCameraImpls.add(cameraDemo);   //save it
                            callback.onOpen(cameraDemo,cameraId);
                        }
                    }

                    @Override
                    public void onDisconnected(@NonNull CameraDevice camera) {
                        LogUtils.logI("StateCallback onDisconnected().");
                        if (firstCallback) {
                            firstCallback = false;
                            camera.close();
                            callback.onClose(cameraId);
                        }
                    }

                    @Override
                    public void onError(@NonNull CameraDevice camera,int error) {
                        LogUtils.logI("StateCallback onError().");
                        if (firstCallback) {
                            firstCallback = false;
                            camera.close();
                        }
                        callback.onFailure(cameraId);
                    }
                }, null);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
     }

    public synchronized void closeCamera(final String cameraId){
        for(CameraDemoImpl cameraImpl:mCameraImpls){
            if(cameraImpl.getCameraId().equals(cameraId)){
                cameraImpl.close();
                mCameraImpls.remove(cameraImpl);
                return ;
            }
        }
        LogUtils.logD("CameraDemoManagerImpl closeCamera().");
    }

    public boolean hasCamera(){
         String[] cameraIds=getCameraIdList();
         return (cameraIds!=null&&cameraIds.length>0);
    }

    /**
     * 判断是否有前置摄像头
     * @return 如果cameraId 数目大于1，返回true，否则返回false
     */
    public boolean hasFrontCamera(){
        String[] cameraIds=getCameraIdList();
        return (cameraIds!=null&&cameraIds.length>1);
    }

     //common api from CameraManager
     public String[] getCameraIdList(){
         try {
             if(checkCameraManager()) {
                return mCameraManager.getCameraIdList();
             }
         } catch (CameraAccessException e) {
             e.printStackTrace();
         }
         return null;
     }

     public CameraCharacteristics getCameraCharacteristics(String cameraId){
         try {
             if(checkCameraManager()&&cameraId!=null) {
                 return mCameraManager.getCameraCharacteristics(cameraId);
             }
         } catch (CameraAccessException e) {
             e.printStackTrace();
         }
         return null;
     }

    /**
     * must call when application exit
     */
    public void release(){
        LogUtils.logD("CameraDemoManagerImpl release().");
        for(CameraDemoImpl cameraImpl:mCameraImpls){
            cameraImpl.close();
        }
        mCameraImpls.clear();
        mRecorderManagerImpl.destroy();
     }

     private boolean isCameraOpened(String cameraId){
         for(CameraDemoImpl cameraImpl:mCameraImpls){
             if(cameraImpl.getCameraId().equals(cameraId)){
                 LogUtils.logD("CameraDemoManagerImpl isCameraOpened().");
                 return true;
             }
         }
         return false;
     }
}
