package com.xjq.music.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 设置播放模式工具类 作用：使用SharedPreferences来保存对播放模式的修改
 * 
 * @author root
 * 
 */
public class SharedPreferenceHelper {

	public static void writePreferenceIntValue(Context context,
			String fileName, String key, int value) {
		try {
			SharedPreferences preferences = context.getSharedPreferences(
					fileName, 0);
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
