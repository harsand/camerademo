package com.hxiong.camerademo.impl;

import android.media.MediaRecorder;
import android.os.Build;
import android.view.Surface;

import com.hxiong.camerademo.params.RecordingParameters;
import com.hxiong.camerademo.util.Error;
import com.hxiong.camerademo.util.LogUtils;

/**
 * Created by hxiong on 2017/8/1 22:03.
 * Email 2509477698@qq.com
 */

public class RecorderManagerImpl implements MediaRecorder.OnInfoListener,MediaRecorder.OnErrorListener{

      private MediaRecorder mMediaRecorder;

      protected RecorderManagerImpl(){
          mMediaRecorder=new MediaRecorder();
          mMediaRecorder.setOnInfoListener(this);
          mMediaRecorder.setOnErrorListener(this);
      }

      public int setupMediaRecorder(RecordingParameters params){

          try {
              mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
              mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
              mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
              mMediaRecorder.setOutputFile(params.getOutputFile());
              mMediaRecorder.setVideoEncodingBitRate(params.getVideoEncodingBitRate());
              mMediaRecorder.setVideoFrameRate(params.getVideoFrameRate());
              mMediaRecorder.setVideoSize(params.getVideoWidth(), params.getVideoHeight());
              mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
              mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
              mMediaRecorder.prepare();
              return Error.OK;
          } catch (Exception e) {
              e.printStackTrace();
          }
          return Error.BAD;
      }

      public Surface getSurface(){
          return mMediaRecorder.getSurface();
      }

      public int start(){
          try {
              mMediaRecorder.start();
              return Error.OK;
          }catch (Exception e){
              e.printStackTrace();
          }
          return Error.BAD;
      }

    /**
     * no need now
     */
      public int pause(){
          try{
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                  mMediaRecorder.pause();
              }
              return Error.OK;
          }catch (Exception e){
              e.printStackTrace();
          }
          return Error.BAD;
      }
    /**
     * no need now
     */
      public int resume(){
          try{
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                  mMediaRecorder.resume();
              }
              return Error.OK;
          }catch (Exception e){
              e.printStackTrace();
          }
          return Error.BAD;
      }

      public int stop(){
          try {
              mMediaRecorder.stop();
              mMediaRecorder.reset();
              return Error.OK;
          }catch (Exception e){
              e.printStackTrace();
          }
          return Error.BAD;
      }


    public void destroy(){
        try {
            if (mMediaRecorder != null) {
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //we should care the callback of MediaRecorder or not

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        LogUtils.logD("onInfo what="+what+" extra="+extra);
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        LogUtils.logD("onError what="+what+" extra="+extra);
    }
}
