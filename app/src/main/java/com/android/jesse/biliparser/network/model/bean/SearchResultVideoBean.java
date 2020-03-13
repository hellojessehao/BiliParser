package com.android.jesse.biliparser.network.model.bean;

/**
 * @Description: 搜索结果视频数据bean
 * @author: zhangshihao
 * @date: 2020/3/13
 */
public class SearchResultVideoBean {

    private String url;
    private String title;
    private String playCount;//播放量
    private String date;
    private String author;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlayCount() {
        return playCount;
    }

    public void setPlayCount(String playCount) {
        this.playCount = playCount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "SearchResultVideoBean{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", playCount='" + playCount + '\'' +
                ", date='" + date + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}
