package com.xjq.music.util;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.xjq.music.model.MusicInfomation;

/**
 * 数据库帮助类
 * 
 * @author root
 * 
 */
public class DatabaseHelper {

	private static final String TAG = "xjq";
	private static SqliteHelper sqliteHelper;
	private static Context mContext;
	private static DatabaseHelper dbHelper;
	private final static String dbName = "system.db";
	private static final boolean DEBUG = false;
	private static Cursor cursor;
	public static int MAX_HISTORYS = 100;

	private DatabaseHelper() {
		if(DEBUG) Log.d(TAG, "******instance DatabaseHelper");
		sqliteHelper = new SqliteHelper(mContext, dbName, null, 1);
	}

	public static DatabaseHelper instance(Context context) {
		if(DEBUG) Log.d(TAG, "******instance DatabaseHelper instance");

		mContext = context;

		if (dbHelper == null) {
			dbHelper = new DatabaseHelper();
			// df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}

		return dbHelper;
	}

	// 获取本地音乐列表
	public static List<MusicInfomation> localMusicList() {
		if(DEBUG) Log.d(TAG, "		--->DatabaseHelper--->localMusicList");

		List<MusicInfomation> list = null;
		SQLiteDatabase database = sqliteHelper.getReadableDatabase();
		if(DEBUG) Log.d(TAG, "		--->DatabaseHelper--->localMusicList #database= "
				+ database);

		String sql = "select distinct name,artist,path from "
				+ SqliteHelper.localmusicTableName;
		if(DEBUG) Log.d(TAG, "		--->DatabaseHelper--->");

		try {
			cursor = database.rawQuery(sql, null);
			if(DEBUG) Log.d(TAG, "		--->DatabaseHelper--->localMusicList #cursor= "
					+ cursor);

			if (cursor != null) {
				list = new ArrayList<MusicInfomation>();
				while (cursor.moveToNext()) {
					try {
						MusicInfomation localInfomation = new MusicInfomation();
						String songName = cursor.getString(cursor
								.getColumnIndex("name"));
						localInfomation.setName(songName);
						String artist = cursor.getString(cursor
								.getColumnIndex("artist"));
						localInfomation.setArtist(artist);
						String path = cursor.getString(cursor
								.getColumnIndex("path"));
						localInfomation.setPath(path);

						if (!list.contains(localInfomation)) {
							list.add(localInfomation);
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			sqliteHelper.close();
		}
		if(DEBUG) Log.d(TAG, "		--->DatabaseHelper--->localMusicList--->return list= "
				+ list);

		return list;
	}

	// 是否存在历史记录
	public boolean isExistHistory(SQLiteDatabase db, MusicInfomation favorites) {

		boolean isExist = false;
		Cursor c = null;
		try {
			if (favorites != null) {
				c = db.rawQuery(
						"SELECT * FROM "
								+ SqliteHelper.historyTableName
								+ " where name=? and artist=? and album=? and duration=? and pluginPath=? and singerImg=? and fileType=?",
						getIsSameFavArrString(favorites));
			}
			if (null == c)
				return isExist;
			if (c.getCount() > 0) {
				isExist = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null)
				c.close();
		}

		return isExist;
		// return isExistFavorites(db, favorites);
	}

	private String[] getIsSameFavArrString(MusicInfomation favorites) {
		String name = null == favorites.getName() ? "" : favorites.getName();
		String artist = null == favorites.getArtist() ? "" : favorites
				.getArtist();
		String album = null == favorites.getAlbum() ? "" : favorites.getAlbum();
		String time = null == String.valueOf(favorites.getPlayTime()) ? ""
				: String.valueOf(favorites.getPlayTime());
		/*
		 * String pluginPath = null == favorites.getPluginPath() ? "" :
		 * favorites .getPluginPath(); String singerImg = null ==
		 * favorites.getSingerPoster() ? "" : favorites .getSingerPoster();
		 * String fileType = null == String.valueOf(favorites.getFileType()) ?
		 * "" : String.valueOf(favorites.getFileType());
		 * 
		 * return new String[] { name, artist, album, time, pluginPath,
		 * singerImg, fileType };
		 */
		return new String[] { name, artist, album, time };
	}

	// 增加历史记录
	public boolean addHistory(MusicInfomation history) {
		if(DEBUG) Log.d(TAG, "	--->DatabaseHelper--->addHistory");

		SQLiteDatabase db = sqliteHelper.getWritableDatabase();
		boolean success = false;
		try {
			if (isExistHistory(db, history)) {
				return false;
			}

			String sql = "insert into " + SqliteHelper.historyTableName
					+ " values(null,?,?,?,?,?,?,?,?)";
			db.execSQL(sql, getAddFavArrString(history));

			success = true;
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			sqliteHelper.close();
		}
		return success;
	}

	// 删除本地音乐歌曲
	public static void deleteLocalDatas(String path) {
		SQLiteDatabase db = sqliteHelper.getWritableDatabase();
		String deletSql = "delete from " + SqliteHelper.localmusicTableName
				+ " where path = \"" + path + "\"";
		try {
			db.execSQL(deletSql);
		} finally {
			// TODO: handle exception
			sqliteHelper.close();
		}
	}

	private String[] getAddFavArrString(MusicInfomation favorites) {
		String name = null == favorites.getName() ? "" : favorites.getName();
		String artist = null == favorites.getArtist() ? "" : favorites
				.getArtist();
		String album = null == favorites.getAlbum() ? "" : favorites.getAlbum();
		String path = null == favorites.getPath() ? "" : favorites.getPath();
		String time = null == String.valueOf(favorites.getPlayTime()) ? ""
				: String.valueOf(favorites.getPlayTime());
		/*
		 * String pluginPath = null == favorites.getPluginPath() ? "" :
		 * favorites .getPluginPath(); String singerImg = null ==
		 * favorites.getSingerPoster() ? "" : favorites .getSingerPoster();
		 * String fileType = null == String.valueOf(favorites.getFileType()) ?
		 * "" : String.valueOf(favorites.getFileType());
		 * 
		 * return new String[] { name, artist, album, path, time, pluginPath,
		 * singerImg, fileType };
		 */
		return new String[] { name, artist, album, path, time };
	}

	// 获取历史播放列表，暂未实现该功能。
	public List<MusicInfomation> getHistoryList() {
		if(DEBUG) Log.d(TAG, "	--->DatabaseHelper--->getHistoryList");
		List<MusicInfomation> list = null;
		SQLiteDatabase db = sqliteHelper.getReadableDatabase();
		try {
			String getSql = "SELECT * FROM " + SqliteHelper.historyTableName
					+ " order by id desc limit 0," + MAX_HISTORYS;
			Cursor cursor = db.rawQuery(getSql, null);
			if(DEBUG) Log.d(TAG, "	--->DatabaseHelper--->getHistoryList #cursor= " +
				cursor);

			if (null != cursor) {
				// Log.d(TAG, "	--->--->");

				list = new ArrayList<MusicInfomation>();
				while (cursor.moveToNext()) {
					try {
						// if(DEBUG) Log.d(TAG, "	--############>");

						MusicInfomation history = new MusicInfomation();
						history.setName(cursor.getString(cursor
								.getColumnIndex("name")));
						history.setArtist(cursor.getString(cursor
								.getColumnIndex("artist")));
						history.setAlbum(cursor.getString(cursor
								.getColumnIndex("album")));
						history.setPath(cursor.getString(cursor
								.getColumnIndex("path")));
						history.setPlayTime(cursor.getInt(cursor
								.getColumnIndex("duration")));
						if(DEBUG) Log.d(TAG,
							"	--->DatabaseHelper--->getHistoryList #history= " +
							history);

						if (!list.contains(history)) {
							if(DEBUG) Log.d(TAG,
								"	--->DatabaseHelper--->getHistoryList #history= "
								+ history);
							list.add(history);
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
				cursor.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			sqliteHelper.close();
		}
		if(DEBUG) Log.d(TAG, "	--->DatabaseHelper--->getHistoryList return #list= " +
			list);

		return list;
	}

	// 扫描本地音乐--->插入到本地音乐库(local)
	public static List<MusicInfomation> scanLocalMusic() {
		if(DEBUG) Log.i(TAG, "	--->DatabaseHelper--->scanLocalMusic");
		if(DEBUG) Log.i(TAG,
				"	-----------------scanLocalMusic------------------start----------------");

		List<MusicInfomation> audioList = LocalMusicUtil
				.getLocalAudioList(mContext);
		SQLiteDatabase db = sqliteHelper.getWritableDatabase();
		String deletesql = "delete from " + SqliteHelper.localmusicTableName;
		String sql = "insert into " + SqliteHelper.localmusicTableName
				+ " values(null,?,?,?,?)";
		try {
			db.execSQL(deletesql);
			for (int i = 0; i < audioList.size(); i++) {
				String path = audioList.get(i).getPath() == null ? ""
						: audioList.get(i).getPath();
				String name = audioList.get(i).getName() == null ? ""
						: audioList.get(i).getName();
				String artist = audioList.get(i).getArtist() == null ? ""
						: audioList.get(i).getArtist();
				String[] args = { name, artist, path, "" };
				try {
					db.execSQL(sql, args);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			sqliteHelper.close();
		}
		if(DEBUG) Log.i(TAG,
				"	--->DatabaseHelper--->scanLocalMusic return######audioList= "
						+ audioList);
		if(DEBUG) Log.i(TAG,
				"	-----------------scanLocalMusic------------------end----------------");
		return audioList;
	}
}
