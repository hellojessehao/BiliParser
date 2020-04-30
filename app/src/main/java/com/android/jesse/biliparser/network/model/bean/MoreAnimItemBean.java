package com.android.jesse.biliparser.network.model.bean;

/**
 * @Description: 更多动漫条目数据
 * @author: zhangshihao
 * @date: 2020/4/28
 */
public class MoreAnimItemBean {

    private String type;
    private String title;
    private String sectionCount;
    private String updateDate;
    private String url;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSectionCount() {
        return sectionCount;
    }

    public void setSectionCount(String sectionCount) {
        this.sectionCount = sectionCount;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "MoreAnimItemBean{" +
                "type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", sectionCount='" + sectionCount + '\'' +
                ", updateDate='" + updateDate + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
