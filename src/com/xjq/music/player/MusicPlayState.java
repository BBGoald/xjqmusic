package com.xjq.music.player;

/**
 * 当前播放歌曲的状态
 * 
 * @author root
 * 
 */
public class MusicPlayState {

	public static final int MPS_NOFILE = -1; // 无音乐文件
	public static final int MPS_INVALID = MPS_NOFILE + 1; // 当前音乐文件无效
	public static final int MPS_PREPARE = MPS_NOFILE + 2; // 准备就绪
	public static final int MPS_PLAYING = MPS_NOFILE + 3; // 播放中
	public static final int MPS_PAUSE = MPS_NOFILE + 4; // 暂停
	public static final int MPS_COMPLETION = MPS_NOFILE + 5; // 播放结束
	public static final int MPS_STOP = MPS_NOFILE + 6; // 停止
	public static final int MPS_PREPAREING = MPS_NOFILE + 7; // 开始准备(获取播放地址)

	public static final int MPS_ERROR_PLAYE = 100; // 播放过程中出错
	public static final int MPS_NET_DISCONNECT = 200; // 没有联网
	public static final int MPS_ERROR_ADDRS = MPS_ERROR_PLAYE + 1; // 播放地址失效

	public static final int MPS_LRC = 600; // 歌词

	public static final String PLAY_STATE_NAME = "PLAY_STATE_NAME";
	public static final String PLAY_MUSIC_INDEX = "PLAY_MUSIC_INDEX";
	public static final String MUSIC_INVALID = "MUSIC_INVALID";
	public static final String MUSIC_PREPARE = "MUSIC_PREPARE";
	public static final String MUSIC_PLAY = "MUSIC_PLAY";
	public static final String MUSIC_PAUSE = "MUSIC_PAUSE";
	public static final String MUSIC_STOP = "MUSIC_STOP";
	public static final String MUSIC_LRC = "MUSIC_LRC";
	public static final String PLAY_TYPE = "PLAY_TYPE";

}
