package com.xjq.music.player;

import com.xjq.xjqgraduateplayer.R;

import android.content.Context;
import android.widget.ImageButton;

/**
 * 选择播放模式类
 * 
 * @author root
 * 
 */
public class MusicPlayMode {

	public static final int MPM_SINGLE_LOOP_PLAY = 0; // 单曲循环

	public static final int MPM_ORDER_PLAY = 1; // 顺序播放

	public static final int MPM_LIST_LOOP_PLAY = 2; // 列表循环

	public static final int MPM_RANDOM_PLAY = 3; // 随机播放

	// toast方式提示用户已经设置的结果
	public static String showPlayMode(Context context, ImageButton button,
			int mode) {
		String result = context.getResources().getString(R.string.music_single);
		switch (mode) {
		case MusicPlayMode.MPM_SINGLE_LOOP_PLAY:
			button.setImageResource(R.drawable.topbar_single_loop);
			result = context.getResources().getString(R.string.music_single);
			break;
		case MusicPlayMode.MPM_LIST_LOOP_PLAY:
			button.setImageResource(R.drawable.topbar_loop_play);
			result = context.getResources().getString(
					R.string.music_circulation);
			break;
		case MusicPlayMode.MPM_ORDER_PLAY:
			button.setImageResource(R.drawable.topbar_order_play);
			result = context.getResources().getString(R.string.music_sequence);
			break;
		case MusicPlayMode.MPM_RANDOM_PLAY:
			button.setImageResource(R.drawable.topbar_random_play);
			result = context.getResources().getString(R.string.music_random);
			break;

		default:
			break;
		}
		return result;
	}
}
