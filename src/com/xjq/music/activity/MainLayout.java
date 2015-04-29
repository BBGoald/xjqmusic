package com.xjq.music.activity;

import com.xjq.xjqgraduateplayer.R;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 主界面activity加载的main_view,加载了布局文件view_main.xml
 * 
 * @author root
 * 
 */
public class MainLayout extends FrameLayout implements OnClickListener {

	private static final String TAG = "xjq";
	private Context mContext;
	View view;

	Button btnToMore;
	Button btnToMusicList;

	LinearLayout linearLayoutLocal;
	LinearLayout linearLayoutLocal1;
	LinearLayout linearLayoutLib;
	ImageView imgLine;

	public MainLayout(Context context) {
		super(context);
		Log.d(TAG, "******instance MainLayout");
		// TODO Auto-generated constructor stub
		mContext = context;
		LayoutInflater layoutInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = layoutInflater.inflate(R.layout.view_main, this);

		btnToMusicList = (Button) findViewById(R.id.btn_to_musiclist);
		btnToMore = (Button) findViewById(R.id.btn_to_more);

		linearLayoutLocal = (LinearLayout) view.findViewById(R.id.btn_local);
		linearLayoutLocal1 = (LinearLayout) view.findViewById(R.id.btn_local1);
		linearLayoutLib = (LinearLayout) view.findViewById(R.id.btn_musiclib);
		imgLine = (ImageView) view.findViewById(R.id.img_line);
		btnToMusicList.setOnClickListener(this);
		linearLayoutLocal.setOnClickListener(this);
		linearLayoutLocal1.setOnClickListener(this);
		linearLayoutLib.setOnClickListener(this);
	}

	// 点击事件的监听
	public void onClick(View view) {
		Intent intent = new Intent();
		if (view.getId() == R.id.btn_to_musiclist) {
			intent.setClass(mContext, LocalMusicListActivity.class);
			mContext.startActivity(intent);
		}
		if (view.getId() == R.id.btn_musiclib) {
			intent.setClass(mContext, LocalMusicListActivity.class);
			mContext.startActivity(intent);
		}
	}

	public void onResume() {
		// TODO Auto-generated method stub

	}
}
