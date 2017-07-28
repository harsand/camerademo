package com.hxiong.camerademo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.hxiong.camerademo.impl.CameraDemoManagerImpl;
import com.hxiong.camerademo.params.PictureParameters;
import com.hxiong.camerademo.util.LogUtils;
import com.hxiong.camerademo.util.SurfaceTextureControl;

public class CameraDemoActivity extends BaseActivity {

    private DisplayMetrics mMetric;

    private CameraDemoManagerImpl mCameraDemoManagerImpl;
    private SurfaceTextureControl mControl;
    private ImageView mSwitchBtn;
    private ImageView mVideoBtn;
    private ImageView mCaptureBtn;

    private CameraDemo mFrontCamera;   //前置摄像头，cameraId 为 1
    private CameraDemo mBackCamera;    //后置摄像头，cameraId 为 0

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

                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    ////////////// handle msg here
    private void handleCameraOpen(CameraDemo camera,int id){
         if(id==0){
             mBackCamera=camera;
         }else if(id==1) {
             mFrontCamera = camera;
         }
         mControl.setCamera(camera);
    }

    private void handleCameraSwitch(){

    }

    private void handleCameraRecord(){

    }

    private void handleCameraCapture(){
        if(mBackCamera!=null){
            PictureParameters params=mBackCamera.getPictureParameters();

            mBackCamera.takePicture(params,mPictureCallback);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCameraDemoManagerImpl.release();
        mCameraDemoManagerImpl=null;
    }
}
