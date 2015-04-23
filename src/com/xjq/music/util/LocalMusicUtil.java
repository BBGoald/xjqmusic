package com.xjq.music.util;

import java.io.File;
import java.text.CollationKey;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.xjq.music.model.MusicInfomation;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;
/**
 * 本地音乐列表工具类
 * 用来获取本地音乐列表
 * @author root
 *
 */
public class LocalMusicUtil {
	
	private static final String TAG = "xjq";

	public static List<MusicInfomation> getLocalAudioList(Context context)
	{
		Log.i(TAG, "	--->LocalMusicUtil--->getLocalAudioList");
		Log.i(TAG, "	-----------------getLocalAudioList------------------start----------------");
		List<MusicInfomation> musicList = new ArrayList<MusicInfomation>();
		ContentResolver resolver = context.getContentResolver();
		String order = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
		Cursor cursor = resolver.query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
				order);
		if (cursor.moveToFirst())
		{
			for (int i = 0; i < cursor.getCount(); i++)
			{
				cursor.moveToPosition(i);
				MusicInfomation audio = new MusicInfomation();
				//audio.setPlayTime(cursor.getLong(cursor
				//		.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
				audio.setName(cursor.getString(cursor
						.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
				audio.setArtist(cursor.getString(cursor
						.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
				//audio.setAlbum(cursor.getString(cursor
				//		.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)));
				audio.setPath(cursor.getString(cursor
						.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
				musicList.add(audio);
			}
		}
		cursor.close();
		Collections.sort(musicList, new CollatorComparator());
		Log.i(TAG, "	--->LocalMusicUtil--->getLocalAudioList return######musicList= " + musicList);
		Log.i(TAG, "	-----------------getLocalAudioList------------------end----------------");
		return musicList;
	}
	
	public static class CollatorComparator implements Comparator<Object>
	{
		Collator collator = Collator.getInstance();

		public int compare(Object element1, Object element2)
		{
			try
			{
				java.io.File file1 = new java.io.File(
						((MusicInfomation) element1).getPath());
				java.io.File file2 = new java.io.File(
						((MusicInfomation) element2).getPath());
				CollationKey key1 = collator.getCollationKey(file1.getName());
				CollationKey key2 = collator.getCollationKey(file2.getName());

				return key1.compareTo(key2);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			return 0;
		}
	}
	
	public static void deleteAudioFile(String path)
	{
		File file = new File(path);
		if(file.exists())
		{
			file.delete();
		}
	}
	
	public static void deleteMediaStoreFile(Context context, String filepath) {
		ContentResolver resolver = context.getContentResolver();
		/*resolver.delete(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, 
				Contacts., null);*/

		Log.i(TAG, "	--->LocalMusicUtil--->deleteMediaStoreFile");
		resolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				MediaStore.Audio.Media.DATA + "=?" /*+ filepath + " '" */, new String[]{filepath});
	}
}
