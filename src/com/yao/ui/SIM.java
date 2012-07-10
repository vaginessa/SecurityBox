package com.yao.ui;


import com.yao.broadcast.MyService;
import com.yao.pw.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SIM extends Activity{
	
	final static int MENU_SAVE=Menu.FIRST;
	final static String PRE="MY_PREF";
	final static String SERVICE_KEY="SERVICE_CONTENT";
	final static String USER_KEY="USER_CONTENT";
	final static String PHONE_KEY="PHONE_CONTENT";
	final static String NAME_KEY="NAME_CONTENT";
	final static String USER_SIM_KEY="USER_SIM_CONTENT";
	final static String SMS_KEY="SMS_CONTENT";
	ToggleButton TgBtn;
	EditText userEdt;
	EditText nameEdt;
	EditText phoneEdt;
	EditText smsEdt;
	ImageButton okBtn;
	ImageButton cancelBtn;
	String preUser;
	String preOnOff;
	String prePhone;
	String preName;
	String nowOnOff;
	SharedPreferences sp;
	SharedPreferences.Editor editor;	
	TelephonyManager telMgr;
	
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.sim);
        this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
        
        
        TgBtn=(ToggleButton)findViewById(R.id.onoff);
        userEdt=(EditText)findViewById(R.id.userName);
        nameEdt=(EditText)findViewById(R.id.toPhoneName);
        phoneEdt=(EditText)findViewById(R.id.toPhoneNum);
        smsEdt=(EditText)findViewById(R.id.keySms);
        TextView tv=(TextView)findViewById(R.id.tv);
        TextView titleLabel = (TextView)this.findViewById(R.id.label);
        titleLabel.setText(R.string.sim);
        userEdt.getBackground().setAlpha(150);
        nameEdt.getBackground().setAlpha(150);
        phoneEdt.getBackground().setAlpha(150);
        smsEdt.getBackground().setAlpha(150);
        
        sp=getSharedPreferences(PRE,0);
        preOnOff = sp.getString(SERVICE_KEY, "");
        preUser = sp.getString(USER_KEY, "");
        prePhone = sp.getString(PHONE_KEY, "");
        preName = sp.getString(NAME_KEY, "");
        
  	
        userEdt.setText(preUser);
        phoneEdt.setText(prePhone);
        nameEdt.setText(preName);
        
        nowOnOff=preOnOff;
        
        if(sp.getString(SERVICE_KEY,"").equals("on")){
        	TgBtn.setChecked(true);
        	Intent i = new Intent( SIM.this, MyService.class ); 
			i.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK ); 
			startService(i);
        }
        editor=sp.edit();
        
        telMgr = (TelephonyManager)getSystemService("phone");
        if(telMgr.getSimSerialNumber()!=null){
    		editor.putString(USER_SIM_KEY, telMgr.getSimSerialNumber());		
    	} 
       
        TgBtn.setOnCheckedChangeListener(new OnCheckedChangeListener(){
        	
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					if(userEdt.getText().toString().equals("")|
							phoneEdt.getText().toString().equals("")|
							nameEdt.getText().toString().equals("")){
						Toast.makeText(SIM.this, R.string.sim_input_msg, Toast.LENGTH_SHORT).show();
						TgBtn.setChecked(!TgBtn.isChecked());
					}else{
						editor.putString(SERVICE_KEY, "on");
						editor.putString(USER_KEY, userEdt.getText().toString());
						editor.putString(PHONE_KEY, phoneEdt.getText().toString());
						editor.putString(NAME_KEY, nameEdt.getText().toString());
						editor.putString(SMS_KEY, smsEdt.getText().toString());
						Intent i = new Intent( SIM.this, MyService.class ); 
						i.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK ); 
						startService(i);
						Toast.makeText(SIM.this, R.string.sim_service_on, Toast.LENGTH_SHORT).show();
					}
				}else{
					editor.putString(SERVICE_KEY, "off");
					Intent i = new Intent( SIM.this, MyService.class );
			        stopService(i);
			        Toast.makeText(SIM.this, R.string.sim_service_off, Toast.LENGTH_SHORT).show();
				}
			}       	
        });
    }
    
   
    public void onDestroy(){
		editor.putString(USER_KEY, userEdt.getText().toString());
		editor.putString(PHONE_KEY, phoneEdt.getText().toString());
		editor.putString(NAME_KEY, nameEdt.getText().toString());
		editor.putString(SMS_KEY, smsEdt.getText().toString());
    	editor.commit();
    	super.onDestroy();
    }
    
    
	
}
