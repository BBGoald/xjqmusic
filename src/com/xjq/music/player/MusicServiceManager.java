package com.xjq.music.player;
import java.util.ArrayList;
import java.util.List;

import com.xjq.music.model.MusicInfomation;
import com.xjq.music.util.MusicPlayMode;

import android.R.integer;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class MusicServiceManager {
	
	private static final String TAG = "xjq";
	private Context mContext;
	private ServiceConnection mServiceConnection;
	private IMusicPlayerConnect mMusicConnect;
	private IOnServiceConnectComplete mIOnServiceConnectComplete;

	private Boolean mConnectComplete;

	private final static String SERVICE_NAME = MusicPlayerService.class.getName();

	
	public MusicServiceManager(Context context) {
		mContext = context;
		Log.d(TAG, "******instance MusicServiceManager");
/*		Log.d(TAG, "this= " + this.hashCode());
		Log.d(TAG, "MusicServiceManager.this= " + MusicServiceManager.this.hashCode());
		Log.d(TAG, "currentThreadID= " + Thread.currentThread().getId());
		Log.d(TAG, "currentProcessID= " + android.os.Process.myPid());
*/
		initDefault();
	}

	private void initDefault() {
		// TODO Auto-generated method stub
		Log.i(TAG, "	--->MusicServiceManager--->initDefault");
		mServiceConnection = new ServiceConnection() {
			
			@Override
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub
				Log.i(TAG, "	--->MusicServiceManager--->onServiceDisconnected");

			}
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				// TODO Auto-generated method stub
				Log.i(TAG, "	--->MusicServiceManager--->onServiceConnected");
				mMusicConnect = IMusicPlayerConnect.Stub.asInterface(service);
				Log.i(TAG, "	MusicServiceManager mMusicConnect = IMusicPlayerConnect.Stub.asInterface(service)");
				Log.i(TAG, "	MusicServiceManager AIDL interface instance: mMusicConnect= " + mMusicConnect);
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
	
	public boolean connectService() {
		Log.i(TAG, "	--->MusicServiceManager--->connectService");
		if (mConnectComplete == true) {
			return true;
		}
		Intent intent = new Intent(SERVICE_NAME);
		Log.d(TAG, "	--->MusicServiceManager SERVICE_NAME= " + SERVICE_NAME);

		if (mContext != null) {
			Log.i(TAG, "	--->MusicServiceManager bindService: " + SERVICE_NAME);
			mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
			Log.i(TAG, "	--->MusicServiceManager bindService: " + SERVICE_NAME);
			
			mContext.startService(intent);
			
			mConnectComplete = true;
			return true;
		}
		return false;
	}
	
	public void setOnServiceConnectComplete(IOnServiceConnectComplete iServiceConnectComplete) {
		mIOnServiceConnectComplete = iServiceConnectComplete;
	}
	
	public boolean disconnectService() {
		if (!mConnectComplete) {
			return true;
		}
		if (mContext != null) {
			Log.i(TAG, "	--->MusicServiceManager--->disconnectService");
			mContext.unbindService(mServiceConnection);
			mConnectComplete = false;
			return true;
		}
		return false;
	}
	
	public void setPlayListAndPlay(List<MusicInfomation> fileList, int index) {
		Log.i(TAG, "	--->MusicServiceManager--->setPlayListAndPlay");
		if (mMusicConnect != null) {
			try {
				mMusicConnect.setPlayListAndPlay(fileList, index);//set data to AIDL
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	
	public void setPlayList(List<MusicInfomation> fileList) {
		if (mMusicConnect != null) {
			try {
				Log.i(TAG, "	--->MusicServiceManager--->setPlayList #fileList= " + fileList);
				mMusicConnect.setPlayList(fileList);
			} catch (RemoteException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	
	public List<MusicInfomation> getFileList(){
		try {
			Log.i(TAG, "	--->MusicServiceManager--->getFileList");
			if (null != mMusicConnect) {
				List<MusicInfomation> musicFileList = new ArrayList<MusicInfomation>();
				mMusicConnect.getFileList(musicFileList);
				Log.i(TAG, "	--->MusicServiceManager--->getFileList #musicFileList.size()= " + musicFileList.size());
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
	
	public boolean play(int position) {
		if (mMusicConnect != null) {
			try {
				Log.i(TAG, "	--->MusicServiceManager--->mMusicConnect.play = " + position);
				return mMusicConnect.play(position);
			} catch (RemoteException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return false;
	}
	
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
	
	public boolean rePlay() {
		if (mMusicConnect != null) {
			try {
				Log.i(TAG, "	--->MusicServiceManager--->mMusicConnect.rePlay()");
				return mMusicConnect.rePlay();
			} catch (RemoteException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public boolean playPre() {
		if (mMusicConnect != null) {
			try {
				Log.i(TAG, "	--->MusicServiceManager--->mMusicConnect.playPre");
				return mMusicConnect.playPre();
			} catch (RemoteException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public boolean playNext() {
		if (mMusicConnect != null) {
			try {
				Log.i(TAG, "	--->MusicServiceManager--->mMusicConnect.playNext()");
				mMusicConnect.playNext();
			} catch (RemoteException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return true;
	}
	
	public boolean seekTo(int rate) {
		if (mMusicConnect != null) {
			try {
				Log.i(TAG, "	--->MusicServiceManager--->mMusicConnect.seekTo");
				return mMusicConnect.seekTo(rate);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		return false;
	}
	
	public void setPlayMode(int mode) {
		if (mMusicConnect != null) {
			try {
				Log.i(TAG, "	--->MusicServiceManager--->setPlayMode");
				mMusicConnect.setPlayMode(mode);
			} catch (RemoteException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	
	public int getPlayMode() {
		if (mMusicConnect != null) {
			try {
				Log.i(TAG, "	--->MusicServiceManager--->getPlayMode");
				return mMusicConnect.getPlayMode();
			} catch (RemoteException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return MusicPlayMode.MPM_LIST_LOOP_PLAY;//初始值设置
	}
	
	public int getCurPosition() {
		if (mMusicConnect != null) {
			try {
				Log.i(TAG, "	--->MusicServiceManager--->getCurPosition");
				return mMusicConnect.getCurPosition();
			} catch (RemoteException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return 0;
	}
	
	public int getDuration() {
		if (mMusicConnect != null) {
			try {
				Log.i(TAG, "	--->MusicServiceManager--->getDuration");
				return mMusicConnect.getDuration();
			} catch (RemoteException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return 0;
	}
	
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
	
	public MusicInfomation getCurrentMusicInfomation() throws RemoteException {
		Log.i(TAG, "	--->MusicServiceManager--->getCurrentMusicInfo");

		MusicInfomation info = null;
		if (mMusicConnect != null) {
			info = mMusicConnect.getCurrentMusicInfo();
		}
		return info;
	}
}
