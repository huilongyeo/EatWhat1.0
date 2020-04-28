package com.example.eatwhat10.Database;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.eatwhat10.bean.Food;
@SuppressLint("DefaultLocale")
public class FoodDBHelper extends SQLiteOpenHelper{
    private static final String TAG = "FoodDBHelper";
    private static final String DB_NAME = "food.db";//name of database数据库的名称
    private static final int DB_VERSION = 1;//version of database数据库的版本号
    private static FoodDBHelper mHelper = null;//case of FoodDBHelper数据库帮助器的实例
    private SQLiteDatabase mDB = null;//case of Database数据库的实例
    private static final String TABLE_NAME = "food_info";//name of table表的名称

    private FoodDBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    private FoodDBHelper(Context context, int version){
        super(context, DB_NAME, null, version);
    }

    //get the only case of DBHelper获取数据库帮助器的唯一实例
    public static FoodDBHelper getInstance(Context context, int version){
        if(version > 0 && mHelper == null){
            mHelper = new FoodDBHelper(context, version);
        }else if(mHelper == null){
            mHelper = new FoodDBHelper(context);
        }
        return  mHelper;
    }

    //open the link to read打开数据的读连接
    public SQLiteDatabase openReadLink(){
        if(mDB == null || !mDB.isOpen()){
            mDB = mHelper.getReadableDatabase();
        }
        return  mDB;
    }

    //open the link to write打开数据库的写连接
    public SQLiteDatabase openWriteLink(){
        if(mDB == null || !mDB.isOpen()){
            mDB = mHelper.getWritableDatabase();
        }
        return mDB;
    }

    //close the link of database 关闭数据库连接
    public void closeLink(){
        if(mDB != null && mDB.isOpen()){
            mDB.close();
            mDB = null;
        }
    }

    //build the database创建数据库
    @Override
    public void onCreate(SQLiteDatabase db){
        Log.d(TAG, "onCreate");
        String drop_sql = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        Log.d(TAG, "drop_sql:" + drop_sql);
        db.execSQL(drop_sql);
        String create_sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + "type INTEGER NOT NULL," + "name VARCHAR NOT NULL " + ");";
        Log.d(TAG, "create_sql:" + create_sql);
        db.execSQL(create_sql);
    }

    //upgrade database修改数据库
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade oldVersion=" + oldVersion + ", newVersion=" + newVersion);
        if(newVersion > 1){
            String alter_sql = "ALTER TABLE" + TABLE_NAME + " ADD COLUMN" + "name VARCHAR;";
            Log.d(TAG, "alter_sql:" + alter_sql);
            db.execSQL(alter_sql);
        }
    }

    //delete information with condition根据条件删除资讯
    public int delete(String condition){
        //return number of information has be delete返回已删除的资讯数量
        return mDB.delete(TABLE_NAME, condition, null);
    }

    //delete all of information in database删除所有资讯
    public int delete_all(){
        //return number of information has be delete返回已删除的资讯数量
        return mDB.delete(TABLE_NAME, "1=1", null);
    }

    //insert an information into table往表添加一个资讯
    public long insert(Food info){
        long result = -1;
        ContentValues cv = new ContentValues();
        cv.put("type", info.food_type);
        cv.put("name", info.food_name);
        //return row id 返回行号
        result = mDB.insert(TABLE_NAME, "", cv);
        return result;
    }

    public int update(Food info, String condition){
        ContentValues cv = new ContentValues();
        cv.put("name", info.food_name);
        cv.put("type", info.food_type);
        return mDB.update(TABLE_NAME, cv, condition, null);
    }

    public int update(Food info){
        return update(info, "rowid= " + info.rowid);
    }

    //return infoArray with condition根据条件返回数据队列
    public ArrayList<Food> query(String condition){
        String sql = String.format("select rowid,_id,type,name from %s where %s", TABLE_NAME, condition);
        Log.d(TAG, "query sql:" + sql);
        ArrayList<Food> infoArray = new ArrayList<>();
        //return cursor of information match condition返回结果集游标
        Cursor cursor = mDB.rawQuery(sql, null);
        //repeat to check the information循环读取每条资讯
        while(cursor.moveToNext()){
            Food info = new Food();
            info.rowid = cursor.getLong(0);
            info.xuhao = cursor.getInt(1);
            info.food_type = cursor.getInt(2);
            info.food_name = cursor.getString(3);
            infoArray.add(info);
        }
        cursor.close();
        return infoArray;
    }
}
