package com.android.jesse.biliparser.db.bean;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * @Description:
 * @author: zhangshihao
 * @date: 2020/4/8
 */
@Entity(tableName = "HistoryVideo")
public class HistoryVideoBean {

    @PrimaryKey
    private int videoId;//保存动漫名的hashcode，每种动漫独一无二
    private String url;
    private String cover;//封面
    private String title;
    private int currentIndex;//最近观看的集数
    private String infos;
    private String alias;
    private String desc;
    private String date;//观看日期

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

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

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public String getInfos() {
        return infos;
    }

    public void setInfos(String infos) {
        this.infos = infos;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "HistoryVideoBean{" +
                "videoId=" + videoId +
                ", url='" + url + '\'' +
                ", cover='" + cover + '\'' +
                ", title='" + title + '\'' +
                ", currentIndex=" + currentIndex +
                ", infos='" + infos + '\'' +
                ", alias='" + alias + '\'' +
                ", desc='" + desc + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
