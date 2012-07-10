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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.yao.pw.R;
import com.yao.util.NoteHelper;
import com.yao.util.TestDES;

public class NoteList extends ListActivity{
	
	public static final String DB_NOTE = "Notepad.db";
    public static final int VERSION = 1;   
    final static String PRE = "MY_PREF";
	final static String EDIT_KEY = "EDIT_CONTENT";
	final static String NOTE_PATH = "data\\data\\com.yao.pw\\databases\\Notepad.db";
	final static String NOTE_PATH_DECYPT = "data\\data\\com.yao.pw\\databases\\Notepad0.db";
	final static String JIAMI_KEY = "JIAMI_KEY";
	final static int MENU_ADD = Menu.FIRST;
	String loginPassword = "111111";
	String jiami_key = "19900911";
//    int titleIndex;
//    int contextIndex;
//    int dateIndex;
//    int status;
//    long read_modify_id;
    NoteHelper helper;
    SQLiteDatabase db;
    Cursor c;
    OnClickListener listener1;    
    SharedPreferences sp;
    TestDES td;
    TextView mText;
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {	
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.account_note_list);
        this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
        
        mText = (TextView)findViewById(R.id.mention);
        ImageView titleBtn=(ImageView)findViewById(R.id.title_right);
        TextView titleLabel = (TextView)this.findViewById(R.id.label);
        titleLabel.setText(R.string.noteInfo);
        titleBtn.setImageResource(R.drawable.title_btn_add);
        titleBtn.setVisibility(View.VISIBLE);
        titleBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
	    		switchActivity(Note_Info.STATUS_ADD, 0);
			}
		});
        
        //获得当前登陆密码
        sp=getSharedPreferences(PRE,0);
		if(sp.getString(JIAMI_KEY,"")!=""){
			jiami_key=sp.getString(JIAMI_KEY,"");
		}
		
		//数据库文件解密,以登陆密码为密匙
	  	td = new TestDES(jiami_key);
	  	try {
	  		td.decrypt(NOTE_PATH_DECYPT ,NOTE_PATH);
	  	} catch (Exception e) {
	  		// TODO Auto-generated catch block
	  		e.printStackTrace();
	  	}
	  	File file=new File(NOTE_PATH_DECYPT);
		file.delete();
		
        //初始化数据库辅助对象，获得可读写的SQLiteDatabase对象
        helper = new NoteHelper(this, DB_NOTE, null, VERSION);
        db = helper.getWritableDatabase();            
        updateList();
        
        //ListActitity长按事件监听
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
							switchActivity(Note_Info.STATUS_ADD, 0);
							break;
						case 1://编辑
							switchActivity(Note_Info.STATUS_EDIT, position);
							break;
						case 2://删除
							c.moveToPosition(position);
							new AlertDialog.Builder(NoteList.this)
							.setIcon(android.R.drawable.ic_dialog_info)
							.setTitle(R.string.about)
							.setMessage(R.string.note_delete_msg)
							.setNegativeButton(R.string.cancel, null)
							.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									db.delete(NoteHelper.TB_NAME, 
											NoteHelper.ID + "=" 
											+ c.getString(c.getColumnIndexOrThrow(NoteHelper.ID)),
											null);
									Toast.makeText(NoteList.this, R.string.delete_ok , Toast.LENGTH_SHORT).show();
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
		        new AlertDialog.Builder(NoteList.this)
		        	.setTitle(R.string.note_operation)
		            .setItems(R.array.noteOperation,listener1)
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
    
    //根据status的状态，切换到Note_Info
	private void switchActivity(int status, int pos) {
		// TODO Auto-generated method stub
		Intent intent=new Intent(NoteList.this,Note_Info.class);
		Bundle bundle=new Bundle();
		bundle.putInt("STATUS", status);
		if(status == Note_Info.STATUS_EDIT){
			c.moveToPosition(pos);
			bundle.putString("ID", 	c.getString(c.getColumnIndexOrThrow(NoteHelper.ID)));
			bundle.putString("TITLE", c.getString(c.getColumnIndexOrThrow(NoteHelper.TITLE)));
			bundle.putString("DATE", c.getString(c.getColumnIndexOrThrow(NoteHelper.DATE)));
			bundle.putString("BODY", c.getString(c.getColumnIndexOrThrow(NoteHelper.CONTEXT)));
		}
		intent.putExtras(bundle);
		startActivityForResult(intent,0);   
	}
    
	//列表单击事件监听
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	switchActivity(Note_Info.STATUS_EDIT, position);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
	 	menu.add(0,MENU_ADD,0,R.string.note_new)
 			.setIcon(android.R.drawable.ic_menu_add);
        return true;
    }
       
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()){
    	case MENU_ADD:
    		switchActivity(Note_Info.STATUS_ADD, 0);
    		break;
    	}
		return false;
    }
  
    @Override
    public void onDestroy(){
		td = new TestDES(jiami_key);
		try {
			td.encrypt(NOTE_PATH , NOTE_PATH_DECYPT);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //加密
		File file=new File(NOTE_PATH);
		file.delete();
    	super.onDestroy();
    }
    
    //根据由Note_Info返回的Intent，完成相应的数据库操作
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data){
    	 switch (resultCode){
    	 case RESULT_OK:     		 
    		 ContentValues values = new ContentValues();
    		 Bundle bnd = data.getExtras();
    		 String title=bnd.getString("TITLE");
        	 String date=bnd.getString("DATE");
        	 String context=bnd.getString("BODY");	    			 
        	 values.put(NoteHelper.TITLE, title);
        	 values.put(NoteHelper.DATE, date);
        	 values.put(NoteHelper.CONTEXT, context);  		   		 
    		 //根据status判断：修改、增加
    		 switch(bnd.getInt("STATUS")){
    		 case Account_Info.STATUS_EDIT:
	        	 db.update(NoteHelper.TB_NAME, values, NoteHelper.ID + "=" + bnd.getString("ID"), null);  
	    		 Toast.makeText(NoteList.this, R.string.modify_ok , Toast.LENGTH_SHORT).show();
    			 break;
    		 case Account_Info.STATUS_ADD:
 	        	 db.insert(NoteHelper.TB_NAME,NoteHelper.ID, values);
 	        	 Toast.makeText(NoteList.this, R.string.add_ok , Toast.LENGTH_SHORT).show();
        	     break;
//    		 case Account_Info2.STATUS_DELETE:
//    			 db.delete(NoteHelper.TB_NAME, NoteHelper.ID+"="+read_modify_id, null);
//    			 Toast.makeText(NoteList.this, R.string.delete_ok , Toast.LENGTH_SHORT).show();
//    			 break;    	 
    		 } 		 
    		 updateList();	//更新记事列表 		 
    	 case RESULT_CANCELED:
    		 break;
    	 }
    	
    }

    /*刷新账号列表*/
    public void updateList(){
	    c = db.query(NoteHelper.TB_NAME,null,null,null,null,null,
	               NoteHelper.DATE);
//	    titleIndex = c.getColumnIndexOrThrow(NoteHelper.TITLE);
//	    contextIndex = c.getColumnIndexOrThrow(NoteHelper.CONTEXT);    
	    
	    SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
	        	R.layout.note_row, c,
	            new String[] {NoteHelper.TITLE,NoteHelper.DATE}, 
	            new int[] {R.id.title_text,R.id.date_text});     
	    setListAdapter(adapter);
	    if(this.getListView().getCount() == 0){
        	mText.setVisibility(0);
        	mText.setText(R.string.note_mention);
        }else{
        	mText.setVisibility(8);
        }
    }
 
}
