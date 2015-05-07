package com.xjq.music.activity;

import java.util.List;

import com.xjq.music.setbg.ChildAdapter;
import com.xjq.music.setbg.SkinUtil;
import com.xjq.xjqgraduateplayer.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
/**
 * 背景图片列表界面，在这里点击某张图片将其设置成背景
 * @author root
 *
 */
public class ShowImageActivity extends Activity {
	private static final boolean DEBUG = false;
	private static final String TAG = "xjq";
	private GridView mGridView;
	private List<String> list;
	private ChildAdapter adapter;
	public static String pathString;

	private RelativeLayout relaytiveLayoutShowBG;
	@SuppressWarnings("unused")
	private LinearLayout linearLayoutCheck;
	@SuppressWarnings("unused")
	private CheckBox checkBox;
	Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_image);

		mContext = ShowImageActivity.this;

		relaytiveLayoutShowBG = (RelativeLayout) findViewById(R.id.show_bg);
		linearLayoutCheck = (LinearLayout) findViewById(R.id.layout_check);
		checkBox = (CheckBox) findViewById(R.id.child_img);

		mGridView = (GridView) findViewById(R.id.child_grid);
		list = getIntent().getStringArrayListExtra("data");
		adapter = new ChildAdapter(this, list, mGridView);
		adapter.setOpenAnimation(false);
		mGridView.setAdapter(adapter);

		//点击某张图片即可将该图片设置为背景
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (DEBUG)
					Log.i(TAG, "	--->ShowImageActivity--->onItemClick");
				// ImageBean imageBean = imgList.get(position);
				pathString = list.get(position);
				if (DEBUG)
					Log.i(TAG,
							"	--->ShowImageActivity--->onItemClick ###pathString= "
									+ pathString);
				//将选择的图片设置进系统
				SkinUtil.setSelectedImg(mContext, pathString);
				//获取已经设置进系统的图片并显示出来
				loadBackGround();
				adapter.setOpenAnimation(true);
				//linearLayoutCheck.setVisibility(View.VISIBLE);
			}
		});
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (DEBUG)
			Log.i(TAG, "	--->ShowImageActivity--->onStart");
		SkinUtil.getSelectedImg(ShowImageActivity.this);
		loadBackGround();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		if (DEBUG)
			Log.i(TAG, "	--->ShowImageActivity--->onRestart");
		SkinUtil.getSelectedImg(ShowImageActivity.this);
		loadBackGround();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (DEBUG)
			Log.i(TAG, "	--->ShowImageActivity--->onResume");
		SkinUtil.getSelectedImg(ShowImageActivity.this);
		loadBackGround();
	}

	//获取已经设置进系统的图片并显示出来
	@SuppressLint("NewApi")
	protected void loadBackGround() {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.i(TAG, "	--->ShowImageActivity--->loadBackGround");
		String urlString = SkinUtil.getSelectedImg(this);
		if (urlString.equals("")) {
			relaytiveLayoutShowBG.setBackgroundResource(R.drawable.warm);
			return;
		}
		Drawable drawable = SkinUtil.getDrawble(mContext, urlString, false);
		if (drawable != null) {
			if (DEBUG)
				Log.i(TAG, "	--->ShowImageActivity--->loadBackGround");
			relaytiveLayoutShowBG.setBackground(drawable);
		}
	}

	@Override
	public void onBackPressed() {
		// Toast.makeText(this, "selected " + adapter.getSelectItems().size() +
		// " item", Toast.LENGTH_LONG).show();
		super.onBackPressed();
	}

	/*
	 * @Override public void onItemClick(AdapterView<?> parent, View view, int
	 * position, long id) { // TODO Auto-generated method stub if(DEBUG)
	 * Log.i(TAG, "	--->ShowImageActivity--->onItemClick"); ContentResolver
	 * contentResolver = ShowImageActivity.this.getContentResolver(); ImageBean
	 * imageBean = imgList.get(position); }
	 */
}
