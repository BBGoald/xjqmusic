package com.xjq.music.activity;

import com.xjq.xjqgraduateplayer.R;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * 主界面的设置部分，加载了布局文件view_settins.xml
 * 
 * @author root
 * 
 */
public class SettingLayout extends FrameLayout implements OnClickListener {

	private Context mContext;
	View view;
	TextView txtToMain;
	Button btnToMain;
	Button btnChangeBG;
	Button btnAbout;

	//
	public SettingLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		LayoutInflater layoutInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = layoutInflater.inflate(R.layout.view_setting, this);
		// txtToMain = (TextView) findViewById(R.id.txt_setting_view);
		// 点击事件的处理在MainLauncherActivity.java里面处理
		btnToMain = (Button) findViewById(R.id.btn_setting_to_main);
		btnChangeBG = (Button) findViewById(R.id.btn_change_background);
		btnAbout = (Button) findViewById(R.id.btn_about);

		btnChangeBG.setOnClickListener(this);
		btnAbout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_about:
			startAboutActivity();
			break;
		case R.id.btn_change_background:
			startChangeBGActivity();
			break;

		default:
			break;
		}
	}

	private void startChangeBGActivity() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(mContext, ChangeBGActivity.class);
		mContext.startActivity(intent);
	}

	private void startAboutActivity() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(mContext, AboutMusicActivity.class);
		mContext.startActivity(intent);
	}

	public void onResume() {
		/*
		 * showDownloadPath(); showCacheSize();
		 */
	}
}
