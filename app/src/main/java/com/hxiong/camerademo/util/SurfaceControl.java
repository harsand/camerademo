package com.hxiong.camerademo.util;

import android.hardware.camera2.CameraCaptureSession;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.hxiong.camerademo.CameraDemo;
import com.hxiong.camerademo.params.PreviewParameters;

/**
 * Created by hxiong on 2017/7/23 22:25.
 * Email 2509477698@qq.com
 */

public class SurfaceControl implements SurfaceHolder.Callback,CameraDemo.PreviewCallback{

    private boolean isSurfaceCreated;
    private SurfaceView mSurfaceView;
    private CameraDemo mCameraDemo;

    public SurfaceControl(SurfaceView surfaceView){
        this.mSurfaceView=surfaceView;
        isSurfaceCreated=false;
        mCameraDemo=null;
        mSurfaceView.getHolder().addCallback(this);
    }

    public void setCamera(CameraDemo cameraDemo){
         if(mCameraDemo==null){  //new camera
             mCameraDemo=cameraDemo;
             if(isSurfaceCreated){
                 PreviewParameters params=mCameraDemo.getPreviewParameters();
                 params.setPreviewSurface(mSurfaceView.getHolder().getSurface());
                 mCameraDemo.startPreview(params,this);
//                 Size[] sizes=params.getPreviewSizes();
//                 if(sizes!=null) {
//                     for (Size size : sizes) {
//                         LogUtils.logD("size is <" + size.getWidth() + "," + size.getHeight() + ">");
//                     }
//                 }
             }
         }else{  //transform to other camera
             mCameraDemo.stopPreview();

         }
    }

    public Surface getSurface(){
        return mSurfaceView.getHolder().getSurface();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
         LogUtils.logI("SurfaceControl surfaceCreated().");
         isSurfaceCreated=true;
         if(mCameraDemo!=null){
             PreviewParameters params=mCameraDemo.getPreviewParameters();
             params.setPreviewSurface(mSurfaceView.getHolder().getSurface());
             mCameraDemo.startPreview(params,this);
         }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        LogUtils.logI("SurfaceControl surfaceChanged().");

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtils.logI("SurfaceControl surfaceDestroyed().");
        isSurfaceCreated=false;
        if(mCameraDemo!=null){
            mCameraDemo.stopPreview();
        }
    }

    @Override
    public void onSuccess(CameraCaptureSession session) {

    }

    @Override
    public void onFailure(CameraCaptureSession session) {

    }
}
