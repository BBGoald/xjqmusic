package com.xjq.music.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.xjq.music.model.MusicInfomation;
import com.xjq.music.util.SharedPreferenceHelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * 最终控制歌曲播放的类
 * 
 * @author root
 * 
 */
public class MusicPlayer implements OnCompletionListener, OnErrorListener,
		OnPreparedListener, OnBufferingUpdateListener, OnInfoListener,
		OnSeekCompleteListener {

	private static final String TAG = "xjq";
	public static final Boolean DEBUG = false;
	private Context mContext;
	private List<MusicInfomation> mMusicFileList; // 音乐文件列表

	private int mPlayState; // 播放器状态
	private int mPlayMode; // 歌曲播放模式
	private int mCurPlayIndex; // 当前播放索引

	public static final String BROCAST_NAME = MusicPlayer.class.getName()
			+ ".PlayStatus.Brocast";
	private MediaPlayer mMediaPlayer; // 播放器对象
	private Random mRandom;

	public MusicPlayer(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;
		defaultParam();
		mRandom = new Random();// 随机数生成器
		mRandom.setSeed(System.currentTimeMillis());// 随机数产生源
	}

	private void defaultParam() {
		// TODO Auto-generated method stub
		mMusicFileList = new ArrayList<MusicInfomation>();
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnCompletionListener(this);
		mMediaPlayer.setOnErrorListener(this);
		mMediaPlayer.setOnPreparedListener(this);
		mMediaPlayer.setOnBufferingUpdateListener(this);
		mMediaPlayer.setOnInfoListener(this);
		mMediaPlayer.setOnSeekCompleteListener(this);

		mCurPlayIndex = -1;
		mPlayState = MusicPlayState.MPS_NOFILE;
	}

	// 重载方法
	@Override
	public void onSeekComplete(MediaPlayer mp) {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.i(TAG, "	--->MusicPlayer--->onSeekComplete");

	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.i(TAG, "	--->MusicPlayer--->onInfo");
		return false;
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.i(TAG, "	--->MusicPlayer--->onBufferingUpdate");

	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.d(TAG,
					"	--->--->--->--->onPrepared--->--->--->--->mMediaPlayer.start()");
		mMediaPlayer.start();
		mPlayState = MusicPlayState.MPS_PLAYING;

		sendPlayStateBrocast();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.i(TAG, "	--->MusicPlayer--->onError");
		return false;
	}

	//当歌曲播放完成时,歌曲的随机播放，顺序播放，单曲循环都在这里设置
	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.i(TAG, "	--->MusicPlayer--->onCompletion ######mPlayMode= "
					+ mPlayMode);
		if (mPlayState == MusicPlayState.MPS_PAUSE) {
			sendPlayStateBrocast(MusicPlayState.MPS_PAUSE);
			return;
		}
		sendPlayStateBrocast(MusicPlayState.MPS_COMPLETION);
		switch (mPlayMode) {
		case MusicPlayMode.MPM_SINGLE_LOOP_PLAY:
			play(mCurPlayIndex);
			break;
		case MusicPlayMode.MPM_LIST_LOOP_PLAY:
			playNext();
			break;
		case MusicPlayMode.MPM_ORDER_PLAY:
			if (mCurPlayIndex < (mMusicFileList.size() - 1)) {
				playNext();
			} else {
				stop();
			}
			break;
		case MusicPlayMode.MPM_RANDOM_PLAY:
			//获取随机数
			int index = getRandomIndex();
			if (index != -1) {
				mCurPlayIndex = index;
			} else {
				mCurPlayIndex++;
			}
			mCurPlayIndex = reviceIndex(mCurPlayIndex);
			//准备好随机数对应的歌曲，准备完毕后就开始播放
			prepare(mCurPlayIndex);
			break;

		default:
			prepare(mCurPlayIndex);
			break;
		}
	}

	//获取随机数
	private int getRandomIndex() {
		// TODO Auto-generated method stub
		int size = mMusicFileList.size();
		if (size == 0) {
			return -1;
		}
		return Math.abs(mRandom.nextInt() % size);
	}

	public void setPlayList(List<MusicInfomation> playList) {
		mMusicFileList = playList;
		if (DEBUG)
			Log.i(TAG, "	--->MusicPlayer--->setPlayList ######playList= "
					+ playList);
	}

	public List<MusicInfomation> getFileList() {
		if (DEBUG)
			Log.i(TAG, "	--->MusicPlayer--->getFileList ######mMusicFileList= "
					+ mMusicFileList);
		return mMusicFileList;
	}

	/**
	 * 设置播放地址(本地文件)
	 * 
	 * @param fileList
	 * @param index
	 * @return void
	 */
	public void setPlayListAndPlay(List<MusicInfomation> fileList, int index) {
		if (DEBUG)
			Log.i(TAG,
					"	--->MusicPlayer--->setPlayListAndPlay ######fileList= "
							+ fileList + " ######index= " + index);
		if (fileList == null || fileList.size() == 0) {
			if (null != mMusicFileList) {
				mMusicFileList.clear();
			}
			mPlayState = MusicPlayState.MPS_NOFILE;
			mCurPlayIndex = -1;
			if (DEBUG)
				Log.i(TAG,
						"	--->MusicPlayer--->setPlayListAndPlay ######fileList= null"
								+ " ######mPlayState= MPS_NOFILE = "
								+ mPlayState);
			return;
		}
		mMusicFileList = fileList;
		if (DEBUG)
			Log.d("setPlayListAndPlay", ":" + mMusicFileList.size());
		switch (mPlayState) {
		case MusicPlayState.MPS_NOFILE:
		case MusicPlayState.MPS_INVALID:
		case MusicPlayState.MPS_STOP:
		case MusicPlayState.MPS_PREPARE:
			if (DEBUG)
				Log.i(TAG,
						"	--->MusicPlayer--->setPlayListAndPlay ######mPlayState= MPS_PREPARE || MPS_STOP || MPS_INVALID || MPS_NOFILE = "
								+ mPlayState);
			prepare(index);
			break;
		case MusicPlayState.MPS_PLAYING:
			if (DEBUG)
				Log.i(TAG,
						"	--->MusicPlayer--->setPlayListAndPlay ######mPlayState= MPS_PLAYING = "
								+ mPlayState);
			stop();
			prepare(index);
			break;
		case MusicPlayState.MPS_PAUSE:
			if (DEBUG)
				Log.i(TAG,
						"	--->MusicPlayer--->setPlayListAndPlay ######mPlayState= MPS_PAUSE = "
								+ mPlayState);
			stop();
			prepare(index);
			break;
		case MusicPlayState.MPS_ERROR_PLAYE:
			if (DEBUG)
				Log.i(TAG,
						"	--->MusicPlayer--->setPlayListAndPlay ######mPlayState= MPS_ERROR_PLAYE = "
								+ mPlayState);
			playNext();
			break;

		default:
			if (DEBUG)
				Log.i(TAG,
						"	--->MusicPlayer--->setPlayListAndPlay ######mPlayState= default");
			break;
		}
	}

	public boolean stop() {
		if (mPlayState != MusicPlayState.MPS_PLAYING
				&& mPlayState != MusicPlayState.MPS_PAUSE) {
			/*
			 * printLog("mPlayState == MPS_NOFILE,can not stop " + mPlayState,
			 * true);
			 */
			return false;
		}
		mMediaPlayer.stop();
		mPlayState = MusicPlayState.MPS_STOP;
		sendPlayStateBrocast();
		return true;
	}

	// 限制当前播放文件索引
	private int reviceIndex(int mCurPlayIndex2) {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.i(TAG,
					"		--->MusicPlayer--->reviceIndex ######mCurPlayIndex2= "
							+ mCurPlayIndex2);
		if (mCurPlayIndex2 < 0) {
			mCurPlayIndex2 = mMusicFileList.size() - 1;
		}
		if (mCurPlayIndex2 >= mMusicFileList.size()) {
			mCurPlayIndex2 = 0;
		}
		return mCurPlayIndex2;
	}

	// 准备数据
	private boolean prepare(final int index) {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.i(TAG, "	--->MusicPlayer--->prepare ######index= " + index
					+ " ######mMusicFileList= " + mMusicFileList
					+ "	######mMusicFileList.get(index).getPath()= "
					+ mMusicFileList.get(index).getPath());

		mCurPlayIndex = index;
		if (null == mMusicFileList || mMusicFileList.size() == 0) {
			mPlayState = MusicPlayState.MPS_INVALID;
			if (DEBUG)
				Log.i(TAG,
						"	--->MusicPlayer--->prepare ######mMusicFileList= null"
								+ " ######mPlayState= MPS_INVALID = "
								+ mPlayState);

			sendPlayStateBrocast();
			return false;
		}
		mPlayState = MusicPlayState.MPS_PREPAREING;
		sendPlayStateBrocast();

		return prepareData(mMusicFileList.get(index).getPath());
	}

	// 准备播放歌曲文件
	private synchronized boolean prepareData(final String path) {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.i(TAG, "	--->MusicPlayer--->prepareData ######path= " + path);

		if (null == path || path.length() == 0) {
			mPlayState = MusicPlayState.MPS_INVALID;
			if (DEBUG)
				Log.i(TAG, "	--->MusicPlayer--->prepareData ######path == null"
						+ "	######mPlayState= " + mPlayState);

			sendPlayStateBrocast();
			return false;
		}
		new Thread() {

			@SuppressLint("NewApi")
			@Override
			public void run() {
				// TODO Auto-generated method stub
				boolean isException = false;
				mMediaPlayer.reset();
				try {
					HashMap<String, String> mHeaders = new HashMap<String, String>();
					mHeaders.put(
							"User-Agent",
							"Mozilla/5.0 (iPad; U; CPU OS 3_2 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 Mobile/7B334b Safari/531.21.10");
					// mMediaPlayer.setDataSource(mContext, Uri.parse(path) ,
					// mHeaders);
					mMediaPlayer.setDataSource(mContext, Uri.parse(path),
							mHeaders);// /need API 14???current API
										// 8???@SuppressLint("NewApi")
					mPlayState = MusicPlayState.MPS_PREPARE;
					// mPlayState = MusicPlayState.MPS_PLAYING;
					mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
					sendPlayStateBrocast();
					mMediaPlayer.prepareAsync();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					isException = true;
				}
				if (isException) {
					mPlayState = MusicPlayState.MPS_INVALID;
					sendPlayStateBrocast();
				}
				if (DEBUG)
					Log.i(TAG,
							"	--->MusicPlayer--->prepareData	##################new Threadid= "
									+ Thread.currentThread().getId());
			};// attention!!!!!here has an symbol ";"

		}.start();
		return true;
	}

	public void sendPlayStateBrocast() {
		// TODO Auto-generated method stub
		sendPlayStateBrocast(mPlayState);
	}

	// 发送播放状态广播
	public void sendPlayStateBrocast(int mPlayState2) {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.i(TAG,
					"	--->MusicPlayer--->sendPlayStateBrocast ######mPlayState= "
							+ mPlayState);
		if (mContext == null) {
			return;
		}
		Intent intent = new Intent(BROCAST_NAME);
		intent.putExtra(MusicPlayState.PLAY_STATE_NAME, mPlayState);
		intent.putExtra(MusicPlayState.PLAY_MUSIC_INDEX, mCurPlayIndex);

		if (mPlayState2 != MusicPlayState.MPS_NOFILE) {
			if (DEBUG)
				Log.i(TAG,
						"	--->MusicPlayer--->sendPlayStateBrocast ######mPlayState != MusicPlayState.MPS_NOFILE	######mMusicFileList= "
								+ mMusicFileList);

			if (null == mMusicFileList || mCurPlayIndex < 0
					|| mMusicFileList.size() <= mCurPlayIndex) {
				return;
			}
			Bundle bundle = new Bundle();
			MusicInfomation dataInfomation = mMusicFileList.get(mCurPlayIndex);
			bundle.putParcelable(MusicInfomation.KEY_MUSIC_INFO, dataInfomation);

			intent.putExtra(MusicInfomation.KEY_MUSIC_INFO, bundle);
		}
		if (DEBUG)
			Log.i(TAG,
					"	--->MusicPlayer--->sendPlayStateBrocast--->mContext.sendBroadcast(intent)");
		if (DEBUG)
			Log.d(TAG, "	--->--->--->--->sendBroadcast--->--->--->--->");
		mContext.sendBroadcast(intent);
	}

	// 播放
	public boolean play(int position) {
		if (DEBUG)
			Log.i(TAG, "	--->MusicPlayer--->play ######position= " + position
					+ " ######mMusicFileList= " + mMusicFileList
					+ "	######mCurPlayIndex= " + mCurPlayIndex);

		if (mPlayState == MusicPlayState.MPS_NOFILE) {
			return false;
		}
		if (mCurPlayIndex == position) {
			if (!mMediaPlayer.isPlaying()) {
				mMediaPlayer.start();
				mPlayState = MusicPlayState.MPS_PLAYING;
				sendPlayStateBrocast();
			}
			return true;
		}
		mCurPlayIndex = position;
		prepare(mCurPlayIndex);
		return true;
	}

	// 暂停
	public boolean pause() {
		if (mPlayState != MusicPlayState.MPS_PLAYING) {
			return false;
		}
		mMediaPlayer.pause();
		mPlayState = MusicPlayState.MPS_PAUSE;
		sendPlayStateBrocast();
		return true;
	}

	// 播放下一首
	public boolean playNext() {
		if (DEBUG)
			Log.i(TAG, "	--->MusicPlayer--->playNext");
		if (mPlayState == MusicPlayState.MPS_NOFILE) {/*
													 * printLog(
													 * "mPlayState == MPS_NOFILE,can not playNext"
													 * , true);
													 */
			return false;
		}

		if (mPlayMode == MusicPlayMode.MPM_RANDOM_PLAY) {
			int index = getRandomIndex();
			if (index != -1) {
				mCurPlayIndex = index;
			} else {
				mCurPlayIndex++;
			}
		} else {
			mCurPlayIndex++;
		}

		mCurPlayIndex = reviceIndex(mCurPlayIndex);
		prepare(mCurPlayIndex);
		return true;
	}

	// 播放上一首
	public boolean playPre() {
		if (DEBUG)
			Log.i(TAG, "	--->MusicPlayer--->playNext");
		if (mPlayState == MusicPlayState.MPS_NOFILE) {
			return false;
		}
		mCurPlayIndex--;
		mCurPlayIndex = reviceIndex(mCurPlayIndex);
		prepare(mCurPlayIndex);
		return true;
	}

	// 快进/快退
	public boolean seekTo(int rate) {
		if (mPlayState == MusicPlayState.MPS_NOFILE
				|| mPlayState == MusicPlayState.MPS_INVALID) {
			return false;
		}
		int receive = limitReceiveSeekValue(rate);
		int time = mMediaPlayer.getDuration();
		//释放拖动动作的最后位置
		int curTime = (int) ((float) receive / 100 * time);
		mMediaPlayer.seekTo(curTime);
		return false;
	}

	// 限制快进/快退的值（最小为0，最大为100）
	private int limitReceiveSeekValue(int rate) {
		// TODO Auto-generated method stub
		if (rate < 0) {
			rate = 0;
		}
		if (rate > 100) {
			rate = 100;
		}
		return rate;
	}

	// 获取当前播放歌曲的信息
	public MusicInfomation getCurrentMusicInfomation() {
		if (mMusicFileList == null || mMusicFileList.size() == 0) {
			return null;
		}
		if (mCurPlayIndex < 0 || mCurPlayIndex >= mMusicFileList.size()) {
			return null;
		}
		return mMusicFileList.get(mCurPlayIndex);
	}

	// 获取当前播放状态
	public int getPlayState() {
		if (DEBUG)
			Log.i(TAG,
					"	--->MusicPlayer--->getPlayState return######mPlayState= "
							+ mPlayState);
		return mPlayState;
	}

	// 设置播放模式
	public void setPlayMode(int mode) {
		if (DEBUG)
			Log.i(TAG, "	--->MusicPlayer--->setPlayMode ######mode= " + mode);

		switch (mode) {
		case MusicPlayMode.MPM_SINGLE_LOOP_PLAY:
		case MusicPlayMode.MPM_LIST_LOOP_PLAY:
		case MusicPlayMode.MPM_ORDER_PLAY:
		case MusicPlayMode.MPM_RANDOM_PLAY:
			if (mPlayMode != mode) {
				mPlayMode = mode;
				SharedPreferenceHelper.writePreferenceIntValue(mContext,
						"test_config", "playmode", mPlayMode);
			}
			break;
		}
	}

	// 获取当前播放模式
	public int getPlayMode() {
		return mPlayMode;
	}

	// 获取当前播放歌曲进度
	public int getCurPosition() {
		if (DEBUG)
			Log.i(TAG, "	--->MusicPlayer--->getCurPosition");
		try {
			if (mPlayState == MusicPlayState.MPS_PLAYING
					|| mPlayState == MusicPlayState.MPS_PAUSE) {
				return mMediaPlayer.getCurrentPosition();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return 0;
	}

	// 获取当前播放歌曲的总时间
	public int getDuration() {
		try {
			if (mPlayState == MusicPlayState.MPS_PLAYING
					|| mPlayState == MusicPlayState.MPS_PAUSE) {
				return mMediaPlayer.getDuration();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return 0;
	}

	// 重新播放（暂停状态下点击播放按钮）
	protected boolean rePlay() {
		if (DEBUG)
			Log.i(TAG, "	--->MusicPlayer--->rePlay");
		if (mPlayState == MusicPlayState.MPS_NOFILE
				|| mPlayState == MusicPlayState.MPS_INVALID) {
			return false;
		}
		mMediaPlayer.start();
		mPlayState = MusicPlayState.MPS_PLAYING;
		sendPlayStateBrocast();
		return true;
	}
}
