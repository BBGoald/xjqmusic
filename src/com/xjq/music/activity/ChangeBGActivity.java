package com.xjq.music.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.xjq.music.setbg.GroupAdapter;
import com.xjq.music.setbg.ImageBean;
import com.xjq.music.setbg.SkinUtil;
import com.xjq.xjqgraduateplayer.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

/**
 * 首次进入设置背景时的界面
 * 
 */
public class ChangeBGActivity extends Activity {
	private HashMap<String, List<String>> mGruopMap = new HashMap<String, List<String>>();
	private List<ImageBean> list = new ArrayList<ImageBean>();
	private final static int SCAN_OK = 1;
	protected static final String TAG = "xjq";
	protected static final boolean DEBUG = false;
	private ProgressDialog mProgressDialog;
	private GroupAdapter adapter;

	private RelativeLayout relativeLayoutSetbg;
	private GridView mGroupGridView;
	Context mContext;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SCAN_OK:
				// 关闭进度条
				mProgressDialog.dismiss();

				if (DEBUG)
					Log.i(TAG, "	--->ChangeBGActivity--->list= " + subGroupOfImage(mGruopMap)
							+ "	--->mGruopMap= " + mGruopMap);
				adapter = new GroupAdapter(ChangeBGActivity.this,
						list = subGroupOfImage(mGruopMap), mGroupGridView);
				mGroupGridView.setAdapter(adapter);
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(DEBUG) Log.i(TAG, "	--->ChangeBGActivity--->onCreate ###currentThread().getId()= "
							+ Thread.currentThread().getId());
		setContentView(R.layout.activity_set_background);
		mContext = ChangeBGActivity.this;

		mGroupGridView = (GridView) findViewById(R.id.main_grid);
		relativeLayoutSetbg = (RelativeLayout) findViewById(R.id.lay_setbg);

		getImages();

		//点击文件夹
		mGroupGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				List<String> childList = mGruopMap.get(list.get(position)
						.getFolderName());

				Intent mIntent = new Intent(ChangeBGActivity.this,
						ShowImageActivity.class);
				mIntent.putStringArrayListExtra("data",
						(ArrayList<String>) childList);
				//进入图片列表界面
				startActivity(mIntent);

			}
		});

	}

	/**
	 * 开启新线程，利用ContentProvider扫描手机外置存储器中的图片
	 */
	private void getImages() {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "没有SD卡", Toast.LENGTH_SHORT).show();
			return;
		}

		// 显示进度条
		mProgressDialog = ProgressDialog.show(this, null, "正在加载...");

		new Thread(new Runnable() {

			@Override
			public void run() {
				/*
				 * ContentResolver mContentResolver =
				 * mContext.getContentResolver(); Uri mImageUri =
				 * MediaStore.Images.Media.EXTERNAL_CONTENT_URI; String
				 * sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " desc";
				 * Log.i(TAG, "	--->mContentResolver= " + mContentResolver +
				 * "	--->mImageUri= " + mImageUri + "	--->sortOrder= " +
				 * sortOrder);
				 */
				// 只查询jpeg和png的图片
				/*
				 * cannot filter Cursor mCursor =
				 * mContentResolver.query(mImageUri, null, null, null,
				 * sortOrder);
				 */

				/*
				 * cannot filter MediaStore.Images.Media.EXTERNAL_CONTENT_URI
				 * Cursor mCursor = mContentResolver.query(mImageUri, null,
				 * MediaStore.Images.Media.MIME_TYPE + "=? or " +
				 * MediaStore.Images.Media.MIME_TYPE + "=?", new String[] {
				 * "image/jpg", "image/png" },
				 * MediaStore.Images.Media.DATE_MODIFIED);
				 */
				ContentResolver resolver = mContext.getContentResolver();
				String order = MediaStore.Images.Media.DEFAULT_SORT_ORDER;
				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				/*
				 * Cursor mCursor = resolver.query(mImageUri, null, null, null,
				 * order);
				 */
				Cursor mCursor = resolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png" }, order);

				if (DEBUG)
					Log.i(TAG,
							"	--->ChangeBGActivity--->getImages--->mCursor= " + mCursor
									+ "	--->mCursor.moveToNext()= "
									+ mCursor.moveToNext());
				while (mCursor.moveToNext()) {
					// if(DEBUG) Log.i(TAG, "	--->= ");
					// 获取图片的路径
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					if (DEBUG)
						Log.i(TAG, "	--->ChangeBGActivity--->getImages--->path= " + path);

					// 获取该图片的父文件夹
					String parentName = new File(path).getParentFile()
							.getName();
					if (DEBUG)
						Log.i(TAG, "	--->ChangeBGActivity--->getImages--->parentName= " + parentName);

					// 以父路径名为键名，以该图片的路径为键值放进键值对mGruopMap中
					if (!mGruopMap.containsKey(parentName)) {
						List<String> chileList = new ArrayList<String>();
						chileList.add(path);
						mGruopMap.put(parentName, chileList);
					} else {
						mGruopMap.get(parentName).add(path);
					}
				}

				mCursor.close();

				// 通知扫描完成
				mHandler.sendEmptyMessage(SCAN_OK);

			}
		}).start();

	}

	/**
	 * 组装分组界面GridView的数据源，因为我们扫描手机的时候将图片信息放在HashMap中 所以需要遍历HashMap将数据组装成List
	 * 
	 * @param mGruopMap
	 * @return
	 */
	private List<ImageBean> subGroupOfImage(
			HashMap<String, List<String>> mGruopMap) {
		if (mGruopMap.size() == 0) {
			return null;
		}
		List<ImageBean> list = new ArrayList<ImageBean>();

		Iterator<Map.Entry<String, List<String>>> it = mGruopMap.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry<String, List<String>> entry = it.next();
			ImageBean mImageBean = new ImageBean();
			String key = entry.getKey();
			List<String> value = entry.getValue();

			mImageBean.setFolderName(key);
			mImageBean.setImageCounts(value.size());
			mImageBean.setTopImagePath(value.get(0));// 获取该组的第一张图片

			list.add(mImageBean);
		}

		return list;

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (DEBUG)
			Log.i(TAG, "	--->ChangeBGActivity--->onStart");
		SkinUtil.getSelectedImg(mContext);
		loadBG();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		if (DEBUG)
			Log.i(TAG, "	--->ChangeBGActivity--->onRestart");
		SkinUtil.getSelectedImg(mContext);
		loadBG();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (DEBUG)
			Log.i(TAG, "	--->ChangeBGActivity--->onResume");
		SkinUtil.getSelectedImg(mContext);
		loadBG();
	}

	@SuppressLint("NewApi")
	private void loadBG() {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.i(TAG, "	--->ChangeBGActivity--->loadBackGround");
		String urlString = SkinUtil.getSelectedImg(mContext);
		if (urlString.equals("")) {
			relativeLayoutSetbg.setBackgroundResource(R.drawable.warm);
			return;
		}
		Drawable drawable = SkinUtil.getDrawble(mContext, urlString, false);
		if (drawable != null) {
			if (DEBUG)
				Log.i(TAG, "	--->ChangeBGActivity--->loadBackGround");
			relativeLayoutSetbg.setBackground(drawable);
		}
	}
}
