package com.xukui.library.upgrade.event;

public class DownloadingProgressEvent {

    public final int progress;
    public final long currentLength;

    public DownloadingProgressEvent(int progress, long currentLength) {
        this.progress = progress;
        this.currentLength = currentLength;
    }

}
