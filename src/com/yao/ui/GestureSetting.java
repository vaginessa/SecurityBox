package com.yao.ui;

import com.yao.pw.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class GestureSetting extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.gestures_list);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title);
		
		
	}
	
}
