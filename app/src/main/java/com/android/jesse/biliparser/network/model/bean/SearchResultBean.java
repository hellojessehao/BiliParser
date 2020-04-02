package com.android.jesse.biliparser.network.model.bean;

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
    private String desc;

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

    @Override
    public String toString() {
        return "SearchResultBean{" +
                "url='" + url + '\'' +
                ", cover='" + cover + '\'' +
                ", title='" + title + '\'' +
                ", infos='" + infos + '\'' +
                ", alias='" + alias + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
