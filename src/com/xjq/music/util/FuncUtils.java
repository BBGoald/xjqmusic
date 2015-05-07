package com.xjq.music.util;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;

/**
 * 动画辅助类
 * 
 * @author root
 * 
 */
public class FuncUtils {

	private static final String TAG = "xjq";
	private static final boolean DEBUG = true;

	// 点击播放，开始动画，这里主要是获取动画的当前位置以及计算结束位置
	public static void beginClickAnimation(Activity activity, View objectView,
			int animViewImageResourceId) {
		if (DEBUG)
			Log.i(TAG, "	--->FuncUtils--->beginClickAnimation");

		int[] location = new int[2];
		// 获取点击歌曲时动画的初始位置
		if (DEBUG)
			Log.i(TAG, "	--->FuncUtils--->beginClickAnimation ###location[0]= "
					+ location[0] + " ###locatio[1]= " + location[1]);
		objectView.getLocationOnScreen(location);

		DefaultPerfectScreen mDefaultPerfectScreen = new DefaultPerfectScreen(
				activity);
		// 计算动画结束位置
		int endX = mDefaultPerfectScreen.getWidth() / 2 - location[0];
		int endY = mDefaultPerfectScreen.getHeight() - location[1];
		if (0 > endX) {
			endX = mDefaultPerfectScreen.getWidth() - location[0];
		}
		if (DEBUG)
			Log.i(TAG, "	--->FuncUtils--->beginClickAnimation ######endX= "
					+ endX + " ######endX=" + endY);

		ImageView animview = findAnimImageView(activity,
				animViewImageResourceId);
		configAnimView(animview, objectView, location);
		setAnimtionAndStart(activity, animview, endX, endY);
	}

	// 设置播放音乐的动画，包括在哪个界面出现（这里自然是LocatMusicListActivity了），动画对象animview，初始和结束位置。
	private static void setAnimtionAndStart(Activity activity,
			final ImageView animview, int endX, int endY) {
		// TODO Auto-generated method stub
		// 动画设置对象，用来保存动画对象animview的设置参数
		AnimationSet set = new AnimationSet(false);

		// 从动画开始到结束X轴的变化速率（匀速）
		TranslateAnimation translateAnimationX = new TranslateAnimation(0,
				endX, 0, 0);
		translateAnimationX.setInterpolator(new LinearInterpolator());
		translateAnimationX.setRepeatCount(0);

		// 从动画开始到結束Y轴的变化速率（自由落体变化）
		TranslateAnimation translateAnimationY = new TranslateAnimation(0, 0,
				0, endY);
		translateAnimationY.setInterpolator(new AccelerateInterpolator());
		translateAnimationY.setRepeatCount(0);

		// 将设置好的参数放进动画参数对象set中
		set.addAnimation(translateAnimationY);
		set.addAnimation(translateAnimationX);
		set.setDuration(500);
		// 动画监听器，监听动画的开始与结束动作
		set.setAnimationListener(new AnimationListener() {

			// 开始动画时显示动画对象animview
			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
				animview.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub

			}

			// 结束时隐藏动画对象animview
			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				animview.setVisibility(View.GONE);
			}
		});
		// 开始绘制动画对象animview
		animview.startAnimation(set);
	}

	// 配置动画效果
	private static void configAnimView(ImageView animview, View objectView,
			int[] location) {
		// TODO Auto-generated method stub
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		int localx = location[0];
		int localy = location[1];
		if (DEBUG)
			Log.i(TAG, "	--->FuncUtils--->configAnimView ###localx= " + localx
					+ " ###localy= " + localy
					+ " ###objectView.getHeight()/2= " + objectView.getHeight()
					/ 2);
		params.leftMargin = localx;
		params.topMargin = localy - objectView.getHeight() / 2;
		animview.setLayoutParams(params);
	}

	// 准备动画
	private static ImageView findAnimImageView(Activity activity,
			int animViewImageResourceId) {
		// TODO Auto-generated method stub
		FrameLayout frameLayout = (FrameLayout) activity
				.findViewById(android.R.id.content);
		Object object = "AnimReferView";
		ImageView iv = null;
		for (int i = 0; i < frameLayout.getChildCount(); i++) {
			View v = frameLayout.getChildAt(i);
			if (v instanceof ImageView) {
				if (null != v.getTag() && v.getTag().equals(object)) {
					v.clearAnimation();
					frameLayout.removeView(v);
					break;
				}
			}
		}
		if (null == iv) {
			iv = new ImageView(activity);
			iv.setTag(object);
		}
		frameLayout.addView(iv);
		iv.setImageResource(animViewImageResourceId);
		return iv;
	}

}
