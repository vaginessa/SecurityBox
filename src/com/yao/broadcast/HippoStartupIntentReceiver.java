package com.yao.broadcast;

import com.yao.pw.R;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.util.Log;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class HippoStartupIntentReceiver extends BroadcastReceiver {

	static final String ACTION = "android.intent.action.BOOT_COMPLETED";
	TelephonyManager telMgr;
	String ReturnStr;
	SharedPreferences sp;
	final static String PRE = "MY_PREF";
	final static String SERVICE_KEY = "SERVICE_CONTENT";
	final static String PHONE_KEY = "PHONE_CONTENT";
	final static String NAME_KEY = "NAME_CONTENT";
	final static String USER_KEY = "USER_CONTENT";
	final static String USER_SIM_KEY = "USER_SIM_CONTENT";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		// locationManager =
		// (LocationManager)context.getSystemService("location");
		if (intent.getAction().equals(ACTION)) {
			Intent i = new Intent(context, MyService.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startService(i);
			// 这边可以添加开机自动启动的应用程序代码
		}

		sp = context.getSharedPreferences(PRE, 0);
		String user_sim = sp.getString(USER_SIM_KEY, "");
		String user = sp.getString(USER_KEY, "");
		String phoneNum = sp.getString(PHONE_KEY, "");
		String name = sp.getString(NAME_KEY, "");
		ReturnStr = "";
		// ReturnStr = ReturnStr + user +
		// "'s Gphone has been lost.Here's the information of it now:";
		// Toast.makeText(context, sp.getString(SERVICE_KEY,""),
		// Toast.LENGTH_LONG).show();
		if (sp.getString(SERVICE_KEY, "").equals("on")) {

			telMgr = (TelephonyManager) context.getSystemService("phone");
			if (!telMgr.getSimSerialNumber().equals(user_sim)) {
				ReturnStr = ReturnStr + name + "," + 
					context.getResources().getString(R.string.sim_sms_msg1) + user + 
					context.getResources().getString(R.string.sim_sms_msg2) + 
					context.getResources().getString(R.string.sim_sms_msg3) + user +
					context.getResources().getString(R.string.sim_sms_msg4);
				
				LocationManager locationManager;
				String serviceName = Context.LOCATION_SERVICE;
				locationManager = (LocationManager) context
						.getSystemService(serviceName);
				// ReturnStr = ReturnStr + "\nGPS:" + getGPS(locationManager);
				SmsManager smsManager = SmsManager.getDefault();
				PendingIntent mPI = PendingIntent.getBroadcast(context, 0,
						new Intent(), 0);
				/* 将地理位置信息回复给发信者 */
				smsManager
						.sendTextMessage(phoneNum, null, ReturnStr, mPI, null);
				Log.d("TAG", "NOT");
			}

		}

	}
	
	/* 获得GPS地址 */
	private String getGPS(LocationManager locationManager) {
		// TODO Auto-generated method stub
		// LocationManager locationManager;
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		// String provider = locationManager.getBestProvider(criteria, true);
		String provider = locationManager.NETWORK_PROVIDER;

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

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	private String updateWithNewLocation(Location location) {
		String latLongString;
		if (location != null) {
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			latLongString = "weidu:" + lat + "\njingdu:" + lng;
		} else {
			latLongString = "not available!";
		}
		return latLongString;
	}
	// ===========================================================================

}
