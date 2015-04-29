package com.xjq.music.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import com.xjq.music.setbg.SkinUtil;
import com.xjq.xjqgraduateplayer.R;

;

public class AboutMusicActivity extends Activity {

	private static final boolean DEBUG = false;
	private static final String TAG = null;
	private RelativeLayout relativeLayoutAbout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		relativeLayoutAbout = (RelativeLayout) findViewById(R.id.lay_about);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SkinUtil.getSelectedImg(AboutMusicActivity.this);
		loadBG();
	}

	@SuppressLint("NewApi")
	private void loadBG() {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.i(TAG, "	--->ShowImageActivity--->loadBackGround");
		String urlString = SkinUtil.getSelectedImg(this);
		if (urlString.equals("")) {
			relativeLayoutAbout.setBackgroundResource(R.drawable.warm);
			return;
		}
		Drawable drawable = SkinUtil.getDrawble(AboutMusicActivity.this,
				urlString, false);
		if (drawable != null) {
			if (DEBUG)
				Log.i(TAG, "	--->ShowImageActivity--->loadBackGround");
			relativeLayoutAbout.setBackground(drawable);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
