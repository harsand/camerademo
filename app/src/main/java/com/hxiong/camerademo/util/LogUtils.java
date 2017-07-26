package com.hxiong.camerademo.util;

import android.util.Log;

/**
 * Created by hxiong on 2017/7/23 22:08.
 * Email 2509477698@qq.com
 */

public class LogUtils {

    public static final String TAG="camerademo_log";

     private static boolean isLogIEnabled = false;
     private static boolean isLogDEnabled = true;

     private LogUtils(){ }





     public static void logI(String msg){
         logI(TAG,msg);
     }

     public static void logI(String tag,String msg){
         if(isLogIEnabled){
             if (tag == null || msg == null) {
                 Log.e(TAG, "error param");
             } else {
                 Log.d(tag, msg);  // why use log.d()
             }
         }
     }

    public static void logD(String msg){
        logD(TAG,msg);
    }

     public static void logD(String tag,String msg){
         if(isLogDEnabled) {
             if (tag == null || msg == null) {
                 Log.e(TAG, "error param");
             } else {
                 Log.d(tag, msg);
             }
         }
     }

     public static void logE(String msg){
         logE(TAG,msg);
     }

     public static void logE(String tag,String msg){
         if(tag==null||msg==null){
             Log.e(TAG, "error param");
         }else{
             Log.e(tag, msg);
         }
     }
}
