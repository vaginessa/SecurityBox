package com.yao.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.yao.pw.R;

public class Setting extends Activity{

	private TextView titleLabel;
	
	private TextView passwordTv;
	private TextView gestureTv;
	private TextView dataBackTv;
	private TextView aboutTv;
	private TextView versionTv;
	private TextView feedbackTv;
	
	private OnClickListener lsn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.setting);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title);
		
		titleLabel = (TextView)findViewById(R.id.label);
		titleLabel.setText("设置");
		
		passwordTv = (TextView)findViewById(R.id.setting_password);
		gestureTv=(TextView)findViewById(R.id.setting_gesture);
		dataBackTv = (TextView)findViewById(R.id.data_back);
		aboutTv=(TextView)findViewById(R.id.setting_about);
		feedbackTv=(TextView)findViewById(R.id.setting_feedback);
		
		lsn = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = null;
				switch (v.getId()) {
				case R.id.setting_password:
					intent = new Intent(Setting.this, PasswordSetting.class);
					break;
				case R.id.setting_gesture:
					intent = new Intent(Setting.this, GestureBuilderActivity.class);
					break;
				case R.id.data_back:
					Intent intent_getFile = new Intent(Setting.this,FileList_forIO.class);
		    		startActivityForResult(intent_getFile,0);
					break;
				case R.id.setting_about:
					intent = new Intent(Setting.this, AboutApp.class);
					break;
				case R.id.setting_feedback:
//					intent = new Intent(Setting.this, FeedBack.class);
					break;
				default:
					break;
				}
				if(intent != null)
					startActivityForResult(intent, 0);
			}
		};
		
		passwordTv.setOnClickListener(lsn);
		gestureTv.setOnClickListener(lsn);
		dataBackTv.setOnClickListener(lsn);
		aboutTv.setOnClickListener(lsn);
		feedbackTv.setOnClickListener(lsn);
		
	}
	
	
}
