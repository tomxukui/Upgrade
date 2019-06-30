package com.xukui.library.upgrade.callback;

import android.content.Context;

import com.xukui.library.upgrade.bean.UpgradeInfo;
import com.xukui.library.upgrade.dialog.DownloadFailedDialog;
import com.xukui.library.upgrade.dialog.DownloadingDialog;
import com.xukui.library.upgrade.dialog.VersionDialog;

public interface OnCustomDialogListener {

    VersionDialog getVersionDialog(Context context, UpgradeInfo upgradeInfo);

    DownloadingDialog getDownloadingDialog(Context context, UpgradeInfo upgradeInfo);

    DownloadFailedDialog getDownloadFailedDialog(Context context, UpgradeInfo upgradeInfo);

}