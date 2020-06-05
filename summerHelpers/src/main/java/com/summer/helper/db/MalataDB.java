package com.summer.helper.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 应用商店数据库
 * @author malata_xiaqiliang
 * @time 2016年6月4日
 */
public class MalataDB extends SQLiteOpenHelper {

	private SQLiteDatabase db = null;
	/** 数据库名称 */
	private static String DB = "malatcreatordb";
	/** 版本号 */
	private final static int VERSIONCODE = 2;

	public MalataDB(Context context) {
		super(context, DB, null, VERSIONCODE);
	}
	
	/**
	 * 需要在引用的Application里设置名称
	 * @param name
	 */
	public static void initDBName(String name){
		DB = name;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		/** 公用数据库 */
		String commonDatabase = "create table commonDatabase(id integer primary key autoincrement,"
				+ "cacheData mediumblob,"
				+ "type integer,"
				+ "count integer,"
				+ "key text,"
				+ "next integer,"
				+ "createTime integer)";
		db.execSQL(commonDatabase);
		
		/** 资源数据库 */
		String shortcutDatabase = "create table "+DBNames.RESOURCE_DB+"(id integer primary key autoincrement,"
				+ DBNames.DOWNLOAD_URL+" text,"
				+ DBNames.ITEM_NAME+" text,"
				+ DBNames.ICON+" text,"
				+ DBNames.DOWNLOAD_STATUS+" integer,"
				+ DBNames.TYPE_ID+" integer,"
				+ DBNames.LOCAL_PATH+" text,"
				+ "type integer,"
				+ "createTime integer)";
		db.execSQL(shortcutDatabase);
		/** 场景数据库 */
		String sceneDatabase = "create table "+SceneDBName.SCENE_DB+"(id integer primary key autoincrement,"
				+ SceneDBName.PREVIEW_URL+" text,"
				+ SceneDBName.ITEM_NAME+" text,"
				+ SceneDBName.TYPE_ID+" integer,"
				+ SceneDBName.SCENE_JSON+" text,"
				+ SceneDBName.UUID+" text,"
				+ "type integer,"
				+ "createTime integer)";
		db.execSQL(sceneDatabase);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "drop table if exists commonDatabase";
		db.execSQL(sql);
		sql = "drop table if exists "+DBNames.RESOURCE_DB;
		db.execSQL(sql);
		sql = "drop table if exists "+SceneDBName.SCENE_DB;
		db.execSQL(sql);
		onCreate(db);
	}
	
	// 开启读事务处理
	public void beginTransaction() {
		if(db == null)
			return;
		db = getWritableDatabase();
		db.beginTransaction();
	}

	// 停止事务处理
	public void endTransaction() {
		if(db == null)
			return;
		db.setTransactionSuccessful();
		db.endTransaction();
	}
}
