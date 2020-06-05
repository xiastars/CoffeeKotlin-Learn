package com.summer.helper.server;

public abstract class RequestCallback<T> {

	public abstract void done(T t);

	public abstract void onError(int errorCode, String errorStr);
	
}
