package com.yao.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yao.data.DataLib;
import com.yao.pw.R;

public class PasswordSetting extends Activity {

	final static int MENU_SAVE = Menu.FIRST;
	EditText oldEdt;
	EditText newEdt1;
	EditText newEdt2;
	private Button savePwBtn;
	String prePassword = "111111";
	SharedPreferences sp;

	boolean Flag = false;

//	final static String PRE = "MY_PREF";
//	final static String PASS_KEY="PassKey";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.pw_reset);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title);

        TextView titleLabel = (TextView)this.findViewById(R.id.label);
        titleLabel.setText(R.string.pwReset);
		oldEdt = (EditText) findViewById(R.id.old_pw);
		newEdt1 = (EditText) findViewById(R.id.new_pw1);
		newEdt2 = (EditText) findViewById(R.id.new_pw2);
		savePwBtn = (Button)findViewById(R.id.pw_save_btn);
		savePwBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				updatePassword();
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_SAVE, 0, R.string.save).setIcon(
				android.R.drawable.ic_menu_save);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_SAVE:
			updatePassword();
			break;
		}
		return false;
	}
	
	private void updatePassword(){
		String oldPassword = oldEdt.getText().toString();
		String newPassword1 = newEdt1.getText().toString();
		String newPassword2 = newEdt2.getText().toString();
		sp = getSharedPreferences(DataLib.PRE, 0);
		if (sp.getString(DataLib.PASS_KEY, "") != "") {
			prePassword = sp.getString(DataLib.PASS_KEY, "");
		}
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(DataLib.PASS_KEY, prePassword);
		if (oldPassword.equals("") || newPassword1.equals("")
				|| newPassword2.equals("")) {
			Toast.makeText(PasswordSetting.this, R.string.pw_input_msg,
					Toast.LENGTH_SHORT).show();
		} else if (!prePassword.equals(oldPassword)) {
			Toast.makeText(PasswordSetting.this, R.string.pw_old_wrong,
					Toast.LENGTH_SHORT).show();
		} else if (!newPassword1.equals(newPassword2)) {
			Toast.makeText(PasswordSetting.this, R.string.pw_old_new_error,
					Toast.LENGTH_SHORT).show();
		} else if (newPassword1.length() < 6) {
			Toast.makeText(PasswordSetting.this,
					R.string.pw_new_short_error, Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(
					PasswordSetting.this,
					PasswordSetting.this.getResources().getString(
							R.string.pw_reset_ok)
							+ newPassword1, Toast.LENGTH_LONG).show();
			Flag = true;
			editor.putString(DataLib.PASS_KEY, String.valueOf(newEdt1.getText()));
			finish();
		}
		editor.commit();
	}
}
