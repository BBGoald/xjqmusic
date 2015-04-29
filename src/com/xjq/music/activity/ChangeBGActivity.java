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
 * @blog http://blog.csdn.net/xiaanming
 * 
 * @author xiaanming
 * 
 * 
 */
public class ChangeBGActivity extends Activity {
	private HashMap<String, List<String>> mGruopMap = new HashMap<String, List<String>>();
	private List<ImageBean> list = new ArrayList<ImageBean>();
	private final static int SCAN_OK = 1;
	protected static final String TAG = "mainactivity";
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
				// ����������
				mProgressDialog.dismiss();

				if (DEBUG)
					Log.i(TAG, "	--->list= " + subGroupOfImage(mGruopMap)
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
		setContentView(R.layout.activity_set_background);
		mContext = ChangeBGActivity.this;

		mGroupGridView = (GridView) findViewById(R.id.main_grid);
		relativeLayoutSetbg = (RelativeLayout) findViewById(R.id.lay_setbg);

		getImages();

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
				startActivity(mIntent);

			}
		});

	}

	/**
	 * ����ContentProvider����������������������������������������
	 */
	private void getImages() {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "������������", Toast.LENGTH_SHORT).show();
			return;
		}

		// ����������
		mProgressDialog = ProgressDialog.show(this, null, "��������...");

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
				// ������jpeg��png������
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
/*				Cursor mCursor = resolver.query(mImageUri, null, null, null,
						order);
*/				
				Cursor mCursor = resolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or " +
						MediaStore.Images.Media.MIME_TYPE + "=?", 
						new String[] {"image/jpeg", "image/png" },
		order);

				if (DEBUG)
					Log.i(TAG,
							"	--->mCursor= " + mCursor
									+ "	--->mCursor.moveToNext()= "
									+ mCursor.moveToNext());
				while (mCursor.moveToNext()) {
					// if(DEBUG) Log.i(TAG, "	--->= ");
					// ��������������
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					if (DEBUG)
						Log.i(TAG, "	--->path= " + path);

					// ��������������������
					String parentName = new File(path).getParentFile()
							.getName();
					if (DEBUG)
						Log.i(TAG, "	--->parentName= " + parentName);

					// ������������������������mGruopMap��
					if (!mGruopMap.containsKey(parentName)) {
						List<String> chileList = new ArrayList<String>();
						chileList.add(path);
						mGruopMap.put(parentName, chileList);
					} else {
						mGruopMap.get(parentName).add(path);
					}
				}

				mCursor.close();

				// ����Handler������������
				mHandler.sendEmptyMessage(SCAN_OK);

			}
		}).start();

	}

	/**
	 * ������������GridView����������������������������������������������HashMap
	 * �� ������������HashMap������������List
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
			mImageBean.setTopImagePath(value.get(0));// ��������������������

			list.add(mImageBean);
		}

		return list;

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (DEBUG)
			Log.i(TAG, "	--->ShowImageActivity--->onStart");
		SkinUtil.getSelectedImg(mContext);
		loadBG();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		if (DEBUG)
			Log.i(TAG, "	--->ShowImageActivity--->onRestart");
		SkinUtil.getSelectedImg(mContext);
		loadBG();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (DEBUG)
			Log.i(TAG, "	--->ShowImageActivity--->onResume");
		SkinUtil.getSelectedImg(mContext);
		loadBG();
	}

	@SuppressLint("NewApi")
	private void loadBG() {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.i(TAG, "	--->ShowImageActivity--->loadBackGround");
		String urlString = SkinUtil.getSelectedImg(mContext);
		if (urlString.equals("")) {
			relativeLayoutSetbg.setBackgroundResource(R.drawable.warm);
			return;
		}
		Drawable drawable = SkinUtil.getDrawble(mContext, urlString, false);
		if (drawable != null) {
			if (DEBUG)
				Log.i(TAG, "	--->ShowImageActivity--->loadBackGround");
			relativeLayoutSetbg.setBackground(drawable);
		}
	}
}
