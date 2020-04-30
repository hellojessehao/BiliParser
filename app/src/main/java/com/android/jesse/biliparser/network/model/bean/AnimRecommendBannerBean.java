package com.android.jesse.biliparser.network.model.bean;

/**
 * @Description: 动漫推荐banner数据
 * @author: zhangshihao
 * @date: 2020/4/28
 */
public class AnimRecommendBannerBean {

    private String title;//动漫名
    private String url;//动漫选集链接
    private String sectionCount;//动漫更新到多少集
    private String cover;//封面地址

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSectionCount() {
        return sectionCount;
    }

    public void setSectionCount(String sectionCount) {
        this.sectionCount = sectionCount;
    }

    @Override
    public String toString() {
        return "AnimRecommendBannerBean{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", sectionCount='" + sectionCount + '\'' +
                ", cover='" + cover + '\'' +
                '}';
    }
}
