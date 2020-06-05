package com.summer.helper.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 公用的数据库，传入序列化对象与类型
 * 警告：Type类型严格按照@DBType 来写
 * @author malata_xiaqiliang
 * @time 2016年6月4日
 */
public class CommonDB extends MalataDB {

	public CommonDB(Context context) {
		super(context);
	}
	
	/**
	 * 插入数据与总数
	 * @param type
	 * @param cacheData
	 * @param createTime
	 * @return
	 */
	public synchronized long commonInsertData(int type,byte[] cacheData,int count,long createTime){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("type",type);
		cv.put("cacheData",cacheData);
		cv.put("count",count);
		cv.put("createTime",createTime);
		return db.insert("commonDatabase",null,cv);		
	}
	
	/**
	 * 插入一个正在下载的ThemeMenuApp
	 * @param type
	 * @param name
	 * @param path
	 * @param state
	 * @param url
	 * @param createTime
	 * @return
	 */
	public synchronized long insertThemeData(int type,String name,String path,int state
			,String url,long createTime){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(DBNames.ITEM_NAME,name);
		cv.put(DBNames.LOCAL_PATH, state);
		cv.put(DBNames.DOWNLOAD_URL,url);
		cv.put("type",type);
		cv.put("createTime",createTime);
		return db.insert(DBNames.RESOURCE_DB,null,cv);
	}

	/**
	 * 插入一个正在下载的ThemeMenuApp
	 * @return
	 */
	public synchronized long insertThemeData(AppsEntity apps){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(DBNames.ITEM_NAME,apps.getName());
		cv.put(DBNames.LOCAL_PATH, apps.getSave_path());
		cv.put(DBNames.DOWNLOAD_URL,apps.getApk_url());
		cv.put(DBNames.ICON,apps.getIcon());
		cv.put("createTime",System.currentTimeMillis());
		cv.put("type",0);
		return db.insert(DBNames.RESOURCE_DB,null,cv);
	}
	
	public synchronized long updateThemeData(String name,int type) {
		SQLiteDatabase db = getWritableDatabase();
		String where = null;
		ContentValues cv = new ContentValues();
		cv.put("createTime", System.currentTimeMillis());
		where = DBNames.ITEM_NAME+"=?"+" and type =?";
	    String[] whereValue = {name,String.valueOf(type)};
	 	return db.update(DBNames.RESOURCE_DB, cv, where, whereValue);
	}
	
	/**
	 * 根据类型删除数据
	 * @param type
	 * @return
	 */
	public synchronized int deleteThemeDataByType(int type ){
		SQLiteDatabase db = getWritableDatabase();
		String where = "type="+type;
		return db.delete(DBNames.RESOURCE_DB,where,null);
	}
	
	/**
	 * 删除单条ThemeData数据
	 * @param name
	 * @param type
	 * @return
	 */
	public synchronized long deleteThemeData(String name,int type) {
		SQLiteDatabase db = getWritableDatabase();
		String where = null;
		where = DBNames.ITEM_NAME+"=?"+" and type =?";
	    String[] whereValue = {name,String.valueOf(type)};
	    return  db.delete(DBNames.RESOURCE_DB, where, whereValue);
	}
	
	public synchronized Cursor getThemeData(int type){		
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.query(DBNames.RESOURCE_DB,null,"type="+type + " order by createTime desc",null,null,null,null);
		return cursor;		
	}
	
	/**
	 * 通过ID与类型获取数据
	 * @param type
	 * @return
	 */
	public synchronized Cursor checkDataExist(String packageName,int type){		
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = null;
		cursor = db.query(DBNames.RESOURCE_DB,null,DBNames.ITEM_NAME+"="+"'" +packageName+ "'"+" and type ="+type,null,null,null,null);
		return cursor;		
	}
	
	
	/**
	 * 插入数据
	 * @param type
	 * @param cacheData
	 * @param createTime
	 * @return
	 */
	public synchronized long commonInsertData(int type,byte[] cacheData,long createTime){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("type",type);
		cv.put("cacheData",cacheData);
		cv.put("createTime",createTime);
		return db.insert("commonDatabase",null,cv);		
	}
	
	/**
	 * 插入数据（不同组里的数据）
	 * @param type
	 * @param cacheData
	 * @param createTime
	 * @return
	 */
	public synchronized long commonInsertData(int type,byte[] cacheData,String key,long createTime){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("type",type);
		cv.put("key",key);
		cv.put("cacheData",cacheData);
		cv.put("createTime",createTime);
		return db.insert("commonDatabase",null,cv);		
	}	
	
	/**
	 * 插入next
	 * @param type
	 * @param createTime
	 * @return
	 */
	public synchronized long commonInsertNext(int type,int next,long createTime){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("type",type);
		cv.put("next",next);
		cv.put("createTime",createTime);
		return db.insert("commonDatabase",null,cv);		
	}	

	/**
	 * 插入数据（不同组里的数据）,带总数
	 * @param type
	 * @param cacheData
	 * @param createTime
	 * @return
	 */
	public synchronized long commonInsertData(int type,byte[] cacheData,int count,String key,long createTime){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("type",type);
		cv.put("key",key);
		cv.put("count",count);
		cv.put("cacheData",cacheData);
		cv.put("createTime",createTime);
		return db.insert("commonDatabase",null,cv);		
	}
	
	/**
	 * 插入数据（不同组里的数据）,带总数,带下一页查找
	 * @param type
	 * @param cacheData
	 * @param createTime
	 * @return
	 */
	public synchronized long commonInsertData(int type,byte[] cacheData,int count,String key,int pageIndex,long createTime){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("type",type);
		cv.put("key",key);
		cv.put("count",count);
		cv.put("cacheData",cacheData);
		cv.put("next",pageIndex);
		cv.put("createTime",createTime);
		return db.insert("commonDatabase",null,cv);		
	}
	
	/**
	 * 根据Type 和ID 修改数据，用于有下一页的
	 * @param type
	 * @param cacheData
	 * @return
	 */
	public synchronized long updateData(int type,byte[] cacheData,int count,String key,long createTime) {
		SQLiteDatabase db = getWritableDatabase();
		String where = "type=? and key =?";
		String[] whereValue = { String.valueOf(key), key};
		ContentValues cv = new ContentValues();
		cv.put("cacheData", cacheData);
		return db.update("commonDatabase", cv, where, whereValue);
	}
	
	/**
	 * 根据Type修改数据
	 * @param type
	 * @param cacheData
	 * @return
	 */
	public synchronized long updateData(int type,byte[] cacheData) {
		SQLiteDatabase db = getWritableDatabase();
		String where = "type="+type;
		String[] whereValue = { String.valueOf(type)};
		ContentValues cv = new ContentValues();
		cv.put("cacheData", cacheData);
		return db.update("commonDatabase", cv, where, whereValue);
	}
	
	/**
	 * 获取数据
	 * @param type
	 * @return
	 */
	public synchronized Cursor commonGetData(int type){		
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query("commonDatabase",null,"type="+type,null,null,null,"createTime desc");
		return cursor;		
	}
	
	/**
	 * 通过ID与类型获取数据
	 * @param type
	 * @return
	 */
	public synchronized Cursor commonGetData(int type, String key){		
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.query("commonDatabase",null,"type="+type+" and key='"+ key + "'",null,null,null,null);
		return cursor;		
	}
	
	/**
	 * 根据类型删除数据
	 * @param type
	 * @return
	 */
	public synchronized int commonDeleteData(int type ){
		SQLiteDatabase db = getWritableDatabase();
		String where = "type="+type;
		return db.delete("commonDatabase",where,null);
	}
	
	/**
	 * 删除所有数据
	 * @return
	 */
	public synchronized int commonDeleteData(){
		SQLiteDatabase db = getWritableDatabase();
		return db.delete("commonDatabase",null,null);
	}
	
	/**
	 * 根据ID与类型删除数据
	 */
	public synchronized int commonDeleteData(int type,String key ){
		SQLiteDatabase db = getWritableDatabase();
		String where = "type="+type+" and key='"+key + "'";
		return db.delete("commonDatabase",where,null);
	}
	
	/**
	 * 根据type获取next
	 * @param type
	 * @return
	 */
	public synchronized Cursor getNext(int type){
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query("commonDatabase",null,"type="+type,null,null,null,null);
		return cursor;		
	}
	
    /**
     * 当运用到一个参数进行判断时，就Next设为参数
     * @param type
     * @param next
     * @return
     */
	public int updateNext(int type, int next) {
		SQLiteDatabase db = getWritableDatabase();
		String where = "type=?";
		String[] whereValue = { String.valueOf(type) };
		ContentValues cv = new ContentValues();
		cv.put("next", next);
		return db.update("commonDatabase", cv, where, whereValue);
	}
	
	/**
     * 修改count
     * @param type
     * @return
     */
	public int updateCount(int type, int count,String key) {
		SQLiteDatabase db = getWritableDatabase();
		String where = "type=?"+" and key=?";
		String[] whereValue = { String.valueOf(type), "'%" + key + "%'"};
		ContentValues cv = new ContentValues();
		cv.put("count", count);
		return db.update("commonDatabase", cv, where, whereValue);
	}
	
	/**
	 * 根据Type修改数据
	 * @param type
	 * @param cacheData
	 * @return
	 */
	public synchronized long commonUpdateData(int type,byte[] cacheData) {
		SQLiteDatabase db = getWritableDatabase();
		String where = "type=?";
		String[] whereValue = { String.valueOf(type)};
		ContentValues cv = new ContentValues();
		cv.put("cacheData", cacheData);
		return db.update("commonDatabase", cv, where, whereValue);
	}
	
	/**
	 * 根据Type修改数据和ID
	 * @param type
	 * @param cacheData
	 * @return
	 */
	public synchronized long commonUpdateData(int type,String key,byte[] cacheData) {
		SQLiteDatabase db = getWritableDatabase();
		String where = "type=?"+" and key=?";
		String[] whereValue = { String.valueOf(type), key};
		ContentValues cv = new ContentValues();
		cv.put("cacheData", cacheData);
		return db.update("commonDatabase", cv, where, whereValue);
	}

}
