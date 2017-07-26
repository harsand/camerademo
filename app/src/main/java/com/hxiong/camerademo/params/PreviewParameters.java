package com.hxiong.camerademo.params;

import android.view.Surface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hxiong on 2017/7/23 21:41.
 * Email 2509477698@qq.com
 */

public class PreviewParameters extends Parameters {

      private Surface mPreviewSurface;

      public void setPreviewSurface(Surface surface){
          mPreviewSurface=surface;
      }

      public List<Surface> getOutputSurface(){

          List<Surface> outputs=new ArrayList<Surface>();
          if(mPreviewSurface!=null){
              mBuilder.addTarget(mPreviewSurface);
              outputs.add(mPreviewSurface);
          }
          return outputs;
      }


}
