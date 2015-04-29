package com.xjq.music.player;

import java.util.ArrayList;
import java.util.List;

import com.xjq.music.model.MusicInfomation;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * 服务管理，管理activity与服务发连接以及断开，数据的传送，广播的收发
 * 
 * @author root
 * 
 */
public class MusicServiceManager {

	private static final String TAG = "xjq";
	private static final boolean DEBUG = false;
	private Context mContext;
	private ServiceConnection mServiceConnection;
	private IMusicPlayerConnect mMusicConnect;
	private IOnServiceConnectComplete mIOnServiceConnectComplete;

	private Boolean mConnectComplete;

	private final static String SERVICE_NAME = MusicPlayerService.class
			.getName();

	public MusicServiceManager(Context context) {
		mContext = context;
		if (DEBUG)
			Log.d(TAG, "******instance MusicServiceManager");
		if (DEBUG)
			Log.d(TAG, "this= " + this.hashCode());
		if (DEBUG)
			Log.d(TAG,
					"MusicServiceManager.this= "
							+ MusicServiceManager.this.hashCode());
		if (DEBUG)
			Log.d(TAG, "currentThreadID= " + Thread.currentThread().getId());
		if (DEBUG)
			Log.d(TAG, "currentProcessID= " + android.os.Process.myPid());

		initDefault();
	}

	// 实例化服务连接类ServiceConnection
	private void initDefault() {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.i(TAG, "	--->MusicServiceManager--->initDefault");
		mServiceConnection = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub
				if (DEBUG)
					Log.i(TAG,
							"	--->MusicServiceManager--->onServiceDisconnected");

			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				// TODO Auto-generated method stub
				if (DEBUG)
					Log.i(TAG, "	--->MusicServiceManager--->onServiceConnected");
				mMusicConnect = IMusicPlayerConnect.Stub.asInterface(service);
				if (DEBUG)
					Log.i(TAG,
							"	MusicServiceManager mMusicConnect = IMusicPlayerConnect.Stub.asInterface(service)");
				if (DEBUG)
					Log.i(TAG,
							"	MusicServiceManager AIDL interface instance: mMusicConnect= "
									+ mMusicConnect);
				if (mMusicConnect != null) {
					if (mIOnServiceConnectComplete != null) {
						mIOnServiceConnectComplete.OnServiceConnectComplete();
					}
				}
			}
		};

		mConnectComplete = false;
		mMusicConnect = null;
	}

	// 连接服务
	public boolean connectService() {
		if (DEBUG)
			Log.i(TAG, "	--->MusicServiceManager--->connectService");
		if (mConnectComplete == true) {
			return true;
		}
		Intent intent = new Intent(SERVICE_NAME);
		if (DEBUG)
			Log.d(TAG, "	--->MusicServiceManager SERVICE_NAME= " + SERVICE_NAME);

		if (mContext != null) {
			if (DEBUG)
				Log.i(TAG, "	--->MusicServiceManager bindService: "
						+ SERVICE_NAME);
			mContext.bindService(intent, mServiceConnection,
					Context.BIND_AUTO_CREATE);
			if (DEBUG)
				Log.i(TAG, "	--->MusicServiceManager bindService: "
						+ SERVICE_NAME);

			mContext.startService(intent);

			mConnectComplete = true;
			return true;
		}
		return false;
	}

	public void setOnServiceConnectComplete(
			IOnServiceConnectComplete iServiceConnectComplete) {
		mIOnServiceConnectComplete = iServiceConnectComplete;
	}

	// 断开服务
	public boolean disconnectService() {
		if (!mConnectComplete) {
			return true;
		}
		if (mContext != null) {
			if (DEBUG)
				Log.i(TAG, "	--->MusicServiceManager--->disconnectService");
			mContext.unbindService(mServiceConnection);
			mConnectComplete = false;
			return true;
		}
		return false;
	}

	// 在“播放列表”界面点击某首歌曲，播放该歌曲
	public void setPlayListAndPlay(List<MusicInfomation> fileList, int index) {
		if (DEBUG)
			Log.i(TAG, "	--->MusicServiceManager--->setPlayListAndPlay");
		if (mMusicConnect != null) {
			try {
				mMusicConnect.setPlayListAndPlay(fileList, index);// set data to
																	// AIDL
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	// 点击底部播放歌曲界面的相应歌曲进行歌曲播放
	public void setPlayList(List<MusicInfomation> fileList) {
		if (mMusicConnect != null) {
			try {
				if (DEBUG)
					Log.i(TAG,
							"	--->MusicServiceManager--->setPlayList #fileList= "
									+ fileList);
				mMusicConnect.setPlayList(fileList);
			} catch (RemoteException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	// 获取歌曲列表
	public List<MusicInfomation> getFileList() {
		try {
			if (DEBUG)
				Log.i(TAG, "	--->MusicServiceManager--->getFileList");
			if (null != mMusicConnect) {
				List<MusicInfomation> musicFileList = new ArrayList<MusicInfomation>();
				mMusicConnect.getFileList(musicFileList);
				if (DEBUG)
					Log.i(TAG,
							"	--->MusicServiceManager--->getFileList #musicFileList.size()= "
									+ musicFileList.size());
				return musicFileList;
			}
		} catch (RemoteException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	// 获取当前播放歌曲在歌曲里表中的索引（位置）
	public int getCurPlayIndex() {
		if (mMusicConnect != null) {
			try {
				return mMusicConnect.getCurPlayIndex();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return -1;
	}

	// 播放歌曲
	public boolean play(int position) {
		if (mMusicConnect != null) {
			try {
				if (DEBUG)
					Log.i(TAG,
							"	--->MusicServiceManager--->mMusicConnect.play = "
									+ position);
				return mMusicConnect.play(position);
			} catch (RemoteException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return false;
	}

	// 获取当前播放歌曲的状态
	public int getPlayState() {
		if (mMusicConnect != null) {
			try {
				return mMusicConnect.getPlayState();
			} catch (RemoteException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return MusicPlayState.MPS_NOFILE;
	}

	// 暂停正在播放的歌曲
	public boolean pause() {
		if (mMusicConnect != null) {
			try {
				return mMusicConnect.pause();
			} catch (RemoteException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return false;
	}

	// 重新播放已经暂停的歌曲
	public boolean rePlay() {
		if (mMusicConnect != null) {
			try {
				if (DEBUG)
					Log.i(TAG,
							"	--->MusicServiceManager--->mMusicConnect.rePlay()");
				return mMusicConnect.rePlay();
			} catch (RemoteException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return false;
	}

	// 播放上一首歌曲
	public boolean playPre() {
		if (mMusicConnect != null) {
			try {
				if (DEBUG)
					Log.i(TAG,
							"	--->MusicServiceManager--->mMusicConnect.playPre");
				return mMusicConnect.playPre();
			} catch (RemoteException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return false;
	}

	// 播放下一首歌曲
	public boolean playNext() {
		if (mMusicConnect != null) {
			try {
				if (DEBUG)
					Log.i(TAG,
							"	--->MusicServiceManager--->mMusicConnect.playNext()");
				mMusicConnect.playNext();
			} catch (RemoteException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return true;
	}

	// 快进/快退
	public boolean seekTo(int rate) {
		if (mMusicConnect != null) {
			try {
				if (DEBUG)
					Log.i(TAG,
							"	--->MusicServiceManager--->mMusicConnect.seekTo");
				return mMusicConnect.seekTo(rate);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	// 设置播放模式
	public void setPlayMode(int mode) {
		if (mMusicConnect != null) {
			try {
				if (DEBUG)
					Log.i(TAG, "	--->MusicServiceManager--->setPlayMode");
				mMusicConnect.setPlayMode(mode);
			} catch (RemoteException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	// 获取播放模式
	public int getPlayMode() {
		if (mMusicConnect != null) {
			try {
				if (DEBUG)
					Log.i(TAG, "	--->MusicServiceManager--->getPlayMode");
				return mMusicConnect.getPlayMode();
			} catch (RemoteException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return MusicPlayMode.MPM_LIST_LOOP_PLAY;// 初始值设置
	}

	// 获取当前播放歌曲的进度
	public int getCurPosition() {
		if (mMusicConnect != null) {
			try {
				if (DEBUG)
					Log.i(TAG, "	--->MusicServiceManager--->getCurPosition");
				return mMusicConnect.getCurPosition();
			} catch (RemoteException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return 0;
	}

	// 获取当前播放歌曲的总时间
	public int getDuration() {
		if (mMusicConnect != null) {
			try {
				if (DEBUG)
					Log.i(TAG, "	--->MusicServiceManager--->getDuration");
				return mMusicConnect.getDuration();
			} catch (RemoteException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return 0;
	}

	// 发送当前播放歌曲的状态（暂停/播放）
	public void sendPlayStateBrocast() {
		if (mMusicConnect != null) {
			try {
				mMusicConnect.sendPlayStateBrocast();
			} catch (RemoteException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	// 获取当前播放歌曲的信息（歌手，歌名）
	public MusicInfomation getCurrentMusicInfomation() throws RemoteException {
		if (DEBUG)
			Log.i(TAG, "	--->MusicServiceManager--->getCurrentMusicInfo");
		MusicInfomation info = null;
		if (mMusicConnect != null) {
			info = mMusicConnect.getCurrentMusicInfo();
		}
		return info;
	}
}
