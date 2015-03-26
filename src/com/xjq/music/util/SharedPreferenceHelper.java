package com.xjq.music.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferenceHelper {

	public static void writePreferenceIntValue(Context context,String fileName,String key, int value) {
		try {
			SharedPreferences preferences = context.getSharedPreferences(fileName, 0);
			Editor editor = preferences.edit();
			editor.remove(key);
			editor.putInt(key, value);
			editor.commit();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
