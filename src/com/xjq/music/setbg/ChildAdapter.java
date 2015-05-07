package com.xjq.music.setbg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.xjq.music.setbg.MyImageView.OnMeasureListener;
import com.xjq.music.setbg.NativeImageLoader.NativeImageCallBack;
import com.xjq.xjqgraduateplayer.R;

public class ChildAdapter extends BaseAdapter {
	private static final boolean DEBUG = true;
	private static final String TAG = "xjq";
	private boolean isOpenAnimation = false;
	private Point mPoint = new Point(0, 0);// 对ImageView进行绘制
	/**
	 * 列出子文件夹下的所有图片
	 */
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();
	private GridView mGridView;
	private List<String> list;
	protected LayoutInflater mInflater;

	public ChildAdapter(Context context, List<String> list, GridView mGridView) {
		if (DEBUG)
			Log.i(TAG, "	--->ChildAdapter");
		this.list = list;
		this.mGridView = mGridView;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		if (DEBUG)
			Log.i(TAG,
					"	--->ChildAdapter--->getCount ###list.size()= "
							+ list.size());
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		if (DEBUG)
			Log.i(TAG, "	--->ChildAdapter--->getItem ###list.get(position)= "
					+ list.get(position));
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		if (DEBUG)
			Log.i(TAG, "	--->ChildAdapter--->getItemId ###position= "
					+ position);
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		String path = list.get(position);

		if (DEBUG)
			Log.i(TAG, "	--->ChildAdapter--->getView");
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_grid_child, null);
			viewHolder = new ViewHolder();
			viewHolder.mImageView = (MyImageView) convertView
					.findViewById(R.id.child_image);
			/*
			 * viewHolder.mCheckBox = (CheckBox) convertView
			 * .findViewById(R.id.child_checkbox);
			 */
			viewHolder.mCheckBox = (CheckBox) convertView
					.findViewById(R.id.child_img);

			// 用来监听ImageView的宽和高
			viewHolder.mImageView.setOnMeasureListener(new OnMeasureListener() {

				@Override
				public void onMeasureSize(int width, int height) {
					mPoint.set(width, height);
				}
			});

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.mImageView
					.setImageResource(R.drawable.friends_sends_pictures_no);
		}
		viewHolder.mImageView.setTag(path);
		viewHolder.mCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// 如果是未选中的CheckBox,则添加动画
						if (!mSelectMap.containsKey(position)
								|| !mSelectMap.get(position)) {
							if (getOpenAnimation()) {
								addAnimation(viewHolder.mCheckBox);
							}
						}
						mSelectMap.put(position, isChecked);
					}
				});

		viewHolder.mCheckBox
				.setChecked(mSelectMap.containsKey(position) ? mSelectMap
						.get(position) : false);

		// 利用NativeImageLoader类加载本地图片
		Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path,
				mPoint, new NativeImageCallBack() {

					@Override
					public void onImageLoader(Bitmap bitmap, String path) {
						ImageView mImageView = (ImageView) mGridView
								.findViewWithTag(path);
						if (bitmap != null && mImageView != null) {
							mImageView.setImageBitmap(bitmap);
						}
					}
				});

		if (bitmap != null) {
			viewHolder.mImageView.setImageBitmap(bitmap);
		} else {
			viewHolder.mImageView
					.setImageResource(R.drawable.friends_sends_pictures_no);
		}
		if (DEBUG)
			Log.i(TAG, "	--->ChildAdapter--->getView ###isOpenAnimation= "
					+ isOpenAnimation);
		if (isOpenAnimation) {
			viewHolder.mCheckBox.setVisibility(View.VISIBLE);
		}

		return convertView;
	}

	public void setOpenAnimation(Boolean isOpen) {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.i(TAG, "	--->ChildAdapter--->setOpenAnimation ###isOpen= "
					+ isOpen);
		this.isOpenAnimation = isOpen;
		notifyDataSetChanged();// notify the holderView to refresh itself...
	}

	protected boolean getOpenAnimation() {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.i(TAG,
					"	--->ChildAdapter--->getOpenAnimation ###isOpenAnimation= "
							+ isOpenAnimation);
		return isOpenAnimation;
	}

	/**
	 * 给CheckBox加点击动画，利用开源库nineoldandroids设置动画
	 * 
	 * @param view
	 */
	private void addAnimation(View view) {
		if (DEBUG)
			Log.i(TAG, "	--->ChildAdapter--->addAnimation");
		float[] vaules = new float[] { 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f,
				1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f };
		AnimatorSet set = new AnimatorSet();
		set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules),
				ObjectAnimator.ofFloat(view, "scaleY", vaules));
		set.setDuration(150);
		set.start();
	}

	/**
	 * 获取选中的Item的position
	 * 
	 * @return
	 */
	public List<Integer> getSelectItems() {
		if (DEBUG)
			Log.i(TAG, "	--->ChildAdapter--->getSelectItems");
		List<Integer> list = new ArrayList<Integer>();
		for (Iterator<Map.Entry<Integer, Boolean>> it = mSelectMap.entrySet()
				.iterator(); it.hasNext();) {
			Map.Entry<Integer, Boolean> entry = it.next();
			if (entry.getValue()) {
				list.add(entry.getKey());
			}
		}

		return list;
	}

	public static class ViewHolder {
		public MyImageView mImageView;
		public CheckBox mCheckBox;
	}

}
