package com.xjq.music.util;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ProgressTimer {
	private static final String TAG = "xjq";
	private int mEventID2;
	private Handler mHandler;
	private Timer mTimer;
	private TimerTask mTimerTask; // 定时器任务
	private int mTimerInterval = 1000; // 定时器触发间隔时间(ms)
	private boolean mBStartTimer; // 定时器是否已开启
	private int count = 0;

	public ProgressTimer(Handler handler,  int eventID2) {
		initParam(handler, eventID2);
	}

	private void initParam(Handler handler, int eventID2) {
		mHandler = handler;
		mEventID2 = eventID2;
		mBStartTimer = false;
		mTimerTask = null;
		mTimer = new Timer();
	}
	
	public void startPlayTimer() {
		if (mHandler == null || mBStartTimer) {
			return;
		}
		mBStartTimer = true;
		mTimerTask = new MusicTimerTask();
		mTimer.schedule(mTimerTask, mTimerInterval, mTimerInterval);
		count = 0;
	}
	
	public void stopPlayTimer() {
		if (!mBStartTimer) {
			return;
		}
		mBStartTimer = false;
		if (mTimerTask != null) {
			//I dont known yet why i should comment it ==!,but it works when i touch "pause" button,
			//it no longer appears "timer already cancel" bug while popup dialog.
			//mTimer.cancel();
			Log.i(TAG, "	--->ProgressTimer--->stopPlayTimer ###mTimer= " + mTimer);
			mTimerTask = null;
			count = 0;
		}
	}
	
	class MusicTimerTask extends TimerTask {
		@Override
		public void run() {
			if (mHandler != null) {				
				int	what =mEventID2;
/*				if (BuildConfig.DEBUG) {
					Log.d("debug", "sendmessage to update progressbar");
				}
				*/
				Message msgMessage = mHandler.obtainMessage(what);
				msgMessage.sendToTarget();
				count++;
			}
		}
	}
}
