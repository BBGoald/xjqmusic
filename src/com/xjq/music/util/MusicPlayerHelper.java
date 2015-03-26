package com.xjq.music.util;

import java.util.Locale;

import android.content.Context;
import android.widget.SeekBar;
import android.widget.TextView;

public class MusicPlayerHelper {
	Context context;

	public MusicPlayerHelper(Context context) {
		this.context = context;
	}

	public static void updateProgress(SeekBar playerSeekbBar,TextView txtCurTime,TextView txtTotalTime) {
		updateProgress(0, 0, playerSeekbBar, txtCurTime, txtTotalTime);
	}

	public static void updateProgress(long curTime, long totalTime, android.widget.ProgressBar playerSeekbBar,
			TextView txtCurTime, TextView txtTotalTime) {
		// TODO Auto-generated method stub
		curTime /= 1000;
		totalTime /= 1000;
		
		long curMinute = curTime / 60;
		long curSecond = curTime % 60;
		String curTimeString = String.format(Locale.getDefault(), "%02d:%02d", curMinute, curSecond);
		
		long totalMinute = totalTime / 60;
		long totalSecond = totalTime % 60;
		String totalTimeString = String.format(Locale.getDefault(), "%02d:%02d", totalMinute, totalSecond);
		
		int rate = 0;
		if (totalTime != 0) {
			rate = (int) ((float) curTime / totalTime * 100);
		}
		if (null != playerSeekbBar) {
			playerSeekbBar.setProgress(rate);
		}
		if (null != txtCurTime) {
			txtCurTime.setText(curTimeString);
		}
		if (null != txtTotalTime) {
			txtTotalTime.setText(totalTimeString);
		}
	}
}
