package com.android.jesse.biliparser.network.model.http.response;

/**
 * Created by yu_yo on 2018/5/24.
 */

public class BillHttpResponse<T> {
    private boolean error;
    private T results;

    public T getResults() {
        return results;
    }

    public void setResults(T results) {
        this.results = results;
    }

    public boolean getError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
