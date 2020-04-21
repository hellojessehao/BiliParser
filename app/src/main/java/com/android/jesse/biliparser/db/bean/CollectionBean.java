package com.android.jesse.biliparser.db.bean;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.android.jesse.biliparser.utils.ListConvert;

import java.util.List;

/**
 * @Description:
 * @author: zhangshihao
 * @date: 2020/4/14
 */
@Entity(tableName = "collection")
@TypeConverters(ListConvert.class)
public class CollectionBean {

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
    //film新增@{
    private String sectionCount;//集数
    private List<String> directorList;
    private List<String> actorList;
    private String type;
    private String area;
    private String publishDate;
    private String updateDate;
    private String score;//评分
    private int searchType;//搜索类型 动漫 或 影视剧
    //@}

    public String getSectionCount() {
        return sectionCount;
    }

    public void setSectionCount(String sectionCount) {
        this.sectionCount = sectionCount;
    }

    public List<String> getDirectorList() {
        return directorList;
    }

    public void setDirectorList(List<String> directorList) {
        this.directorList = directorList;
    }

    public List<String> getActorList() {
        return actorList;
    }

    public void setActorList(List<String> actorList) {
        this.actorList = actorList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public int getSearchType() {
        return searchType;
    }

    public void setSearchType(int searchType) {
        this.searchType = searchType;
    }

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
        return "CollectionBean{" +
                "videoId=" + videoId +
                ", url='" + url + '\'' +
                ", cover='" + cover + '\'' +
                ", title='" + title + '\'' +
                ", currentIndex=" + currentIndex +
                ", infos='" + infos + '\'' +
                ", alias='" + alias + '\'' +
                ", desc='" + desc + '\'' +
                ", date='" + date + '\'' +
                ", sectionCount='" + sectionCount + '\'' +
                ", directorList=" + directorList +
                ", actorList=" + actorList +
                ", type='" + type + '\'' +
                ", area='" + area + '\'' +
                ", publishDate='" + publishDate + '\'' +
                ", updateDate='" + updateDate + '\'' +
                ", score='" + score + '\'' +
                ", searchType=" + searchType +
                '}';
    }
}
