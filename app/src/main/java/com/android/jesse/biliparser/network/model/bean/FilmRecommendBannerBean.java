package com.android.jesse.biliparser.network.model.bean;

/**
 * @Description: 推荐影视数据
 * @author: zhangshihao
 * @date: 2020/4/29
 */
public class FilmRecommendBannerBean {

    private String url;
    private String cover;
    private String title;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "FilmRecommendBannerBean{" +
                "url='" + url + '\'' +
                ", cover='" + cover + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
