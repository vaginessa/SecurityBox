package com.yao.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.yao.pw.R;

public class AboutApp extends Activity{

	private TextView titleLabel;
	private ImageView titleBack; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.about_app);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title);
		
		titleBack = (ImageView) findViewById(R.id.title_back);
		titleLabel = (TextView)findViewById(R.id.label);
		titleLabel.setText(R.string.about);
		titleBack.setVisibility(View.VISIBLE);
		
		titleBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
	}
	
}
