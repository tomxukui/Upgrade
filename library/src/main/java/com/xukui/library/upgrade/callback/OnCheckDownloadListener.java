package com.xukui.library.upgrade.callback;

import java.io.File;

public interface OnCheckDownloadListener {

    void onCheckerStartDownload();

    void onCheckerDownloading(int progress);

    void onCheckerDownloadSuccess(File file);

    void onCheckerDownloadFail();

}