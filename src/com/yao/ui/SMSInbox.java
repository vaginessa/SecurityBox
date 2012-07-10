package com.yao.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.gsm.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yao.pw.R;
import com.yao.util.DesUtils;
import com.yao.util.SmsHelper;

public class SMSInbox extends Activity {
	
	public static final String DB_NAME = "Sms.db";
	public static final int VERSION = 1;
	final static String ACCOUNT_PATH = "/data/data/com.yao.pw/databases/Sms.db";
	private ListView lv;
	private ImageView titleBtn;
	private Cursor cursor;
	private ItemAdapter adapter;
	private Handler handler;
	private SMSObserver sObserver;
	SmsHelper helper;
	SQLiteDatabase db;
	Cursor c;
	OnClickListener listener1;
	SmsItem sms = new SmsItem();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.smsinbox);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title);

		lv = (ListView) findViewById(R.id.itemlist);
		titleBtn = (ImageView) findViewById(R.id.title_right);
		titleBtn.setImageResource(R.drawable.title_sms_decrypt);
		titleBtn.setVisibility(View.VISIBLE);
		TextView titleLabel = (TextView)this.findViewById(R.id.label);
        titleLabel.setText(R.string.msgDecryptInbox);
        
		titleBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(SMSInbox.this, SmsDecrypt.class);
				startActivity(i);
			}
		});

		helper = new SmsHelper(this, DB_NAME, null, VERSION);
		db = helper.getWritableDatabase();
		String[] projection = new String[] { "_id", "address", "person",
				"body", "date", "type" };

		/* 收件箱Uri为content://sms/inbox ，通过Uri查询 */
		cursor = getContentResolver().query(Uri.parse("content://sms/inbox"),
				projection, null, null, "date desc");
		queryColumnName(cursor);
		/* 定义adapter */
		adapter = new ItemAdapter(this);
		/* 设置adapter */
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				// TODO Auto-generated method stub
				cursor.moveToPosition(arg2);
				sms.id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
				sms.body = cursor.getString(cursor
						.getColumnIndexOrThrow("body"));
				sms.address = cursor.getString(cursor
						.getColumnIndexOrThrow("address"));
				sms.name = getContactByAddr(SMSInbox.this, sms.address);
				listener1 = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						ContentValues values = new ContentValues();
						switch (which) {
						case 0:
							/* DES加密默认密匙：19900911 */
							DesUtils des1;
							try {
								des1 = new DesUtils(SMSInbox.this
										.getResources().getString(
												R.string.default_key_char));
								sms.body = des1.encrypt(sms.body);
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}// 自定义密钥
							values.put(
									SmsHelper.KEY,
									SMSInbox.this.getResources().getString(
											R.string.default_key_char));
							values.put(SmsHelper.ADDRESS, sms.address);
							values.put(SmsHelper.BODY, sms.body);
							db.insert(SmsHelper.TB_NAME, SmsHelper.ID, values);
							Toast.makeText(SMSInbox.this,
									R.string.sms_encrypt_ok, Toast.LENGTH_SHORT)
									.show();
							cursor.moveToPosition(arg2);
							new DeleteMessage(SMSInbox.this, sms.id)
									.DeleteShortMessage();
							break;
						case 1:
							/* 异或加密法 */
							char b[] = new char[100];
							b = sms.body.toCharArray();
							for (int i = 0; i < b.length; i++) {
								b[i] = (char) (b[i] ^ 22);
							}
							ContentValues cv = new ContentValues(); // 628
							values.put(
									SmsHelper.KEY,
									SMSInbox.this.getResources().getString(
											R.string.default_key_char));
							values.put(SmsHelper.ADDRESS, sms.address);
							values.put(SmsHelper.BODY, sms.body);
							db.insert(SmsHelper.TB_NAME, SmsHelper.ID, values);
							Toast.makeText(SMSInbox.this,
									R.string.sms_encrypt_ok, Toast.LENGTH_SHORT)
									.show();
							new DeleteMessage(SMSInbox.this, sms.id)
									.DeleteShortMessage();
							break;
						case 2:
							DesUtils des2;
							try {
								des2 = new DesUtils(SMSInbox.this
										.getResources().getString(
												R.string.default_key_char));
								sms.body = des2.decrypt(sms.body);
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}// 自定义密钥
							new AlertDialog.Builder(SMSInbox.this)
									.setIcon(R.drawable.chat)
									.setTitle(
											SMSInbox.this.getResources()
													.getString(
															R.string.sms_from)
													+ ((sms.name == null) ? sms.address
															: sms.name))
									.setMessage(sms.body)
									.setPositiveButton(R.string.ok, null)
									.show();
							break;
						default:
							break;
						}
					}
				};
				new AlertDialog.Builder(SMSInbox.this).setItems(
						R.array.smsOperation2, listener1).show();
			}
		});

		/* 定义handler 用于接收变动 当接到通知后，查询Uri并刷新显示 */
		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.what == SMSObserver.SMS_CHANGE) {
					cursor = getContentResolver().query(
							Uri.parse("content://sms/inbox"), null, null, null,
							null);
					adapter.notifyDataSetChanged();
				}
			}

		};
		/* 注册ContentObserver */
		sObserver = new SMSObserver(handler);
		this.getContentResolver().registerContentObserver(
				Uri.parse("content://sms"), true, sObserver);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, R.string.sms_send).setIcon(
				android.R.drawable.ic_menu_send);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case 0:
			Builder Send_Dialog = new AlertDialog.Builder(this);
			LayoutInflater inflater = (LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			LinearLayout layout = (LinearLayout) inflater.inflate(
					R.layout.send_sms_dialog, null);
			Send_Dialog.setView(layout);
			final EditText smsNumEdt = (EditText) layout
					.findViewById(R.id.sms_send_phoneNum);
			final EditText smsBodyEdt = (EditText) layout
					.findViewById(R.id.sms_send_body);
			Send_Dialog.setTitle(R.string.sms_send);
			Send_Dialog.setPositiveButton(R.string.sms_send_des,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							String smsNum = smsNumEdt.getText().toString();
							String smsBody = smsBodyEdt.getText().toString();
							sendShortMsg(smsNum, smsBody, 0);
						}

					});
			Send_Dialog.setNegativeButton(R.string.sms_send_xor,
					new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							String smsNum = smsNumEdt.getText().toString();
							String smsBody = smsBodyEdt.getText().toString();
							sendShortMsg(smsNum, smsBody, 1);
						}
					});
			Send_Dialog.create().show();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	protected void sendShortMsg(String smsNum, String smsBody, int flag) {
		// TODO Auto-generated method stub
		String strMessage = "";
		switch (flag) {
		case 0:
			DesUtils des;
			try {
				des = new DesUtils(SMSInbox.this.getResources().getString(
						R.string.default_key_char));
				strMessage = des.encrypt(smsBody);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}// 自定义密钥
			break;
		case 1:
			char b[] = new char[100];
			b = smsBody.toCharArray();
			for (int i = 0; i < b.length; i++) {
				b[i] = (char) (b[i] ^ 22);
			}
			strMessage = String.valueOf(b);
			break;
		default:
			break;
		}

		SmsManager smsManager = SmsManager.getDefault();

		if (isPhoneNumberValid(smsNum) && iswithin70(strMessage)) {
			try {
				PendingIntent mPI = PendingIntent.getBroadcast(SMSInbox.this,
						0, new Intent(), 0);
				smsManager.sendTextMessage(smsNum, null, strMessage, mPI, null);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			Toast.makeText(SMSInbox.this, R.string.sms_send_ok,
					Toast.LENGTH_SHORT).show();
		} else {
			if (!isPhoneNumberValid(smsNum)) {
				if (!iswithin70(strMessage))
					Toast.makeText(SMSInbox.this, R.string.sms_send_wrong,
							Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(SMSInbox.this, R.string.sms_send_num_wrong,
							Toast.LENGTH_SHORT).show();
			} else if (!iswithin70(strMessage)) {
				Toast.makeText(SMSInbox.this, R.string.sms_send_body_wrong,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	public static boolean isPhoneNumberValid(String phoneNumber) {
		boolean isValid = false;
		/*
		 * 可接受的电话格式有： ^\\(? :可以使用"("作为开头 (\\d{3}): 紧接着三个数字 \\)? : 可以使用")"继续 [-
		 * ]? : 在上述格式后可以是通用具有选择性的"-" (\\d{3}) : 再紧接着三个数字 [- ]? : 可以使用具有选择性的"-"继续
		 * (\\d{5})$: 以五个数字结束 可以比较下列数字格式： (123)456-7890, 123-456-7890,
		 * 1234567890, (123)-456-7890
		 */
		String expression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{5})$";

		/*
		 * 可接受的电话格式有： ^\\(? :可以使用"("作为开头 (\\d{3}): 紧接着三个数字 \\)? : 可以使用")"继续 [-
		 * ]? : 在上述格式后可以是通用具有选择性的"-" (\\d{3}) : 再紧接着四个数字 [- ]? : 可以使用具有选择性的"-"继续
		 * (\\d{5})$: 以四个数字结束 可以比较下列数字格式： (02)3456-7890, 02-3456-7890,
		 * 0234567890, (02)-3456-7890
		 */
		String expression2 = "^\\(?(\\d{3})\\)?[- ]?(\\d{4})[- ]?(\\d{4})$";

		CharSequence inputStr = phoneNumber;
		/* 创建Pattern */
		Pattern pattern = Pattern.compile(expression);
		/* �将Pattern以参数传入Matcher作Regular expression */
		Matcher matcher = pattern.matcher(inputStr);
		/* 创建Pattern2 */
		Pattern pattern2 = Pattern.compile(expression2);
		/* 将Pattern2 以参数传入Matcher2作Regular expression */
		Matcher matcher2 = pattern2.matcher(inputStr);
		if (matcher.matches() || matcher2.matches()) {
			isValid = true;
		}
		return isValid;
	}

	public static boolean iswithin70(String text) {
		if (text.length() <= 70) {
			return true;
		} else {
			return false;
		}
	}

	private class ItemAdapter extends BaseAdapter {
		SMSInbox smsInbox;

		public ItemAdapter(SMSInbox smsInbox) {
			this.smsInbox = smsInbox;
		}

		@Override
		public int getCount() {
			return cursor.getCount();
		}

		@Override
		public Object getItem(int arg0) {
			return cursor.getString(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		public View getView(int position, View arg1, ViewGroup arg2) {

			cursor.moveToPosition(position);
			String body = cursor
					.getString(cursor.getColumnIndexOrThrow("body"));
			String number = cursor.getString(cursor
					.getColumnIndexOrThrow("address"));
			String person = cursor.getString(cursor
					.getColumnIndexOrThrow("person"));

			LinearLayout item = (LinearLayout) smsInbox.getLayoutInflater()
					.inflate(R.layout.smsinbox_item, null);// 第外层的Linear
			Log.d("TAG", "getView: test");
			LinearLayout l_1 = (LinearLayout) item.getChildAt(0);
			LinearLayout l_2 = (LinearLayout) l_1.getChildAt(1);
			LinearLayout l_3 = (LinearLayout) l_2.getChildAt(0);
			TextView tbody = (TextView) l_2.getChildAt(1);
			if (tbody != null) {
				tbody.setText(body);
			}

			TextView tNum = (TextView) l_3.getChildAt(1);
			if (tNum != null) {
				tNum.setText(number);
				if ((person = getContactByAddr(SMSInbox.this, number)) != null) {
					tNum.setText(person);
				}
			}

			ImageView img = (ImageView) l_3.getChildAt(0);
			img.setBackgroundResource(R.drawable.chat);

			return item;
		}

	}

	class SmsItem {
		int id;
		String name;
		String address;
		String body;
		String date;
	}

	private String getContactByAddr(Context context, final String number) {
		Uri personUri = Uri.withAppendedPath(
				ContactsContract.PhoneLookup.CONTENT_FILTER_URI, number);
		Cursor cur = context.getContentResolver().query(personUri,
				new String[] { PhoneLookup.DISPLAY_NAME }, null, null, null);
		if (cur.moveToFirst()) {
			int nameIdx = cur.getColumnIndex(PhoneLookup.DISPLAY_NAME);
			String name = cur.getString(nameIdx);
			cur.close();
			return name;
		}
		return null;
	}

	public void queryColumnName(Cursor cursor) {
		for (int i = 0; i < cursor.getColumnCount(); i++) {
			String columnName = cursor.getColumnName(i);
			Log.d("TAG", "column name:" + columnName);
		}

	}

}
