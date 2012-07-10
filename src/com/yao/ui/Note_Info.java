package com.yao.ui;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yao.pw.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class Note_Info extends Activity {

	final int MENU_DELETE = Menu.FIRST;
	final int MENU_MODIFY = Menu.FIRST + 1;
	final int MENU_SAVE = Menu.FIRST + 2;
	final int MENU_CANCEL = Menu.FIRST + 3;
	final int MENU_CLEAR = Menu.FIRST + 4;

	final static int STATUS_ADD = 1;
	final static int STATUS_EDIT = 2;
	private int status;
	
	private String noteId;
	String title;
	String body;
	String date;
	
	Bundle bundle;
	Intent intent;
	Calendar calendar;
	EditText bodyEdt;
	private ImageButton titleBtn;
	private TextView titleLable;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.note_info);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title);

		bodyEdt = (EditText) findViewById(R.id.bodyEdit);

		titleLable = (TextView) findViewById(R.id.label);
		titleLable.setText(R.string.noteInfo);

		intent = this.getIntent();
		bundle = intent.getExtras();
		status = bundle.getInt("STATUS");
		initStatusMode(status);

	}

	private void initStatusMode(int sta) {
		// TODO Auto-generated method stub
		switch (status = sta) {
		case STATUS_EDIT:
			titleLable.setText("编辑记事");
			noteId = bundle.getString("ID");
			title = bundle.getString("TITLE");
			body = bundle.getString("BODY");
			date = bundle.getString("DATE");
			bodyEdt.setText(body);
			break;
		case STATUS_ADD:
			titleLable.setText("新增记事");
			break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == event.KEYCODE_BACK){
			if(!bodyEdt.getText().toString().equals(""))
				saveNote();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
 
	private void saveNote() {
		// TODO Auto-generated method stub
		body = bodyEdt.getText().toString();
		title = body;
		if(body.length() >= 10)
			title = body.substring(0, 10);
		title = replaceBlank(title);
		
		calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		StringBuilder dateBuilder = new StringBuilder().append(year)
			.append("/").append(format(month + 1)).append("/")
			.append(format(day)).append(" ");
		String date = dateBuilder.toString();
		
		Bundle b = new Bundle();
		b.putInt("STATUS", status);
		b.putString("TITLE", title);
		b.putString("BODY", body);
		if(status == STATUS_ADD){
			b.putString("ID", noteId);
			b.putString("DATE", date);
		}
		intent.putExtras(b);
		
		Note_Info.this.setResult(RESULT_OK, intent);
		Note_Info.this.finish();
		
	}


	public static String replaceBlank(String str)
	{
	   Pattern p = Pattern.compile("\\s*|\t|\r|\n");
	   System.out.println("before:"+str);
	   Matcher m = p.matcher(str);
	   String after = m.replaceAll(""); 
	   return after;
	}
	
	private Object format(int x) {
		// TODO Auto-generated method stub
		String s = "" + x;
		if (s.length() == 1)
			s = "0" + s;
		return s;
	}

}
