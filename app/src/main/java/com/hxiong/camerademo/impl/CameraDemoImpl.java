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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

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
     private byte[] mPictureData = new byte[0];
     private int mPictureDataSize = 0;

     //session callback
     private CameraCaptureSession.CaptureCallback mCaptureCallback=new CameraCaptureSession.CaptureCallback(){

         @Override
         public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
             super.onCaptureCompleted(session, request, result);
         }
     };

     //private picture callback
     private PictureCallback mPictureCallback;


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
                 closeInternal();
                 mCameraDevice.close();
             }catch (Exception e){
                 e.printStackTrace();
             }finally {
                 mCameraState = CameraState.IDLE;   //close之后变成idl，应该设置一个新的状态会更好
                 mCameraDevice=null;
                 mHandlerThread.quit();
             }
         }
     }

     private void closeInternal(){
         switch (mCameraState){
             case PREVIEW:
                 stopPreview();
                 break;
             case CAPTURE:
                 cancelCapture();
                 break;
             case RECORDING:
                 stopRecording();
                 break;
             default:
                 LogUtils.logD("closeInternal state is "+mCameraState);
                 break;
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
                LogUtils.logD("stopPreview close().");
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
    public int takePicture(final PictureParameters params, final PictureCallback callback) {
        if(mCameraState==CameraState.PREVIEW){
           stopPreview();  //停止preview
            try {
                createImageReader(params);  //拍照参数的设置
                //可以在PictureParameters 设置不同的参数，用于创建不同的CaptureSession
                mCameraDevice.createCaptureSession(params.getOutputSurfaces(), new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        mSession=session;
                        try {
                            mSession.capture(params.getCaptureRequest(),mCaptureCallback,mHandler);
                            callback.onConfigured(mSession);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                        callback.onFailure(-1);
                    }
                },mHandler);
                mPictureCallback=callback;
                mCameraState = CameraState.CAPTURE;
                return Error.OK;
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
        return Error.BAD;
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
        Image image=reader.acquireNextImage();
        LogUtils.logI("savePicture image.getWidth()="+image.getWidth()+" image.getHeight()="+image.getHeight()+
                " image.getFormat()="+image.getFormat()+" image.getTimestamp()="+image.getTimestamp());
        ByteBuffer buffer=image.getPlanes()[0].getBuffer();
        String picPath=params.getPicturePath();
        LogUtils.logI("savePicture buffer.remaining()="+buffer.remaining());
        mCameraState = CameraState.IDLE;
        if(mPictureCallback!=null){
            mPictureCallback.onPictureSaving(picPath);
        }
        onSavePicture(buffer,picPath);
        image.close();
        mImageReader.close();
    }

    private void onSavePicture(ByteBuffer buffer,final String picPath){
         //copy buffer
         int bufferSize=buffer.remaining();
         if(mPictureData.length<bufferSize){
             mPictureData=new byte[bufferSize];
         }
        mPictureDataSize=bufferSize;
        //因为mPictureData 的大小可能超过bufferSize，所以不能拷贝mPictureData.length 的大小
        buffer.get(mPictureData,0,mPictureDataSize);
        //创建一个线程去做保存图片
        new Thread(new Runnable() {
            @Override
            public void run() {
                saveDataToFile(mPictureData,mPictureDataSize,picPath);
            }
        }).start();
    }

    private void saveDataToFile(byte[] data,int size,String path){
        FileOutputStream fous=null;
        try {
            fous=new FileOutputStream(path);
            fous.write(data,0,size);
            fous.flush();  //must
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            closeOutputStream(fous);
        }
        if(mPictureCallback!=null){
            mPictureCallback.onPictureTaken(path);
        }
    }

    private void closeOutputStream(OutputStream ous){
        try {
            if(ous!=null) ous.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
