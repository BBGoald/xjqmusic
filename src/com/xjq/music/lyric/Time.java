package com.xjq.music.lyric;

import android.util.Log;


public class Time {

	private static final String TAG = "xjq";
	protected int mSeconds;

	public Time(int ms){
		this.mSeconds = ms;
	}
	protected Time(String format, String value) {
		Log.i(TAG, "******Time ###format= " + format + "	value= " + value);
		try {
			Log.i(TAG, "..........");
			//if (format.equalsIgnoreCase("mm:ss.cs")) {
			//if (format.equals("mm:ss.cs")) {
				int m, s, cs;
				m = Integer.parseInt(value.substring(0, 2));
				s = Integer.parseInt(value.substring(3, 4));
				cs = Integer.parseInt(value.substring(6, 7));
				mSeconds = cs*10 + s*1000 + m*60000;
				Log.i(TAG, "******Time ###mSeconds= " + mSeconds);
			//}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			mSeconds = 0;
		}
	}
}
