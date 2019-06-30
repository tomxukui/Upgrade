package com.xukui.library.upgrade.event;

public class UpgradeEvent {

    public static final String CONFIRM_UPGRADE = "CONFIRM_UPGRADE";
    public static final String CANCEL_UPGRADE = "CANCEL_UPGRADE";

//    public static final String START_DOWNLOADING = "START_DOWNLOADING";
    public static final String CANCEL_DOWNLOADING = "CANCEL_DOWNLOADING";
    public static final String DOWNLOAD_COMPLETE = "DOWNLOAD_COMPLETE";
    public static final String RETRY_DOWNLOAD = "RETRY_DOWNLOAD";
    public static final String CANCEL_RETRY_DOWNLOAD = "CANCEL_RETRY_DOWNLOAD";

    public final String type;

    public UpgradeEvent(String type) {
        this.type = type;
    }

}