package com.summer.helper.server;

/**
 *Title: 异常处理类<br>
 *@author xiastars@vip.qq.com
 */
public class SummerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SummerException() {
	}

	public SummerException(String message) {
		super(message);
	}

	public SummerException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public SummerException(Throwable throwable) {
		super(throwable);
	}
}
