package com.xjq.music.setbg;

import java.io.IOException;
import java.io.InputStream;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.util.Log;
/**
 * 利用ContentResolver将选择的图片设置进系统，并将已经设置进系统的图片取出
 * @author root
 *
 */
public class SkinUtil {

	private static final boolean DEBUG = false;
	private static final String TAG = "xjq";
	private static final String key = "music_skin";

	//将选择的图片路径利用ContentResolver以键值对Key-Value的形式保存到系统中
	public static void setSelectedImg(Context context, String url) {
		if (DEBUG)
			Log.i(TAG, "	--->SkinUtil--->setSelectedImg");
		ContentResolver resolver = context.getContentResolver();
		Settings.System.putString(resolver, key, url);// 注意在配置文件Manifest.xml中添加读写权限
	}

	//根据键取出保存在系统中的对应的值，该值即图片的路径
	public static String getSelectedImg(Context context) {
		if (DEBUG)
			Log.i(TAG, "	--->SkinUtil--->setSelectedImg");
		ContentResolver resolver = context.getContentResolver();
		String url = Settings.System.getString(resolver, key);
		if (url == null || url.equals("")) {
			return "";
		}
		return url;
	}

	//根据保存的图片路径获取该图片
	@SuppressWarnings("deprecation")
	public static Drawable getDrawble(Context context, String path,
			boolean isAssets) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inPurgeable = true;
		Bitmap bitmap = null;
		try {
			if (!isAssets) {
				bitmap = BitmapFactory.decodeFile(path, options);
				if (bitmap == null) {
					return null;
				} else {
					return new BitmapDrawable(bitmap);
				}
			}
			InputStream inputStream = context.getAssets().open(path);
			bitmap = BitmapFactory.decodeStream(inputStream);
			inputStream.close();
			if (bitmap != null) {
				return new BitmapDrawable(bitmap);
			}
		} catch (OutOfMemoryError e) {
			// TODO: handle exception
			e.printStackTrace();
			if (bitmap != null && !bitmap.isRecycled()) {
				bitmap.recycle();
			}
			System.gc();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
