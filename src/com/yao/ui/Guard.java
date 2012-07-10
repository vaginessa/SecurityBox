package com.yao.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.yao.broadcast.MyService;
import com.yao.data.DataLib;
import com.yao.pw.R;

public class Guard extends Activity {

	private TextView titleLabel;
	
	private CheckBox switchBtn;
	
	private TextView helpTv;
	private TextView userNameTv;
	private TextView receiverNameTv;
	private TextView receiverPhoneTv;
	private TextView userEmailTv;
	private TextView smsKeyTv;
	
	private TextView userNameVal;
	private TextView receiverNameVal;
	private TextView receiverPhoneVal;
	private TextView userEmailVal;
	private TextView smsKeyVal;
	
	private String preSwitch;
	private String preUserName;
	private String preReceiverName;
	private String preReceiverPhone;
	private String preUserEmail;
	private String preSmsKey;
	
	private TelephonyManager telMgr;
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	private OnClickListener listener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.guard_setting);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title);

		titleLabel = (TextView) findViewById(R.id.label);
		titleLabel.setText("手机防盗");

		switchBtn = (CheckBox)findViewById(R.id.btn_toggle);
		
		helpTv = (TextView) findViewById(R.id.guard_help);
		
		userNameTv = (TextView) findViewById(R.id.guard_user_name);
		receiverNameTv = (TextView) findViewById(R.id.guard_receiver_name);
		receiverPhoneTv = (TextView) findViewById(R.id.guard_receiver_phone);
		userEmailTv = (TextView)findViewById(R.id.guard_user_email);
		smsKeyTv = (TextView) findViewById(R.id.guard_sms_key);
		
		userNameVal = (TextView)findViewById(R.id.guard_user_name_value);
		receiverNameVal = (TextView)findViewById(R.id.guard_receiver_name_value);
		receiverPhoneVal = (TextView)findViewById(R.id.guard_receiver_phone_value);
		userEmailVal = (TextView)findViewById(R.id.guard_user_email_value);
		smsKeyVal = (TextView)findViewById(R.id.guard_sms_key_value);
		
		listener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Guard.this, GuardEdit.class);
				Bundle b = new Bundle();
				String s = "";
				switch (v.getId()) {
				case R.id.guard_user_name://用户名
					s = userNameVal.getHint().toString();
					break;
				case R.id.guard_receiver_name://收信人姓名
					s = receiverNameVal.getHint().toString();
					break;
				case R.id.guard_receiver_phone://收信人电话
					s = receiverPhoneVal.getHint().toString();
					break;
				case R.id.guard_sms_key://短信关键字
					s = smsKeyVal.getHint().toString();
					break;
				case R.id.guard_user_email://绑定邮箱
					s = preUserEmail;
					break;
				}
				b.putInt("typeId", v.getId());
				b.putString("value", s);
				intent.putExtras(b);
				startActivityForResult(intent, 0);
			}
		};
		
		userNameTv.setOnClickListener(listener);
		receiverNameTv.setOnClickListener(listener);
		receiverPhoneTv.setOnClickListener(listener);
		smsKeyTv.setOnClickListener(listener);
		userEmailTv.setOnClickListener(listener);
		helpTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(Guard.this, GuardHelp.class));
			}
		});
		
		sp=getSharedPreferences(DataLib.PRE,0);
		preSwitch = sp.getString(DataLib.SERVICE_KEY, "");
		
		switchBtn.setChecked(preSwitch.equals("on"));
		
        preUserName = sp.getString(DataLib.USER_KEY, "");
        preReceiverPhone = sp.getString(DataLib.PHONE_KEY, "");
        preReceiverName = sp.getString(DataLib.NAME_KEY, "");
        preUserEmail = sp.getString(DataLib.USER_EMAIL_KEY, "");
        preSmsKey = sp.getString(DataLib.SMS_KEY, "");
        
        userNameVal.setHint(preUserName);
        receiverNameVal.setHint(preReceiverName);
        receiverPhoneVal.setHint(preReceiverPhone);
        if(!preUserEmail.equals(""))
        	userEmailVal.setHint("已绑定");
        smsKeyVal.setHint(preSmsKey);
		
        if(sp.getString(DataLib.SERVICE_KEY,"").equals("on")){
        	switchBtn.setChecked(true);
        	Intent i = new Intent( Guard.this, MyService.class ); 
			i.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK ); 
			startService(i);
        }
        editor=sp.edit();
        
        telMgr = (TelephonyManager)getSystemService("phone");
        if(telMgr.getSimSerialNumber()!=null){
    		editor.putString(DataLib.USER_SIM_KEY, telMgr.getSimSerialNumber());		
    	} 
        
		switchBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton btnView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					if(userNameVal.getHint().toString().equals("") 
							|| receiverNameVal.getHint().toString().equals("") 
							|| receiverPhoneVal.getHint().toString().equals("") 
							|| smsKeyVal.getHint().toString().equals("")){
						Toast.makeText(Guard.this, "请先填写下列信息", Toast.LENGTH_SHORT).show();
						switchBtn.setChecked(!switchBtn.isChecked());
					}else{
						editor.putString(DataLib.SERVICE_KEY, "on");
						editor.putString(DataLib.USER_KEY, userNameVal.getHint().toString());
						editor.putString(DataLib.PHONE_KEY, receiverPhoneVal.getHint().toString());
						editor.putString(DataLib.NAME_KEY, receiverNameVal.getHint().toString());
						editor.putString(DataLib.USER_EMAIL_KEY, preUserEmail);
						editor.putString(DataLib.SMS_KEY, smsKeyVal.getHint().toString());
						editor.commit();
						Intent i = new Intent(Guard.this, MyService.class ); 
						i.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK ); 
						startService(i);
						Toast.makeText(Guard.this, R.string.sim_service_on, Toast.LENGTH_SHORT).show();
					}
				}else{
					editor.putString(DataLib.SERVICE_KEY, "off");
					Intent i = new Intent( Guard.this, MyService.class );
			        stopService(i);
			        Toast.makeText(Guard.this, R.string.sim_service_off, Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (resultCode) {
		case RESULT_OK:
			Bundle b = data.getExtras();
			String value = b.getString("value");
			switch (b.getInt("typeId")) {
			case R.id.guard_user_name://用户名
				userNameVal.setHint(value);
				editor.putString(DataLib.USER_KEY, value);
				editor.commit();
				break;
			case R.id.guard_receiver_name://收信人姓名
				receiverNameVal.setHint(value);
				editor.putString(DataLib.NAME_KEY, value);
				editor.commit();
				break;
			case R.id.guard_receiver_phone://收信人电话
				receiverPhoneVal.setHint(value);
				editor.putString(DataLib.PHONE_KEY, value);
				editor.commit();
				break;
			case R.id.guard_user_email:
				userEmailVal.setHint("已绑定");
				editor.putString(DataLib.USER_EMAIL_KEY, value);
				preUserEmail = value;
				editor.commit();
				break;
			case R.id.guard_sms_key://短信关键字
				smsKeyVal.setHint(value);
				editor.putString(DataLib.SMS_KEY, value);
				editor.commit();
				break;
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	

}
