package com.xjq.music.activity;

import java.util.ArrayList;
import java.util.List;

import com.xjq.music.model.MusicInfomation;
import com.xjq.music.player.IOnServiceConnectComplete;
import com.xjq.music.player.MusicPlayState;
import com.xjq.music.player.MusicPlayer;
import com.xjq.music.player.MusicPlayerHelper;
import com.xjq.music.player.MusicServiceManager;
import com.xjq.music.util.DatabaseHelper;
import com.xjq.music.util.ProgressTimer;
import com.xjq.xjqgraduateplayer.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 基类，每个活动窗口（activity）底部的播放界面都是从此类中派生出来的。
 * 服务的连接也是在这里
 * @author root
 *
 */
public class BaseActivity extends Activity{

	private static final String TAG = "xjq";
	
	private View mBottomView;
	
	private ProgressTimer mMusicTimer; // 检测歌曲进度的定时器
	private ProgressBar progressbar;
	
	private final int playNextDelay = 10000;
	private final int event_update_progressbar = playNextDelay + 1;

	protected DatabaseHelper dbHelper;
	
	protected MusicServiceManager mServiceManager;
	private MusicBroadcastReceiver mBroadcastReceiver;// 广播接收事件

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.d(TAG, "******instance BaseActivity--->onCreate");/*
		Log.d(TAG, "this= " + this.hashCode());
		Log.d(TAG, "BaseActivity.this= " + BaseActivity.this.hashCode());
		Log.d(TAG, "current Thread= " + Thread.currentThread().getId());
		Log.d(TAG, "current Process= " + android.os.Process.myPid());*/
		dbHelper = DatabaseHelper.instance(this);
		
		mMusicTimer = new ProgressTimer(mHandler, event_update_progressbar);
		
		mServiceManager = new MusicServiceManager(this);//MusicServiceManager contains the AIDL interface
		mServiceManager.connectService();//contains bindService
		mServiceManager.setOnServiceConnectComplete(new IOnServiceConnectComplete() {
			
			@Override
			public void OnServiceConnectComplete() {
				// TODO Auto-generated method stub
				try {
					List<MusicInfomation> list = mServiceManager.getFileList();
					Log.d(TAG, "	--->BaseActivity--->OnServiceConnectComplete ######list= " + list);
					if (list == null || list.size() == 0) {
						list = dbHelper.getHistoryList();
						mServiceManager.setPlayList(list);
						if (list != null && list.size() > 0) {
							MusicInfomation Infomation = list.get(0);
							refreshButtomPlayBar(Infomation);
						}
					} else {
						refreshButtomPlayBar();
						Log.d(TAG, "	--->BaseActivity--->OnServiceConnectComplete list!=null");
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		});
		Log.i(TAG, "	-----------------onCreate------------------end----------------");
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what == event_update_progressbar) {
				//更新底部进度条
				updateProgressBar();
			} else {
				super.handleMessage(msg);
			}
		}
		
	};
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		Log.d(TAG, "	--->BaseActivity--->onRestart");
		super.onRestart();
	}

	//更新底部进度条
	protected void updateProgressBar() {
		// TODO Auto-generated method stub
		if (null == mServiceManager || progressbar == null) {
			return;
		}
		MusicPlayerHelper.updateProgress(mServiceManager.getCurPosition(), mServiceManager.getDuration(), progressbar, null, null);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Log.d(TAG, "	--->BaseActivity--->onResume ######mBottomView= " + mBottomView);
		super.onResume();
		if (mBottomView == null) {
			Log.d(TAG, "	--->BaseActivity--->onResume=++++++++");
			InitBottomView();
		}
		if (null == mBroadcastReceiver) {
			mBroadcastReceiver = new MusicBroadcastReceiver();
			IntentFilter intentFilter = new IntentFilter(MusicPlayer.BROCAST_NAME);
			intentFilter.addAction("cn.abel.action.connect");
			registerReceiver(mBroadcastReceiver, intentFilter);
		}
		refreshButtomPlayBar();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		Log.d(TAG, "	--->BaseActivity--->onPause ######mBottomView= " + mBottomView);
		super.onPause();
		if (mBroadcastReceiver == null) {
			return;
		}
		unregisterReceiver(mBroadcastReceiver);
		mBroadcastReceiver = null;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.d(TAG, "	--->BaseActivity--->onDestroy");
		if (mBroadcastReceiver != null) {
			unregisterReceiver(mBroadcastReceiver);
		}
		if (null != mServiceManager) {
			mServiceManager.disconnectService();
		}
		mServiceManager = null;
		super.onDestroy();
	}

	//更新底部播放按钮
	private void refreshButtomPlayBar() {
		// TODO Auto-generated method stub
		Log.d(TAG, "	--->BaseActivity--->refreshButtomPlayBar()");

		try {
			MusicInfomation info = mServiceManager.getCurrentMusicInfomation();
			refreshButtomPlayBar(info);
			handleUpdatePlayTimer();
		} catch (RemoteException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	//更新底部播放按钮的同时，停止/开始计时
	private synchronized void handleUpdatePlayTimer() {
		// TODO Auto-generated method stub
		Log.d(TAG, "	--->BaseActivity--->handleUpdatePlayTimer");
		if (null == mServiceManager) {
			return;
		}
		if (null == mMusicTimer) {
			return;
		}
		if (mServiceManager.getPlayState() != MusicPlayState.MPS_PLAYING) {
			mMusicTimer.stopPlayTimer();
		} else {
			mMusicTimer.startPlayTimer();
		}
	}

	//内部类，定义一个广播接收器，接收来自服务的广播
	private class MusicBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Log.i(TAG, "	--->BaseActivity--->BroadcastReceiver");
			Log.d(TAG, "	--->--->--->--->BroadcastReceiver--->--->--->--->");
			handleReceive(intent);
		}
	}
	
	//此类（基类）控制子类的界面显示，在子类的实例中可以调用此方法并传递需要显示的xml的ID即可显示该xml
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		clickBack();
		InitBottomView();
	}

	//处理接收的广播
	public void handleReceive(Intent intent) {
		Log.i(TAG, "	--->BaseActivity--->handleReceive");
		// TODO Auto-generated method stub
		int playState = intent.getIntExtra(MusicPlayState.PLAY_STATE_NAME, MusicPlayState.MPS_NOFILE);
		switch (playState) {
		case MusicPlayState.MPS_INVALID:
		case MusicPlayState.MPS_ERROR_PLAYE:
			Log.i(TAG, "	--->BaseActivity--->handleReceive ######playState= MPS_INVALID || MPS_INVALID = " + playState);
			break;
		case MusicPlayState.MPS_PLAYING:
			Log.i(TAG, "	--->BaseActivity--->handleReceive ######playState= MPS_PLAYING = " + playState);
			Bundle tbundle2 = intent.getBundleExtra(MusicInfomation.KEY_MUSIC_INFO);
			if (tbundle2 != null) {
				refreshButtomPlayBar((MusicInfomation) tbundle2.getParcelable(MusicInfomation.KEY_MUSIC_INFO));
			}
			addHistory();
			break;
		case MusicPlayState.MPS_PREPARE:
			Log.i(TAG, "	--->BaseActivity--->handleReceive ######playState= MPS_PREPARE = " + playState);
			Bundle tbundle3 = intent.getBundleExtra(MusicInfomation.KEY_MUSIC_INFO);
			if (tbundle3 != null) {
				refreshButtomPlayBar((MusicInfomation) tbundle3.getParcelable(MusicInfomation.KEY_MUSIC_INFO));
			}
			break;
		default:
			break;
		}
	}

	//更新底部播放按钮，包括停止/播放
	private void refreshButtomPlayBar(MusicInfomation info) {
		// TODO Auto-generated method stub
		Log.d(TAG, "	--->BaseActivity--->refreshButtomPlayBar(info) ######info= " + info + " ######mBottomView= " + mBottomView);
		if (info == null) {
			return;
		}
		if (mBottomView == null) {
			//InitBottomView();
			Log.d(TAG, "	--->BaseActivity--->refreshPlayBar(info) ######mBottomView=null");
			return;
		} 
		((TextView)mBottomView.findViewById(R.id.txt_songname)).setText(info.getName());
		((TextView)mBottomView.findViewById(R.id.txt_singername)).setText(info.getArtist());
		ImageButton btnPlay = (ImageButton) mBottomView.findViewById(R.id.btn_play);
		if (mServiceManager.getPlayState() == MusicPlayState.MPS_PLAYING) {
			btnPlay.setImageResource(R.drawable.btn_play);
		} else {
			btnPlay.setImageResource(R.drawable.btn_pause);
		}
	}

	//添加到播放历史，暂未实现
	protected void addHistory() {
		// TODO Auto-generated method stub
		try {
			MusicInfomation info = mServiceManager.getCurrentMusicInfomation();
			dbHelper.addHistory(info);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//初始化底部播放界面
	private void InitBottomView() {
		// TODO Auto-generated method stub
		Log.d(TAG, "	--->BaseActivity--->InitBottomView");
		mBottomView = findViewById(R.id.layout_bottom);
		if (mBottomView == null) {
			Log.d(TAG, "	--->BaseActivity--->InitBottomView=null");
			return;
		}
		mBottomView.findViewById(R.id.btn_play).setOnClickListener(mOnClickListener);
		mBottomView.findViewById(R.id.btn_next).setOnClickListener(mOnClickListener);
		mBottomView.findViewById(R.id.btn_songimg).setOnClickListener(mOnClickListener);
		mBottomView.findViewById(R.id.lay_songinfo).setOnClickListener(mOnClickListener);
		mBottomView.findViewById(R.id.txt_songname).setOnClickListener(mOnClickListener);
		mBottomView.findViewById(R.id.txt_singername).setOnClickListener(mOnClickListener);
		progressbar = (ProgressBar) mBottomView.findViewById(R.id.base_playerbar_progress);
	}

	//底部播放界面监听器，用来监听点击事件是点击了播放/停止按钮还是下一首歌曲或者是歌曲详细信息
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			BaseActivity.this.handleClick(v);
		}
	};
	
	protected void clickBack() {
		// TODO Auto-generated method stub
		
	}

	//处理底部播放界面的点击事件
	private void handleClick(View v) {
		// TODO Auto-generated method stub
        Log.d(TAG, "	--->BaseActivity--->handleClick");
        switch (v.getId()) {
		case R.id.btn_play:
			Log.i(TAG, "	--->BaseActivity--->handleClick ######btn_play");
			if (mServiceManager.getPlayState() == MusicPlayState.MPS_PLAYING) {
				mServiceManager.pause();
				((ImageButton)v).setImageResource(R.drawable.btn_pause);
			} else if (mServiceManager.getPlayState() == MusicPlayState.MPS_PAUSE) {
				mServiceManager.rePlay();
				((ImageButton)v).setImageResource(R.id.btn_play);
			}
			break;
		case R.id.btn_next:
	        Log.d(TAG, "	--->BaseActivity--->handleClick--->btn_next");
	        if (mServiceManager != null) {
				mServiceManager.playNext();
			}
	        break;
		case R.id.lay_songinfo:
		case R.id.txt_songname:
		case R.id.txt_singername:
		case R.id.btn_songimg:
			List<MusicInfomation> playList3 = mServiceManager.getFileList();
	        Log.d(TAG, "	--->BaseActivity--->handleClick--->lay_songinfo || btn_songimg ######mServiceManager.getFileList()= " + mServiceManager.getFileList());
			jumpToPlayDetailActivity(this, playList3, -1);
			//jumpToPlayDetailActivity(this, null, -1);
			break;

		default:
	        Log.d(TAG, "	--->BaseActivity--->handleClick--->default");
			break;
		}
	}
	
	//点击了底部歌曲播放界面的歌曲名称或者歌手名称的时候进入“正在播放歌曲”界面
	public void jumpToPlayDetailActivity(Context context, List<MusicInfomation> listMusic,
			int position) {
		// TODO Auto-generated method stub
        Log.d(TAG, "	--->BaseActivity--->jumpToPlayDetailActivity ######listMusic= " + listMusic + " ######position= " + position);
		Intent intent = new Intent();
		intent.setClass(context, PlayDetailActivity.class);
		
		Bundle bundle = new Bundle();
		bundle.putParcelableArrayList(PlayDetailActivity.FLAG_PLAY_LIST, (ArrayList<? extends Parcelable>) listMusic);
		bundle.putInt(MusicPlayState.PLAY_MUSIC_INDEX, position);
		
		intent.putExtras(bundle);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	//点击播放“本地歌曲列表”的某一首歌曲时播放该歌曲，在LocalMusicListActivity.java的适配器adapter中被调用
	public void playMusic(List<MusicInfomation> playList, int position) {
        Log.i(TAG, "	--->BaseActivity--->playMusic ######playList= " + playList + " ######position= " + position);
		Log.i(TAG, "	-----------------playMusic------------------start----------------");

        if (null == mServiceManager) {
	        Log.d(TAG, "	--->BaseActivity--->playMusic mServiceManager=null");
			return;
		}
        if (playList == null) {
			return;
		}
        Log.i(TAG, "	--->BaseActivity--->playMusic ######mServiceManager.getFileList()= " + mServiceManager.getFileList());

        if (!playList.equals(mServiceManager.getFileList())) {//first touch a song
            Log.i(TAG, "	--->BaseActivity--->playMusic !playList.equals(mServiceManager.getFileList())");
			mServiceManager.setPlayListAndPlay(playList, position);
		} else {
            Log.i(TAG, "	--->BaseActivity--->playMusic playList.equals(mServiceManager.getFileList())");
			if (position == mServiceManager.getCurPlayIndex()) {
	            Log.i(TAG, "	--->BaseActivity--->playMusic position == mServiceManager.getCurPlayIndex()");
				handleClick(findViewById(R.id.btn_play));
			} else {
	            Log.i(TAG, "	--->BaseActivity--->playMusic position != mServiceManager.getCurPlayIndex()");
				mServiceManager.play(position);
			}
		}
		Log.i(TAG, "	-----------------playMusic------------------end----------------");
	}
}
