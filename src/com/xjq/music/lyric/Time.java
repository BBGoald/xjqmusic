package com.xjq.music.lyric;


public class Time {

	protected int mSeconds;

	public Time(int ms){
		this.mSeconds = ms;
	}
	protected Time(String format, String value) {
		try {
			if (format.equalsIgnoreCase("mm:ss.cs")) {
				int m, s, cs;
				m = Integer.parseInt(value.substring(0, 2));
				s = Integer.parseInt(value.substring(3, 8));
				cs = Integer.parseInt(value.substring(6, 8));
				mSeconds = cs*10 + s*1000 + m*60000;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			mSeconds = 0;
		}
	}
}
