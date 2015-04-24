package com.xjq.music.lyric;

import android.util.Log;


public class Time {

	private static final String TAG = "xjq";
	private static final Boolean DEBUG = false;
	protected int mSeconds;

	public Time(int ms){
		this.mSeconds = ms;
	}
	protected Time(String format, String value) {
		if (DEBUG) Log.i(TAG, "******Time ###format= " + format + "	value= " + value);
		try {
			if (DEBUG) Log.i(TAG, ".....mm:ss.cs..00:04.35...");
			//if (format.equalsIgnoreCase("mm:ss.cs")) {
			//if (format.equals("mm:ss.cs")) {
				int m, s, cs;
				m = Integer.parseInt(value.substring(0, 2));//此处注意substring的用法：0表示开始处的索引（包括），2表示结束处的索引（不包括）
				s = Integer.parseInt(value.substring(3, 5));
				cs = Integer.parseInt(value.substring(6, 8));
				mSeconds = m*60000 + s*1000 + cs*10;
				if (DEBUG) Log.i(TAG, "******Time ###mSeconds= " + mSeconds + " m= " + m + " s= " + s + " cs= " + cs);
			//}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			mSeconds = 0;
		}
	}
}
