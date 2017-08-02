package com.hxiong.camerademo.params;

import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.util.Size;
import android.view.Surface;

/**
 * Created by hxiong on 2017/7/23 21:42.
 * Email 2509477698@qq.com
 */

public class PictureParameters extends Parameters {

      private Surface mPreviewSurface;
      private Surface mPictureSurface;
      //picture param set
      private int mPictureWidth;
      private int mPictureHeight;
      private int mPictureFormat;
      //
      private String mPicturePath;

      public PictureParameters(CameraCharacteristics characteristics, CaptureRequest.Builder builder){
         super(characteristics, builder);
      }

      public Size[] getPictureSizes(){
         return getPreviewSizes(ImageReader.class);
      }

     // public

      public Size getMaxPictureSizes(int referWidth,int referHeight){
          Size[] sizes=getPictureSizes();
          if(sizes!=null){
              for(Size size:sizes){

              }
          }
          return new Size(referHeight,referWidth);
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
          mPictureWidth=width;
          mPictureHeight=height;
      }

      public int getPictureWidth(){
          return mPictureWidth;
      }

      public int getPictureHeight(){
          return mPictureHeight;
      }

      public void setPictureFormat(int format){
          mPictureFormat=format;
      }

      public int getPictureFormat(){
          return mPictureFormat;
      }

      public void setPicturePath(String path){
          mPicturePath=path;
      }

      public String getPicturePath(){
          return mPicturePath;
      }

      public static enum Rotation{
          /**
           * Rotation constant: 0 degree rotation (natural orientation)
           */
          ROTATION_0 (0),

          /**
           * Rotation constant: 90 degree rotation.
           */
           ROTATION_90 (90),

          /**
           * Rotation constant: 180 degree rotation.
           */
          ROTATION_180 (180),

          /**
           * Rotation constant: 270 degree rotation.
           */
          ROTATION_270 (270);

           final int mRotation;
           Rotation(int rotation){ this.mRotation=rotation; }
      }

      public void setPictureOrientation(Rotation rotation){
          mBuilder.set(CaptureRequest.JPEG_ORIENTATION,rotation.mRotation);
      }


}
