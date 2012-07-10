package com.yao.ui;

import java.util.ArrayList;
import java.util.HashMap;
import com.yao.pw.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainMenu extends Activity{
	
	int style_pos;
	int bg_pos;
	ImageButton bgBtn1;
	ImageButton bgBtn2;
	ImageButton bgBtn3;
	View myView;
	OnClickListener listener;
	SharedPreferences sp;
	final int DIALOG_KEY = 0;
	final int DIALOG_SET_KEY = 1;
	final int DIALOG_DEFAULT_KEY = 2;
	final String DEFAULT_KEY = "19900911";
	String now_Key;
	final static String JIAMI_KEY = "JIAMI_KEY";
	final static String PRE="MY_PREF";
	android.content.DialogInterface.OnClickListener listener1,listener2,listener3;
	
	final static int[] icon=new int[]{R.drawable.icon_account,R.drawable.icon_note,
		R.drawable.icon_filesafe,R.drawable.icon_sms,R.drawable.icon_sim,R.drawable.icon_setting};
	
	final static String[] label=new String[]{"帐号中心","隐私记事","文件安全","短信加密","手机防盗","更多设置"};
	
	final static String[] action=new String[]{"com.yao.ui.AccountClass","com.yao.ui.NoteList",
				"com.yao.ui.FileSafe","com.yao.ui.SMSInbox","com.yao.ui.SIM","com.yao.ui.pw_reset"};
	
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        //setAppearance();
        
        this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.fuction_menu2);	
        this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
        
        sp=getSharedPreferences(PRE,0);
		if(sp.getString(JIAMI_KEY, "") == ""){
			showDialog(DIALOG_KEY);
		}	    	

        
		GridView gridview = (GridView) findViewById(R.id.GridView); 
		ArrayList<HashMap<String, Object>> menuList = new ArrayList<HashMap<String, Object>>(); 
		
		for(int i=0;i<icon.length;i++){
			HashMap<String, Object> map = new HashMap<String, Object>(); 
			map.put("ItemImage", icon[i]);
			map.put("ItemText", label[i]);
			menuList.add(map);
		}
		
		SimpleAdapter saMenuItem = new SimpleAdapter(this, 
		  menuList, //数据源 
		  R.layout.menuitem, //xml实现 
		  new String[]{"ItemImage","ItemText"}, //对应map的Key 
		  new int[]{R.id.ItemImage,R.id.ItemText});  //对应R的Id 

		//添加Item到网格中 
		gridview.setAdapter(saMenuItem); 
		gridview.setOnItemClickListener(new OnItemClickListener() { 
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
				// TODO Auto-generated method stub
				Intent intent;
				switch(pos){
				case 0:
					intent=new Intent(MainMenu.this,AccountClass.class);
//					intent=new Intent("com.yao.ui.AccountClass");
					startActivity(intent);
					break;
				case 1:
					intent=new Intent(MainMenu.this,NoteList.class);						
					startActivity(intent);
					break;
				case 2:
					intent=new Intent(MainMenu.this,FileSafe.class);
					startActivity(intent);
					break;
				case 3:
					intent=new Intent(MainMenu.this,SMSInbox.class);
					startActivity(intent);
					break;
				case 4:
					intent=new Intent(MainMenu.this,Guard.class);
					startActivity(intent);
					break;
				case 5:
					intent=new Intent(MainMenu.this,Setting.class);
					startActivity(intent);
					break;
				default:
					break;
				}		
			} 
		});
        
    }

    
    @Override
    protected Dialog onCreateDialog(int id){
    	switch(id){
    	case DIALOG_KEY:
    		return new AlertDialog.Builder(MainMenu.this)
			.setIcon(android.R.drawable.ic_dialog_info)
			.setTitle(R.string.first_guide_title)
			.setMessage(R.string.first_guide_body)
			.setNegativeButton(R.string.default_key, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					SharedPreferences.Editor editor=sp.edit();
        			editor.putString(JIAMI_KEY, MainMenu.this.getResources().getString(R.string.default_key_char));
        			editor.commit();
					Toast.makeText(MainMenu.this, R.string.default_key_in_use, Toast.LENGTH_SHORT).show();
				}
				
			})
			.setPositiveButton(R.string.self_key, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					showDialog(DIALOG_SET_KEY);
				}
				
			})
			.create();
    	case DIALOG_SET_KEY:
    		Builder Key_Dialog = new AlertDialog.Builder(this);
    		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.login_dilog_view, null);
    		Key_Dialog.setView(layout);
    		final EditText keyText = (EditText)layout.findViewById(R.id.searchC);
    		Key_Dialog.setTitle(R.string.self_key_input);	
			sp=getSharedPreferences(PRE,0);	
    		Key_Dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int which) {
    			//loginPassword;
    			String input_Key = keyText.getText().toString();
    			SharedPreferences.Editor editor=sp.edit();
    			editor.putString(JIAMI_KEY, input_Key);
    			editor.commit();
    			Toast.makeText(MainMenu.this, R.string.self_key_in_use, Toast.LENGTH_SHORT).show();
    		}
    		});   		  
    		Key_Dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
    		    public void onClick(DialogInterface dialog, int which) {
    		    	keyText.setText("");
    		    	SharedPreferences.Editor editor=sp.edit();
        			editor.putString(JIAMI_KEY, MainMenu.this.getResources().getString(R.string.default_key_char));
        			editor.commit();
    		    	Toast.makeText(MainMenu.this, R.string.default_key_in_use, Toast.LENGTH_SHORT).show();
    		    }
    		   });
    		return Key_Dialog.create(); 
    	}
		return null;
    }


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			new AlertDialog.Builder(MainMenu.this)
			.setIcon(android.R.drawable.ic_dialog_info)
			.setTitle(R.string.mention)
			.setMessage(R.string.exit_msg)
			.setNegativeButton(R.string.cancel, null)
			.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub	
					finish();
				}				
			})
			.show();	
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

    
    
}
