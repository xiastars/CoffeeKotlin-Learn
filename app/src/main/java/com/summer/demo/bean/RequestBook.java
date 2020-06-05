package com.summer.demo.bean;

import java.util.List;

/**
 * Created by summer on 2016年12月15日 18:04.
 */

public class RequestBook {

    int status;
    List<BookBean> data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<BookBean> getData() {
        return data;
    }

    public void setData(List<BookBean> data) {
        this.data = data;
    }
}