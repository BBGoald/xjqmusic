package com.xjq.music.player;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import com.xjq.music.model.MusicInfomation;

/**
 * 后台播放歌曲的服务
 * 
 * @author root
 * 
 */
public class MusicPlayerService extends Service {

	protected static final String TAG = "xjq";
	protected static final boolean DEBUG = false;
	private MusicPlayer mMusicPlayer;

	// private boolean isBinder = false;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.d(TAG, "--->MusicPlayerService--->onBind");
		// isBinder = true;
		return mBinder;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		if (DEBUG)
			Log.d(TAG, "******instance MusicPlayerService");
		mMusicPlayer = new MusicPlayer(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private IMusicPlayerConnect.Stub mBinder = new IMusicPlayerConnect.Stub() {

		@Override
		public boolean stop() throws RemoteException {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void setPlayMode(int mode) throws RemoteException {
			// TODO Auto-generated method stub
			if (DEBUG)
				Log.i(TAG, "	--->MusicPlayerService--->setPlayMode");
			mMusicPlayer.setPlayMode(mode);
		}

		@Override
		public void setPlayListAndPlay(List<MusicInfomation> playList, int index)
				throws RemoteException {
			// TODO Auto-generated method stub
			if (DEBUG)
				Log.i(TAG,
						"	--->MusicPlayerService--->setPlayListAndPlay ######mBinder = new IMusicPlayerConnect.Stub()= "
								+ mBinder.hashCode());
			mMusicPlayer.setPlayListAndPlay(playList, index);
		}

		@Override
		public void setPlayList(List<MusicInfomation> playList)
				throws RemoteException {
			// TODO Auto-generated method stub
			if (DEBUG)
				Log.i(TAG, "	--->MusicPlayerService--->setPlayList #playList= "
						+ playList);
			mMusicPlayer.setPlayList(playList);
		}

		@Override
		public void sendPlayStateBrocast() throws RemoteException {
			// TODO Auto-generated method stub
			mMusicPlayer.sendPlayStateBrocast();
		}

		@Override
		public boolean seekTo(int rate) throws RemoteException {
			// TODO Auto-generated method stub
			if (DEBUG)
				Log.i(TAG, "	--->MusicPlayerService--->seekTo");
			return mMusicPlayer.seekTo(rate);
		}

		@Override
		public boolean rePlay() throws RemoteException {
			// TODO Auto-generated method stub
			if (DEBUG)
				Log.i(TAG, "	--->MusicPlayerService--->rePlay");
			return mMusicPlayer.rePlay();
		}

		@Override
		public boolean playPre() throws RemoteException {
			// TODO Auto-generated method stub
			if (DEBUG)
				Log.i(TAG, "	--->MusicPlayerService--->playPre");
			return mMusicPlayer.playPre();
		}

		@Override
		public boolean playNext() throws RemoteException {
			// TODO Auto-generated method stub
			if (DEBUG)
				Log.i(TAG, "	--->MusicPlayerService--->playNext");
			return mMusicPlayer.playNext();
		}

		@Override
		public boolean play(int position) throws RemoteException {
			// TODO Auto-generated method stub
			if (DEBUG)
				Log.i(TAG, "	--->MusicPlayerService--->play ");
			return mMusicPlayer.play(position);
		}

		@Override
		public boolean pause() throws RemoteException {
			// TODO Auto-generated method stub
			if (DEBUG)
				Log.i(TAG, "	--->MusicPlayerService--->pause");
			return mMusicPlayer.pause();
		}

		@Override
		public int getPlayState() throws RemoteException {
			// TODO Auto-generated method stub
			if (DEBUG)
				Log.i(TAG, "	--->MusicPlayerService--->getPlayState");
			return mMusicPlayer.getPlayState();
		}

		@Override
		public int getPlayMode() throws RemoteException {
			// TODO Auto-generated method stub
			if (DEBUG)
				Log.i(TAG, "	--->MusicPlayerService--->getPlayMode");
			return mMusicPlayer.getPlayMode();
		}

		@Override
		public int getMusicFileType() throws RemoteException {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void getFileList(List<MusicInfomation> playList)
				throws RemoteException {
			// TODO Auto-generated method stub
			if (DEBUG)
				Log.i(TAG, "	--->MusicPlayerService--->getFileList #playList= "
						+ playList);
			List<MusicInfomation> tmp = mMusicPlayer.getFileList();
			int count = tmp.size();
			for (int i = 0; i < count; i++) {
				playList.add(tmp.get(i));
			}
		}

		@Override
		public int getDuration() throws RemoteException {
			// TODO Auto-generated method stub
			if (DEBUG)
				Log.i(TAG, "	--->MusicPlayerService--->getDuration");
			return mMusicPlayer.getDuration();
		}

		@Override
		public MusicInfomation getCurrentMusicInfo() throws RemoteException {
			// TODO Auto-generated method stub
			if (DEBUG)
				Log.i(TAG, "	--->MusicPlayerService--->getCurrentMusicInfo");
			return mMusicPlayer.getCurrentMusicInfomation();
		}

		@Override
		public int getCurPosition() throws RemoteException {
			// TODO Auto-generated method stub
			if (DEBUG)
				Log.i(TAG,
						"	--->MusicPlayerService--->getCurPosition ######mMusicPlayer.getCurPosition()= "
								+ mMusicPlayer.getCurPosition());
			return mMusicPlayer.getCurPosition();
		}

		@Override
		public int getCurPlayIndex() throws RemoteException {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getAudioSessionId() throws RemoteException {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void exit() throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void destroy() throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public IBinder asBinder() {
			// TODO Auto-generated method stub
			return super.asBinder();
		}

		@Override
		public boolean onTransact(int code, Parcel data, Parcel reply, int flags)
				throws RemoteException {
			// TODO Auto-generated method stub
			return super.onTransact(code, data, reply, flags);
		}

		public String getInterfaceDescriptor() {
			return null;
		}
	};
}
