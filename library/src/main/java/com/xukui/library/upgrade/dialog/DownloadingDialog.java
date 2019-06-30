package com.xukui.library.upgrade.dialog;

import android.content.DialogInterface;
import android.view.View;

public interface DownloadingDialog {

    void show();

    void dismiss();

    boolean isShowing();

    void setOnCancelListener(DialogInterface.OnCancelListener listener);

    void setOnDismissListener(DialogInterface.OnDismissListener listener);

    void showProgress(int progress);

    void setOnInstallListener(View.OnClickListener listener);

}