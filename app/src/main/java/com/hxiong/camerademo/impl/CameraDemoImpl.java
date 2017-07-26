package com.hxiong.camerademo.impl;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

import com.hxiong.camerademo.CameraDemo;
import com.hxiong.camerademo.params.PictureParameters;
import com.hxiong.camerademo.params.PreviewParameters;
import com.hxiong.camerademo.params.RecordingParameters;
import com.hxiong.camerademo.util.Error;
import com.hxiong.camerademo.util.LogUtils;

/**
 * Created by hxiong on 2017/6/25 18:49.
 * Email 2509477698@qq.com
 */

public class CameraDemoImpl implements CameraDemo{

     private CameraDevice mCameraDevice;
     private CameraCharacteristics mCameraCharacteristics;
     private PreviewParameters mPreviewParameters;
     private PictureParameters mPictureParameters;
     private RecordingParameters mRecordingParameters;

     //for state
     protected  CameraState mCameraState;

     //for looper
     protected HandlerThread mHandlerThread;
     protected CameraDemoHandler mHandler;

     //session
     private CameraCaptureSession mSession;

     //session callback
     private CameraCaptureSession.CaptureCallback mCaptureCallback=new CameraCaptureSession.CaptureCallback(){

     };



     protected CameraDemoImpl(CameraDevice camera,CameraCharacteristics characteristics){
         this.mCameraDevice=camera;
         this.mCameraCharacteristics=characteristics;
         this.mCameraState=CameraState.IDLE;
         mHandlerThread=new HandlerThread("CameraDemoImpl");
         mHandlerThread.start();  //must call it
         mHandler=new CameraDemoHandler(mHandlerThread.getLooper());
     }

     protected void close(){
         if(mCameraDevice!=null){
             try {
                 LogUtils.logD("CameraDemoImpl close().");
                 mCameraDevice.close();
             }catch (Exception e){
                 e.printStackTrace();
             }finally {
                 mCameraDevice=null;
                 mHandlerThread.quit();
             }
         }
     }

     protected String getCameraId(){
         return mCameraDevice==null?"":mCameraDevice.getId();  // do not return null,why?
     }

    @Override
    public CameraState getCameraState() {
        return mCameraState;
    }

    @Override
    public PreviewParameters getPreviewParameters() {
        try {
            CaptureRequest.Builder builder=mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            if(mPreviewParameters==null){
                mPreviewParameters=new PreviewParameters();
                mPreviewParameters.setCharacteristics(mCameraCharacteristics);  //just one
            }
            mPreviewParameters.setBuilder(builder);
            return mPreviewParameters;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int startPreview(final PreviewParameters params, final PreviewCallback callback) {
        if(mCameraState==CameraState.IDLE) {
            try {
                mCameraDevice.createCaptureSession(params.getOutputSurface(), new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        mSession = session;
                        try {
                            mSession.setRepeatingRequest(params.getCaptureRequest(), mCaptureCallback, mHandler);
                            callback.onSuccess(session);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                        callback.onFailure(session);
                    }
                }, mHandler);
                mCameraState = CameraState.PREVIEW;
                return Error.OK;
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
        return Error.BAD;
    }

    @Override
    public int stopPreview() {
        if(mCameraState==CameraState.PREVIEW) {
            try {
                mSession.stopRepeating();
                mSession.close();  //need or no need
                mCameraState = CameraState.IDLE;
                return Error.OK;
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
        return Error.BAD;
    }

    @Override
    public PictureParameters getPictureParameters() {
        try {
            CaptureRequest.Builder builder=mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            if(mPictureParameters==null){
                mPictureParameters=new PictureParameters();
            }
            mPictureParameters.setBuilder(builder);
            return mPictureParameters;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int takePicture(PictureParameters params, PictureCallback callback) {
        return 0;
    }

    @Override
    public RecordingParameters getRecordingParameters() {
        try {
            CaptureRequest.Builder builder=mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            if(mRecordingParameters==null){
                mRecordingParameters=new RecordingParameters();
            }
            mRecordingParameters.setBuilder(builder);
            return mRecordingParameters;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int startRecording(RecordingParameters params, RecordingCallback callback) {
        return 0;
    }

    @Override
    public int stopRecording() {
        return 0;
    }

    class CameraDemoHandler extends Handler{

        public CameraDemoHandler(Looper looper){
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
        }
    }

}
