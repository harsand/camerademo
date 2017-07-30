package com.hxiong.camerademo.params;

import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.util.Size;
import android.view.Surface;

/**
 * Created by hxiong on 2017/7/23 21:41.
 * Email 2509477698@qq.com
 */

public class PreviewParameters extends Parameters {

      private Surface mPreviewSurface;

      public PreviewParameters(CameraCharacteristics characteristics,CaptureRequest.Builder builder){
          super(characteristics, builder);
      }

      public void setPreviewSurface(Surface surface){
          setSurface(mPreviewSurface,surface);
          mPreviewSurface=surface;
      }

      public Size[] getPreviewSizes(){
          if(mCharacteristics!=null) {
              StreamConfigurationMap map = mCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
              return map.getOutputSizes(SurfaceTexture.class);
          }
          return new Size[0];
      }

    /**
     *  获取一个和参考大小相同或者相近的预览画面
     * @param referWidth 参考宽度
     * @param referHeight 参考高度
     * @return 如果无法匹配返回参考大小，否则返回匹配大小
     */
      public Size getNearPreviewSize(int referWidth,int referHeight){
           Size[] sizes=getPreviewSizes();
           if(sizes!=null){
               for(Size size:sizes){

               }
           }
           return new Size(referHeight,referWidth);
      }


}
