package com.yao.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class ClassHelper extends SQLiteOpenHelper {
    
    public static final String TB_NAME = "classinfo";		//表名
    /*ID、NAME、ACCOUNT、PASSWORD分别指定了各个列的列名*/
    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String TOTAL= "tatal";
    
    
    public ClassHelper(Context context, String name, 
            CursorFactory factory,int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {        
        // 创建accountinfo表
        db.execSQL("CREATE TABLE IF NOT EXISTS " 
                + TB_NAME + " (" 
        		+ ID + " INTEGER PRIMARY KEY," 
        	    + NAME + " VARCHAR,"
        	    + TOTAL + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, 
            int oldVersion, int newVersion) {
        
        //删除以前的旧表，创建一张新空表
        db.execSQL("DROP TABLE IF EXISTS "+TB_NAME);
        onCreate(db);
    }

}
