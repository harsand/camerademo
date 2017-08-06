package com.hxiong.camerademo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraCaptureSession;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.hxiong.camerademo.impl.CameraDemoManagerImpl;
import com.hxiong.camerademo.impl.SensorManagerImpl;
import com.hxiong.camerademo.impl.StorageManagerImpl;
import com.hxiong.camerademo.params.PictureParameters;
import com.hxiong.camerademo.params.RecordingParameters;
import com.hxiong.camerademo.util.LogUtils;
import com.hxiong.camerademo.util.SurfaceTextureControl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraDemoActivity extends BaseActivity {

    private DisplayMetrics mMetric;

    private CameraDemoManagerImpl mCameraDemoManagerImpl;
    private StorageManagerImpl mStorageManagerImpl;
    private SensorManagerImpl mSensorManagerImpl;
    private SurfaceTextureControl mControl;
    private ImageView mSwitchBtn;
    private ImageView mVideoBtn;
    private ImageView mCaptureBtn;

    private CameraDemo mFrontCamera;   //前置摄像头，cameraId 为 1
    private CameraDemo mBackCamera;    //后置摄像头，cameraId 为 0

    //是否正在拍照
    private boolean isCapture = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mMetric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mMetric);
        setContentView(R.layout.activity_camera_demo);
        init();
    }

    private void init(){
        TextureView textureView=(TextureView)findViewById(R.id.camera_demo_surface);
        mControl=new SurfaceTextureControl(textureView,mMetric);

        mSwitchBtn=(ImageView)findViewById(R.id.camera_switch);
        mVideoBtn=(ImageView)findViewById(R.id.camera_video);
        mCaptureBtn=(ImageView)findViewById(R.id.camera_capture);

        mSwitchBtn.setOnClickListener(mClickListener);
        mVideoBtn.setOnClickListener(mClickListener);
        mCaptureBtn.setOnClickListener(mClickListener);

        mCameraDemoManagerImpl=new CameraDemoManagerImpl(this);
        mStorageManagerImpl = new StorageManagerImpl(this);
        SensorManager sensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mSensorManagerImpl=new SensorManagerImpl(sensorManager);
        if(!mCameraDemoManagerImpl.hasFrontCamera()){  //如果没有两个摄像头，则不显示切换按钮
            mSwitchBtn.setVisibility(View.GONE);
        }
        if(mCameraDemoManagerImpl.hasCamera()){
            mCameraDemoManagerImpl.openCamera("0",mCameraCallback);
        }else{
            LogUtils.logE("machine has not camera.");
        }
    }

    private View.OnClickListener mClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.camera_switch:
                    mHandler.sendEmptyMessage(MSG_CAMERA_SWITCH);
                    break;
                case R.id.camera_video:
                    mHandler.sendEmptyMessage(MSG_CAMERA_RECORD);
                    break;
                case R.id.camera_capture:
                    mHandler.sendEmptyMessage(MSG_CAMERA_CAPTURE);
                    break;
                default:  break;
            }
        }
    };

    private CameraDemo.CameraCallback mCameraCallback=new CameraDemo.CameraCallback() {
        @Override
        public void onOpen(CameraDemo camera, String id) {
            LogUtils.logD("CameraCallback onOpen:camera id is "+id);
            Message message=Message.obtain();
            message.what=MSG_CAMERA_OPEN;
            message.arg1=Integer.parseInt(id);
            message.obj=camera;
            mHandler.sendMessage(message);
        }

        @Override
        public void onFailure(String id) {
            LogUtils.logI("CameraCallback onFailure(). cameraId="+id);
        }

        @Override
        public void onClose(String id) {
            LogUtils.logI("CameraCallback onClose().cameraId="+id);
        }
    };

    private CameraDemo.PictureCallback mPictureCallback=new CameraDemo.PictureCallback() {

        @Override
        public void onConfigured(CameraCaptureSession session) {

        }

        @Override
        public void onFailure(int reason) {
            LogUtils.logD("PictureCallback onFailure call");
            mHandler.sendEmptyMessage(MSG_CAMERA_PREVIEW); //also need to start preview
            isCapture=false;
        }

        @Override
        public void onPictureSaving(String picturePath) {
            LogUtils.logD("onPictureSaving picturePath is "+picturePath);
            mHandler.sendEmptyMessage(MSG_CAMERA_PREVIEW);
        }

        @Override
        public void onPictureTaken(String picturePath) {
            LogUtils.logD("onPictureTaken picturePath is "+picturePath);
            isCapture=false;
        }
    };

    private CameraDemo.RecordingCallback mRecordingCallback=new CameraDemo.RecordingCallback() {


        @Override
        public void onConfigured(CameraCaptureSession session) {

        }

        @Override
        public void onFailure(int reason) {

        }
    };

    private Handler mHandler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            LogUtils.logI("handleMessage msg.what="+msg.what);
            switch (msg.what){
                case MSG_CAMERA_OPEN:
                    handleCameraOpen((CameraDemo) msg.obj,msg.arg1);
                    break;
                case MSG_CAMERA_SWITCH:
                    handleCameraSwitch();
                    break;
                case MSG_CAMERA_RECORD:
                    handleCameraRecord();
                    break;
                case MSG_CAMERA_CAPTURE:
                    handleCameraCapture();
                    break;
                case MSG_CAMERA_PREVIEW:
                    handleCameraPreview();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    ////////////// handle msg here
    private void handleCameraOpen(CameraDemo camera,int id){
         if(id==0){
             mBackCamera = camera;
         }else if(id==1) {
             mFrontCamera = camera;
         }
         mControl.setCamera(camera);
    }

    private void handleCameraSwitch(){
        if(mBackCamera==null){   //当前使用的是前置摄像头
            mCameraDemoManagerImpl.closeCamera("1");
            mFrontCamera=null;
            mCameraDemoManagerImpl.openCamera("0",mCameraCallback);
        }else{   //当前使用的是后置摄像头，切换成前置
            mCameraDemoManagerImpl.closeCamera("0");
            mBackCamera=null;
            mCameraDemoManagerImpl.openCamera("1",mCameraCallback);
        }
    }

    private void handleCameraRecord(){
        CameraDemo cameraDemo = mBackCamera==null?mFrontCamera:mBackCamera;
        if(cameraDemo.getCameraState()== CameraDemo.CameraState.RECORDING){
            cameraDemo.stopRecording();
            mVideoBtn.setImageResource(R.mipmap.ic_video);
        }else{
            RecordingParameters params=cameraDemo.getRecordingParameters();
            params.setPreviewSurface(mControl.getSurface());
            params.setOutputFile(mStorageManagerImpl.createVideoPath());
            LogUtils.logD("output file is "+mStorageManagerImpl.createVideoPath());
            Size videoSize=params.getNearVideoSize(mMetric.widthPixels,mMetric.heightPixels);
            params.setVideoSize(videoSize.getWidth(),videoSize.getHeight());
            cameraDemo.startRecording(params,mRecordingCallback);
            mVideoBtn.setImageResource(R.mipmap.ic_video_active);
        }
    }

    private void handleCameraCapture(){
        if(isCapture){
            LogUtils.logE("now is capturing,please wait.");
        }
        isCapture=true;
        CameraDemo cameraDemo = mBackCamera==null?mFrontCamera:mBackCamera;
        if(cameraDemo.getCameraState()== CameraDemo.CameraState.RECORDING){
            snapshotCapture();
        }else{
            normalCapture(cameraDemo);
        }
    }

    /**
     * preview 状态下的拍照
     * @param cameraDemo
     */
    private void normalCapture(CameraDemo cameraDemo){
        try {
            PictureParameters params = cameraDemo.getPictureParameters();
            params.setPreviewSurface(mControl.getSurface());
            Size pictureSize=params.getMaxPictureSizes(mMetric.widthPixels,mMetric.heightPixels);
            params.setPictureSize(pictureSize.getWidth(),pictureSize.getHeight());
            params.setPictureFormat(ImageFormat.JPEG);
            params.setPictureOrientation(getPictureRotation());
            params.setPicturePath(mStorageManagerImpl.createPicturePath());
            cameraDemo.takePicture(params, mPictureCallback);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 录制状态下的拍照
     */
    private void snapshotCapture(){
        try {
            TextureView textureView=mControl.getTextureView();
            Bitmap bitmap=textureView.getBitmap();  //直接从textureview上获取内容
            if(bitmap!=null) {
                saveBitmapAsPicture(bitmap);
                bitmap.recycle();  //take care
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveBitmapAsPicture(final Bitmap bitmap){
        /**
         * 是应该创建一个新的线程来保存数据好，还是放到一个handler 线程去处理
         * 需要根据实际情况来考虑，每次创建一个线程也是消耗时间的
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                onSavePicture(bitmap);
            }
        }).start();

    }

    private void onSavePicture(final Bitmap bitmap){
        FileOutputStream fous=null;
        try {
            fous=new FileOutputStream(mStorageManagerImpl.createPicturePath());
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fous);
            fous.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            if(fous!=null) fous.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isCapture=false;  //very importance
    }

    private void handleCameraPreview(){
         mControl.restartPreview();
    }

    private PictureParameters.Rotation getPictureRotation(){
        int orientation=mSensorManagerImpl.getGsensorOrientation();
        LogUtils.logD("getGsensorOrientation orientation="+orientation);

        switch (orientation){
            case SensorManagerImpl.GSENSOE_ORIENTATION_0:
                return PictureParameters.Rotation.ROTATION_0;
            case SensorManagerImpl.GSENSOE_ORIENTATION_90:
                return PictureParameters.Rotation.ROTATION_90;
            case SensorManagerImpl.GSENSOE_ORIENTATION_180:
                return PictureParameters.Rotation.ROTATION_180;
            case SensorManagerImpl.GSENSOE_ORIENTATION_270:
                return PictureParameters.Rotation.ROTATION_270;
            default: break;
        }
        return PictureParameters.Rotation.ROTATION_0;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSensorManagerImpl.enableSensor();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSensorManagerImpl.disableSensor();
        CameraDemo cameraDemo = mBackCamera==null?mFrontCamera:mBackCamera;
        if(cameraDemo.getCameraState()== CameraDemo.CameraState.RECORDING){
            cameraDemo.stopRecording();
            mVideoBtn.setImageResource(R.mipmap.ic_video);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCameraDemoManagerImpl.release();
        mCameraDemoManagerImpl=null;
        mSensorManagerImpl.destroy();
        mSensorManagerImpl=null;
    }
}
