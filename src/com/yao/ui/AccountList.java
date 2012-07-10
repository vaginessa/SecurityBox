package com.yao.ui;

import java.io.File;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.yao.data.DataLib;
import com.yao.pw.R;
import com.yao.util.AccountHelper;
import com.yao.util.TestDES;

public class AccountList extends ListActivity {

	private static final int VERSION = 1;
	public final static String ACCOUNT_DB_NAME = "Account.db";
	
	private Intent catIntent;
	private Bundle catBundle;
	private String catId;
	private String catName;
	private String key = "19900911";
	private SharedPreferences sp;
	private SQLiteDatabase db;
	private AccountHelper helper;
	private Cursor mCursor;
	private OnClickListener listener1;
	private ListView list;

	private TextView mText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.account_note_list);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title);

		catIntent = this.getIntent();
		if (catIntent != null) {
			catBundle = catIntent.getExtras();
			catId = catBundle.getString("ID");
			catName = catBundle.getString("NAME");
		}


		initAppearance();

		getListView().setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int position, final long id) {
				// TODO Auto-generated method stub
				listener1=new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch(which){
						case 0://新增
							switchActivity(Account_Info.STATUS_ADD, 0);
							break;
						case 1://修改
							switchActivity(Account_Info.STATUS_EDIT, position);
							break;
						case 2://删除
							mCursor.moveToPosition(position);
							new AlertDialog.Builder(AccountList.this)
							.setIcon(android.R.drawable.ic_dialog_info)
							.setTitle(R.string.about)
							.setMessage(R.string.delete_or_not)
							.setNegativeButton(R.string.cancel, null)
							.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub							
									db.delete(AccountHelper.TB_NAME, 
											AccountHelper.ID + "=" + 
											mCursor.getString(mCursor.getColumnIndexOrThrow(AccountHelper.ID)), 
											null);
									updateList();				
								}				
							})
							.show();							
							break;
						default:
							break;
						}
					}				 		
		    	};	    	
		        new AlertDialog.Builder(AccountList.this)
		        	.setTitle(R.string.account_operation)
		            .setItems(R.array.accountOperation,listener1)
		            .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener()
		            {
		              public void onClick(DialogInterface dialog, int which)
		              {
		              }
		            })
		            .show();
		  
				return false;
			}
        	});
		
		
	}


	private void initAppearance() {
		// TODO Auto-generated method stub
		mText = (TextView) findViewById(R.id.mention);

		TextView titleLabel = (TextView) this.findViewById(R.id.label);
		ImageView titleBtn = (ImageView) findViewById(R.id.title_right);
		titleBtn.setImageResource(R.drawable.title_btn_add);
		titleLabel.setText(R.string.accountCenter);
		titleBtn.setVisibility(View.VISIBLE);
		titleBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 添加Account
				switchActivity(Account_Info.STATUS_ADD, 0);
			}
		});

		// 初始化数据库辅助对象，获得可读写的SQLiteDatabase对象
		helper = new AccountHelper(this, ACCOUNT_DB_NAME, null, VERSION);
		db = helper.getWritableDatabase();
		
		updateList();

	}

	private void updateList() {
		// TODO Auto-generated method stub
		mCursor = db.query(AccountHelper.TB_NAME, null,
				AccountHelper.CATEGORY_ID + "=?",
				new String[] { String.valueOf(catId) }, null, null,
				AccountHelper.NAME);

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.account_row, mCursor, new String[] {
						AccountHelper.NAME, AccountHelper.ACCOUNT }, new int[] {
						R.id.text1, R.id.text2 });
		setListAdapter(adapter);
		
		
		
		if (this.getListView().getCount() == 0) {
			mText.setVisibility(0);
			mText.setText(R.string.account_mention);
		} else {
			mText.setVisibility(8);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		switchActivity(Account_Info.STATUS_EDIT, position);
		
	}
	
	private void switchActivity(int status, int pos){
		Intent intent=new Intent(AccountList.this,Account_Info.class);
		Bundle bundle=new Bundle();
		bundle.putInt("STATUS", status);
		bundle.putString("CATEGORY", catName);
		
		switch(status){
		case Account_Info.STATUS_ADD:
			intent.putExtras(bundle);
			startActivityForResult(intent,0);
			break;
		case Account_Info.STATUS_EDIT:
			mCursor.moveToPosition(pos);
			bundle.putString("ID", mCursor.getString(
					mCursor.getColumnIndexOrThrow(AccountHelper.ID)));
			bundle.putString("NAME", mCursor.getString(
					mCursor.getColumnIndexOrThrow(AccountHelper.NAME)));
			bundle.putString("ACCOUNT", mCursor.getString(
					mCursor.getColumnIndexOrThrow(AccountHelper.ACCOUNT)));
			bundle.putString("PASSWORD", mCursor.getString(
					mCursor.getColumnIndexOrThrow(AccountHelper.PASSWORD)));
			intent.putExtras(bundle);
			startActivityForResult(intent,0);
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (resultCode) {
		case RESULT_OK:// 添加account
			ContentValues values = new ContentValues();
			Bundle bnd = data.getExtras();
			
			String name = bnd.getString(AccountHelper.NAME);
			String account = bnd.getString(AccountHelper.ACCOUNT);
			String password = bnd.getString(AccountHelper.PASSWORD);

			values.put(AccountHelper.CATEGORY_ID, catId);
			values.put(AccountHelper.NAME, name);
			values.put(AccountHelper.ACCOUNT, account);
			values.put(AccountHelper.PASSWORD, password);
			
			Log.d("Tag", bnd.getString(AccountHelper.NAME));
			
			switch (bnd.getInt("STATUS")) {
			case Account_Info.STATUS_ADD:
				db.insert(AccountHelper.TB_NAME, AccountHelper.ID, values);
				Toast.makeText(AccountList.this, "新增成功", Toast.LENGTH_SHORT).show();
				break;
			case Account_Info.STATUS_EDIT:
				db.update(AccountHelper.TB_NAME, values, 
						AccountHelper.ID + "=" + bnd.getString(AccountHelper.ID), null);
				break;
			default:
				break;
			}
			
			updateList(); // 更新账号列表
		case RESULT_CANCELED:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		db.close();
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			int total = mCursor.getCount();
			mCursor.close();
			
	  		Log.d("Tag", "Account:"+total);
	  		catBundle.putInt("TOTAL", total);
	  		catIntent.putExtras(catBundle);
	  		AccountList.this.setResult(RESULT_OK, catIntent);
	  		AccountList.this.finish();

		}
		return false;
	}
	
}
