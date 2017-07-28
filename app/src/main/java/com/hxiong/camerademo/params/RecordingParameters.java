package com.hxiong.camerademo.params;

import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;

/**
 * Created by hxiong on 2017/7/23 21:50.
 * Email 2509477698@qq.com
 */

public class RecordingParameters extends Parameters {

    public RecordingParameters(CameraCharacteristics characteristics, CaptureRequest.Builder builder){
        super(characteristics, builder);
    }


}
