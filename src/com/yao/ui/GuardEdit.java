package com.yao.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yao.pw.R;

public class GuardEdit extends Activity{

	private EditText edt;
	private Button btn;
	private TextView tv;
	
	private TextView titleLabel;
	private ImageView titleBack;
	private Intent intent;
	private int typeId = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.guard_receiver_name);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title);
		
		titleLabel=(TextView)findViewById(R.id.label);
		edt=(EditText)findViewById(R.id.guard_edt);
		tv=(TextView)findViewById(R.id.about_this);
		btn=(Button)findViewById(R.id.guard_edt_btn);
		titleBack=(ImageView)findViewById(R.id.title_back);
		tv.setText("");
		
		titleBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		intent = getIntent();
		if(intent != null){
			Bundle bnd = intent.getExtras();
			typeId = bnd.getInt("typeId");
			String value = bnd.getString("value");
			switch(bnd.getInt("typeId")){
			case R.id.guard_user_name:
				titleLabel.setText("用户名");
				edt.setHint("请输入您的姓名");
				break;
			case R.id.guard_receiver_name:
				titleLabel.setText("收信人姓名");
				edt.setHint("请输入收信人姓名");
				tv.setText(R.string.guard_receiver_about);
				break;
			case R.id.guard_receiver_phone:
				titleLabel.setText("收信人电话");
				edt.setHint("请输入收信人电话");
				tv.setText(R.string.guard_receiver_about);
				edt.setInputType(InputType.TYPE_CLASS_NUMBER);
				break;
			case R.id.guard_sms_key:
				titleLabel.setText("短信关键字");
				edt.setHint("请输入短信关键字");
				tv.setText(R.string.guard_sms_key_about);
				break;
			case R.id.guard_user_email:
				titleLabel.setText("绑定邮箱");
				edt.setHint("请输入邮箱地址");
				tv.setText(R.string.guard_email_about);
				break;
			}
			if(!value.equals(""))
				edt.setText(value);
		}
		
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String str = edt.getText().toString();
				
				Bundle b = new Bundle();
				b.putString("value", str);
				b.putInt("typeId", typeId);
				intent.putExtras(b);
				
				setResult(RESULT_OK, intent);
				GuardEdit.this.finish();
			}
		});
		
		
	}
	
	
}
