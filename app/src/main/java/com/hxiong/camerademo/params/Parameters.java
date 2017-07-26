package com.hxiong.camerademo.params;

import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;

/**
 * Created by hxiong on 2017/7/23 21:39.
 * Email 2509477698@qq.com
 */

public abstract class Parameters {

    protected CameraCharacteristics mCharacteristics;
    protected CaptureRequest.Builder mBuilder;

    public void setCharacteristics(CameraCharacteristics characteristics){
        mCharacteristics=characteristics;
    }

    public void setBuilder(CaptureRequest.Builder builder){
        mBuilder=builder;
    }

    public CaptureRequest getCaptureRequest(){
        return mBuilder==null?null:mBuilder.build();
    }
}
