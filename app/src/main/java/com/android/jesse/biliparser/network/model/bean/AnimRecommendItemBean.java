package com.android.jesse.biliparser.network.model.bean;

import java.util.List;

/**
 * @Description: 推荐数据bean
 * @author: zhangshihao
 * @date: 2020/4/27
 */
public class AnimRecommendItemBean {

    private int typeId;//0最近更新 1日本动漫 2国产动漫 3美国动漫 4动漫电影
    private String type;//类型
    private String moreUrl;//更多的链接
    private List<DataBean> dataBeanList;

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMoreUrl() {
        return moreUrl;
    }

    public void setMoreUrl(String moreUrl) {
        this.moreUrl = moreUrl;
    }

    public List<DataBean> getDataBeanList() {
        return dataBeanList;
    }

    public void setDataBeanList(List<DataBean> dataBeanList) {
        this.dataBeanList = dataBeanList;
    }

    @Override
    public String toString() {
        return "AnimRecommendItemBean{" +
                "type='" + type + '\'' +
                ", moreUrl='" + moreUrl + '\'' +
                ", dataBeanList=" + dataBeanList +
                '}';
    }

    public static class DataBean {

        private String url;//跳转选集链接
        private String cover;//封面链接
        private String title;//名字
        private String sectionCount;//更新到多少集
        private String updateDate;//更新时间

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

        public String getSectionCount() {
            return sectionCount;
        }

        public void setSectionCount(String sectionCount) {
            this.sectionCount = sectionCount;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "url='" + url + '\'' +
                    ", cover='" + cover + '\'' +
                    ", title='" + title + '\'' +
                    ", sectionCount='" + sectionCount + '\'' +
                    ", updateDate='" + updateDate + '\'' +
                    '}';
        }
    }

}
