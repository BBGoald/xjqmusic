package com.xjq.music.activity;

import com.xjq.xjqgraduateplayer.R;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainLayout extends FrameLayout implements OnClickListener{
	
	private static final String TAG = "xjq";
	private Context mContext;
	View view;
	
	Button btnToMore;
	Button btnToMusicList;
	
	LinearLayout linearLayoutLocal;
	LinearLayout linearLayoutLocal1;
	ImageView imgLine;
	
	public MainLayout(Context context) {
		super(context);
		Log.d(TAG, "******instance MainLayout");
		// TODO Auto-generated constructor stub
		mContext = context;
		LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = layoutInflater.inflate(R.layout.main_view, this);
		
		btnToMusicList = (Button) findViewById(R.id.btn_to_musiclist);
		btnToMore = (Button) findViewById(R.id.btn_to_more);
		
		linearLayoutLocal = (LinearLayout) view.findViewById(R.id.btn_local);
		linearLayoutLocal1 = (LinearLayout) view.findViewById(R.id.btn_local1);
		imgLine = (ImageView) view.findViewById(R.id.img_line);
		btnToMusicList.setOnClickListener(this);
		linearLayoutLocal.setOnClickListener(this);
		linearLayoutLocal1.setOnClickListener(this);
	}
	
	public void onClick(View view) {
		Intent intent = new Intent();
		if (view.getId() == R.id.btn_to_musiclist) {
			intent.setClass(mContext, LocalMusicListActivity.class);
			mContext.startActivity(intent);
		}
	}

	public void onResume() {
		// TODO Auto-generated method stub
		
	}
}
