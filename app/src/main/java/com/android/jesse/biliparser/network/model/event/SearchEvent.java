package com.android.jesse.biliparser.network.model.event;

/**
 * Created by yuyoucheng on 19/5/7.
 */

public class SearchEvent {

    public SearchEvent(String query, String type) {
        this.query = query;
        this.type = type;
    }

    private String query;

    private String type;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
