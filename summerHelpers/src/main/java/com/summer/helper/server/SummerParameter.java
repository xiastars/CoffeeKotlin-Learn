package com.summer.helper.server;

import android.graphics.Bitmap;

import com.summer.helper.utils.Logs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Set;

public class SummerParameter {
	private static final String DEFAULT_CHARSET = "UTF-8";
	private LinkedHashMap<String, Object> mParams = new LinkedHashMap<String, Object>();

	public LinkedHashMap<String, Object> getParams() {
		return this.mParams;
	}

	public void setParams(LinkedHashMap<String, Object> params) {
		this.mParams = params;
	}

	public void put(String key, String val) {
		this.mParams.put(key, val);
	}

	public void put(String key, int value) {
		this.mParams.put(key, String.valueOf(value));
	}

	public void put(String key, long value) {
		this.mParams.put(key, String.valueOf(value));
	}

	public void put(String key, Bitmap bitmap) {
		this.mParams.put(key, bitmap);
	}

	public void put(String key, File file) {
		this.mParams.put(key, file);
	}

	public void put(String key, Object val) {
		this.mParams.put(key, val.toString());
	}

	public Object get(String key) {
		return this.mParams.get(key);
	}

	public void remove(String key) {
		if (this.mParams.containsKey(key)) {
			this.mParams.remove(key);
			this.mParams.remove(this.mParams.get(key));
		}
	}

	public Set<String> keySet() {
		return this.mParams.keySet();
	}

	public boolean containsKey(String key) {
		return this.mParams.containsKey(key);
	}

	public boolean containsValue(String value) {
		return this.mParams.containsValue(value);
	}

	public int size() {
		return this.mParams.size();
	}

	public String encodeUrlAndLog(String url) {
		String content = encodeUrl(url);
		String requestType = (String) mParams.get("requestType");
		if (requestType != null) {
			Logs.i(requestType + ": " + content);
		} else {
			Logs.i("请求数据" + content);
		}
		return content;
	}

	public String encodeUrl(String url) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String key : this.mParams.keySet()) {
			if (!key.equals("requestType")) {
				if (first)
					first = false;
				else {
					sb.append("&");
				}
				Object value = this.mParams.get(key);
				if (value instanceof String) {
					String param = (String) value;
					try {
						sb.append(URLEncoder.encode(key, DEFAULT_CHARSET)).append("=").append(URLEncoder.encode(param, DEFAULT_CHARSET));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return url +"?"+ sb.toString();
	}

	public String encodeLogoUrl(String url) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String key : this.mParams.keySet()) {
			if (!key.equals("requestType") && !key.equals("t")) {
				if (first)
					first = false;
				else {
					sb.append("&");
				}
				Object value = this.mParams.get(key);
				if (value instanceof String) {
					String param = (String) value;
					try {
						sb.append(URLEncoder.encode(key, DEFAULT_CHARSET)).append("=").append(URLEncoder.encode(param, DEFAULT_CHARSET));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return url + sb.toString();
	}

	public void putLog(String action) {
		this.mParams.put("requestType", action);
	}

	/**
	 * 设置不支持缓存
	 */
	public void setDisableCache() {
		this.mParams.put("supportcache", false);
	}

	public boolean isCacheSupport() {
		if (mParams.containsKey("supportcache")) {
			return (boolean) mParams.get("supportcache");
		}
		return true;
	}

	/**
	 * 安全获取某一个参数
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public Object getParamSecurity(String key,Object defaultValue) {
		if (mParams.containsKey(key)) {
			return  mParams.get(key);
		}
		return defaultValue;
	}

	/**
	 * 设置虚拟数据，在网络差时使用
	 */
	public void setShowVirtualData() {
		this.mParams.put("virtualData", true);
	}

	/**
	 * 设置虚拟数据，在网络差时使用
	 */
	public void disableToast() {
		this.mParams.put("toastmsg", false);
	}

	/**
	 * 获取虚拟数据的Code
	 * @return
	 */
	public boolean isToastEnable() {
		if (mParams.containsKey("toastmsg")) {
			return (boolean) mParams.get("toastmsg");
		}
		return true;
	}


	/**
	 * 获取虚拟数据的Code
	 * @return
	 */
	public String getVirtualCode() {
		if (mParams.containsKey("requestType")) {
			return (String) mParams.get("requestType");
		}
		return null;
	}

	public boolean isVirtualData() {
		if (mParams.containsKey("virtualData")) {
			return (boolean) mParams.get("virtualData");
		}
		return false;
	}

	public boolean hasBinaryData() {
		Set<String> keys = this.mParams.keySet();
		for (String key : keys) {
			Object value = this.mParams.get(key);

			if ((value instanceof ByteArrayOutputStream)
					|| (value instanceof File)
					|| (value instanceof Bitmap)) {
				return true;
			}
		}
		return false;
	}
}