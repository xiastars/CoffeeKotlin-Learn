package com.summer.demo.selectmorepic;

import java.io.Serializable;

public class ImageInfo implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String path ="";
	private boolean isSelected = false ;
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}


}
