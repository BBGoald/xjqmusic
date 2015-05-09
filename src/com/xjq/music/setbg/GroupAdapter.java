package com.xjq.music.setbg;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.xjq.music.setbg.MyImageView.OnMeasureListener;
import com.xjq.music.setbg.NativeImageLoader.NativeImageCallBack;
import com.xjq.xjqgraduateplayer.R;

public class GroupAdapter extends BaseAdapter {
	private static final boolean DEBUG = true;
	private static final String TAG = "xjq";
	private List<ImageBean> list;
	private Point mPoint = new Point(0, 0);// 用来封装ImageView的宽和高的对象
	private GridView mGridView;
	protected LayoutInflater mInflater;

	@Override
	public int getCount() {
		if (DEBUG)
			Log.i(TAG,
					"	--->GroupAdapter--->getCount ###list.size()= "
							+ list.size());
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		if (DEBUG)
			Log.i(TAG, "	--->GroupAdapter--->getItem ###list.get(position)= "
					+ list.get(position));
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		if (DEBUG)
			Log.i(TAG, "	--->GroupAdapter--->getItemId ###position= "
					+ position);
		return position;
	}

	public GroupAdapter(Context context, List<ImageBean> list,
			GridView mGridView) {
		if (DEBUG)
			Log.i(TAG, "	--->GroupAdapter");
		this.list = list;
		this.mGridView = mGridView;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (DEBUG)
			Log.i(TAG,
					"	--->GroupAdapter--->getView ###currentThread().getId()= "
							+ Thread.currentThread().getId() + " ###position= "
							+ position + " ###parent= " + parent);
		final ViewHolder viewHolder;
		ImageBean mImageBean = list.get(position);
		String path = mImageBean.getTopImagePath();
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_grid_group, null);
			viewHolder.mImageView = (MyImageView) convertView
					.findViewById(R.id.group_image);
			viewHolder.mTextViewTitle = (TextView) convertView
					.findViewById(R.id.group_title);
			viewHolder.mTextViewCounts = (TextView) convertView
					.findViewById(R.id.group_count);

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

		viewHolder.mTextViewTitle.setText(mImageBean.getFolderName());
		viewHolder.mTextViewCounts.setText(Integer.toString(mImageBean
				.getImageCounts()));
		// 给ImageView设置路径Tag,这是异步加载图片的小技巧
		viewHolder.mImageView.setTag(path);

		// 利用NativeImageLoader类加载本地图片
		Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path,
				mPoint, new NativeImageCallBack() {

					@Override
					public void onImageLoader(Bitmap bitmap, String path) {
						if (DEBUG)
							Log.i(TAG,
									"	--->GroupAdapter--->getView--->onImageLoader");
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

		return convertView;
	}

	public static class ViewHolder {
		public MyImageView mImageView;
		public TextView mTextViewTitle;
		public TextView mTextViewCounts;
	}

}
