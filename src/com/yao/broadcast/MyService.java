package com.yao.broadcast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yao.data.DataLib;
import com.yao.pw.R;
import com.yao.util.LewatekGPSAddress;
import com.yao.util.MailSender;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.telephony.gsm.SmsMessage;
import android.util.Log;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class MyService extends Service {

	private Handler objHandler = new Handler();
	private int intCounter = 0;

	public static final String HIPPO_SERVICE_IDENTIFIER = "HIPPO_ON_SERVICE_001";
	private static final String HIPPO_SMS_ACTION = "android.provider.Telephony.SMS_RECEIVED";

	private mSMSReceiver mReceiver01;
	final static String EDIT_KEY = "EDIT_CONTENT";
	private static boolean bIfDebug = true;

	private Runnable mTasks = new Runnable() {
		public void run() {
			intCounter++;
			Log.i("HIPPO", "Counter:" + Integer.toString(intCounter));
			objHandler.postDelayed(mTasks, 1000);
		}
	};

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub

		if (bIfDebug) {
			objHandler.postDelayed(mTasks, 1000);
		}
		super.onStart(intent, startId);
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		IntentFilter mFilter01;
		mFilter01 = new IntentFilter(HIPPO_SMS_ACTION);
		mReceiver01 = new mSMSReceiver();
		registerReceiver(mReceiver01, mFilter01);

		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		objHandler.removeCallbacks(mTasks);
		unregisterReceiver(mReceiver01);
		super.onDestroy();
	}

	/* 自定义继承自BroadcastReceiver类,聆听系统服务广播的信息 */
	public class mSMSReceiver extends BroadcastReceiver {
		/*
		 * 声明静态字符串,并使用undried.provider.Telephony.SMS_RECEIVED 作为Action为短信的依据
		 */
		@SuppressWarnings("unused")
		private TelephonyManager telMgr;
		private static final String mACTION = "android.provider.Telephony.SMS_RECEIVED";
		String ReturnStr;
		SharedPreferences sp;

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			// TODO Auto-generated method stub
			telMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
			sp = getSharedPreferences(DataLib.PRE, 0);
			/* 判断传来Intent是否为短信 */
			if (action.equals(mACTION)) {
				/* 建构一字符串集合变量sb */
				ReturnStr = "";
				StringBuilder sb = new StringBuilder();
				/* 接收由Intent传来的数据 */
				Bundle bundle = intent.getExtras();
				/* 判断Intent是有数据 */
				if (bundle != null) {
					/*
					 * pdus为 android内置短信参数 identifier
					 * 通过bundle.get("")返回一包含pdus的对象
					 */
					Object[] myOBJpdus = (Object[]) bundle.get("pdus");
					/* 构建短信对象array,并依据收到的对象长度来创建array的大小 */
					SmsMessage[] messages = new SmsMessage[myOBJpdus.length];
					for (int i = 0; i < myOBJpdus.length; i++) {
						messages[i] = SmsMessage
								.createFromPdu((byte[]) myOBJpdus[i]);
					}
					String address = "";
					String body = "";
					/* 将送来的短信合并自定义信息于StringBuilder当中 */
					for (SmsMessage currentMessage : messages) {
						sb.append("接收到来自:\n");
						/* 来讯者的电话号码 */
						String displayOriginatingAddress2 = currentMessage
								.getDisplayOriginatingAddress();
						address = displayOriginatingAddress2;
						body = currentMessage.getDisplayMessageBody();
						sb.append(address);
						sb.append("\n------传来的短信2------\n");
						/* 取得传来信息的BODY */
						sb.append(currentMessage.getDisplayMessageBody());
						if (body.equals(sp.getString(DataLib.SMS_KEY, ""))) {
							String locateStr = getGPS();
							ReturnStr = ReturnStr
									+ "GPS_Location of the stolen Gphone:\n"
									+ locateStr;
							SmsManager smsManager = SmsManager.getDefault();
							PendingIntent mPI = PendingIntent.getBroadcast(
									context, 0, new Intent(), 0);
							/* 将地理位置信息回复给发信者 */
							smsManager.sendTextMessage(address, null,
									ReturnStr, mPI, null);
							/* 以Notification(Toase)显示来讯信息 */
							Log.d("Tag", sb.toString() + locateStr);
							// 终止广播,manifest.xml的priority优先权很高，终止广播传给其他应用
							abortBroadcast();
							
							// 发送邮件到指定邮箱，附件为账号、记事数据
							String userEmail = sp.getString(DataLib.USER_EMAIL_KEY, "");
							String encryptKey = sp.getString(DataLib._KEY, "");
							if(!userEmail.equals("")){
								MailSender mSender = new MailSender(userEmail, 
										"手机被盗,附件是数据,密钥是" + encryptKey + "。\n"
										+ "你可以在现在用的手机上安装“安全盒子”，设置密钥为上述值，然后将附件放入SD卡，在设置中恢复数据。");
								try {
									mSender.send();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							
						}
					}
					
				}
			}
		}

		// ======================================================================
		/* 获得GPS地址 */
		// ======================================================================
		private String getGPS() {
			// TODO Auto-generated method stub
			LocationManager locationManager;
			String serviceName = Context.LOCATION_SERVICE;
			locationManager = (LocationManager) getSystemService(serviceName);
			String provider = LocationManager.NETWORK_PROVIDER;

			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setAltitudeRequired(false);
			criteria.setBearingRequired(false);
			criteria.setCostAllowed(true);
			criteria.setPowerRequirement(Criteria.POWER_LOW);

			Location location = locationManager.getLastKnownLocation(provider);
			String locateStr = updateWithNewLocation(location);
			locationManager.requestLocationUpdates(provider, 2000, 10,
					locationListener);
			return locateStr;
		}

		private final LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				updateWithNewLocation(location);
			}

			public void onProviderDisabled(String provider) {
				updateWithNewLocation(null);
			}

			public void onProviderEnabled(String provider) {
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}
		};

		private String updateWithNewLocation(Location location) {
			String latLongString;
			final double lat;
			final double lng;
			if (location != null) {
				lat = location.getLatitude();
				lng = location.getLongitude();
				
				latLongString = MyService.this.getResources().getString(R.string.sim_latitude) + lat 
						+ "\n" + MyService.this.getResources().getString(R.string.sim_longtitude) + lng;
			} else {
				latLongString = MyService.this.getResources().getString(R.string.sim_gps_fail);
			}
			return latLongString;
		}
		// ===========================================================================
		// ===========================================================================

	}
	
}
