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

    /**
     * 获取Camera当前的状态
     * @return 当前的camera状态
     */
     CameraState getCameraState();

    /**
     *  获取preview相关参数
     * @return camera preview相关参数集合
     */
     public PreviewParameters getPreviewParameters();

    /**
     * 启动preview
     * @param params  需要设置的preview参数
     * @param callback 回调函数
     * @return 返回OK表示成功，否则失败
     */
      public int startPreview(PreviewParameters params,PreviewCallback callback);

     /**
      * 停止preview显示
      * @return 返回OK表示成功，否则失败
      */
      public int stopPreview();

     /**
      * 获取拍照相关参数
      * @return
      */
      public PictureParameters getPictureParameters();

     /**
      * 开始进行拍照
      * @param params 需要设置的拍照参数
      * @param callback 回调函数
      * @return  返回OK表示成功，否则失败
      */
      public int takePicture(PictureParameters params,PictureCallback callback);

     /**
      * 取消拍照
      * @return 返回OK表示成功，否则失败
      */
      public int cancelCapture();

     /**
      * 获取录制相关的参数
      * @return
      */
      public RecordingParameters getRecordingParameters();

     /**
      * 启动录制
      * @param params 需要设置的录制参数
      * @param callback 回调函数
      * @return 返回OK表示成功，否则失败
      */
      public int startRecording(RecordingParameters params,RecordingCallback callback);

     /**
      * 停止录制
      * @return 返回OK表示成功，否则失败
      */
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
          public void onConfigured(CameraCaptureSession session);
          public void onFailure(int reason);
          //表示正在将数据写到sdcard，这时候可以进行preview，但是不能进行拍照
          public void onPictureSaving(String picturePath);
          //收到这个消息表示拍照已经完成，即保存到sdcard ,可以进行下一次拍照
          public void onPictureTaken(String picturePath);
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
