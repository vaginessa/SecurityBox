package com.yao.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.yao.data.DataLib;
import com.yao.pw.R;
import com.yao.util.AccountHelper;
import com.yao.util.CategoryHelper;
import com.yao.util.TestDES;
import com.yao.util.ZipUtils;

public class AccountClass extends ListActivity{
	
//	public final static String ACCOUNT_PATH = "/data/data/com.yao.pw/databases/";
//	public static String EXPORT_PATH="/sdcard/Security/Export/";
	public static final String CAT_DB_NAME = "Category.db";
//	public final static String ACCOUNT_DB_NAME = "Account";
	private final static int MENU_ADD = Menu.FIRST;
	private final static int MENU_OUT = Menu.FIRST + 1;
	private final static int MENU_IN = Menu.FIRST + 2;
	public static final int VERSION = 1;
	final static int FileIN_OK = 1;
	final static int FileIN_FAIL = 2;
	final int DIALOG_CREATE=1;
	final int DIALOG_RENAME=2;
	final int DIALOG_DELETE=3;
	final int DIALOG_EXPORT=4;
	int current_pos;
    String itemName;
    String itemId;
    OnClickListener listener1;
	CategoryHelper helper;
	private String key = DataLib.DEFAULT_KEY;
	private SharedPreferences sp;
    private SQLiteDatabase db;
    Cursor c;
    TextView mText;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {	
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.account_note_list);
        this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
        
        mText = (TextView)findViewById(R.id.mention);
        TextView titleLabel = (TextView)this.findViewById(R.id.label);
        titleLabel.setText(R.string.accountCenter);
        ImageView titleBtn=(ImageView)findViewById(R.id.title_right);
        titleBtn.setVisibility(View.VISIBLE);
        titleBtn.setImageResource(R.drawable.title_btn_add);
        titleBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(DIALOG_CREATE);
			}
		});
        
        decryptAccountDB();
        
        helper = new CategoryHelper(this, CAT_DB_NAME, null, VERSION);
        db = helper.getWritableDatabase(); 	
        updateList();
        
        //ListActitity长按事件监听
        this.getListView().setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int position, final long id) {
				// TODO Auto-generated method stub
//				now_id = id;
				current_pos=position;
				c.moveToFirst();
				c.move(position);
				final int idIndex = c.getColumnIndexOrThrow(CategoryHelper.ID);
				final int nameIndex=c.getColumnIndexOrThrow(CategoryHelper.NAME);
				itemName=c.getString(nameIndex);
				itemId=c.getString(idIndex);
				
				listener1=new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch(which){
						case 0://新建组别
							showDialog(DIALOG_CREATE);
							break;
						case 1://重命名组
//							showDialog(DIALOG_RENAME);
							Builder reame_Dialog = new AlertDialog.Builder(AccountClass.this);
							LayoutInflater inflater = (LayoutInflater) AccountClass.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
							LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.newclass_dilog_view, null);
							reame_Dialog.setView(layout);
							final EditText classText = (EditText)layout.findViewById(R.id.classname);
					    	classText.setText(itemName);
							reame_Dialog.setTitle(R.string.account_group_modify);
//							classText.setText(c.getString(nameIndex));
							reame_Dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
//									
									ContentValues values = new ContentValues();
									values.put(CategoryHelper.NAME, classText.getText().toString());
									db.update(CategoryHelper.TB_NAME, values, CategoryHelper.ID+ "=" + itemId, null);
									updateList();
									
								}		
							}); 
							reame_Dialog.show();
							break;
						case 2://删除组别
							new AlertDialog.Builder(AccountClass.this)
							.setIcon(android.R.drawable.ic_dialog_info)
							.setTitle(R.string.about)
							.setMessage(R.string.account_group_delete_msg)
							.setNegativeButton(R.string.cancel, null)
							.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub	
									//根据数据库的id删除选中项
									db.delete(CategoryHelper.TB_NAME, CategoryHelper.ID+ "=" + itemId, null);
									updateList();
									
									AccountHelper hh = new AccountHelper(AccountClass.this, 
											AccountList.ACCOUNT_DB_NAME + ".db", null, VERSION);
									SQLiteDatabase accDB = hh.getWritableDatabase();
									
									accDB.delete(AccountHelper.TB_NAME, 
											AccountHelper.CATEGORY_ID + "=" + itemId, null);
								}

							})
							.show();							
							break;
						default:
							break;
						}
					}				 		
		    	};	    	
//		        String[] menu={"新建组别","修改组名","删除该组"};
		        new AlertDialog.Builder(AccountClass.this)
		        	.setTitle(R.string.account_group_operation)
		            .setItems(R.array.groupOperation,listener1)
		            .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener()
		            {
		              public void onClick(DialogInterface dialog, int which)
		              {
		              }
		            })
		            .show();
		  
				return false;
			}
        });	//End: ListActitity长按事件监听
        
    }
       
    /*
     * 点击item，跳转到AccountList，将类别id，name传入intent
     * (non-Javadoc)
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	current_pos=position;
    	c.moveToFirst();
		c.move(position);
		
		//Category Name
		itemName = c.getString(c.getColumnIndexOrThrow(CategoryHelper.NAME));
		//Category Id
		itemId = c.getString(c.getColumnIndexOrThrow(CategoryHelper.ID));
		
    	Intent intent = new Intent(AccountClass.this,AccountList.class);
    	Bundle bundle = new Bundle();
    	bundle.putString("ID", itemId);
    	bundle.putString("NAME", itemName);
    	intent.putExtras(bundle);
    	
    	startActivityForResult(intent, 0);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
	 	menu.add(0,MENU_ADD,0,R.string.account_group_new)
 			.setIcon(android.R.drawable.ic_menu_add);
	 	menu.add(0,MENU_IN,0,R.string.account_group_import)
	 		.setIcon(android.R.drawable.ic_menu_rotate);
	 	menu.add(0, MENU_OUT, 0, R.string.account_group_export)
	 		.setIcon(android.R.drawable.ic_menu_share);
        return true;
    }
     
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()){
    	case MENU_ADD:
    		showDialog(DIALOG_CREATE);
    		break;
    	case MENU_IN:
    		Intent intent_getFile = new Intent(AccountClass.this,FileList_forIO.class);
    		startActivityForResult(intent_getFile,0);
    		break;
    	case MENU_OUT:
    		showDialog(DIALOG_EXPORT);
    		break;
    	}
		return false;
    }
    
    
    @Override
    protected Dialog onCreateDialog(int id){
    	
    	Builder Login_Dialog = new AlertDialog.Builder(this);
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.newclass_dilog_view, null);
		Login_Dialog.setView(layout);
		final EditText classText = (EditText)layout.findViewById(R.id.classname);
		classText.setText("");
		
    	switch(id){
    	case DIALOG_CREATE:
    		//新建类别
    		Login_Dialog.setTitle(R.string.account_group_new);
    		Login_Dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {
    				ContentValues values = new ContentValues();
    				values.put(CategoryHelper.NAME, classText.getText().toString());
    				values.put(CategoryHelper.TOTAL, 0);
    				db.insert(CategoryHelper.TB_NAME, CategoryHelper.ID, values);
    				updateList();
    			}    			
    		}); 
    		Login_Dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
    		    public void onClick(DialogInterface dialog, int which) {
    		    	classText.setText("");
    		    }
    		   
    		   });
    		return Login_Dialog.create();
    	case DIALOG_EXPORT:
    		return new AlertDialog.Builder(AccountClass.this)
			.setIcon(android.R.drawable.ic_dialog_info)
			.setTitle(R.string.about)
			.setMessage(R.string.account_export_msg)
			.setNegativeButton(R.string.cancel, null)
			.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
					
					File export_out = new File(DataLib.ACCOUNT_EXPORT_PATH);	
					if (!export_out.exists()) 
						export_out.mkdirs();
					
					try {
						File cat_DB = new File(DataLib.CATEGORY_PATH);
						File acc_DB = new File(DataLib.ACCOUNT_DE_PATH);
						File zipOut = new File(DataLib.ACCOUNT_EXPORT_PATH+ "/Account.zip");
						List<File> list = new ArrayList<File>();
						list.add(cat_DB);
						list.add(acc_DB);
						ZipUtils.zipFiles(list, zipOut, DataLib.ZIP_INFO);
						Toast.makeText(AccountClass.this, R.string.account_export_ok, Toast.LENGTH_SHORT).show();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						Toast.makeText(AccountClass.this, 
								R.string.account_export_error, Toast.LENGTH_SHORT).show();
					}
				}
			}).create();
					
    	default:
    		return null;
    	}
    }
    
    private void exportDataBase(String fileDB, String fileOut) throws IOException{
    	
    	InputStream in = new FileInputStream(fileDB);
	    OutputStream out = new FileOutputStream(fileOut);
	    // Transfer bytes from in to out   
        byte[] buf = new byte[1024];    
        int len;    
        while ((len = in.read(buf)) > 0)    
        {    
            out.write(buf, 0, len);    
        }    
	    out.close();
	    in.close();
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data){   	
    	Bundle bnd;  
    	ContentValues values = new ContentValues();
    	Log.d("Tag", "result");
    	switch (resultCode){
    	 case RESULT_OK: 
    		 bnd = data.getExtras();
    		 int total=bnd.getInt("TOTAL");
    		 Log.d("Tag", ""+total);
    		 values.put(CategoryHelper.TOTAL, total);
    		 db.update(CategoryHelper.TB_NAME, values, CategoryHelper.ID+ "=" + itemId, null);
    		 updateList();
    		 break;
    	 case FileIN_OK:
    		 //导入数据
			 updateList();
    		 break;
    	 case FileIN_FAIL:
    		 Toast.makeText(AccountClass.this, R.string.account_no_import_file, Toast.LENGTH_SHORT).show();
    		 break;
    	 default:
    		 break;
    	 }
    }
    
    /*刷新账号列表*/
    public void updateList(){
	    c = db.query(CategoryHelper.TB_NAME,null,null,null,null,null,
	             CategoryHelper.ID);
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
	    		R.layout.account_class_row, 
	    		c,
	            new String[] {CategoryHelper.NAME,CategoryHelper.TOTAL},
	            new int[] {R.id.text1,R.id.text2});
	    setListAdapter(adapter);
        if(this.getListView().getCount() == 0){
        	mText.setVisibility(0);
        }else{
        	mText.setVisibility(8);
        }
    }
    
    private void decryptAccountDB() {
		// TODO Auto-generated method stub
		// 获得加密密钥
		sp = getSharedPreferences(DataLib.PRE, 0);
		if (sp.getString(DataLib._KEY, "") != "") {
			key = sp.getString(DataLib._KEY, "");
		}

		// 数据库文件解密
		TestDES td = new TestDES(key);
		try {
			td.decrypt(DataLib.ACCOUNT_EN_PATH, DataLib.ACCOUNT_DE_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 解密
		File file = new File(DataLib.ACCOUNT_EN_PATH);
		file.delete();

	}
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	
    	TestDES td = new TestDES(key);
		try {
			td.encrypt(DataLib.ACCOUNT_DE_PATH, DataLib.ACCOUNT_EN_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 加密
		File file=new File(DataLib.ACCOUNT_DE_PATH);
		file.delete();
    	
    	db.close();
    }
    
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
//    	c.close();
    	super.onStop();
    }
    
}
