package com.xjq.music.model;

import java.util.List;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
/**
 * MainLauncherActivity显示界面需要用到的适配器
 * 用来将MainLayout布局界面（由view_main.xml定义）以及SettingLayout布局界面（由view_setting.xml定义）显示出来
 * @author root
 *
 */
public class MyViewPaperAdapter extends PagerAdapter{
	
	public List<View> mListViews;

	public MyViewPaperAdapter(List<View> mListViews) {
		// TODO Auto-generated constructor stub
		this.mListViews = mListViews;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mListViews.size();//记得此行代码！！！！
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == (arg1);//记得此行代码！！！！
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView(mListViews.get(arg1));
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		((ViewPager) arg0).addView(mListViews.get(arg1), 0);
		return mListViews.get(arg1);
	}

	@Override
	public void finishUpdate(View arg0) {
	}
	
	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {
		// TODO Auto-generated method stub
		//super.restoreState(state, loader);
	}

	@Override
	public void startUpdate(ViewGroup container) {
		// TODO Auto-generated method stub
		//super.startUpdate(container);
	}
}
