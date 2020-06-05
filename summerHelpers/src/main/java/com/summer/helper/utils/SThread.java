package com.summer.helper.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

public class SThread {
	
	public static final SThread commons = new SThread();
	//消息线程池
	private ThreadPool threadPool = new ThreadPool();
	private Handler mMainHandler;
	
	public static SThread getIntances(){
		return commons;
	}

	private SThread(){

	}

	/**
	 * 清空所有线程
	 */
	public void clear(){
		threadPool.removeAllTask();
	}
	
	/**
	 * 返回当前线程池
	 * @return
	 */
	private ThreadPool getThreadPool() {
		return threadPool;
	}
	
	/**
	 * 提交一个线程交给线程池处理 不用返回结果
	 * @param runnable 线程
	 */
	public void submit(Runnable runnable) {
		threadPool.submit(runnable);
	}

	/**
	 * Activity的请求会转到主线程操作
	 */
	public void runOnUIThreadIfNeed(Context context,Runnable runnable){
		if(context instanceof Activity){
			Activity activity=(Activity)context;
			activity.runOnUiThread(runnable);
		}else{
			threadPool.submit(runnable);
		}
	}

	public void runOnUiThread(Runnable runnable){
		if (mMainHandler == null)
			mMainHandler = new Handler(Looper.getMainLooper());
		mMainHandler.post(runnable);
	}
}
