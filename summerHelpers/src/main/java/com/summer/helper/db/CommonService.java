package com.summer.helper.db;

import java.util.ArrayList;
import java.util.List;

import com.summer.helper.utils.Logs;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import static android.R.attr.type;

/**
 * 公用的数据库，传入序列化对象与类型
 * 警告：Type类型严格按照@CommonDBType 来写
 */
public class CommonService {
	
	private Cursor mCursor ;
	private CommonDB commonDB ;
	private Context context;

	public CommonService(Context context) {
		super();
		this.context = context;
		commonDB = new CommonDB(context);
	}
	/**
	 * 插入数据
	 * @param type 
	 * @param cacheData
	 * @param createTime
	 */
	public synchronized void commonInsertData(final int type,final byte[] cacheData,final long createTime){
		if(null == commonDB){
			commonDB = new CommonDB(context);
		}
		try {
			commonDB.commonInsertData(type, cacheData, createTime);
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
	}
	
	/**
	 * 插入对象
	 * @param type
	 * @param cacheData
	 */
	public synchronized void insert(final int type,final Object cacheData){
		if(null == commonDB){
			commonDB = new CommonDB(context);
		}
		try {
			commonDB.commonDeleteData(type);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDB();
			commonInsertData(type,SerializeUtil.serializeObject(cacheData),System.currentTimeMillis());
		}
	}
	
	/**
	 * 插入最近玩的app
	 */
	public synchronized void insertThemeData(String name,String path,String url,int type){
		try {
			commonDB.insertThemeData(type,name, path, type, url,System.currentTimeMillis());
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDB();
		}
	}

	/**
	 * 插入最近玩的app
	 */
	public synchronized void insertThemeData(AppsEntity entity){
		try {
			commonDB.insertThemeData(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDB();
		}
	}
	
	/**
	 * 插入多个下载链接 
	 */
	public synchronized void insertThemeData(List<AppsEntity> infos,int type){
		for(AppsEntity info : infos){
//			insertThemeData(info,type);
		}
	}
	
	/**
	 * 根据ID删除数据
	 */
	public synchronized void deleteThemeDataByType(final int type){
		try {
			commonDB.deleteThemeDataByType(type);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDB();
		}
	}
	
	/**
	 * 获取最近下载过的APP N条数据
	 * @return
	 */
	public List<AppsEntity> getRecentThemeData(int count,int type){
		List<AppsEntity> activitys = new ArrayList<AppsEntity>();
		try {
			mCursor = commonDB.getThemeData(type);
			int index = 0;
			while(mCursor.moveToNext()){
				index ++;
				AppsEntity info = new AppsEntity();
				String name = mCursor.getString(mCursor.getColumnIndex(DBNames.ITEM_NAME));
				String path = mCursor.getString(mCursor.getColumnIndex(DBNames.DOWNLOAD_URL));
				int state = mCursor.getInt(mCursor.getColumnIndex(DBNames.TYPE_ID));
				String iconUrl = mCursor.getString(mCursor.getColumnIndex(DBNames.ICON));
				info.setIcon(iconUrl);
				String url = mCursor.getString(mCursor.getColumnIndex(DBNames.DOWNLOAD_URL));
				info.setApk_url(url);
				info.setName(name);
				info.setSave_path(path);
				activitys.add(info);
				if(index  == count){
					break;
				}
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		}catch(OutOfMemoryError e){
			
		}finally{
			closeDB();
		}
		return activitys != null ? activitys : null;		
	}
	
	public List<?> getMySceneData(int type){
		if(null == commonDB){
			commonDB = new CommonDB(context);
		}
		List<Object> activitys = new ArrayList<>();
		try {
			mCursor = commonDB.commonGetData(type);
			
			if(mCursor != null){
				while(mCursor.moveToNext()){
					Object sceneJson = (Object) SerializeUtil.deserializeObject(mCursor.getBlob(
							mCursor.getColumnIndex("cacheData")));
					Logs.i("xia","--"+sceneJson);
					if(null != sceneJson){
						activitys.add(sceneJson);
					}
				}
			}
			
		} catch (SQLiteException e) {
			e.printStackTrace();
			closeDB();
		}catch(OutOfMemoryError e){
			closeDB();
		}finally{
			closeDB();
		}
		return (activitys != null ? activitys : null);		
	}
	
	/**
	 * 获取最近玩过的APP十条数据
	 * @return
	 */
	public boolean isAPPExist(AppsEntity info){
		try {
			mCursor = commonDB.checkDataExist(info.getName(),0);
			Log.i("mCursor...get", mCursor.getCount()+"----");
			if(mCursor.getCount() == 0){
				insertThemeData(info);
			}else{
				updateThemeData(info,0);
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
		return false;		
	}
	
	/**
	 * 更新某条APP数据
	 * @param info
	 */
	public void updateThemeData(AppsEntity info,int type) {
		try {
			commonDB.updateThemeData(info.getName(),type);
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			closeDB();
		}
	}
	
	/**
	 * 删除某条APP数据
	 * @param info
	 */
	public void deleteAPP(AppsEntity info,int type) {
		try {
			if(info == null){
				return;
			}
			commonDB.deleteThemeData(info.getName(),type);
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			closeDB();
		}
	}
	
	
	/**
	 * 插入数据,带总数
	 * @param type 
	 * @param cacheData
	 * @param createTime
	 */
	public synchronized void commonInsertData(final int type,final byte[] cacheData,final int count,final long createTime){
		try {
			commonDB.commonInsertData(type,cacheData,count,createTime);
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
	}
	
	/**
	 * 根据ID插入数据,带总数，先删除之前的数据
	 */
	public synchronized void commonInsertSafeData(final int type,final byte[] cacheData,final int count,final long createTime){
		try {
			commonDB.commonDeleteData(type);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDB();
			commonInsertData(type,cacheData,count,createTime);
		}
	}
	
	/**
	 * 插入next
	 * @param type 
	 * @param cacheData
	 * @param createTime
	 */
	public synchronized void commonInsertNext(final int type,final int next,final long createTime){
		if(null == commonDB){
			commonDB = new CommonDB(context);
		}
		try {
			commonDB.commonInsertNext(type,next,createTime);
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
	}
	
	/**
	 * 根据ID插入数据
	 * @param type 
	 * @param cacheData
	 * @param createTime
	 */
	private synchronized long commonInsertData(final int type,final byte[] cacheData,final String id ,final long createTime){
		if(null == commonDB){
			commonDB = new CommonDB(context);
		}
		long result = 0;
		try {
			result = commonDB.commonInsertData(type,cacheData,id,createTime);
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
		return result;
	}
	
	/**
	 * 根据ID插入数据,带总数，先删除之前的数据
	 */
	public synchronized void commonInsertSafeData(final int type,final byte[] cacheData,final String id,final long createTime){
		if(null == commonDB){
			commonDB = new CommonDB(context);
		}
		try {
			commonDB.commonDeleteData(type,id);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDB();
			commonInsertData(type,cacheData,id,createTime);
		}
	}
	
	public synchronized void insert(final int type,final String id,final Object cacheData){
		if(null == commonDB){
			commonDB = new CommonDB(context);
		}
		try {
			int s = commonDB.commonDeleteData(type,id);
			Logs.i("xia","insert database status : "+s);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDB();
			commonInsertData(type,SerializeUtil.serializeObject(cacheData),id,System.currentTimeMillis());
		}
	}
	
	public synchronized void insert(final int type,final long id,final Object cacheData){
		if(null == commonDB){
			commonDB = new CommonDB(context);
		}
		try {
			commonDB.commonDeleteData(type,id+"");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDB();
			if(cacheData instanceof byte[]){
				commonInsertData(type,(byte[])cacheData,id+"",0);
			}else{
				commonInsertData(type,SerializeUtil.serializeObject(cacheData),id+"",0);
			}

		}
	}
	
	/**
	 * 根据ID插入数据,带总数
	 * @param type 
	 * @param cacheData
	 * @param createTime
	 */
	private synchronized void commonInsertData(final int type,final byte[] cacheData,final int count,final String id ,final long createTime){
		try {
			commonDB.commonInsertData(type, cacheData, count, id, createTime);
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
	}
	
	/**
	 * 根据ID插入数据,带总数，先删除之前的数据
	 */
	public synchronized void commonInsertSafeData(final int type,final byte[] cacheData,final int count,final String id,final long createTime){
		try {
			commonDB.commonDeleteData(type,id);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDB();
			commonInsertData(type,cacheData,count,id,createTime);
		}
	}
	
	/**
	 * 根据ID插入数据,带总数,带下一页参数
	 * @param type 
	 * @param cacheData
	 * @param createTime
	 */
	public synchronized void commonInsertData(final int type,final byte[] cacheData,final int count,final String id ,final int pageIndex,final long createTime){
		try {
			commonDB.commonInsertData(type,cacheData,count,id,pageIndex,createTime);
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
	}
	
//	public synchronized void insert(final int type,int count,final Object cacheData){
//		try {
//			commonDB.commonDeleteData(type);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			closeDB();
//			commonInsertData(type,SerializeUtil.serializeObject(cacheData),count,0);
//		}
//	}
	
	public synchronized void insert(final int type,final String id,int count,int pageIndex,final Object cacheData){
		try {
			commonDB.commonDeleteData(type,id);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDB();
			commonInsertData(type,SerializeUtil.serializeObject(cacheData),count,id,pageIndex,0);
		}
	}
	
	/**
	 * 根据ID插入数据,带总数,带下一页参数，先删除之前的数据
	 */
	public synchronized void commonInsertSafeData(final int type,final byte[] cacheData,final int count,final String id,final int pageIndex,final long createTime){
		try {
			commonDB.commonDeleteData(type,id);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDB();
			commonInsertData(type,cacheData,count,id,pageIndex,createTime);
		}
	}
	
	/**
	 * 根据Type 和ID 修改数据，用于有下一页的
	 * @param type
	 * @param cacheData
	 * @param count
	 * @param id
	 * @param createTime
	 */
	public synchronized void commonUpdateData(int type,byte[] cacheData,int count,String id,long createTime){
		try {
			commonDB.updateData(type,cacheData,count,id,createTime);
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
	}
	
	/**
	 * 获取数据
	 * @param 
	 * @return
	 */
	public List<?> getListData(int type){
		if(null == commonDB){
			commonDB = new CommonDB(context);
		}
		List<?> activitys = null;
		try {
			mCursor = commonDB.commonGetData(type);
			if(mCursor != null && mCursor.moveToNext()){
				activitys = (List<?> ) SerializeUtil.deserializeObject(mCursor.getBlob(
						mCursor.getColumnIndex("cacheData")));
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
		return activitys != null ? activitys : null;		
	}
		
	/**
	 * 获取对象数据
	 * @param 
	 * @return
	 */
	public Object getObjectData(int type){
		if(null == commonDB){
			commonDB = new CommonDB(context);
		}
		Object activitys = null;
		try {
			mCursor = commonDB.commonGetData(type);
			if(mCursor != null && mCursor.moveToNext()){
				activitys = SerializeUtil.deserializeObject(mCursor.getBlob(
						mCursor.getColumnIndex("cacheData")));
			}
		} catch (SQLiteException e) {
			closeDB();
			e.printStackTrace();
		} finally{
			closeDB();
		}
		return activitys != null ? activitys : null;		
	}
	
	/**
	 * 根据ID获取数据
	 * @param 
	 * @return
	 */
	public List<?> getListData(int type,String id){
		if(null == commonDB){
			commonDB = new CommonDB(context);
		}
		List<?> activitys = null;
		try {
			mCursor = commonDB.commonGetData(type,id);
			if(null != mCursor && !mCursor.isClosed() && mCursor.moveToNext() ){
				activitys = (List<?> ) SerializeUtil.deserializeObject(mCursor.getBlob(
						mCursor.getColumnIndex("cacheData")));
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			closeDB();
		}
		return activitys != null ? activitys : null;		
	}
	
	/**
	 * 根据ID获取数据
	 * @param 
	 * @return
	 */
	public List<?> getListData(int type,long id){
		if(null == commonDB){
			commonDB = new CommonDB(context);
		}
		List<?> activitys = null;
		try {
			mCursor = commonDB.commonGetData(type,id+"");
			
			if(null != mCursor && !mCursor.isClosed() && mCursor.moveToNext() ){
				activitys = (List<?> ) SerializeUtil.deserializeObject(mCursor.getBlob(
						mCursor.getColumnIndex("cacheData")));
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		}catch(IllegalStateException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeDB();
		}
		return activitys != null ? activitys : null;		
	}
	
	/**
	 * 根据ID获取上对象的缓冲 
	 * @param 
	 * @return
	 */
	public Object getObjectData(int type, String id){
		if(null == commonDB){
			commonDB = new CommonDB(context);
		}
		Object activitys = null;
		Cursor cursor = null;
		try {
			cursor = commonDB.commonGetData(type,id);
			if(null != cursor && cursor.moveToNext()){
				activitys =  SerializeUtil.deserializeObject(cursor.getBlob(
						cursor.getColumnIndex("cacheData")));
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB(cursor);
		}
		return activitys != null ? activitys : null;		
	}
	
	/**
	 * 根据ID获取上对象的缓冲 
	 * @param 
	 * @return
	 */
	public Object getObjectData(int type, long id){
		Object activitys = null;
		try {
			mCursor = commonDB.commonGetData(type,id + "");
			if(mCursor != null && mCursor.moveToNext()){
				byte[] data =  mCursor.getBlob(
						mCursor.getColumnIndex("cacheData"));
				activitys =  SerializeUtil.deserializeObject(data);
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
		return activitys != null ? activitys : null;		
	}

	/**
	 * 根据ID获取上对象的缓冲
	 * @param
	 * @return
	 */
	public Object getByteArrayData(int type, long id){
		Object activitys = null;
		try {
			mCursor = commonDB.commonGetData(type,id + "");
			if(mCursor.moveToNext()){
				activitys =  mCursor.getBlob(
						mCursor.getColumnIndex("cacheData"));
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
		return activitys != null ? activitys : null;
	}
	
	/**
	 * 根据Url获取上对象的缓冲 
	 * @param 
	 * @return
	 */
	public boolean isHistoryItemExist(int type, String keywords){
		try {
			mCursor = commonDB.commonGetData(type, keywords);
			if(mCursor != null && mCursor.getCount() > 0)
				return true;
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
		return false;		
	}
	
	/**
	 * 根据Url获取上对象的缓冲 
	 * @param 
	 * @return
	 */
	public boolean isDownloadItemExist(int type, String url){
		try {
			mCursor = commonDB.commonGetData(type, url);
			if(mCursor != null && mCursor.getCount() > 0)
				return true;
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
		return false;		
	}
	
	/**
	 * 根据ID获取集合数据
	 * @param 
	 * @return
	 */
	public List<?> commonGetListData(int type){
		List<Object> activitys = new ArrayList<Object>();
		try {
			mCursor = commonDB.commonGetData(type);
			while(mCursor.moveToNext()){
				Object obj = (Object) SerializeUtil.deserializeObject(mCursor.getBlob(
						mCursor.getColumnIndex("cacheData")));
				activitys.add(obj);
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
		return activitys != null ? activitys : null;		
	}
	
	/**
	 * 根据类型删除数据
	 */
	public synchronized void commonDeleteData(final int type){
		try {
			commonDB.commonDeleteData(type);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDB();
		}
	}
	
	/**
	 * 删除所有数据
	 */
	public synchronized void commonDeleteData(){
		try {
			commonDB.commonDeleteData();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDB();
		}
	}
	
	/**
	 * 根据ID删除数据
	 */
	public synchronized void commonDeleteData(final int type, final String id){
		if(null == commonDB){
			commonDB = new CommonDB(context);
		}
		try {
			int s = commonDB.commonDeleteData(type,id);
			Logs.i("xia",s+"ll");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDB();
		}
	}
	
	/**
	 * 获取Count
	 */
	public int getCount(int type){
		int count = 0;
		try {
			mCursor = commonDB.commonGetData(type);
			if( mCursor.moveToNext()){
				count = mCursor.getInt(mCursor.getColumnIndex("count"));
				return count ;
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
		return 0;	
	}
	
	/**
	 * 根据ID获取Count
	 */
	public int getCount(int type,String id){
		int count = 0;
		try {
			mCursor = commonDB.commonGetData(type,id);
			if(mCursor.moveToNext()){
				count = mCursor.getInt(mCursor.getColumnIndex("count"));
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
		return count  ;	
	}
	
	/**
	 * 根据type获取next
	 */
	public int getNext(int type){
		int next = 0;
		try {
			mCursor = commonDB.commonGetData(type);
			if(mCursor.moveToNext()){
				next = mCursor.getInt(mCursor.getColumnIndex("next"));
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
		return next  ;	
	}
	
	/**
	 * 根据ID和type获取next
	 */
	public int getNext(int type,String id){
		int next = 0;
		try {
			mCursor = commonDB.commonGetData(type,id);
			if(mCursor.moveToNext()){
				next = mCursor.getInt(mCursor.getColumnIndex("next"));
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
		return next  ;	
	}
	
	/**
	 * 根据ID和type获取next
	 */
	public int getNext(int type,long id){
		int next = 0;
		try {
			mCursor = commonDB.commonGetData(type,id+"");
			if(mCursor.moveToNext()){
				next = mCursor.getInt(mCursor.getColumnIndex("next"));
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally{
			closeDB();
		}
		return next  ;	
	}
	
	/**
	 * 修改Next
	 * @param type
	 * @param next
	 * @return
	 */
	public int updateNext(int type, int next) {
		int i = -1;
		try {
			i = commonDB.updateNext(type, next);
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			closeDB();
		}
		return i;
	}
	
	/**
	 * 修改用户数据,根据TYPE
	 * @param dingzaiId
	 * @param cacheData
	 */
	public void commonUpdateData(int type,byte[] cacheData) {
		try {
			commonDB.commonUpdateData(type, cacheData);
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			closeDB();
		}
	}
	
	/**
	 * 修改用户数据,根据TYPE和ID
	 * @param dingzaiId
	 * @param cacheData
	 */
	public void commonUpdateData(int type,String id,byte[] cacheData) {
		if(null == commonDB){
			commonDB = new CommonDB(context);
		}
		try {
			long s =commonDB.commonUpdateData(type, id, cacheData);
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			closeDB();
		}
	}
	
	/**
	 * 关闭数据库
	 */
	public void closeDB() {
		if (mCursor != null) {
			mCursor.close();
		}
		if (commonDB != null) {
			commonDB.endTransaction();
			commonDB.close();
			commonDB = null;
		}
	}
	
	private void closeDB(Cursor mCursor) {
		if (mCursor != null) {
			mCursor.close();
		}
		if (commonDB != null) {
			commonDB.endTransaction();
			commonDB.close();
			commonDB = null;
		}
	}

}
