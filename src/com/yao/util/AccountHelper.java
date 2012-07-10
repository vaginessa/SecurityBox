package com.yao.util;

import java.io.File;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class AccountHelper extends SQLiteOpenHelper {
	
	public static final int VERSION = 1;
	public final static String PRE = "MY_PREF";
	public final static String EDIT_KEY = "EDIT_CONTENT";
	public final static String ACCOUNT_PATH = "/data/data/com.yao.pw/databases/";
	public final static String ACCOUNT_DB_NAME = "Account";
    
    public static final String TB_NAME = "accounts";		//表名
    
    public static final String ID = "_id";		//id
    
    public static final String CATEGORY_ID = "category_id";	
    
    public static final String NAME = "name";
    
    public static final String ACCOUNT= "account";
    
    public static final String PASSWORD = "password";
    
    final static String _KEY = "JIAMI_KEY";
    
    public AccountHelper(Context context, String name, 
            CursorFactory factory,int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        
        // 创建accountinfo表
        db.execSQL("CREATE TABLE IF NOT EXISTS " 
                + TB_NAME + " (" 
        		+ ID + " INTEGER PRIMARY KEY," 
        		+ CATEGORY_ID + " INTEGER,"
        	    + NAME + " VARCHAR,"
        	    + ACCOUNT + " VARCHAR,"
        	    + PASSWORD + " VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, 
            int oldVersion, int newVersion) {
        
        //删除以前的旧表，创建一张新空表
        db.execSQL("DROP TABLE IF EXISTS "+TB_NAME);
        onCreate(db);
    }

}
