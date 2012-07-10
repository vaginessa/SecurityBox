package com.yao.data;

import android.os.Environment;

public class DataLib {

	/*
	 * SharedPreferences相关
	 */
	public final static String PRE = "MY_PREF";
	
	public final static String FIRST_USE_KEY = "first_use";// 首次使用
	
	public final static String PASS_KEY = "PassKey";// 密码

	public final static String SERVICE_KEY = "SERVICE_CONTENT";// 防盗服务开关

	public final static String USER_KEY = "USER_CONTENT";// 用户姓名

	public final static String PHONE_KEY = "PHONE_CONTENT";// 收信人电话

	public final static String NAME_KEY = "NAME_CONTENT";// 收信人姓名

	public final static String USER_SIM_KEY = "USER_SIM_CONTENT";// 用户SIM号

	public final static String USER_EMAIL_KEY = "USER_EMAIL_ADDRESS";// 用户邮箱

	public final static String SMS_KEY = "SMS_CONTENT";// 短信关键字

	public final static String _KEY = "JIAMI_KEY";// 加密密钥
	
	
	/*
	 * 文件路徑
	 */
	public final static String SD_APP_PATH = Environment.getExternalStorageDirectory() + "/Security";
	
	public final static String APP_DATA_PATH = "/data/data/com.yao.pw/databases/";
	
	public final static String ACCOUNT_EN_PATH = APP_DATA_PATH + "Account0.db";
	
	public final static String ACCOUNT_DE_PATH = APP_DATA_PATH + "Account.db";
	
	public final static String CATEGORY_PATH = APP_DATA_PATH + "Category.db";
	
	public final static String NOTE_EN_PATH = APP_DATA_PATH + "Notepad0.db";
	
	public final static String NOTE_DE_PATH = APP_DATA_PATH + "Notepad.db";
	
	public final static String ACCOUNT_EXPORT_PATH = SD_APP_PATH + "/Export";
	
	/*
	 * 
	 */
	public final static String ZIP_INFO = "SecurityBox";
	
	public final static String DEFAULT_KEY = "19900911";
	
	public final static String DEFAULT_PASS = "111111";
	

}
