package com.hxiong.camerademo.impl;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;

import com.hxiong.camerademo.util.LogUtils;

/**
 * Created by hxiong on 2017/8/2 22:26.
 * Email 2509477698@qq.com
 */

public class SensorManagerImpl implements SensorEventListener{

    //10毫秒，一秒上报 100笔数据
    public static final int SENSOR_EVNET_RATE_US = 10 * 1000;

    public static final int GSENSOE_ORIENTATION_0 = 0;

    public static final int GSENSOE_ORIENTATION_90 = 1;

    public static final int GSENSOE_ORIENTATION_180 = 2;

    public static final int GSENSOE_ORIENTATION_270 = 3;

    //gsensor
    private SensorManager mSensorManager;
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private float[] mData;
    private float mAxisX;
    private float mAxisY;
    private float mAxisZ;

     public SensorManagerImpl(SensorManager sensorManager){
         this.mSensorManager=sensorManager;
         mHandlerThread=new HandlerThread("SensorManagerImpl");
         mHandlerThread.start();  //must call
         mHandler=new Handler(mHandlerThread.getLooper());
         mData=new float[3];
     }

     public void enableSensor(){
         try{
            Sensor sensor=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
             if(sensor!=null){
                 mSensorManager.registerListener(this,sensor,SENSOR_EVNET_RATE_US,mHandler);
             }
         }catch (Exception e){
             e.printStackTrace();
         }
     }

     public void disableSensor(){
         try{
             mSensorManager.unregisterListener(this);
         }catch (Exception e){
             e.printStackTrace();
         }
     }

     public void destroy(){
         try{
             mHandlerThread.quit();
         }catch (Exception e){
             e.printStackTrace();
         }
     }

    /**
     * 简单粗暴的计算方法，待改进
     * @return
     */
    public int getGsensorOrientation(){
         if(-5<mAxisY&&mAxisY<5&&mAxisX>0&&mAxisX>Math.abs(mAxisY)){
             return GSENSOE_ORIENTATION_0;
         }else if(-5<mAxisX&&mAxisX<5&&mAxisY>0&&mAxisY>mAxisX){
             return GSENSOE_ORIENTATION_90;
         }else if(-5<mAxisY&&mAxisY<5&&mAxisX<0&&Math.abs(mAxisX)>Math.abs(mAxisY)){
             return GSENSOE_ORIENTATION_180;
         }else if(-5<mAxisX&&mAxisX<5&&mAxisY<0&&Math.abs(mAxisY)>Math.abs(mAxisX)){
             return GSENSOE_ORIENTATION_270;
         }
         return GSENSOE_ORIENTATION_90;
     }

     public float[] getGsensorData(){
         mData[0]=mAxisX;
         mData[1]=mAxisY;
         mData[2]=mAxisZ;
         return mData;
     }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor!=null&&event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            mAxisX=event.values[0];
            mAxisY=event.values[1];
            mAxisZ=event.values[2];
            LogUtils.logI("onSensorChanged mAxisX="+mAxisX+" mAxisY="+mAxisY+" mAxisZ="+mAxisZ);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
