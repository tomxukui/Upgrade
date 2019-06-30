package com.xukui.library.upgrade.builder;

import com.xukui.library.upgrade.callback.OnRequestVersionListener;
import com.xukui.library.upgrade.http.HttpHeaders;
import com.xukui.library.upgrade.http.HttpParams;
import com.xukui.library.upgrade.http.HttpRequestMethod;

public class RequestVersionBuilder {

    private HttpRequestMethod mRequestMethod;
    private HttpParams mRequestParams;
    private String mRequestUrl;
    private HttpHeaders mHttpHeaders;
    private OnRequestVersionListener mOnRequestVersionListener;

    public RequestVersionBuilder() {
        mRequestMethod = HttpRequestMethod.GET;
    }

    public HttpRequestMethod getRequestMethod() {
        return mRequestMethod;
    }

    public RequestVersionBuilder setRequestMethod(HttpRequestMethod requestMethod) {
        mRequestMethod = requestMethod;
        return this;
    }

    public HttpParams getRequestParams() {
        return mRequestParams;
    }

    public RequestVersionBuilder setRequestParams(HttpParams requestParams) {
        mRequestParams = requestParams;
        return this;
    }

    public String getRequestUrl() {
        return mRequestUrl;
    }

    public RequestVersionBuilder setRequestUrl(String requestUrl) {
        mRequestUrl = requestUrl;
        return this;
    }

    public HttpHeaders getHttpHeaders() {
        return mHttpHeaders;
    }

    public RequestVersionBuilder setHttpHeaders(HttpHeaders httpHeaders) {
        mHttpHeaders = httpHeaders;
        return this;
    }

    public OnRequestVersionListener getOnRequestVersionListener() {
        return mOnRequestVersionListener;
    }

    public DownloadBuilder request(OnRequestVersionListener listener) {
        mOnRequestVersionListener = listener;

        return new DownloadBuilder(this);
    }

}