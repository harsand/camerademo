package com.hxiong.camerademo.impl;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
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

     //for capture buffer data
     private ImageReader mImageReader;

     //session callback
     private CameraCaptureSession.CaptureCallback mCaptureCallback=new CameraCaptureSession.CaptureCallback(){

         @Override
         public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
             super.onCaptureCompleted(session, request, result);
         }
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
            if(mPreviewParameters==null){   //just once
                CaptureRequest.Builder builder=mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                mPreviewParameters=new PreviewParameters(mCameraCharacteristics,builder);
            }
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
                mCameraDevice.createCaptureSession(params.getOutputSurfaces(), new CameraCaptureSession.StateCallback() {
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
            if(mPictureParameters==null){
                CaptureRequest.Builder builder=mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                mPictureParameters=new PictureParameters(mCameraCharacteristics,builder);
            }
            return mPictureParameters;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int takePicture(PictureParameters params, PictureCallback callback) {
        if(mCameraState==CameraState.PREVIEW){
           //stopPreview();

        }

        return 0;
    }

    @Override
    public int cancelCapture() {

        return 0;
    }

    @Override
    public RecordingParameters getRecordingParameters() {
        try {
            if(mRecordingParameters==null){  //just once
                CaptureRequest.Builder builder=mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
                mRecordingParameters=new RecordingParameters(mCameraCharacteristics,builder);
            }
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

    //
    private void createImageReader(final PictureParameters params){

        mImageReader=ImageReader.newInstance(params.getPictureWidth(),params.getPictureHeight(),params.getPictureFormat(),2);
        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                savePicture(reader,params);
            }
        },mHandler);
        params.setPictureSurface(mImageReader.getSurface());  //
    }

    private void savePicture(ImageReader reader,final PictureParameters params){
        Image image=reader.acquireLatestImage();
        //todo

        image.close();
        mImageReader.close();
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
