package com.yao.pw;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

import com.yao.data.DataLib;
import com.yao.ui.AboutApp;
import com.yao.ui.MainMenu;

public class passwordmain extends Activity implements OnGesturePerformedListener{

	final int DIALOG_ABOUT = 1;
	final int DIALOG_ENTER = 2;
	String loginPassword = DataLib.DEFAULT_PASS;
	SharedPreferences sp;

	private EditText pwEdt;
	private Button loginBtn;
	private ImageView titleBack, titleInfo;
	ProgressDialog checkDialog;
	private GestureOverlayView gestureOverlayView;
	private GestureLibrary gestureLib;

	private SlidingDrawer drawer;
	
	private final File mStoreFile = new File(Environment.getExternalStorageDirectory(), "Security/gestures");
	private final File pathFile = new File(Environment.getExternalStorageDirectory(),"Security");

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		gestureOverlayView = new GestureOverlayView(passwordmain.this);
		View inflate = getLayoutInflater().inflate(R.layout.log_in, null);
		gestureOverlayView.addView(inflate);
		
		if(!pathFile.exists()){
			pathFile.mkdir();
		}
		
		if((mStoreFile.exists())){
			gestureLib = GestureLibraries.fromFile(mStoreFile);
			gestureOverlayView.addOnGesturePerformedListener(passwordmain.this);
			if (!gestureLib.load()) {
				finish();
			}
		}
		setContentView(gestureOverlayView);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title);
		drawer = (SlidingDrawer) findViewById(R.id.drawer);
		pwEdt = (EditText) findViewById(R.id.login_pw);
		loginBtn = (Button) findViewById(R.id.loginBtn);
		titleBack = (ImageView) findViewById(R.id.title_back);
		titleBack.setVisibility(View.VISIBLE);
		titleInfo = (ImageView)findViewById(R.id.title_right);
		titleInfo.setVisibility(View.VISIBLE);
		
		titleBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		titleInfo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				passwordmain.this.startActivity(new Intent(passwordmain.this, AboutApp.class));
			}
		});
		
		if(!drawer.isOpened()){
			gestureOverlayView.setEnabled(false);
		}

		loginBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String password = pwEdt.getText().toString();
				loginByPassword(password);
			}
		});
		
		drawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			
			@Override
			public void onDrawerOpened() {
				// TODO Auto-generated method stub
				gestureOverlayView.setEnabled(true);
			}
		});
		
		drawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
			
			@Override
			public void onDrawerClosed() {
				// TODO Auto-generated method stub
				gestureOverlayView.setEnabled(false);
			}
		});

		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		sp = getSharedPreferences(DataLib.PRE, 0);
		if(sp.getBoolean(DataLib.FIRST_USE_KEY, true)){
			TextView tt = (TextView) findViewById(R.id.pw_info_tv);
			tt.setVisibility(View.VISIBLE);
			SharedPreferences.Editor editor = sp.edit();
			editor.putBoolean(DataLib.FIRST_USE_KEY, false);
			editor.commit();
		}
		
	}
	
	
	protected void loginByPassword(String pw) {
		// TODO Auto-generated method stub
		checkDialog = ProgressDialog.show(passwordmain.this, null, getResources().getText(R.string.checking_pw),
				true);
		sp = getSharedPreferences(DataLib.PRE, 0);
		if (sp.getString(DataLib.PASS_KEY, "") != "")
			loginPassword = sp.getString(DataLib.PASS_KEY, "");
		if (pw.equals(loginPassword)) {
			new Thread() {
				public void run() {
					try {
						Intent intent = new Intent(passwordmain.this,
								MainMenu.class);
						startActivity(intent);
						passwordmain.this.finish();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						checkDialog.dismiss();
					}
				}
			}.start();
		} else {
			checkDialog.dismiss();
			Toast.makeText(passwordmain.this, R.string.pass_error, Toast.LENGTH_LONG)
					.show();
		}
	}


	@Override
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) { 
		
		ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
		for (Prediction prediction : predictions) {
			if (prediction.score > 1.5) {
//				Toast.makeText(this, prediction.name, Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(passwordmain.this, MainMenu.class);
				startActivity(intent);
				passwordmain.this.finish();
				return;
			}else{
				Toast.makeText(passwordmain.this, R.string.gesture_error, Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(drawer.isOpened()){
			drawer.animateClose();
			return false;
		}
			
		return super.onKeyDown(keyCode, event);
	}
	

}