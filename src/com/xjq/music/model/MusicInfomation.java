package com.xjq.music.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * 歌曲模型
 * 
 * @author root
 * 
 */
public class MusicInfomation implements Parcelable {

	private String mName;
	private final static String KEY_MUSIC_NAME = "MusicName";
	private String mArtist;
	private final static String KEY_MUSIC_ARTIST = "MusicArtist";
	private String mAlbum;
	private final static String KEY_MUSIC_ALBUM = "MusicAlbum";
	private String mPath;
	public final static String KEY_MUSIC_PATH = "MusicPath";
	private long mPlayTime;

	private static final String TAG = "xjq";
	

	private boolean isSelected = false;
	
	public final static String KEY_MUSIC_INFO = "MusicInfo";

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		Log.i(TAG, "	--->MusicInfomation--->describeContents = 0");
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		//Log.i(TAG, "	--->MusicInfomation--->writeToParcel");
		Bundle mBundle = new Bundle();
		mBundle.putString(KEY_MUSIC_NAME, mName);
		mBundle.putString(KEY_MUSIC_ARTIST, mArtist);
		mBundle.putString(KEY_MUSIC_ALBUM, mAlbum);
		mBundle.putString(KEY_MUSIC_PATH, mPath);
		dest.writeBundle(mBundle);
	}

	public static final Parcelable.Creator<MusicInfomation> CREATOR = new Parcelable.Creator<MusicInfomation>() {

		@Override
		public MusicInfomation createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			//Log.i(TAG, "	--->MusicInfomation--->createFromParcel");
			MusicInfomation data = new MusicInfomation();
			
			Bundle mBundle = new Bundle();
			mBundle = source.readBundle();
			data.mName = mBundle.getString(KEY_MUSIC_NAME);
			data.mArtist = mBundle.getString(KEY_MUSIC_ARTIST);
			data.mAlbum = mBundle.getString(KEY_MUSIC_ALBUM);
			data.mPath = mBundle.getString(KEY_MUSIC_PATH);
			
			return data;
		}

		@Override
		public MusicInfomation[] newArray(int size) {
			// TODO Auto-generated method stub
			//Log.i(TAG, "	--->MusicInfomation--->newArray");
			return new MusicInfomation[size];
		}
		
	};
	
	public void setName(String mMusicName) {
		//Log.i(TAG, "	--->MusicInfomation--->setName ######mMusicName= " + mMusicName);
		this.mName = mMusicName;
	}
	
	public String getName() {
		//Log.i(TAG, "	--->MusicInfomation--->getName ######mName= " + mName);
		return mName;
	}
	
	public void setArtist(String mMusicArtist) {
		//Log.i(TAG, "	--->MusicInfomation--->setArtist ######mMusicArtist= " + mMusicArtist);
		this.mArtist = mMusicArtist;
	}
	
	public String getArtist() {
		//Log.i(TAG, "	--->MusicInfomation--->getArtist ######mArtist= " + mArtist);
		return mArtist;
	}
	
	public void setAlbum(String mMusicAlbum) {
		//Log.i(TAG, "	--->MusicInfomation--->setAlbum ######mMusicAlbum= " + mMusicAlbum);
		this.mAlbum = mMusicAlbum;
	}
	
	public String getAlbum() {
		//Log.i(TAG, "	--->MusicInfomation--->getAlbum ######mAlbum= " + mAlbum);
		return mAlbum;
	}
	
	public void setPath(String mMusicPath) {
		//Log.i(TAG, "	--->MusicInfomation--->setPath ######mMusicPath= " + mMusicPath);
		this.mPath = mMusicPath;
	}

	public String getPath() {
		//Log.i(TAG, "	--->MusicInfomation--->getPath ######mPath= " + mPath);
		return mPath;
	}
	
	public void setPlayTime(long mMusicTime) {
		//Log.i(TAG, "	--->MusicInfomation--->setPlayTime ######mMusicTime= " + mMusicTime);
		this.mPlayTime = mMusicTime;
	}
	
	public long getPlayTime() {
		//Log.i(TAG, "	--->MusicInfomation--->getPlayTime ######mPlayTime= " + mPlayTime);
		return mPlayTime;
	}
	
	public void setSelected(boolean isSelected) {
		Log.i(TAG, "	--->MusicInfomation--->setSelected ######isSelected= " + isSelected);
		this.isSelected = isSelected;
	}
	
	public boolean isSelected() {
		Log.i(TAG, "	--->MusicInfomation--->isSelected ######isSelected= " + isSelected);
		return isSelected ;
	}
}
