package com.summer.demo.module.emoji;

import android.graphics.Bitmap;

/**
 * Created by xiaqiliang on 2017/9/7.
 */
public class IconEntity {

	private int id;
	private String key;
	private int res;
	private String version;   //表情版本号
	private int packageID; //表情包名
	private String emojiPath; //取得表情的路径
	private String emojiText; //取到EditText的文本
	private String name;	//中文对应名称
	private boolean isEmojiPic = false;//是否是贴图，默认不是
	private Bitmap bitmap ;//贴图

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public boolean isEmojiPic() {
		return isEmojiPic;
	}

	public void setEmojiPic(boolean isEmojiPic) {
		this.isEmojiPic = isEmojiPic;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmojiText() {
		return emojiText;
	}

	public void setEmojiText(String emojiText) {
		this.emojiText = emojiText;
	}

	public IconEntity() {
		super();
	}

	public IconEntity(String name, int res) {
		super();
		this.res = res;
		this.name = name;
	}

	public IconEntity(int id, String name, String emojiPath, String version, int packageID) {
		super();
		this.emojiPath = emojiPath;
		this.name = name;
		this.version = version;
		this.packageID = packageID;
	}

	public IconEntity(int id, String key, int res) {
		super();
		this.id = id;
		this.key = key;
		this.res = res;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getRes() {
		return res;
	}

	public void setRes(int res) {
		this.res = res;
	}

	public String getEmojiPath() {
		return emojiPath;
	}

	public void setEmojiPath(String emojiPath) {
		this.emojiPath = emojiPath;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getPackageID() {
		return packageID;
	}

	public void setPackageID(int packageID) {
		this.packageID = packageID;
	}
}
