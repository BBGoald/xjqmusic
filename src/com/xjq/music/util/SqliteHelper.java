package com.xjq.music.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库工具类 作用：创建数据库的相应表
 * 
 * @author root
 * 
 */
public class SqliteHelper extends SQLiteOpenHelper {

	public static final String localmusicTableName = "local";
	public static final String historyTableName = "history";

	public int referencedCount;

	public SqliteHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
		referencedCount = 0;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		createLocalMusic(db);
		createHistoryTable(db);

	}

	// 创建历史记录表
	private void createHistoryTable(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String historySql = "CREATE TABLE IF NOT EXISTS "
				+ historyTableName
				+ " (id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "name VARCHAR,artist VARCHAR,album VARCHAR,path VARCHAR,duration INT,"
				+ "pluginPath VARCHAR,singerImg VARCHAR,fileType INT)";
		db.execSQL(historySql);
	}

	// 创建本地表
	private void createLocalMusic(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String localMusiSql = "CREATE TABLE IF NOT EXISTS "
				+ localmusicTableName
				+ " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ " name VARCHAR,artist VARCHAR,path VARCHAR UNIQUE,stat VERCHAR)";
		db.execSQL(localMusiSql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
