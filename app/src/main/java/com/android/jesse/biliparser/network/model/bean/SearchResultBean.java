package com.android.jesse.biliparser.network.model.bean;

import java.util.List;

/**
 * @Description: 搜索结果数据
 * @author: zhangshihao
 * @date: 2020/3/24
 */
public class SearchResultBean {

    private String url;
    private String cover;
    private String title;
    private String infos;
    private String alias;
    private String desc;//剧情简介
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
    private String hotValue;//影片的热度
    //@}

    public String getHotValue() {
        return hotValue;
    }

    public void setHotValue(String hotValue) {
        this.hotValue = hotValue;
    }

    public int getSearchType() {
        return searchType;
    }

    public void setSearchType(int searchType) {
        this.searchType = searchType;
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

    @Override
    public String toString() {
        return "SearchResultBean{" +
                "url='" + url + '\'' +
                ", cover='" + cover + '\'' +
                ", title='" + title + '\'' +
                ", infos='" + infos + '\'' +
                ", alias='" + alias + '\'' +
                ", desc='" + desc + '\'' +
                ", sectionCount='" + sectionCount + '\'' +
                ", directorList=" + directorList +
                ", actorList=" + actorList +
                ", type='" + type + '\'' +
                ", area='" + area + '\'' +
                ", publishDate='" + publishDate + '\'' +
                ", updateDate='" + updateDate + '\'' +
                ", score='" + score + '\'' +
                ", searchType=" + searchType +
                ", hotValue='" + hotValue + '\'' +
                '}';
    }
}
