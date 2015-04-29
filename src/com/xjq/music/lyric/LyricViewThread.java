package com.xjq.music.lyric;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import android.os.Handler;
import android.util.Log;

import com.xjq.music.lyric.TimedTextObject.TimedIndex;
import com.xjq.music.model.MusicInfomation;

public class LyricViewThread extends Thread {

	private static final String TAG = "xjq";
	public static final Boolean DEBUG = false;
	private static int interval = 100;

	MusicInfomation infomation;
	LyricView lyricView;
	TimedTextObject timedTextObject;
	Handler mHandler;
	private boolean isFinish;
	private String lyricPathString;
	private String mTextString = "";

	// private String mEncode = "UTF-8";

	public LyricViewThread(MusicInfomation infomation, Handler mHandler,
			LyricView lyricView) {
		this.infomation = infomation;
		this.mHandler = mHandler;
		this.lyricView = lyricView;
		this.isFinish = false;
	}

	public void setFinishFlag(boolean isFinish) {
		this.isFinish = isFinish;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		// Log.i(TAG, "	--->LyricViewThread--->run ###infomation= " +
		// infomation);
		lyricPathString = loadCurLyricPath(infomation);
		if (DEBUG)
			Log.i(TAG, "	--->LyricViewThread--->run ###lyricPathString= "
					+ lyricPathString);
		parseAndSetLyricFromPath(lyricPathString);
		TimedIndex timedIndex = new TimedIndex(0);
		while (!isFinish) {
			timedIndex.startIndex = getCurrentPosition();
			timedIndex.endIndex = timedIndex.startIndex + 100;
			if (DEBUG)
				Log.i(TAG, "	--->LyricViewThread--->run ###timedIndex= "
						+ timedIndex);
			if (timedTextObject == null) {
				isFinish = true;
			}
			Lyric lyric = timedTextObject.getLyric(timedIndex);
			if (DEBUG)
				Log.i(TAG, "	--->LyricViewThread--->run ###lyric= " + lyric);
			if (lyric == null) {
				isFinish = false;
			}
			if (lyric != null && !mTextString.equals(lyric.getTextContent())) {
				mTextString = lyric.getTextContent();
				if (DEBUG)
					Log.i(TAG, "	--->LyricViewThread--->run ###mTextString= "
							+ mTextString);
				lyricView.updateIndex(lyric);
				handleSubTitleChanged();// 处理下一行歌词显示
			}
			try {
				Thread.sleep(interval);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	public void handleSubTitleChanged() {
		// TODO Auto-generated method stub
		// 将更新lyricView的动作（lyricView.updateView();）提交到UI线程执行。
		// 方式是利用UI线程与子线程的通信机制Handler来实现
		// 通过调用PlayDetailActivity.java中传进来的mHandler对象引用调用post()函数，将一个Urnnable对象发送消息到UI线程，
		// 最终在UI线程中执行动作updateView()，从而更新该UI界面的LyricView。
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				lyricView.updateView();
			}
		});
	}

	public int getCurrentPosition() {// 此方法已在MusicPlayActivity中重写
		// TODO Auto-generated method stub
		// Log.i(TAG, "	--->LyricViewThread--->getCurrentPosition");
		return 0;
	}

	private void parseAndSetLyricFromPath(String lyricPathString2) {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.i(TAG, "	--->LyricViewThread--->parseAndSetLyricFromPath");
		InputStream inputStream = null;

		File file = new File(lyricPathString2);
		if (DEBUG)
			Log.i(TAG,
					"	--->LyricViewThread--->parseAndSetLyricFromPath ###file= "
							+ file);
		if (!file.exists()) {
			if (DEBUG)
				Log.i(TAG, "	--->LyricViewThread ###!file.exists()");
			isFinish = true;
			return;
		}
		try {
			inputStream = new FileInputStream(file);
			timedTextObject = FormatLyric.parseFile(inputStream, "UTF-8");
			lyricView.setLyricObject(timedTextObject);
			inputStream.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private String loadCurLyricPath(MusicInfomation infomation) {
		// TODO Auto-generated method stub
		String tempPathString = "";
		tempPathString = infomation.getPath();
		if (DEBUG)
			Log.i(TAG,
					"	--->LyricViewThread--->loadCurLyricPath ###tempPathString= "
							+ tempPathString);
		tempPathString = tempPathString.substring(0,
				tempPathString.indexOf("."))
				+ ".lrc";
		if (tempPathString.equals("")) {
			return tempPathString;
		}
		return tempPathString;
	}

}
