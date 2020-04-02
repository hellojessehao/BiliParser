package com.android.jesse.biliparser.network.model.bean;

/**
 * @Description: 集数列表中每集的数据
 * @author: zhangshihao
 * @date: 2020/3/26
 */
public class SectionBean {

    private String title;
    private String url;

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

    @Override
    public String toString() {
        return "SectionBean{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
