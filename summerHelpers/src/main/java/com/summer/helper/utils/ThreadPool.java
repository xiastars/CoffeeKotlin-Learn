package com.summer.helper.utils;

import java.util.Vector;

public class ThreadPool implements Runnable {
	// ThreadPool里最大线程数、最少线程数、释放延时时间10s
	public final static int DEFAULT_MIN_SIZE = 1;
	public final static int DEFAULT_MAX_SIZE = 100;
	public final static long DEFAULT_RELEASE_DELAY = 10 * 1000;

	// 用户定制的 threadpool属性值
	protected int minSize;
	protected int maxSize;
	protected long releaseDelay;

	// ThreadPool里现有的线程数
	protected int currentSize;

	// ThreadPool里还可以提供的线程数
	protected int availableThreads;

	// 任务列表线程池
	protected Vector<Runnable> taskList;

	/**
	 * 缺省的ThreadPool
	 */
	public ThreadPool() {
		this(DEFAULT_MIN_SIZE, DEFAULT_MAX_SIZE, DEFAULT_RELEASE_DELAY);
	}

	/**
	 * 用户定制的ThreadPool
	 * 
	 * @param minSize
	 *            线程池最少线程数
	 * @param maxSize
	 *            线程池最多线程数
	 * @param releaseDelay
	 *            线程释放延时
	 */

	public ThreadPool(int minSize, int maxSize, long releaseDelay) {
		this.minSize = minSize;
		this.maxSize = maxSize;
		this.releaseDelay = releaseDelay;
		taskList = new Vector<Runnable>(maxSize);
		availableThreads = 0;
	}

	/**
	 * 设置线程池中最少的线程数
	 * 
	 * @param minSize
	 */
	public synchronized void setMinSize(int minSize) {
		this.minSize = minSize;
	}

	/**
	 * 读取线程池最少线程数
	 * 
	 * @return
	 */
	public synchronized int getMinSize() {
		return minSize;
	}

	/**
	 * 设置线程池中最大线程数
	 * 
	 * @param maxSize
	 */
	public synchronized void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	/**
	 * 读取线程池最大线程数
	 * 
	 * @return
	 */
	public synchronized int getMaxSize() {
		return maxSize;
	}

	/**
	 * 设置线程池中线程释放延时
	 * 
	 * @param releaseDelay
	 *            线程释放延时时间
	 */
	public synchronized void setReleaseDelay(long releaseDelay) {
		this.releaseDelay = releaseDelay;
	}

	/**
	 * 读取线程池中线程释放延时时间
	 * 
	 * @return
	 */
	public synchronized long getRelaeseDelay() {
		return releaseDelay;
	}

	/**
	 * 往ThreadPool里添加新的任务
	 * 
	 * @param runnable
	 *            新任务Runnable
	 */
	public synchronized void submit(Runnable runnable) {
		taskList.addElement(runnable);
		if (availableThreads > 0) {
			this.notify();
		} else {
			if (currentSize < maxSize) {
				Thread thread = new Thread(this);
				thread.start();
				currentSize++;
			}
		}
	}

	@Override
	public void run() {
		Runnable task = null;
		while (true) {
			synchronized (this) {
				if (currentSize > maxSize) {
					currentSize--;
					break;
				}

				task = getNextTask();
				if (null == task) {
					try {
						availableThreads++;
						wait(releaseDelay);
						availableThreads--;

					} catch (InterruptedException e) {
						// do something you want;
					}
					task = getNextTask();
					if (null == task) {
						if (currentSize < minSize)
							continue;
						currentSize--;
						break;
					}
				}
			}
			try {
				task.run();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 从任务列表中获取下一个任务
	 * 
	 * @return
	 */
	protected synchronized Runnable getNextTask() {
		Runnable task;
		try {
			task = null;
			if (null != taskList && !taskList.isEmpty()) {
				task = (Runnable) (taskList.elementAt(0));
				taskList.removeElementAt(0);
			}
			return task;
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		return null;
	}

	/**
	 * 移除所有任务
	 */
	public void removeAllTask() {
		if (null != taskList)
			taskList.removeAllElements();
	}

	/**
	 * 返回ThreadPool的信息
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("DEFAULT_MIN_SIZE:" + DEFAULT_MIN_SIZE + "\n");
		sb.append("DEFAULT_MAX_SIZE:" + DEFAULT_MAX_SIZE + "\n");
		sb.append("DEFAULT_RELEASE_DELAY:" + DEFAULT_RELEASE_DELAY + "\n");
		sb.append("minSize \t maxSize \t releaseDelay \t currentSize \n");
		sb.append(minSize + " \t " + maxSize + " \t " + releaseDelay + " \t "
				+ currentSize);
		return sb.toString();
	}
}
