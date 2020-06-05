//package com.shenqijiazu.helper.db;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.shenqijiazu.creativecenter.bean.MainSceneJson;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteException;
//import android.util.Log;
//
///**
// * 与场景相关的数据库
// * 警告：Type类型严格按照@DBType 来写
// * @author xiaqiliang
// *
// */
//public class SceneService {
//	
//	private Cursor mCursor ;
//	private SceneDB commonDB ;
//
//	public SceneService(Context context) {
//		super();
//		commonDB = new SceneDB(context);
//	}
//	
//	/**
//	 * 获取我创建的场景
//	 * @return
//	 */
//	public boolean saveOrUpdateScene(MainSceneJson info){
//		int type = DBType.MY_CREATOR_JSON;
//		try {
//			mCursor = commonDB.checkDataExist(info.getUuid(),type);
//			Log.i("mCursor...get", mCursor.getCount()+"----");
//			if(mCursor.getCount() == 0){
//				insertSceneData(info, type);
//			}else{
//				updateSceneData(info,type);
//			}
//		} catch (SQLiteException e) {
//			e.printStackTrace();
//		} finally{
//			closeDB();
//		}
//		return false;		
//	}
//	
//	/**
//	 * 插入最近玩的app
//	 * @param info
//	 * @param bitmap
//	 */
//	public synchronized void insertSceneData(MainSceneJson info,int type){
//		try {
//			commonDB.insertSceneData(type,info);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			closeDB();
//		}
//	}
//	
//	/**
//	 * 更新某条场景数据
//	 * @param info
//	 */
//	public void updateSceneData(MainSceneJson info,int type) {
//		try {
//			commonDB.updateThemeData(info,type);
//		} catch (SQLiteException e) {
//			e.printStackTrace();
//		} finally {
//			closeDB();
//		}
//	}
//	
//	/**
//	 * 根据type删除全部数据
//	 */
//	public synchronized void deleteMyScene(){
//		try {
//			commonDB.deleteThemeDataByType(DBType.MY_CREATOR_JSON);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			closeDB();
//		}
//	}
//	
//	/**
//	 * 获取最近下载过的APP N条数据
//	 * @return
//	 */
//	public List<MainSceneJson> getMySceneData(){
//		List<MainSceneJson> activitys = new ArrayList<MainSceneJson>();
//		try {
//			mCursor = commonDB.getThemeData(DBType.MY_CREATOR_JSON);
//			while(mCursor.moveToNext()){
//				MainSceneJson info = new MainSceneJson();
//				String name = mCursor.getString(mCursor.getColumnIndex(SceneDBName.ITEM_NAME));
//				String icon = mCursor.getString(mCursor.getColumnIndex(SceneDBName.PREVIEW_URL));
//				String json = mCursor.getString(mCursor.getColumnIndex(SceneDBName.SCENE_JSON));
//				String uuid = mCursor.getString(mCursor.getColumnIndex(SceneDBName.UUID));
//				long time = mCursor.getLong(mCursor.getColumnIndex("createTime"));
//				info.setPreviewUrl(icon);
//				info.setScenejson(json);
//				info.setUuid(uuid);
//				info.setTitle(name);
//				info.setCreateTime(time);
//				activitys.add(info);
//			}
//		} catch (SQLiteException e) {
//			e.printStackTrace();
//		}catch(OutOfMemoryError e){
//			
//		}finally{
//			closeDB();
//		}
//		return activitys != null ? activitys : null;		
//	}
//
//	
//	/**
//	 * 删除某条APP数据
//	 * @param info
//	 */
//	public void deleteScene(MainSceneJson info) {
//		try {
//			if(info == null){
//				return;
//			}
//			commonDB.deleteSceneData(info.getUuid(),DBType.MY_CREATOR_JSON);
//		} catch (SQLiteException e) {
//			e.printStackTrace();
//		} finally {
//			closeDB();
//		}
//	}
//	
//	
//	/**
//	 * 插入数据,带总数
//	 * @param type 
//	 * @param cacheData
//	 * @param createTime
//	 */
//	public synchronized void commonInsertData(final int type,final byte[] cacheData,final int count,final long createTime){
//		try {
//			commonDB.commonInsertData(type,cacheData,count,createTime);
//		} catch (SQLiteException e) {
//			e.printStackTrace();
//		} finally{
//			closeDB();
//		}
//	}
//	
//	/**
//	 * 根据ID插入数据,带总数，先删除之前的数据
//	 */
//	public synchronized void commonInsertSafeData(final int type,final byte[] cacheData,final int count,final long createTime){
//		try {
//			commonDB.commonDeleteData(type);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			closeDB();
//			commonInsertData(type,cacheData,count,createTime);
//		}
//	}
//	
//	/**
//	 * 插入next
//	 * @param type 
//	 * @param cacheData
//	 * @param createTime
//	 */
//	public synchronized void commonInsertNext(final int type,final int next,final long createTime){
//		try {
//			commonDB.commonInsertNext(type,next,createTime);
//		} catch (SQLiteException e) {
//			e.printStackTrace();
//		} finally{
//			closeDB();
//		}
//	}
//	
//	/**
//	 * 根据ID插入数据
//	 * @param type 
//	 * @param cacheData
//	 * @param createTime
//	 */
//	private synchronized void commonInsertData(final int type,final byte[] cacheData,final String id ,final long createTime){
//		try {
//			commonDB.commonInsertData(type,cacheData,id,createTime);
//		} catch (SQLiteException e) {
//			e.printStackTrace();
//		} finally{
//			closeDB();
//		}
//	}
//	
//	/**
//	 * 根据ID获取数据
//	 * @param 
//	 * @return
//	 */
//	public List<?> getListData(int type,String id){
//		List<?> activitys = null;
//		try {
//			mCursor = commonDB.commonGetData(type,id);
//			if(mCursor.moveToNext()){
//				activitys = (List<?> ) SerializeUtil.deserializeObject(mCursor.getBlob(
//						mCursor.getColumnIndex("cacheData")));
//			}
//		} catch (SQLiteException e) {
//			e.printStackTrace();
//		} finally{
//			closeDB();
//		}
//		return activitys != null ? activitys : null;		
//	}
//	
//	/**
//	 * 根据ID获取数据
//	 * @param 
//	 * @return
//	 */
//	public List<?> getListData(int type,long id){
//		List<?> activitys = null;
//		try {
//			mCursor = commonDB.commonGetData(type,id+"");
//			if(mCursor.moveToNext()){
//				activitys = (List<?> ) SerializeUtil.deserializeObject(mCursor.getBlob(
//						mCursor.getColumnIndex("cacheData")));
//			}
//		} catch (SQLiteException e) {
//			e.printStackTrace();
//		} finally{
//			closeDB();
//		}
//		return activitys != null ? activitys : null;		
//	}
//	
//	/**
//	 * 根据ID获取上对象的缓冲 
//	 * @param 
//	 * @return
//	 */
//	public Object getObjectData(int type, String id){
//		Object activitys = null;
//		try {
//			mCursor = commonDB.commonGetData(type,id);
//			if(mCursor.moveToNext()){
//				activitys =  SerializeUtil.deserializeObject(mCursor.getBlob(
//						mCursor.getColumnIndex("cacheData")));
//			}
//		} catch (SQLiteException e) {
//			e.printStackTrace();
//		} finally{
//			closeDB();
//		}
//		return activitys != null ? activitys : null;		
//	}
//	
//	/**
//	 * 根据ID获取上对象的缓冲 
//	 * @param 
//	 * @return
//	 */
//	public Object getObjectData(int type, long id){
//		Object activitys = null;
//		try {
//			mCursor = commonDB.commonGetData(type,id + "");
//			if(mCursor.moveToNext()){
//				activitys =  SerializeUtil.deserializeObject(mCursor.getBlob(
//						mCursor.getColumnIndex("cacheData")));
//			}
//		} catch (SQLiteException e) {
//			e.printStackTrace();
//		} finally{
//			closeDB();
//		}
//		return activitys != null ? activitys : null;		
//	}
//	
//	/**
//	 * 根据Url获取上对象的缓冲 
//	 * @param 
//	 * @return
//	 */
//	public boolean isHistoryItemExist(int type, String keywords){
//		try {
//			mCursor = commonDB.commonGetData(type, keywords);
//			if(mCursor != null && mCursor.getCount() > 0)
//				return true;
//		} catch (SQLiteException e) {
//			e.printStackTrace();
//		} finally{
//			closeDB();
//		}
//		return false;		
//	}
//	
//	/**
//	 * 根据Url获取上对象的缓冲 
//	 * @param 
//	 * @return
//	 */
//	public boolean isDownloadItemExist(int type, String url){
//		try {
//			mCursor = commonDB.commonGetData(type, url);
//			if(mCursor != null && mCursor.getCount() > 0)
//				return true;
//		} catch (SQLiteException e) {
//			e.printStackTrace();
//		} finally{
//			closeDB();
//		}
//		return false;		
//	}
//	
//	/**
//	 * 根据ID获取集合数据
//	 * @param 
//	 * @return
//	 */
//	public List<?> commonGetListData(int type){
//		List<Object> activitys = new ArrayList<Object>();
//		try {
//			mCursor = commonDB.commonGetData(type);
//			while(mCursor.moveToNext()){
//				Object obj = (Object) SerializeUtil.deserializeObject(mCursor.getBlob(
//						mCursor.getColumnIndex("cacheData")));
//				activitys.add(obj);
//			}
//		} catch (SQLiteException e) {
//			e.printStackTrace();
//		} finally{
//			closeDB();
//		}
//		return activitys != null ? activitys : null;		
//	}
//	
//	/**
//	 * 根据类型删除数据
//	 */
//	public synchronized void commonDeleteData(final int type){
//		try {
//			commonDB.commonDeleteData(type);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			closeDB();
//		}
//	}
//	
//	/**
//	 * 删除所有数据
//	 */
//	public synchronized void commonDeleteData(){
//		try {
//			commonDB.commonDeleteData();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			closeDB();
//		}
//	}
//	
//	/**
//	 * 根据ID删除数据
//	 */
//	public synchronized void commonDeleteData(final int type, final String id){
//		try {
//			commonDB.commonDeleteData(type,id);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			closeDB();
//		}
//	}
//	
//	/**
//	 * 获取Count
//	 */
//	public int getCount(int type){
//		int count = 0;
//		try {
//			mCursor = commonDB.commonGetData(type);
//			if( mCursor.moveToNext()){
//				count = mCursor.getInt(mCursor.getColumnIndex("count"));
//				return count ;
//			}
//		} catch (SQLiteException e) {
//			e.printStackTrace();
//		} finally{
//			closeDB();
//		}
//		return 0;	
//	}
//	
//	/**
//	 * 根据ID获取Count
//	 */
//	public int getCount(int type,String id){
//		int count = 0;
//		try {
//			mCursor = commonDB.commonGetData(type,id);
//			if(mCursor.moveToNext()){
//				count = mCursor.getInt(mCursor.getColumnIndex("count"));
//			}
//		} catch (SQLiteException e) {
//			e.printStackTrace();
//		} finally{
//			closeDB();
//		}
//		return count  ;	
//	}
//	
//	/**
//	 * 根据type获取next
//	 */
//	public int getNext(int type){
//		int next = 0;
//		try {
//			mCursor = commonDB.commonGetData(type);
//			if(mCursor.moveToNext()){
//				next = mCursor.getInt(mCursor.getColumnIndex("next"));
//			}
//		} catch (SQLiteException e) {
//			e.printStackTrace();
//		} finally{
//			closeDB();
//		}
//		return next  ;	
//	}
//	
//	/**
//	 * 根据ID和type获取next
//	 */
//	public int getNext(int type,String id){
//		int next = 0;
//		try {
//			mCursor = commonDB.commonGetData(type,id);
//			if(mCursor.moveToNext()){
//				next = mCursor.getInt(mCursor.getColumnIndex("next"));
//			}
//		} catch (SQLiteException e) {
//			e.printStackTrace();
//		} finally{
//			closeDB();
//		}
//		return next  ;	
//	}
//	
//	/**
//	 * 根据ID和type获取next
//	 */
//	public int getNext(int type,long id){
//		int next = 0;
//		try {
//			mCursor = commonDB.commonGetData(type,id+"");
//			if(mCursor.moveToNext()){
//				next = mCursor.getInt(mCursor.getColumnIndex("next"));
//			}
//		} catch (SQLiteException e) {
//			e.printStackTrace();
//		} finally{
//			closeDB();
//		}
//		return next  ;	
//	}
//	
//	/**
//	 * 修改Next
//	 * @param type
//	 * @param next
//	 * @return
//	 */
//	public int updateNext(int type, int next) {
//		int i = -1;
//		try {
//			i = commonDB.updateNext(type, next);
//		} catch (SQLiteException e) {
//			e.printStackTrace();
//		} finally {
//			closeDB();
//		}
//		return i;
//	}
//	
//	/**
//	 * 修改用户数据,根据TYPE
//	 * @param dingzaiId
//	 * @param cacheData
//	 */
//	public void commonUpdateData(int type,byte[] cacheData) {
//		try {
//			commonDB.commonUpdateData(type, cacheData);
//		} catch (SQLiteException e) {
//			e.printStackTrace();
//		} finally {
//			closeDB();
//		}
//	}
//	
//	/**
//	 * 修改用户数据,根据TYPE和ID
//	 * @param dingzaiId
//	 * @param cacheData
//	 */
//	public void commonUpdateData(int type,String id,byte[] cacheData) {
//		try {
//			commonDB.commonUpdateData(type, id, cacheData);
//		} catch (SQLiteException e) {
//			e.printStackTrace();
//		} finally {
//			closeDB();
//		}
//	}
//	
//	/**
//	 * 关闭数据库
//	 */
//	public void closeDB() {
//		if (mCursor != null) {
//			mCursor.close();
//		}
//		if (commonDB != null) {
//			commonDB.endTransaction();
//			commonDB.close();
//		}
//	}
//
//}
