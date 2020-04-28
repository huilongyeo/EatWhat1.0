package com.example.eatwhat10;

import java.util.HashMap;

import android.app.Application;
import android.util.Log;
import android.view.View;

import com.example.eatwhat10.bean.Food;

/**
 * Created by huilongyeo on 27/4/2020
 */
public class MainApplication extends Application{
    private final static String TAG = "MainApplication";
    private static MainApplication mApp;//create a case of MainApplication声明一个当前应用的静态实例
    public Food info = null;
    //get the only object of MainApplication获取当前应用的唯一实例
    public static MainApplication getInstance(){
        return mApp;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        //open object when open Application打开应用时打开实例
        mApp = this;
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onTerminate(){
        Log.d(TAG, "onTerminate");
        super.onTerminate();
    }


}
