package com.yao.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yao.pw.R;
import com.yao.util.AccountHelper;

public class Account_Info extends Activity {

	public final static int STATUS_ADD = 1;
	public final static int STATUS_EDIT = 2;
	// public final static int STATUS_MODIFY = 2;
	// public final static int STATUS_ADD = 3;
	// public final static int STATUS_DELETE = 4;
	private EditText nameEdt;
	private EditText accountEdt;
	private EditText passwordEdt;
	private ImageView titleBtn;
	private TextView titleLabel;
	private Button saveBtn;

	private int status;
	private String currentId;

	private Intent intent;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.account_info);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title);

		nameEdt = (EditText) findViewById(R.id.name);
		accountEdt = (EditText) findViewById(R.id.account);
		passwordEdt = (EditText) findViewById(R.id.password);
		saveBtn = (Button) findViewById(R.id.saveAccountBtn);

		titleBtn = (ImageView) findViewById(R.id.title_right);
		titleLabel = (TextView) findViewById(R.id.label);
		titleLabel.setText(R.string.accountCenter);
		titleBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 保存item
				saveItem();
			}
		});

		saveBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveItem();
			}
		});
		
		intent = getIntent();
		if (intent != null) {
			Bundle b = intent.getExtras();
			switch (status = b.getInt("STATUS")) {
			case STATUS_ADD:
				break;
			case STATUS_EDIT:
				currentId = b.getString("ID");
				nameEdt.setText(b.getString("NAME"));
				accountEdt.setText(b.getString("ACCOUNT"));
				passwordEdt.setText(b.getString("PASSWORD"));
				break;
			default:
				break;
			}
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == event.KEYCODE_BACK) {
			saveItem();
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void saveItem() {
		// TODO Auto-generated method stub
		String name = nameEdt.getText().toString();
		String account = accountEdt.getText().toString();
		String password = passwordEdt.getText().toString();
		if (name.equals("")) {
			Toast.makeText(this, R.string.account_input_info,
					Toast.LENGTH_SHORT).show();
			return;
		}
		Bundle bnd = new Bundle();
		bnd.putInt("STATUS", status);
		if (status == STATUS_EDIT)
			bnd.putString(AccountHelper.ID, currentId);
		bnd.putString(AccountHelper.NAME, name);
		bnd.putString(AccountHelper.ACCOUNT, account);
		bnd.putString(AccountHelper.PASSWORD, password);
		intent.putExtras(bnd);
		Account_Info.this.setResult(RESULT_OK, intent);
		Account_Info.this.finish();
	}

}