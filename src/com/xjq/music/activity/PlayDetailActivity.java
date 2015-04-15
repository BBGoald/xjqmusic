package com.xjq.music.activity;

import java.util.List;

import com.xjq.music.model.MusicInfomation;
import com.xjq.music.player.IOnServiceConnectComplete;
import com.xjq.music.player.MusicPlayState;
import com.xjq.music.player.MusicPlayer;
import com.xjq.music.player.MusicServiceManager;
import com.xjq.music.util.MusicPlayMode;
import com.xjq.music.util.MusicPlayerHelper;
import com.xjq.music.util.ProgressTimer;
import com.xjq.xjqgraduateplayer.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class PlayDetailActivity extends Activity implements IOnServiceConnectComplete, 
	OnSeekBarChangeListener, OnClickListener{

	private static final String TAG = "xjq";

	public static String FLAG_PLAY_LIST = "flag_play_list";
	public static String BROCAST_NAME = MusicPlayer.class.getName() + ".PlayStatus.Brocast";
	private final static int EVENT_SD_STATUS = 0x2300;
	private final static int EVENT_REFRESH_PROGRESS = EVENT_SD_STATUS + 1;

	private boolean seekBarBusy = false;
	private ImageButton btnNextButton;
	private ImageButton btnPreButton;
	private ImageButton btnPlayButton;
	private ImageButton btnPlayModeButton;
	private ImageButton btnMyFavoriteButton;

	private TextView txtTitle;
	private TextView txtSinger;
	private TextView txtCurTime;
	private TextView txtTotalTime;
	
	private SeekBar playerSeekbBar;
	
	private List<MusicInfomation> mMusicFileList;
	
	private MusicServiceManager mServiceManager; // 服务管理	
	private ProgressTimer mMusicTimer;
	private MusicPlayStateBrocast mMusicPlayStateBrocast;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.i(TAG, "******PlayDetailActivity--->onCreate");
		setContentView(R.layout.activity_play_detail);
		initData();
		initView();
	}

	private void initData() {
		// TODO Auto-generated method stub
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
		Log.i(TAG, "	--->PlayDetailActivity--->initView");
		btnNextButton = (ImageButton) findViewById(R.id.btn_next);
		btnPreButton = (ImageButton) findViewById(R.id.btn_prev);
		btnPlayButton = (ImageButton) findViewById(R.id.btn_play);
		btnPlayModeButton = (ImageButton) findViewById(R.id.btn_playmode);
		btnMyFavoriteButton = (ImageButton) findViewById(R.id.img_fav);
		
		playerSeekbBar = (SeekBar) findViewById(R.id.player_seekbar);
		txtSinger = (TextView) findViewById(R.id.txt_singername);
		txtCurTime = (TextView) findViewById(R.id.player_position);
		txtTotalTime = (TextView) findViewById(R.id.player_duration);
		
		btnNextButton.setOnClickListener(this);
		btnPreButton.setOnClickListener(this);
		btnPlayButton.setOnClickListener(this);
		btnPlayModeButton.setOnClickListener(this);
		playerSeekbBar.setOnSeekBarChangeListener(this);
		//btnMyFavoriteButton.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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

	private void destryData() {
		// TODO Auto-generated method stub
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
		case R.id.btn_next:
			Log.i(TAG, "	--->PlayDetailActivity--->onClick--->btn_next");
			playNext();
			break;
		case R.id.btn_prev:
			Log.i(TAG, "	--->PlayDetailActivity--->onClick--->btn_prev");
			playPre();
			break;
		case R.id.btn_play:
			Log.i(TAG, "	--->PlayDetailActivity--->onClick--->btn_play");
			playOrStop();
			break;
		case R.id.btn_playmode:
			Log.i(TAG, "	--->PlayDetailActivity--->onClick--->btn_playmode");
			switchPlayMode((ImageButton) v);
			break;

		default:
			break;
		}
	}

	private Handler mHandler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case EVENT_REFRESH_PROGRESS:
				if (mServiceManager != null) {
					MusicPlayerHelper.updateProgress(mServiceManager.getCurPosition(), mServiceManager.getDuration(), playerSeekbBar, txtCurTime, txtTotalTime);
				}
				break;

			default:
				break;
			}
			return false;
		}
	});
	
	private void switchPlayMode(ImageButton button) {
		// TODO Auto-generated method stub
		int mode = mServiceManager.getPlayMode() + 1;
		Log.i(TAG, "	--->PlayDetailActivity--->switchPlayMode ######mServiceManager.getPlayMode() + 1 = " + mode);
		if (mode > MusicPlayMode.MPM_RANDOM_PLAY) {
			mode = MusicPlayMode.MPM_SINGLE_LOOP_PLAY;
		}
		String msg = "当前播放状态：" + "\n--->" + MusicPlayMode.showPlayMode(this, button, mode) + "<---";
		mServiceManager.setPlayMode(mode);
		if (msg != null) {
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
		}
	}

	private void playOrStop() {
		// TODO Auto-generated method stub
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
		Log.i(TAG, "	--->PlayDetailActivity--->playPre");
		if (mServiceManager != null) {
			mServiceManager.playPre();
		}
	}

	private void playNext() {
		// TODO Auto-generated method stub
		Log.i(TAG, "	--->PlayDetailActivity--->playNext");
		if (mServiceManager != null	) {
			try {
				mServiceManager.playNext();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

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
		mMusicFileList = mServiceManager.getFileList();
		int playState = mServiceManager.getPlayState();
		switch (playState) {
		case MusicPlayState.MPS_NOFILE:
			mServiceManager.setPlayListAndPlay(mMusicFileList, 0);
			break;
		case MusicPlayState.MPS_PLAYING:
		case MusicPlayState.MPS_PAUSE:
			mServiceManager.sendPlayStateBrocast();//首次启动活动，成功连接到服务之后主动要求服务发送广播更新显示时间
			break;

		default:
			break;
		}
	}

	private class MusicPlayStateBrocast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Log.i(TAG, "	--->PlayDetailActivity--->onReceive");
			Log.i(TAG, "	--->--->--->--->onReceive--->--->--->--->");
			String action = intent.getAction();
			if (action.equals(MusicPlayer.BROCAST_NAME)) {
				TranslatePlayStateEvent(intent);
			}
		}

		public void TranslatePlayStateEvent(Intent intent) {
			// TODO Auto-generated method stub
			MusicInfomation data = new MusicInfomation();
			int playState = intent.getIntExtra(MusicPlayState.PLAY_STATE_NAME, -1);
			int palyIndex = intent.getIntExtra(MusicPlayState.PLAY_MUSIC_INDEX, -1);
			Bundle bundle = intent.getBundleExtra(MusicInfomation.KEY_MUSIC_INFO);
			if (bundle != null) {
				data = bundle.getParcelable(MusicInfomation.KEY_MUSIC_INFO);
			}
			Log.i(TAG, "	--->PlayDetailActivity--->TranslatePlayStateEvent ######playState= " + playState);

			switch (playState) {
			case MusicPlayState.MPS_ERROR_PLAYE:
				break;
			case MusicPlayState.MPS_INVALID:
				mMusicTimer.stopPlayTimer();
				/*new WoboToast(getApplicationContext()).show(getResources()
						.getString(R.string.music_file_void));*/
				MusicPlayerHelper.updateProgress(mServiceManager.getCurPosition(), data.getPlayTime(), playerSeekbBar, txtCurTime, txtTotalTime);
				break;
			case MusicPlayState.MPS_PREPARE:			
				//loadLrc();
				mMusicTimer.stopPlayTimer();
				MusicPlayerHelper.updateProgress(mServiceManager.getCurPosition(), data.getPlayTime(), playerSeekbBar, txtCurTime, txtTotalTime);
				break;
			case MusicPlayState.MPS_PREPAREING:
				mMusicTimer.stopPlayTimer();			
				MusicPlayerHelper.updateProgress(mServiceManager.getCurPosition(), data.getPlayTime(), playerSeekbBar, txtCurTime, txtTotalTime);
				break;
			case MusicPlayState.MPS_PLAYING:
				Log.i(TAG, "	--->PlayDetailActivity--->TranslatePlayStateEvent ######playState= MPS_PLAYING = " + playState);
				mMusicTimer.startPlayTimer();
				MusicPlayerHelper.updateProgress(mServiceManager.getCurPosition(), data.getPlayTime(), playerSeekbBar, txtCurTime, txtTotalTime);
				break;
			case MusicPlayState.MPS_PAUSE:
				Log.i(TAG, "	--->PlayDetailActivity--->TranslatePlayStateEvent ######playState= MPS_PAUSE = " + playState);
				mMusicTimer.stopPlayTimer();
				MusicPlayerHelper.updateProgress(mServiceManager.getCurPosition(), data.getPlayTime(), playerSeekbBar, txtCurTime, txtTotalTime);
				break;
			case MusicPlayState.MPS_COMPLETION:
				mMusicTimer.stopPlayTimer();
				break;

			default:
				//MusicPlayerHelper.updateProgress(mServiceManager.getCurPosition(), data.getPlayTime(), playerSeekbBar, txtCurTime, txtTotalTime);
				break;
			}
			MusicPlayerHelper.updateProgress(mServiceManager.getCurPosition(), data.getPlayTime(), playerSeekbBar, txtCurTime, txtTotalTime);
			refreshBottumPlayBar();
		}
		
	}

	public void refreshBottumPlayBar() {
		// TODO Auto-generated method stub
		MusicPlayMode.showPlayMode(this, btnPlayModeButton, mServiceManager.getPlayMode());
		if (mServiceManager.getPlayState() == MusicPlayState.MPS_PLAYING) {
			btnPlayButton.setImageResource(R.drawable.btn_play);
		} else {
			btnPlayButton.setImageResource(R.drawable.btn_pause);
		}
	}
}
