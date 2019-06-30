package com.xukui.upgrade.sample.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.View;
import android.widget.Button;

import com.xukui.upgrade.sample.R;
import com.xukui.library.upgrade.bean.UpgradeInfo;
import com.xukui.library.upgrade.dialog.DownloadingDialog;

public class CustomDownloadingDialog extends Dialog implements DownloadingDialog {

    private ContentLoadingProgressBar bar_progress;
    private Button btn_cancel;
    private Button btn_install;

    private UpgradeInfo mUpgradeInfo;

    public CustomDownloadingDialog(@NonNull Context context, UpgradeInfo upgradeInfo) {
        super(context, R.style.BaseDialog);
        mUpgradeInfo = upgradeInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_custom_downloading);

        bar_progress = findViewById(R.id.bar_progress);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_install = findViewById(R.id.btn_install);

        btn_cancel.setOnClickListener(v -> cancel());
    }

    @Override
    public void showProgress(int progress) {
        bar_progress.setProgress(progress);
    }

    @Override
    public void setOnInstallListener(View.OnClickListener listener) {
        btn_install.setOnClickListener(listener);
    }

}