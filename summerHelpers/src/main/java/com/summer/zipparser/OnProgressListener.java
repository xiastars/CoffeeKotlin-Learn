package com.summer.zipparser;

/**
 * 应用于解析zip
 * @author malata_xiaqiliang
 * @time 2016年5月27日
 */
public interface OnProgressListener {
	/** 正在解析 */
	void onParse(int size);
}
