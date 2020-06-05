package com.summer.demo.bean;

import java.io.Serializable;

public class BaseResp implements Serializable {

    boolean result;
    int error;
    String msg = "";
    long time;
    Object info;
    int fun;

    public int getFun() {
        return fun;
    }

    public void setFun(int fun) {
        this.fun = fun;
    }


    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Object getInfo() {
        return info;
    }

    public void setInfo(Object info) {
        this.info = info;
    }
}
