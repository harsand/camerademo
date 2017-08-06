package com.hxiong.camerademo.params;

import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.media.MediaRecorder;
import android.util.Size;
import android.view.Surface;

/**
 * Created by hxiong on 2017/7/23 21:50.
 * Email 2509477698@qq.com
 */

public class RecordingParameters extends Parameters {

    private Surface mPreviewSurface;
    private Surface mVideoSurface;

    private String mOutputFile;
    private int mVideoWidth;
    private int mVideoHeight;

    //
    private int mVideoEncodingBitRate;
    private int mVideoFrameRate;

    public RecordingParameters(CameraCharacteristics characteristics, CaptureRequest.Builder builder){
        super(characteristics, builder);
        //default value
        mVideoEncodingBitRate=10000000;
        mVideoFrameRate=30;
    }

    public void setPreviewSurface(Surface surface){
        setSurface(mPreviewSurface,surface);
        mPreviewSurface=surface;
    }

    public void setVideoSurface(Surface surface){
        setSurface(mVideoSurface,surface);
        mVideoSurface=surface;
    }

    public Size[] getVideoSizes(){
        return getPreviewSizes(MediaRecorder.class);
    }

    public Size getNearVideoSize(int referWidth, int referHeight){
        Size[] sizes=getVideoSizes();
        if(sizes!=null){
            for(Size size:sizes){
               return size;
            }
        }
        return new Size(referHeight,referWidth);
    }

    public void setOutputFile(String outputFile){
        mOutputFile=outputFile;
    }

    public String getOutputFile(){
        return mOutputFile;
    }

    public void setVideoSize(int width,int height){
        mVideoWidth=width;
        mVideoHeight=height;
    }

    public int getVideoWidth(){
        return mVideoWidth;
    }

    public int getVideoHeight(){
        return mVideoHeight;
    }

    public void setVideoEncodingBitRate(int videoEncodingBitRate){
        mVideoEncodingBitRate=videoEncodingBitRate;
    }

    public int getVideoEncodingBitRate(){
        return mVideoEncodingBitRate;
    }

    public void setVideoFrameRate(int videoFrameRate){
        mVideoFrameRate=videoFrameRate;
    }

    public int getVideoFrameRate(){
        return  mVideoFrameRate;
    }

}
