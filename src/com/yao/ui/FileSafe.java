package com.yao.ui;

/* import����class */
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.yao.pw.R;
import com.yao.util.MyAdapter;
import com.yao.util.TestDES;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FileSafe extends ListActivity{

  private List<String> items=null;
  private List<String> paths=null;
  private String rootPath="/sdcard";
  private TextView mPath;
  private View pathBar;
  private View myView;
  private EditText myEditText;

  @Override
  protected void onCreate(Bundle icicle)
  {
    super.onCreate(icicle); 
    this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
    setContentView(R.layout.filelist );
    this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
    
    
    mPath=(TextView)findViewById(R.id.mPath);
    pathBar=(View)findViewById(R.id.pathBar);
    pathBar.getBackground().setAlpha(100);
    TextView titleLabel = (TextView)this.findViewById(R.id.label);
    titleLabel.setText(R.string.fileSafe);
    
    getFileDir(rootPath);
	
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
    File file = new File(paths.get(position));
  
    if(file.isDirectory())
    {
      getFileDir(paths.get(position));
    }
    else
    {
      fileHandle(file);
    }
  }
  

  private void fileHandle(final File file){
	
    OnClickListener listener1=new DialogInterface.OnClickListener()
    {
      String fName = file.getName();
      String a_fname=fName.substring(0,fName.lastIndexOf(".")).toLowerCase();	//文件名
      String b_fname=fName.substring(fName.lastIndexOf("."),fName.length()).toLowerCase();	//扩展名
      String pFile = file.getParentFile().getPath()+"/";
      String enPath, dePath;
      public void onClick(DialogInterface dialog,int which)
      {
    	  
    	  switch(which){
    	  case 0:
              openFile(file);
    		  break;
    	  case 1:
              /* 文件重命名 */
              LayoutInflater factory = LayoutInflater.from(FileSafe.this);
              myView=factory.inflate(R.layout.rename_alert_dialog,null);
              myEditText=(EditText)myView.findViewById(R.id.mEdit);  
              myEditText.setText(a_fname);
                
              OnClickListener listener2=new DialogInterface.OnClickListener()
              {
            	String newPath;
                public void onClick(DialogInterface dialog, int which)
                {
                  String modName=myEditText.getText().toString();
                  newPath = pFile + modName + b_fname;
                  
                  /* 判断文件是否存在 */
                  if(new File(newPath).exists())
                  {
                    if(!modName.equals(file.getName()))
                    {
                      /* 注意对话框 */
                      new AlertDialog.Builder(FileSafe.this)
                          .setTitle(R.string.attention)
                          .setMessage(R.string.file_exsisted)
                          .setPositiveButton(R.string.ok,new DialogInterface.OnClickListener()
                          {
                            public void onClick(DialogInterface dialog,int which)
                            {          
                              /* 文件重命名 */
                              file.renameTo(new File(newPath));
                              /* 刷新文件列表 */
                              getFileDir(pFile);
                            }
                          })
                          .setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener()
                          {
                            public void onClick(DialogInterface dialog,int which)
                            {
                            }
                          }).show();
                    }
                  }
                  else
                  {
                    /* 文件重命名*/
                    file.renameTo(new File(newPath));
                    /* 刷新文件列表 */
                    getFileDir(pFile);
                  }
                }
              };

              /* 创建文件重命名对话框 */
              AlertDialog renameDialog=new AlertDialog.Builder(FileSafe.this).create();
              renameDialog.setView(myView);
              
              /* 重命名对话框监听 */
              renameDialog.setButton(FileSafe.this.getResources().getString(R.string.ok),listener2);
              renameDialog.setButton2(FileSafe.this.getResources().getString(R.string.cancel),new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface dialog, int which)
                {
                }
              });
              renameDialog.show();   		  
    		  break;
    	  case 2://加密文件
    		  ProgressBarAsyncTask encryptTask = new ProgressBarAsyncTask(FileSafe.this, file, 0);
    		  encryptTask.execute("");
    		  break;
    	  case 3://解密文件
    		  ProgressBarAsyncTask decryptTask = new ProgressBarAsyncTask(FileSafe.this, file, 1);
    		  decryptTask.execute("");
    		  break;
    	  case 4:
    		  /*删除文件 */
              new AlertDialog.Builder(FileSafe.this).setTitle(R.string.attention)
                  .setMessage(R.string.file_delete_msg)
                  .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                  {
                    public void onClick(DialogInterface dialog, int which)
                    {          
                      /* 删除文件，刷新文件列表*/
                      file.delete();
                      getFileDir(file.getParent());
                    }
                  })
                  .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                  {
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                  }).show();
    		  break;   	  
    	  }
      }
    };
        
    /* 文件操作ListDialog */
    new AlertDialog.Builder(FileSafe.this)
        .setTitle(R.string.file_operation)
        .setItems(R.array.fileOperation,listener1)
        .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface dialog, int which)
          {
          }
        })
        .show();
  }
  
  /* 打开文件方法 */
  private void openFile(File f) 
  {
    Intent intent = new Intent();
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.setAction(android.content.Intent.ACTION_VIEW);
    
    String type = getMIMEType(f);
    intent.setDataAndType(Uri.fromFile(f),type);
    startActivity(intent); 
  }

  
  private String getMIMEType(File f) 
  { 
    String type="";
    String fName=f.getName();
    String end=fName.substring(fName.lastIndexOf(".")+1,fName.length()).toLowerCase(); 
    
    if(end.equals("m4a")||end.equals("mp3")||end.equals("mid")||end.equals("xmf")||end.equals("ogg")||end.equals("wav"))
    {
      type = "audio"; 
    }
    else if(end.equals("3gp")||end.equals("mp4"))
    {
      type = "video";
    }
    else if(end.equals("jpg")||end.equals("gif")||end.equals("png")||end.equals("jpeg")||end.equals("bmp"))
    {
      type = "image";
    }
    else
    {
      type="*";
    }
    type += "/*"; 
    return type; 
  }
  
  	/*处理加密/解密--ProgressDialog、AsyncTask*/
	private class ProgressBarAsyncTask extends AsyncTask<String, Integer, String> {   
        // 可变长的输入参数，与AsyncTask.exucute()对应   
		ProgressDialog pdialog;
		File file;
		int flag = -1;
		Context con;
		
        public ProgressBarAsyncTask(Context context, File file, int flag){   
            pdialog = ProgressDialog.show(FileSafe.this, 
				  FileSafe.this.getResources().getString(R.string.mention), 
				  FileSafe.this.getResources().getString(R.string.file_encrypting),true);
            this.file = file;
            this.flag = flag;
            this.con = context;
        }
        
        @Override  
        protected String doInBackground(String... params) {
        	TestDES td = new TestDES(FileSafe.this.getResources().getString(R.string.default_key_char));
        	switch(flag){
        	case 0://加密
        		try {
    				td.encrypt(file.toString(), file.getParentFile().getPath() + "/En-" + file.getName());
    			} catch (Exception e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			break;
        	case 1://解密
        		try {
					td.decrypt(file.toString(),  file.getParentFile().getPath() + "/De-" + file.getName());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
        	default:
        		break;
        	}
        	return null;
        }
  
        @Override  
        protected void onPostExecute(String result) {   
            pdialog.dismiss(); 
            // 删除源文件
            file.delete();
            
            switch(flag){
            case 0:
            	Toast.makeText(con, R.string.file_encrypt_ok, Toast.LENGTH_LONG).show();
            	break;
            case 1:
            	Toast.makeText(con, R.string.file_decrypt_ok, Toast.LENGTH_LONG).show();
            	break;
            }
            
			// 刷新目录
			FileSafe.this.getFileDir(file.getParentFile().getPath()+"/");
        } 
        
     }   
  
}

