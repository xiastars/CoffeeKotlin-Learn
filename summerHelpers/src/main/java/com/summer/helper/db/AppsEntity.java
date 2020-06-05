/**
 *
 */
package com.summer.helper.db;

import java.io.Serializable;
public class AppsEntity implements Serializable {
	private String uid;
	private String size;
	private String package_name;
	private String name;
	private String icon;
	private float starts;
	private String version;
	private String published_at;
	private long version_code;
	private String apk_url;
	private int progress;
	private String updateTime;

	private long downloadId;
	private int totalBytes;
	private int currentBytes;
	private int status = 0;
	private String save_path;
	private String filetype;
	private String launcher_tag;
	// 安装的 版本号
	private int localVersion =-1;
	//数据库里的版本号
	private int dataVersion =-1;

	public int getProgress() {
		return progress;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	/**
	 * 数据库中保存的文件名  及本地保存的文件名
	 */
	private String title;

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public void setPackage_name(String package_name) {
		this.package_name = package_name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public void setStarts(float starts) {
		this.starts = starts;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUid() {
		return uid;
	}

	public String getSize() {
		return size;
	}

	public String getPackage_name() {
		return package_name;
	}

	public String getName() {
		if(name == null){
			return "";
		}
		return name;
	}

	public String getIcon() {
		return icon;
	}

	public float getStarts() {
		return starts;
	}

	public String getVersion() {
		return version;
	}

	/**
	 * @return the downloadId
	 */
	public long getDownloadId() {
		return downloadId;
	}

	/**
	 * @param downloadId
	 *            the downloadId to set
	 */
	public void setDownloadId(long downloadId) {
		this.downloadId = downloadId;
	}

	/**
	 * @return the totalBytes
	 */
	public int getTotalBytes() {
		return totalBytes;
	}

	/**
	 * @param totalBytes
	 *            the totalBytes to set
	 */
	public void setTotalBytes(int totalBytes) {
		this.totalBytes = totalBytes;
	}

	/**
	 * @return the currentBytes
	 */
	public int getCurrentBytes() {
		return currentBytes;
	}

	/**
	 * @param currentBytes
	 *            the currentBytes to set
	 */
	public void setCurrentBytes(int currentBytes) {
		this.currentBytes = currentBytes;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the published_at
	 */
	public String getPublished_at() {
		return published_at;
	}

	/**
	 * @param published_at the published_at to set
	 */
	public void setPublished_at(String published_at) {
		this.published_at = published_at;
	}

	/**
	 * @return the version_code
	 */
	public long getVersion_code() {
		return version_code;
	}

	/**
	 * @param version_code the version_code to set
	 */
	public void setVersion_code(long version_code) {
		this.version_code = version_code;
	}

	/**
	 * @return the apk_url
	 */
	public String getApk_url() {
		return apk_url;
	}

	/**
	 * @param apk_url the apk_url to set
	 */
	public void setApk_url(String apk_url) {
		this.apk_url = apk_url;
	}

	/**
	 * @return the save_path
	 */
	public String getSave_path() {
		return save_path;
	}

	/**
	 * @param save_path the save_path to set
	 */
	public void setSave_path(String save_path) {
		this.save_path = save_path;
	}

	/**
	 * @return the filetype
	 */
	public String getFiletype() {
		return filetype;
	}

	/**
	 * @param filetype the filetype to set
	 */
	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}

	/**
	 * @return the launcher_tag
	 */
	public String getLauncher_tag() {
		return launcher_tag;
	}

	/**
	 * @param launcher_tag the launcher_tag to set
	 */
	public void setLauncher_tag(String launcher_tag) {
		this.launcher_tag = launcher_tag;
	}

	public int getLocalVersion() {
		return localVersion;
	}

	public void setLocalVersion(int localVersion) {
		this.localVersion = localVersion;
	}

	/**
	 * @return the dataVersion
	 */
	public int getDataVersion() {
		return dataVersion;
	}

	/**
	 * @param dataVersion the dataVersion to set
	 */
	public void setDataVersion(int dataVersion) {
		this.dataVersion = dataVersion;
	}

	public class FileType{
		public static final String FILETYPE_APK = ".apk";
	}



}
