package com.hxiong.camerademo.params;

import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.view.Surface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hxiong on 2017/7/23 21:39.
 * Email 2509477698@qq.com
 */

public abstract class Parameters {

    protected CameraCharacteristics mCharacteristics;
    protected CaptureRequest.Builder mBuilder;
    protected List<Surface> outputSurfaces;

    public Parameters(CameraCharacteristics characteristics,CaptureRequest.Builder builder){
        this.mCharacteristics=characteristics;
        this.mBuilder=builder;
        this.outputSurfaces=new ArrayList<Surface>();
    }

    protected void setSurface(Surface preSurface,Surface curSurface){
        if(preSurface!=null){
            mBuilder.removeTarget(preSurface);   //try to remove
            outputSurfaces.remove(preSurface);  //try to remove
        }
        if(curSurface!=null){
            mBuilder.addTarget(curSurface);
            outputSurfaces.add(curSurface);
        }
    }

    public List<Surface> getOutputSurfaces(){
        return outputSurfaces;
    }

    public CaptureRequest getCaptureRequest(){
        return mBuilder==null?null:mBuilder.build();
    }
}
