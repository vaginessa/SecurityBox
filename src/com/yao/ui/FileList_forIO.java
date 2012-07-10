package com.yao.ui;

/*账号的导入*/
/* import����class */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.yao.data.DataLib;
import com.yao.pw.R;
import com.yao.util.AccountHelper;
import com.yao.util.MyAdapter;
import com.yao.util.ZipUtils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FileList_forIO extends ListActivity{

  private List<String> items=null;
  private List<String> paths=null;
  private String rootPath="/sdcard";
  private TextView mPath;
  private View pathBar;
  Intent intent;
  final static int FileIN_OK = 1;
  final static int FileIN_FAIL = 2;
  final static int DIALOG_DB_IN = 0;
  String fileName;
  File file;
  @Override
  protected void onCreate(Bundle icicle)
  {
    super.onCreate(icicle); 
    setContentView(R.layout.filelist );
    
    setTitle(R.string.account_import_msg);
    
    mPath=(TextView)findViewById(R.id.mPath);
    pathBar=(View)findViewById(R.id.pathBar);
    pathBar.getBackground().setAlpha(100);
    
    intent=this.getIntent();
    getFileDir(rootPath);
    
	String status = Environment.getExternalStorageState();
	if (!status.equals(Environment.MEDIA_MOUNTED)) {
		Toast.makeText(this, R.string.account_no_sdcard, Toast.LENGTH_SHORT).show();
	}
  }
  

  private void getFileDir(String filePath)
  {
    mPath.setText(filePath);
    items=new ArrayList<String>();
    paths=new ArrayList<String>();
    
    File f=new File(filePath);  
    File[] files=f.listFiles();

    if(!filePath.equals(rootPath))
    {
      items.add("b1");
      paths.add(rootPath);
      items.add("b2");
      paths.add(f.getParent());
    }
    for(int i=0;i<files.length;i++)
    {
      File file=files[i];
      items.add(file.getName());
      paths.add(file.getPath());
    }
    
    setListAdapter(new MyAdapter(this,items,paths));
  }
  
  @Override
  protected void onListItemClick(ListView l,View v,int position,long id)
  {
    file = new File(paths.get(position));
  
    if(file.isDirectory())
    {
      getFileDir(paths.get(position));
    }
    else
    {
    	showDialog(DIALOG_DB_IN);
    	
    }
  }
  
  @Override
  protected Dialog onCreateDialog(int id){
  	switch(id){
  	case DIALOG_DB_IN:
  		return new AlertDialog.Builder(FileList_forIO.this)
			.setIcon(android.R.drawable.ic_dialog_info)
			.setTitle(R.string.mention)
			.setMessage(R.string.account_import_msg2)
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					
				}		
			})
			.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
			    	fileName = file.getName();
			    	String file_firstName=fileName.substring(0,fileName.lastIndexOf(".")).toLowerCase();	//文件名
			    	String file_lastName=fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
			    	if(file_lastName.equals("zip")){	
			    		try    
			            { 
			        	    
			    			ZipUtils.upZipFile(file, DataLib.APP_DATA_PATH);
			    			
			        	    Toast.makeText(FileList_forIO.this, R.string.account_import_ok, Toast.LENGTH_LONG).show();
			        	    
			        		FileList_forIO.this.setResult(FileIN_OK, intent);
			        		FileList_forIO.this.finish();
			            }
			    		catch (FileNotFoundException e)    
			            {    
			                e.printStackTrace();    
			            }    
			            catch (IOException e)    
			            {    
			                e.printStackTrace();    
			            }    

			    	}else{
			    		Toast.makeText(FileList_forIO.this, R.string.account_import_error, Toast.LENGTH_LONG).show();
			    	}				
				}		
			})
			.create();
  		default:
  			return null;
  	}
  }
  
  public boolean onKeyDown(int keyCode,KeyEvent event){
  	if(keyCode == KeyEvent.KEYCODE_BACK){
  		FileList_forIO.this.setResult(FileIN_FAIL, intent);
		FileList_forIO.this.finish();
  	}
		return false;
  }
  
  
}