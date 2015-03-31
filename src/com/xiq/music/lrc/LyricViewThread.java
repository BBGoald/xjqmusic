package com.xiq.music.lrc;

import android.os.Handler;

import com.xjq.music.model.MusicInfomation;

public class LyricViewThread extends Thread{
	
	MusicInfomation infomation;
	LyricView lyricView;
	TimedTextObject timedTextObject;
	@SuppressWarnings("unused")
	private Handler mHandler;
	

	public LyricViewThread(MusicInfomation infomation,Handler mHandler,LyricView lyricView) {
		this.infomation = infomation;
		this.mHandler = mHandler;
		this.lyricView = lyricView;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
	}

}
