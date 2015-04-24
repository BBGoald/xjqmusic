package com.xjq.music.lyric;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class LyricView extends TextView {

	private static final String TAG = "xjq";
	private static final Boolean DEBUG = false;

	private Paint mPaint;
	private Paint mCurPaint;
	
	List<Lyric> list;
	TimedTextObject lyricObject;
	
	public int index = 0;

	private float mX;
	
	private float mY;

	private float middleY = 100;

	private float LineDistance = 40;
	
	/**
	 * 下面三个构造函数必须要都重载，否则在activity_play_detailxml文件里的<com.xjq.music.lyric.LyricView会报错。
	 * @param context
	 */
	public LyricView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public LyricView(Context context, AttributeSet attr) {
		super(context, attr);
		init();
	}
	
	public LyricView(Context context, AttributeSet attr, int i) {
		super(context, attr, i);
		init();
	}
	
	private void init() {
		// TODO Auto-generated method stub
		if (DEBUG) Log.i(TAG, "******instance LyricView--->init");
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

	@SuppressWarnings("rawtypes")
	public void setLyricObject(TimedTextObject lyricObject) {
		if (DEBUG) Log.i(TAG, "	--->LyricView--->setLrcObject before###lyricObject= " + lyricObject);
		this.lyricObject = lyricObject;
		list = new ArrayList<Lyric>();
		Iterator iterator = lyricObject.lyricsMap.entrySet().iterator();
		int i = 0;
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			//String keyEntryString = entry.getKey().toString();
			Lyric entryValueLyric = (Lyric) entry.getValue();
			list.add(entryValueLyric);
			i++;
		}
		if (DEBUG) Log.i(TAG, "	--->LyricView--->setLrcObject after###list= " + list + "	i= " + i);
	}
		
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		if (list != null) {
			if (DEBUG) Log.i(TAG, "	--->LyricView--->onDraw ###list.get(index).getTextContent()= "
					+ list.get(index).getTextContent()
					+ "	index= " + index);
		}
		if (canvas == null) {
			return;
		}
		
		super.onDraw(canvas);
		Paint paintPreOrLaterIndex = mPaint;
		Paint painCurrentIndex = mCurPaint;
		paintPreOrLaterIndex.setTextAlign(Paint.Align.CENTER);
		if (index == -1) {
			return;
		}
		painCurrentIndex.setTextAlign(Paint.Align.CENTER);

		if (list == null || list.size() == 0) {
			if (DEBUG) Log.i(TAG, "	--->LyricView--->onDraw ###list= null");
			canvas.drawText("There is no Lyric@_@\n木有歌词捏-_><_-", mX, middleY, painCurrentIndex);
			return;
		}
		
		// 先画当前行，之后再画他的前面和后面，这样就保持当前行在中间的位置
		if (list.get(index) == null) {
			return;
		}
		canvas.drawText(list.get(index).getTextContent(), mX, middleY, painCurrentIndex);

		// 画出当前行之前的句子
		float tempY = middleY;
		for (int i = index - 1; i > 0; i--) {
			tempY = tempY - LineDistance ;//当前行的上一行
			if (tempY < 0) {
				break;
			}
			if (DEBUG) Log.i(TAG, "	--->LyricView--->onDraw ###list.get(i).getTextContent()= "
					+ list.get(i).getTextContent());
			canvas.drawText(list.get(i).getTextContent(), mX, tempY, paintPreOrLaterIndex);
		}
		
		//画出当前行之后的句子
		tempY = middleY;
		for (int i = index + 1; i < list.size(); i++) {
			tempY = tempY + LineDistance;//当前行的下一行
			if (tempY > mY) {
				break;
			}
			canvas.drawText(list.get(i).getTextContent(), mX, tempY, paintPreOrLaterIndex);
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
	
	private int getCurIndex(Lyric lyric) {
		if (list == null || list.size() == 0) {
			return 0;
		}
		for (int i = 0; i < list.size(); i++) {
			Lyric lyricTemp = list.get(i);
			if (lyricTemp != lyric) {
				continue;
			}else {
				return i;
			}
		}
		return 0;
	}

	public long updateIndex(Lyric lyric) {
		if (lyric == null) {
			return -1;
		}
		index = getCurIndex(lyric);
		Log.i(TAG, "	--->LyricView--->updateIndex ###index= " + index);
		Log.i(TAG, "	--->LyricView--->updateIndex ###lyric.endTime.mSeconds - lyric.startTime.mSeconds= "
				+ (lyric.endTime.mSeconds - lyric.startTime.mSeconds));
		return lyric.endTime.mSeconds - lyric.startTime.mSeconds;
	}
	
	public void updateView() {
		Log.i(TAG, "	--->LyricView--->updateView--->postInvalidate");
		postInvalidate();
	}
}
