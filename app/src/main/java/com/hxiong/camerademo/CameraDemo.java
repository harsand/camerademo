package com.hxiong.camerademo;

import android.hardware.camera2.CameraCaptureSession;

import com.hxiong.camerademo.params.PictureParameters;
import com.hxiong.camerademo.params.PreviewParameters;
import com.hxiong.camerademo.params.RecordingParameters;

/**
 * Created by hxiong on 2017/7/23 21:04.
 * Email 2509477698@qq.com
 */

public interface CameraDemo {

      public CameraState getCameraState();

      public PreviewParameters getPreviewParameters();

      public int startPreview(PreviewParameters params,PreviewCallback callback);

      public int stopPreview();

      public PictureParameters getPictureParameters();

      public int takePicture(PictureParameters params,PictureCallback callback);

      public RecordingParameters getRecordingParameters();

      public int startRecording(RecordingParameters params,RecordingCallback callback);

      public int stopRecording();

     /**
      *
      */
      public static interface CameraCallback{

          public void onOpen(CameraDemo camera,String id);

          public void onFailure(String id);

          public void onClose(String id);
      }

     /**
     *
     */
      public static interface PreviewCallback{
         public void onSuccess(CameraCaptureSession session);
         public void onFailure(CameraCaptureSession session);
      }

      public static interface PictureCallback{

      }

      public static interface RecordingCallback{

      }

     /**
     *
     */
      public static enum CameraState{

          IDLE      (0),
          PREVIEW   (1),
          CAPTURE   (2),
          RECORDING (3);

          final int mState;
          CameraState(int state){
              this.mState=state;
          }
      }

}
