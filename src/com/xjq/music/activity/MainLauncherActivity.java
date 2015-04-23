package com.xjq.music.activity;

import java.util.ArrayList;
import java.util.List;

import com.xjq.music.model.MyViewPaperAdapter;
import com.xjq.xjqgraduateplayer.R;

import android.os.Bundle;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
/**
 * 整个应用的入口activity，主界面显示，包含view_main以及view_settins两个可以左右滑动的界面，
 * 分别由view_main.xml和view_setting.xml这两个布局文件定义
 * @author root
 *
 */
public class MainLauncherActivity extends BaseActivity {
	
	private static final String TAG = "bangliang";
	private ViewPager viewPager;
	private List<View> listViews;
	
	//private Button mButton;
	
	MainLayout mainLayout;//不能设置为私有类型private MainLayout mainLayout;？？？
	SettingLayout settingLayout;
	Context mcontext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "******instance MainLauncherActivity--->onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_launcher);
		mcontext = MainLauncherActivity.this;
		initViewPaper();
	}

	private void initViewPaper() {
		// TODO Auto-generated method stub
		setTitle("主界面");

		viewPager = (ViewPager) findViewById(R.id.vPager);
		listViews = new ArrayList<View>();

		//mButton = (Button) findViewById(R.id.btn_main_lancher);
		
/*		mainLayout = new MainLayout(context);
		settingLayout = new SettingLayout(context);*/
		mainLayout = new MainLayout(this);
		settingLayout = new SettingLayout(this);

		listViews.add(settingLayout);
		listViews.add(mainLayout);
		//Log.i(TAG, "listViews= " + listViews + " mainLayout= " + mainLayout + " settingLayout= " + settingLayout);
		viewPager.setBackgroundResource(R.drawable.warm);
		viewPager.setAdapter(new MyViewPaperAdapter(listViews));
		viewPager.setCurrentItem(1);//mainLayout=1,settingLayout=0
		mainLayout.btnToMore.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "	--->mainlyaout.btnToMore.setOnClickListener");
				//第一次进入看到的是view_main.xml布局文件定义的界面
				viewPager.setCurrentItem(0);
			}
		});
		settingLayout.btnToMain.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(TAG, "	--->mainlyaout.btnToMain.setOnClickListener");
				//向右滑动看的是view_settins.xml布局文件定义的界面
				viewPager.setCurrentItem(1);
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		settingLayout.onResume();
		mainLayout.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_launcher, menu);
		return true;
	}
}
