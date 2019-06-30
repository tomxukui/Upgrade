package com.xukui.library.upgrade.builder;

public class NotificationBuilder {

    private int mIcon;//图标
    private String mContentTitle;//标题
    private String mTicker;
    private String mContentText;//内容
    private boolean mIsRingtone;//是否响铃

    public static NotificationBuilder create() {
        return new NotificationBuilder();
    }

    private NotificationBuilder() {
        mIsRingtone = false;
    }

    public int getIcon() {
        return mIcon;
    }

    public NotificationBuilder setIcon(int icon) {
        mIcon = icon;
        return this;
    }

    public String getContentTitle() {
        return mContentTitle;
    }

    public NotificationBuilder setContentTitle(String contentTitle) {
        mContentTitle = contentTitle;
        return this;
    }

    public String getTicker() {
        return mTicker;
    }

    public NotificationBuilder setTicker(String ticker) {
        mTicker = ticker;
        return this;
    }

    public String getContentText() {
        return mContentText;
    }

    public NotificationBuilder setContentText(String contentText) {
        mContentText = contentText;
        return this;
    }

    public boolean isRingtone() {
        return mIsRingtone;
    }

    public NotificationBuilder setRingtone(boolean ringtone) {
        mIsRingtone = ringtone;
        return this;
    }

}