package com.hxiong.camerademo.params;

import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.media.MediaRecorder;
import android.util.Size;
import android.view.Surface;

import com.hxiong.camerademo.util.LogUtils;

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
    private int mOrientationHint;
    private int mVideoFrameRate;

    public RecordingParameters(CameraCharacteristics characteristics, CaptureRequest.Builder builder){
        super(characteristics, builder);
        //default value
        mVideoEncodingBitRate=10000000;
        mOrientationHint=90;
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
        if(referWidth<referHeight){  //保证宽度大于高度
            int temp=referWidth;
            referWidth=referHeight;
            referHeight=temp;
        }
        int sizeScale=referWidth*10/referHeight;
        Size  tempSize=null;
        Size[] sizes=getVideoSizes();
        if(sizes!=null){
            for(Size size:sizes){
                LogUtils.logI("VideoSizes width="+size.getWidth()+" height="+size.getHeight());
                if(tempSize==null) tempSize=size;  //确保tempSize 必须是VideoSizes 中的一个
                //宽高比例小数点一位相等，乘以10就是为了保存小数点后一位
                if((size.getWidth()*10/size.getHeight())==sizeScale){
                    //如果误差不超过20，就认为是找到了
                    if(Math.abs(size.getWidth()-referWidth)<20&&Math.abs(size.getHeight()-referHeight)<20){
                        return size;
                    }
                }
            }
        }
        //if(tempSize!=null) LogUtils.logD("tempSize width="+tempSize.getWidth()+" height="+tempSize.getHeight());
        return tempSize==null?new Size(referWidth,referHeight):tempSize;
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

    public void setOrientationHint(int orientationHint){
         mOrientationHint=orientationHint;
    }

    public int getOrientationHint(){
         return mOrientationHint;
    }

}
