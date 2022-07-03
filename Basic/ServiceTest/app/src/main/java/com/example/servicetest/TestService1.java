package com.example.servicetest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;


public class TestService1 extends Service {
    private final String TAG="TestService1";

    ///必须被实现
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG,"onBind()方法被调用");
        return null;
    }

    ///Service被创建时调用
    @Override
    public void onCreate(){
        Log.i(TAG,"onCreate()方法被调用");
        super.onCreate();
    }

    ///Service启动时调用
    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        Log.i(TAG,"onStartCommand()方法被调用");
        return super.onStartCommand(intent,flags,startId);
    }

    ///Service被关闭之前回调
    @Override
    public void onDestroy(){
        Log.i(TAG,"onDestroy()方法被调用");
        super.onDestroy();
    }

}




