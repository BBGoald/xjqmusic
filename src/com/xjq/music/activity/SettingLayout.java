package com.xjq.music.activity;

import com.xjq.xjqgraduateplayer.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
/**
 * 主界面的设置部分，加载了布局文件view_settins.xml
 * @author root
 *
 */
public class SettingLayout extends FrameLayout{
	
	private Context mContext;
	View view;
	TextView txtToMain;
	Button btnToMain;
	Button btnChangeBG;
//
	public SettingLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = layoutInflater.inflate(R.layout.view_setting, this);
		//txtToMain = (TextView) findViewById(R.id.txt_setting_view);
		btnToMain = (Button) findViewById(R.id.btn_setting_to_main);
		btnChangeBG = (Button) findViewById(R.id.btn_change_background);
	}

	public void onResume() {
/*		showDownloadPath();
		showCacheSize();*/
	}
}
