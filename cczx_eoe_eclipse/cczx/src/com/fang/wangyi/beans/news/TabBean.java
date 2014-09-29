package com.fang.wangyi.beans.news;

import java.io.Serializable;

/**
 * Created by changliang on 14-6-16.
 */
public class TabBean implements Serializable{
    public String title;
    public String url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
