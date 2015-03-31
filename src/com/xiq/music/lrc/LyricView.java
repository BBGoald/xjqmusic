package com.xiq.music.lrc;

import java.util.List;

import android.R.integer;
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
	
	public int index = 0;

	private float mX;
	
	private float mY;

	private float middleY;

	private float LineDistance = 50;
	
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
		
		if (list == null || list.size() == 0) {
			return;
		}
		
		super.onDraw(canvas);
		Paint paintLastIndex = mPaint;
		Paint painCurrentIndex = mCurPaint;
		paintLastIndex.setTextAlign(Paint.Align.CENTER);
		if (index == -1) {
			return;
		}
		painCurrentIndex.setTextAlign(Paint.Align.CENTER);
		
		// 先画当前行，之后再画他的前面和后面，这样就保持当前行在中间的位置
		if (list.get(index) == null) {
			return;
		}
		canvas.drawText(list.get(index).getTextContent(), mX, middleY, painCurrentIndex);

		// 画出当前行之前的句子
		float tempY = middleY;
		for (int i = index - 1; i > 0; i++) {
			tempY = tempY - LineDistance ;//当前行的上一行
			if (tempY < 0) {
				break;
			}
			canvas.drawText(list.get(i).getTextContent(), mX, tempY, painCurrentIndex);
		}
		
		//画出当前行之后的句子
		tempY = middleY;
		for (int i = index + 1; i < list.size(); i++) {
			tempY = tempY + LineDistance;//当前行的下一行
			if (tempY > mY) {
				break;
			}
			canvas.drawText(list.get(i).getTextContent(), mX, tempY, paintLastIndex);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		mX = w * 0.5f;//remember the center of the screen
		mY = h;
		middleY = h * 0.5f;
	}

	public void updateView() {
		Log.i(TAG, "	--->LyricView--->updateView--->postInvalidate");
		postInvalidate();
	}
}
