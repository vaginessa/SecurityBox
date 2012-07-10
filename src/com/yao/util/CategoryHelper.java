package com.yao.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CategoryHelper extends SQLiteOpenHelper {
    
    public static final String TB_NAME = "category";		//表名
    /*ID、NAME、TATAL分别指定了各个列的列名*/
    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String TOTAL= "tatal";
    
    
    public CategoryHelper(Context context, String name, 
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
    
//    public void deleteByCategory(SQLiteDatabase db, int catId){
//    	
////    	Cursor mCursor = db.query(AccountHelper.TB_NAME, null,
////				AccountHelper.CATEGORY_ID + "=?",
////				new String[] { String.valueOf(catId) }, null, null,
////				AccountHelper.NAME);
//    	mCursor.moveToFirst();
//    	while(mCursor.moveToNext()){
//    		mCursor.d
//    	}
//    	
//    }
    

}
