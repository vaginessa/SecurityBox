package com.yao.util;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.yao.pw.R;
import com.yao.ui.MainMenu;

public class TitleBar {

	private TextView label;

	private ImageButton homeBtn;

	private ImageView rightBtn;
	
	private Activity ac;

	private static TitleBar titleBar;
	
	public TitleBar(Activity ac, boolean isHome) {
		label = (TextView) ac.findViewById(R.id.label);
		rightBtn = (ImageView) ac.findViewById(R.id.title_right);
		if(!isHome){
			homeBtn = (ImageButton)ac.findViewById(R.id.title_back);
		}
		this.ac=ac;
	}
	

	
	public void setTitleBar(int titleStrId, int imgId) {
		label.setText(titleStrId);
		rightBtn.setImageResource(imgId);
		if(homeBtn!=null){
			homeBtn.setImageResource(R.drawable.icon);
			homeBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent i=new Intent(ac,MainMenu.class);
					ac.startActivity(i);
				}
			});
		}
		
	}
	
	public ImageView getRightBtn(){
		return rightBtn;
	}
	
}
