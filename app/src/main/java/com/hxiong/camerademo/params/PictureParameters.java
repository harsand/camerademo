package com.hxiong.camerademo.params;

import android.graphics.ImageFormat;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.view.Surface;

/**
 * Created by hxiong on 2017/7/23 21:42.
 * Email 2509477698@qq.com
 */

public class PictureParameters extends Parameters {

      private Surface mPreviewSurface;
      private Surface mPictureSurface;

      public PictureParameters(CameraCharacteristics characteristics, CaptureRequest.Builder builder){
         super(characteristics, builder);
      }

      //
      public void setPreviewSurface(Surface surface){
          setSurface(mPreviewSurface,surface);
          mPreviewSurface=surface;
     }

      public void setPictureSurface(Surface surface){
          setSurface(mPictureSurface,surface);
          mPictureSurface=surface;
    }

      public void setPictureSize(int width,int height){

      }

      public int getPictureWidth(){

          return 0;
      }

      public int getPictureHeight(){

          return 0;
      }

      public void setPictureFormat(int format){

      }

      public int getPictureFormat(){

          return ImageFormat.JPEG;
      }
}
