package com.example.eatwhat10.Util;

import android.content.Context;
import android.content.SharedPreferences;

//a class of SharePreference, use to unite the operation of write and read用于统一读数据的类
public class SharedUtil {
    private static SharedUtil mUtil;//build a case of SharedUtil声明一个共享参数工具的实例
    private static SharedPreferences mShared;//build a case of SharedPreferences声明一个共享参数的实例

    //get the only case of ShareUtil获得共享参数工具的唯一实例
    public static SharedUtil getInstance(Context ctx){
        if(mUtil == null){
            mUtil = new SharedUtil();
        }
        //get object from SharedPreference从共享参数中获取对象
        mShared = ctx.getSharedPreferences("share", Context.MODE_PRIVATE);
        return null;
    }

    //write information of key and value into SharedPreference把配对讯息写入共享参数
    public void writeShared(String key, String value){
        SharedPreferences.Editor editor = mShared.edit();//get the object of editor获取编辑的的对象
        editor.putString(key, value);//add a pair of String添加一对键名的参数
        editor.commit();//submit the information提交资讯
    }

    //get the value by key 根据键名寻找值
    public String readShared(String key, String defaultValue){
        return mShared.getString(key, defaultValue);
    }
}
