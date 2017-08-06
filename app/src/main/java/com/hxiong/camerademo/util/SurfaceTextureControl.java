package com.hxiong.camerademo.util;

import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import com.hxiong.camerademo.CameraDemo;
import com.hxiong.camerademo.params.PreviewParameters;

/**
 * Created by hxiong on 2017/7/28 23:04.
 * Email 2509477698@qq.com
 */

public class SurfaceTextureControl implements TextureView.SurfaceTextureListener,CameraDemo.PreviewCallback{

    private boolean isSurfaceCreated;
    private TextureView mTextureView;
    private DisplayMetrics mMetric;
    private CameraDemo mCameraDemo;

    public SurfaceTextureControl(TextureView textureView,DisplayMetrics metric){
          this.mTextureView=textureView;
          this.mMetric=metric;
          mTextureView.setSurfaceTextureListener(this);
    }

    public void setCamera(CameraDemo camera){
        if(mCameraDemo!=null){  // we need to stoppreview
            mCameraDemo.stopPreview();
        }
        mCameraDemo=camera;  //设置新的camera
        restartPreview();
    }

    public TextureView getTextureView(){ return mTextureView;}

    public Surface getSurface(){
        return new Surface(mTextureView.getSurfaceTexture());
    }

    public void restartPreview(){
        if(isSurfaceCreated&&mCameraDemo!=null&&mCameraDemo.getCameraState()== CameraDemo.CameraState.IDLE) {
            PreviewParameters params=mCameraDemo.getPreviewParameters();
            Size previewSize=params.getNearPreviewSize(mMetric.widthPixels,mMetric.heightPixels);
            SurfaceTexture surface=mTextureView.getSurfaceTexture();
            surface.setDefaultBufferSize(previewSize.getWidth(),previewSize.getHeight());
            params.setPreviewSurface(new Surface(surface));
            mCameraDemo.startPreview(params,this);
        }
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        LogUtils.logD("onSurfaceTextureAvailable width="+width+" height="+height);
        isSurfaceCreated=true;
        if(mCameraDemo!=null&&mCameraDemo.getCameraState()== CameraDemo.CameraState.IDLE){
            PreviewParameters params=mCameraDemo.getPreviewParameters();
            Size previewSize=params.getNearPreviewSize(mMetric.widthPixels,mMetric.heightPixels);
            surface.setDefaultBufferSize(previewSize.getWidth(),previewSize.getHeight());
            params.setPreviewSurface(new Surface(surface));
            mCameraDemo.startPreview(params,this);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        LogUtils.logD("onSurfaceTextureAvailable width="+width+" height="+height);

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        isSurfaceCreated=false;
        if(mCameraDemo!=null){
            mCameraDemo.stopPreview();
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onSuccess(CameraCaptureSession session) {

    }

    @Override
    public void onFailure(CameraCaptureSession session) {

    }
}
