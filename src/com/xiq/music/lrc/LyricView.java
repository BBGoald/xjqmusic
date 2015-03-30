package com.xiq.music.lrc;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.TextView;

public class LyricView extends TextView{

	private static final String TAG = "xjq";

	private Paint mPaint;
	private Paint mCurPaint;
	
	List<Lyric> list;
	
	public LyricView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		Log.i(TAG, "******instance LyricView--->init");
		//非高亮部分
		mPaint = new Paint();
		mPaint.setAntiAlias(true);//设置为抗锯齿
		mPaint.setTextSize(24);
		mPaint.setColor(Color.WHITE);
		mPaint.setTypeface(Typeface.SERIF);//设置字体样式为西文
		
		//高亮部分，显示当前歌词
		mCurPaint = new Paint();
		mCurPaint.setAntiAlias(true);
		mCurPaint.setTextSize(36);
		mCurPaint.setColor(Color.YELLOW);
		mCurPaint.setTypeface(Typeface.SANS_SERIF);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		Log.i(TAG, "	--->LyricView--->onDraw");
		if (canvas == null) {
			return;
		}
		super.onDraw(canvas);
	}

}
