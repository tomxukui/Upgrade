package com.xukui.library.upgrade.bean;

import java.io.Serializable;

public class UpgradeInfo implements Serializable {

    private String title;//标题
    private String content;//内容
    private String downloadUrl;//下载地址
    private boolean force = false;//是否强制更新

    public String getTitle() {
        return title == null ? "更新提示" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content == null ? "检测到新版本" : content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

}