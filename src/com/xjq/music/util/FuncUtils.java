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

public class FuncUtils {

	private static final String TAG = "xjq";
	
	public static void beginClickAnimation(Activity activity,View objectView, int animViewImageResourceId) {
		Log.i(TAG, "	--->FuncUtils--->beginClickAnimation");

		int[] location = new int[2];
		objectView.getLocationOnScreen(location);
		
		DefaultPerfectScreen mDefaultPerfectScreen = new DefaultPerfectScreen(activity);
		//Log.i(TAG, "	--->FuncUtils--->beginClickAnimation ######endX= " + size.x);
		int endX = mDefaultPerfectScreen.getWidth() / 2 - location[0];
		int endY = mDefaultPerfectScreen.getHeight() - location[1];
		if (0 > endX) {
			endX = mDefaultPerfectScreen.getWidth() - location[0];
		}
		
		ImageView animview = findAnimImageView(activity, animViewImageResourceId);
		configAnimView(animview, objectView, location);
		setAnimtion(activity, animview, endX, endY);
	}

	private static void setAnimtion(Activity activity, final ImageView animview,
			int endX, int endY) {
		// TODO Auto-generated method stub
		AnimationSet set = new AnimationSet(false);
		
		TranslateAnimation translateAnimationX = new TranslateAnimation(0, endX, 0, 0);
		translateAnimationX.setInterpolator(new LinearInterpolator());
		translateAnimationX.setRepeatCount(0);

		TranslateAnimation translateAnimationY = new TranslateAnimation(0, 0,  0, endY);
		translateAnimationY.setInterpolator(new AccelerateInterpolator());
		translateAnimationY.setRepeatCount(0);
		
		set.addAnimation(translateAnimationY);
		set.addAnimation(translateAnimationX);
		set.setDuration(500);
		set.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
				animview.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				animview.setVisibility(View.GONE);
			}
		});
		animview.startAnimation(set);
	}

	private static void configAnimView(ImageView animview, View objectView,
			int[] location) {
		// TODO Auto-generated method stub
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		int localx = location[0];
		int localy = location[1];
		params.leftMargin = localx;
		params.topMargin = localy - objectView.getHeight() / 2;
		animview.setLayoutParams(params);
	}

	private static ImageView findAnimImageView(Activity activity,
			int animViewImageResourceId) {
		// TODO Auto-generated method stub
		FrameLayout frameLayout = (FrameLayout) activity.findViewById(android.R.id.content);
		Object object = "AnimReferView";
		ImageView iv = null;
		for (int i = 0; i < frameLayout.getChildCount(); i++) {
			android.view.View v = frameLayout.getChildAt(i);
			if (v instanceof android.widget.ImageView) {
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
