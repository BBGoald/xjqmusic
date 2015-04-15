package com.xiq.music.lyric;
import android.os.Handler;

import com.xjq.music.model.MusicInfomation;

public class LyricViewThread extends Thread{
	
	MusicInfomation infomation;
	LyricView lyricView;
	TimedTextObject timedTextObject;
	@SuppressWarnings("unused")
	private Handler mHandler;

	private String mEncode = "UTF-8";

	public LyricViewThread(MusicInfomation infomation,Handler mHandler,LyricView lyricView) {
		this.infomation = infomation;
		this.mHandler = mHandler;
		this.lyricView = lyricView;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		TimedTextObject timedTextObject;
		//timedTextObject = FormatLyric.parseFile(is, mEncode);
	}

}
