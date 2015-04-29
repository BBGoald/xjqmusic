package com.xjq.music.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

/**
 * 点击某首歌曲播放时的动画辅助类 作用：获取默认屏幕的尺寸
 * 
 * @author root
 * 
 */
public class DefaultPerfectScreen {

	private static final String TAG = "xjq";
	private Point sizePoint;

	@SuppressWarnings("deprecation")
	public DefaultPerfectScreen(Context context) {
		Display display = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		sizePoint = getRealSize(display);
		if (sizePoint.x > 0 && sizePoint.y > 0) {
			Log.i(TAG,
					"	--->FuncUtils--->beginClickAnimation size.x > 0 && size.y > 0 ###return"
							+ " sizePoint.x= " + sizePoint.x + " sizePoint.y= "
							+ sizePoint.y);
			return;
		}
		sizePoint.x = display.getWidth();
		sizePoint.y = display.getHeight();
	}

	private Point getRealSize(Display display) {
		// TODO Auto-generated method stub
		Point outSizePoint = new Point(-1, -1);
		Class<Display> c = Display.class;
		// int[] wh = {-1, -1};
		try {
			Class<?>[] params = new Class[1];
			params[0] = Point.class;
			Method m = c.getMethod("getRealSize", params);
			m.invoke(display, outSizePoint);
			return outSizePoint;
		} catch (NoSuchMethodException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return outSizePoint;
	}

	public int getWidth() {
		return sizePoint.x;
	}

	public int getHeight() {
		return sizePoint.y;
	}
}
