package com.xukui.library.upgrade.callback;

import java.io.File;

public interface OnDownloadListener {

    void onDownloading(int progress, long currentLength);

    void onDownloadSuccess(File file);

    void onDownloadFail();

}