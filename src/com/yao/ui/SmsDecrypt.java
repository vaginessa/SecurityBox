package com.yao.ui;

import com.yao.pw.R;
import com.yao.util.DesUtils;
import com.yao.util.MyHelper;
import com.yao.util.SmsHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class SmsDecrypt extends ListActivity{
	public static final String DB_NAME = "Sms.db";
	public static final int VERSION = 1;
	SmsHelper helper;
	SQLiteDatabase db;
	Cursor c;
	int keyIndex;
    int addressIndex;
    int bodyIndex;
    OnClickListener listener1;
    String key;
    String body;
    String address;
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.account_note_list);
        this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
        
        TextView titleLabel = (TextView)this.findViewById(R.id.label);
        titleLabel.setText(R.string.msgDecryptDb);
        
        helper = new SmsHelper(this, DB_NAME, null, VERSION);
        db = helper.getWritableDatabase();    	
        c = db.query(SmsHelper.TB_NAME,null,null,null,null,null,
                SmsHelper.ADDRESS);
        keyIndex = c.getColumnIndexOrThrow(SmsHelper.KEY);
        addressIndex = c.getColumnIndexOrThrow(SmsHelper.ADDRESS);
        bodyIndex = c.getColumnIndexOrThrow(SmsHelper.BODY);    
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.smsinbox_item,
                c,
                new String[] {SmsHelper.ADDRESS,SmsHelper.BODY},
                new int[] {R.id.num,R.id.body});
        setListAdapter(adapter);
            
        
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, final long id) {
		key = c.getString(keyIndex);
		body = c.getString(bodyIndex);
		address = c.getString(addressIndex);
		listener1=new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				switch(which){
				case 0:
					DesUtils des;
					try {
						des = new DesUtils("19900911");
						body = des.decrypt(body);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					new AlertDialog.Builder(SmsDecrypt.this)
					.setIcon(android.R.drawable.ic_dialog_info)
					.setTitle(SmsDecrypt.this.getResources().getString(R.string.sms_from)+ address)
					.setMessage(body)
					.setPositiveButton(R.string.ok, null)
					.show();
					break;
				case 1:
					char b[]=new char[100];
					b=body.toCharArray();
					for(int i=0;i<b.length;i++){
						b[i]=(char)(b[i]^22);
					}
					new AlertDialog.Builder(SmsDecrypt.this)
					.setIcon(android.R.drawable.ic_dialog_info)
					.setTitle(SmsDecrypt.this.getResources().getString(R.string.sms_from)+ address)
					.setMessage(String.valueOf(b))
					.setPositiveButton(R.string.ok, null)
					.show();
					break;
				case 2:
					new AlertDialog.Builder(SmsDecrypt.this)
					.setIcon(android.R.drawable.ic_dialog_info)
					.setTitle(R.string.about)
					.setMessage(R.string.sms_delete_msg)
					.setNegativeButton(R.string.cancel, null)
					.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub							
							db.delete(SmsHelper.TB_NAME, MyHelper.ID+ "=" + id, null);
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
    	
        new AlertDialog.Builder(SmsDecrypt.this)
            .setItems(R.array.smsOperation1,listener1)
            .show();
		
	}
	
	/*刷新账号列表*/
    public void updateList(){
	    c = db.query(SmsHelper.TB_NAME,null,null,null,null,null,
	             SmsHelper.ADDRESS);
	    keyIndex = c.getColumnIndexOrThrow(SmsHelper.KEY);
	    addressIndex = c.getColumnIndexOrThrow(SmsHelper.ADDRESS);
	    bodyIndex = c.getColumnIndexOrThrow(SmsHelper.BODY);	     
	    SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.smsinbox_item,
                c,
                new String[] {SmsHelper.ADDRESS,SmsHelper.BODY},
                new int[] {R.id.num,R.id.body});
        setListAdapter(adapter);
    }
	
}
