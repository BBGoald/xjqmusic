package com.xjq.music.activity;

import java.util.List;

import com.xjq.music.lyric.LyricView;
import com.xjq.music.lyric.LyricViewThread;
import com.xjq.music.model.MusicInfomation;
import com.xjq.music.player.IOnServiceConnectComplete;
import com.xjq.music.player.MusicPlayMode;
import com.xjq.music.player.MusicPlayState;
import com.xjq.music.player.MusicPlayer;
import com.xjq.music.player.MusicPlayerHelper;
import com.xjq.music.player.MusicServiceManager;
import com.xjq.music.setbg.SkinUtil;
import com.xjq.music.shaker.ShakeListener;
import com.xjq.music.shaker.ShakeListener.OnShakeListener;
import com.xjq.music.util.ProgressTimer;
import com.xjq.xjqgraduateplayer.R;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 正在播放的歌曲信息
 * 
 * @author root
 * 
 */
public class PlayDetailActivity extends Activity implements
		IOnServiceConnectComplete, OnSeekBarChangeListener, OnClickListener {

	private static final String TAG = "xjq";
	public static final Boolean DEBUG = true;

	public static String FLAG_PLAY_LIST = "flag_play_list";
	public static String BROCAST_NAME = MusicPlayer.class.getName()
			+ ".PlayStatus.Brocast";
	private final static int EVENT_SD_STATUS = 0x2300;
	private final static int EVENT_REFRESH_PROGRESS = EVENT_SD_STATUS + 1;

	private boolean seekBarBusy = false;
	private ImageButton btnNextButton;
	private ImageButton btnPreButton;
	private ImageButton btnPlayButton;
	private ImageButton btnPlayModeButton;
	// private ImageButton btnMyFavoriteButton;
	private ImageButton btnBackButton;

	private RelativeLayout relativeLayoutPlayDetail;

	private TextView txtTitle;
	private TextView txtSinger;
	private TextView txtCurTime;
	private TextView txtTotalTime;
	LyricView txtLyricView;
	private SeekBar playerSeekbBar;

	private List<MusicInfomation> mMusicFileList;

	private Context mContext;

	private MusicServiceManager mServiceManager; // 服务管理
	private ProgressTimer mMusicTimer;
	private MusicPlayStateBrocast mMusicPlayStateBrocast;
	private boolean isFirstLoad = true;

	private LyricViewThread lyricViewThread;

	private Vibrator mVibrator;
	ShakeListener mShakeListener = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (DEBUG)
			Log.i(TAG, "******PlayDetailActivity--->onCreate");
		mContext = PlayDetailActivity.this;
		setContentView(R.layout.activity_play_detail);
		txtLyricView = (LyricView) findViewById(R.id.txt_lyricView);
		initData();// 初始化数据
		initView();// 初始化显示界面
		addViberation();
	}

	private void addViberation() {
		// TODO Auto-generated method stub
		// 通过getSystemService获取sensor服务,其实就是初始化一个SensorManager实例。
		mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		mShakeListener = new ShakeListener(mContext);
		mShakeListener.setOnShakeListener(new MusicShakeListener());
	}

	private void initData() {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.i(TAG, "	--->PlayDetailActivity--->initData");
		if (null == mServiceManager) {
			mServiceManager = new MusicServiceManager(this);
			mServiceManager.setOnServiceConnectComplete(this);
		}
		mServiceManager.connectService();
		mMusicTimer = new ProgressTimer(mHandler, EVENT_REFRESH_PROGRESS);
		mMusicPlayStateBrocast = new MusicPlayStateBrocast();
		IntentFilter intentFilter = new IntentFilter(MusicPlayer.BROCAST_NAME);
		registerReceiver(mMusicPlayStateBrocast, intentFilter);
	}

	private void initView() {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.i(TAG, "	--->PlayDetailActivity--->initView");
		relativeLayoutPlayDetail = (RelativeLayout) findViewById(R.id.playDetail);
		relativeLayoutPlayDetail.setBackgroundResource(R.drawable.warm);

		btnBackButton = (ImageButton) findViewById(R.id.btn_back);
		btnNextButton = (ImageButton) findViewById(R.id.btn_next);
		btnPreButton = (ImageButton) findViewById(R.id.btn_prev);
		btnPlayButton = (ImageButton) findViewById(R.id.btn_play);
		btnPlayModeButton = (ImageButton) findViewById(R.id.btn_playmode);
		// btnMyFavoriteButton = (ImageButton) findViewById(R.id.img_fav);

		playerSeekbBar = (SeekBar) findViewById(R.id.player_seekbar);
		txtTitle = (TextView) findViewById(R.id.txt_title);
		txtSinger = (TextView) findViewById(R.id.txt_singer);
		txtCurTime = (TextView) findViewById(R.id.player_position);
		txtTotalTime = (TextView) findViewById(R.id.player_duration);

		btnBackButton.setOnClickListener(this);
		btnNextButton.setOnClickListener(this);
		btnPreButton.setOnClickListener(this);
		btnPlayButton.setOnClickListener(this);
		btnPlayModeButton.setOnClickListener(this);
		playerSeekbBar.setOnSeekBarChangeListener(this);
		txtLyricView.setOnClickListener(this);
		// btnMyFavoriteButton.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SkinUtil.getSelectedImg(mContext);
		loadBG();
	}

	@SuppressLint("NewApi")
	private void loadBG() {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.i(TAG, "	--->PlayDetailActivity--->loadBackGround");
		String urlString = SkinUtil.getSelectedImg(this);
		if (urlString.equals("")) {
			relativeLayoutPlayDetail.setBackgroundResource(R.drawable.warm);
			return;
		}
		Drawable drawable = SkinUtil.getDrawble(mContext, urlString, false);
		if (drawable != null) {
			if (DEBUG)
				Log.i(TAG, "	--->PlayDetailActivity--->loadBackGround");
			relativeLayoutPlayDetail.setBackground(drawable);
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		destryData();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent intent = new Intent();
		intent.setClass(mContext, LocalMusicListActivity.class);
		startActivity(intent);
	}

	// 退出时销毁相关数据
	private void destryData() {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.i(TAG, "	--->PlayDetailActivity--->destryData");
		unregisterReceiver(mMusicPlayStateBrocast);
		mMusicPlayStateBrocast = null;
		mMusicTimer.stopPlayTimer();
		mMusicTimer = null;
		if (null != mServiceManager) {
			mServiceManager.disconnectService();
		}
		mServiceManager = null;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_back:
			if (DEBUG)
				Log.i(TAG, "	--->PlayDetailActivity--->onClick--->btn_back");
			backToMusicList();
			break;
		case R.id.btn_next:
			if (DEBUG)
				Log.i(TAG, "	--->PlayDetailActivity--->onClick--->btn_next");
			playNext();
			break;
		case R.id.btn_prev:
			if (DEBUG)
				Log.i(TAG, "	--->PlayDetailActivity--->onClick--->btn_prev");
			playPre();
			break;
		case R.id.btn_play:
			if (DEBUG)
				Log.i(TAG, "	--->PlayDetailActivity--->onClick--->btn_play");
			playOrStop();
			break;
		case R.id.btn_playmode:
			if (DEBUG)
				Log.i(TAG, "	--->PlayDetailActivity--->onClick--->btn_playmode");
			switchPlayMode((ImageButton) v);
			break;

		default:
			break;
		}
	}

	// 返回到音乐列表界面
	private void backToMusicList() {
		Intent intent = new Intent();
		intent.setClass(mContext, LocalMusicListActivity.class);
		startActivity(intent);
	}

	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case EVENT_REFRESH_PROGRESS:
				if (mServiceManager != null) {
					// 更新显示界面，包括歌曲进度条，当前时间，总时间
					MusicPlayerHelper.updateProgress(
							mServiceManager.getCurPosition(),
							mServiceManager.getDuration(), playerSeekbBar,
							txtCurTime, txtTotalTime);
				}
				break;

			default:
				break;
			}
			return false;
		}
	});

	// 选择播放模式
	private void switchPlayMode(ImageButton button) {
		// TODO Auto-generated method stub
		// 获取当前歌曲播放模式，默认单曲循环
		int mode = mServiceManager.getPlayMode() + 1;
		if (DEBUG)
			Log.i(TAG,
					"	--->PlayDetailActivity--->switchPlayMode ######mServiceManager.getPlayMode() + 1 = "
							+ mode);
		if (mode > MusicPlayMode.MPM_RANDOM_PLAY) {
			mode = MusicPlayMode.MPM_SINGLE_LOOP_PLAY;
		}
		String msg = "当前播放状态：" + "\n--->"
				+ MusicPlayMode.showPlayMode(this, button, mode) + "<---";
		// 设置播放模式并提示
		mServiceManager.setPlayMode(mode);
		if (msg != null) {
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG)
					.show();
		}
	}

	private void playOrStop() {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.i(TAG, "	--->PlayDetailActivity--->playOrStop");
		if (mServiceManager != null) {
			if (mServiceManager.getPlayState() == MusicPlayState.MPS_PLAYING) {
				mServiceManager.pause();
				btnPlayButton.setImageResource(R.drawable.btn_pause);
			} else if (mServiceManager.getPlayState() == MusicPlayState.MPS_PAUSE) {
				mServiceManager.rePlay();
				btnPlayButton.setImageResource(R.drawable.btn_play);
			}
		}
	}

	private void playPre() {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.i(TAG, "	--->PlayDetailActivity--->playPre");
		if (mServiceManager != null) {
			mServiceManager.playPre();
		}
	}

	private void playNext() {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.i(TAG, "	--->PlayDetailActivity--->playNext");
		if (mServiceManager != null) {
			try {
				mServiceManager.playNext();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	// 拖动进度条实现快/慢进
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		if (fromUser && !seekBarBusy) {
			mServiceManager.seekTo(progress);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnServiceConnectComplete() {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.i(TAG, "	--->PlayDetailActivity-->OnServiceConnectComplete");
		mMusicFileList = mServiceManager.getFileList();
		int playState = mServiceManager.getPlayState();
		switch (playState) {
		case MusicPlayState.MPS_NOFILE:
			mServiceManager.setPlayListAndPlay(mMusicFileList, 0);
			break;
		case MusicPlayState.MPS_PLAYING:
		case MusicPlayState.MPS_PAUSE:
			mMusicFileList = mServiceManager.getFileList();
			mServiceManager.sendPlayStateBrocast();// 首次启动活动，成功连接到服务之后主动要求服务发送广播更新显示时间
			break;

		default:
			break;
		}
	}

	// 广播接收器，接收来自服务的广播，并进行界面更新。
	private class MusicPlayStateBrocast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (DEBUG)
				Log.i(TAG, "	--->PlayDetailActivity--->onReceive");
			if (DEBUG)
				Log.i(TAG, "	--->--->--->--->onReceive--->--->--->--->");
			String action = intent.getAction();
			if (action.equals(MusicPlayer.BROCAST_NAME)) {
				TranslatePlayStateEvent(intent);
			}
		}

		public void TranslatePlayStateEvent(Intent intent) {
			// TODO Auto-generated method stub
			MusicInfomation data = new MusicInfomation();
			int playState = intent.getIntExtra(MusicPlayState.PLAY_STATE_NAME,
					-1);
			int playIndex = intent.getIntExtra(MusicPlayState.PLAY_MUSIC_INDEX,
					-1);
			Bundle bundle = intent
					.getBundleExtra(MusicInfomation.KEY_MUSIC_INFO);
			if (bundle != null) {
				data = bundle.getParcelable(MusicInfomation.KEY_MUSIC_INFO);
			}
			if (DEBUG)
				Log.i(TAG,
						"	--->PlayDetailActivity--->TranslatePlayStateEvent ######playState= "
								+ playState);

			switch (playState) {
			case MusicPlayState.MPS_ERROR_PLAYE:
				break;
			case MusicPlayState.MPS_INVALID:
				mMusicTimer.stopPlayTimer();
				/*
				 * new WoboToast(getApplicationContext()).show(getResources()
				 * .getString(R.string.music_file_void));
				 */
				MusicPlayerHelper.updateProgress(0, data.getPlayTime(),
						playerSeekbBar, txtCurTime, txtTotalTime);
				break;
			case MusicPlayState.MPS_PREPARE:
				loadLrc();
				mMusicTimer.stopPlayTimer();
				MusicPlayerHelper.updateProgress(0, data.getPlayTime(),
						playerSeekbBar, txtCurTime, txtTotalTime);
				break;
			case MusicPlayState.MPS_PREPAREING:
				mMusicTimer.stopPlayTimer();
				MusicPlayerHelper.updateProgress(playerSeekbBar, txtCurTime,
						txtTotalTime);
				break;
			case MusicPlayState.MPS_PLAYING:
				if (DEBUG)
					Log.i(TAG,
							"	--->PlayDetailActivity--->TranslatePlayStateEvent ######playState= MPS_PLAYING = "
									+ playState);
				mMusicTimer.startPlayTimer();
				if (isFirstLoad) {
					loadLrc();
				}
				MusicPlayerHelper.updateProgress(
						mServiceManager.getCurPosition(), data.getPlayTime(),
						playerSeekbBar, txtCurTime, txtTotalTime);
				break;
			case MusicPlayState.MPS_PAUSE:
				if (DEBUG)
					Log.i(TAG,
							"	--->PlayDetailActivity--->TranslatePlayStateEvent ######playState= MPS_PAUSE = "
									+ playState);
				mMusicTimer.stopPlayTimer();
				MusicPlayerHelper.updateProgress(
						mServiceManager.getCurPosition(), data.getPlayTime(),
						playerSeekbBar, txtCurTime, txtTotalTime);
				break;
			case MusicPlayState.MPS_COMPLETION:
				mMusicTimer.stopPlayTimer();
				break;
			case MusicPlayState.MPS_NET_DISCONNECT:
				mMusicTimer.stopPlayTimer();
				MusicPlayerHelper.updateProgress(playerSeekbBar, txtCurTime,
						txtTotalTime);
				break;
			case MusicPlayState.MPS_ERROR_ADDRS:
				mMusicTimer.stopPlayTimer();
				MusicPlayerHelper.updateProgress(playerSeekbBar, txtCurTime,
						txtTotalTime);
				break;

			default:
				// MusicPlayerHelper.updateProgress(mServiceManager.getCurPosition(),
				// data.getPlayTime(), playerSeekbBar, txtCurTime,
				// txtTotalTime);
				break;
			}
			// MusicPlayerHelper.updateProgress(mServiceManager.getCurPosition(),
			// data.getPlayTime(), playerSeekbBar, txtCurTime, txtTotalTime);
			updateMusicInfo(playIndex);
			refreshBottumPlayBar();
		}

	}

	// 更新顶部歌曲信息，包括歌曲名称，歌手名称。
	private void updateMusicInfo(int playindex) {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.i(TAG,
					"	--->PlayDetailActivity--->updateMusicInfo ###playindex= "
							+ playindex);
		if (mMusicFileList == null) {
			if (DEBUG)
				Log.i(TAG,
						"	--->PlayDetailActivity--->updateMusicInfor ###mMusicFileList==null");
			return;
		}
		if (playindex < 0 || mMusicFileList.size() <= playindex) {
			if (DEBUG)
				Log.i(TAG,
						"	--->PlayDetailActivity--->updateMusicInfor ###playindex < 0");
			return;
		}
		MusicInfomation musicInfomation = mMusicFileList.get(playindex);
		if (DEBUG)
			Log.i(TAG, "	--->--->txtTitle " + txtTitle + " txtSinger"
					+ txtSinger + " musicInfomation" + musicInfomation);
		txtTitle.setText(musicInfomation.getName());
		txtSinger.setText(musicInfomation.getArtist());
	}

	// 加载歌词
	public void loadLrc() {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.i(TAG, "	--->PlayDetailActivity--->loadLrc");
		if (lyricViewThread != null) {
			lyricViewThread.setFinishFlag(true);
		}
		isFirstLoad = false;
		lyricViewThread = new LyricViewThread(getCurrentPlayingMusicInfo(),
				mHandler, txtLyricView) {

			@Override
			public int getCurrentPosition() {
				// TODO Auto-generated method stub
				if (mServiceManager == null) {
					if (DEBUG)
						Log.i(TAG,
								"	--->PlayDetailActivity--->loadLrc mServiceManager == null");
					return 0;
				}
				if (DEBUG)
					Log.i(TAG,
							"	--->PlayDetailActivity--->loadLrc ###mServiceManager.getCurPosition()= "
									+ mServiceManager.getCurPosition());
				return mServiceManager.getCurPosition();
			}
		};
		lyricViewThread.start();
	}

	private MusicInfomation getCurrentPlayingMusicInfo() {
		// TODO Auto-generated method stub
		if (mServiceManager == null) {
			return null;
		}
		try {
			return mServiceManager.getCurrentMusicInfomation();
		} catch (RemoteException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	// 更新底部播放按钮显示，包括暂停/播放
	public void refreshBottumPlayBar() {
		// TODO Auto-generated method stub
		MusicPlayMode.showPlayMode(this, btnPlayModeButton,
				mServiceManager.getPlayMode());
		if (mServiceManager.getPlayState() == MusicPlayState.MPS_PLAYING) {
			btnPlayButton.setImageResource(R.drawable.btn_play);
		} else {
			btnPlayButton.setImageResource(R.drawable.btn_pause);
		}
	}

	public class MusicShakeListener implements OnShakeListener {

		@Override
		public void onShake() {
			if (DEBUG)
				Log.i(TAG, "	--->PlayDetailActivity--->onShake");
			mShakeListener.stop();
			startVibration();

			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(getApplicationContext(), "已切换音乐",
							Toast.LENGTH_LONG).show();
					mVibrator.cancel();
					mShakeListener.start();
					playNext();
				}
			}, 2000);
		}
	}

	public void startVibration() {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.i(TAG, "	--->PlayDetailActivity--->startVibration");
		mVibrator.vibrate(new long[] { 500, 200, 500, 200 }, -1);
	}
}
