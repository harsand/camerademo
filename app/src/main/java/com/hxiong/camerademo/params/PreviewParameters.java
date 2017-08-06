package com.hxiong.camerademo.params;

import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.util.Size;
import android.view.Surface;

import com.hxiong.camerademo.util.LogUtils;

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
          return getPreviewSizes(SurfaceTexture.class);
      }

    /**
     *  获取一个和参考大小相同或者相近的预览画面
     * @param referWidth 参考宽度
     * @param referHeight 参考高度
     * @return 如果无法匹配返回参考大小，否则返回匹配大小
     */
      public Size getNearPreviewSize(int referWidth,int referHeight){
           if(referWidth<referHeight){  //保证宽度大于高度
               int temp=referWidth;
               referWidth=referHeight;
               referHeight=temp;
           }
           int sizeScale=referWidth*10/referHeight;
           Size[] sizes=getPreviewSizes();
           if(sizes!=null){
               for(Size size:sizes){
                    LogUtils.logI("PreviewSize width="+size.getWidth()+" height="+size.getHeight());
                    //宽高比例小数点一位相等，乘以10就是为了保存小数点后一位
                    if((size.getWidth()*10/size.getHeight())==sizeScale){
                        //如果误差不超过20，就认为是找到了
                         if(Math.abs(size.getWidth()-referWidth)<20&&Math.abs(size.getHeight()-referHeight)<20){
                             return size;
                         }
                    }
               }
           }
           return new Size(referWidth,referHeight);
      }


}
