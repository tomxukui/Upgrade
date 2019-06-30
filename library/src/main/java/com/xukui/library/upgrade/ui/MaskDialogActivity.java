package com.xukui.library.upgrade.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.xukui.library.upgrade.UpgradeClient;
import com.xukui.library.upgrade.bean.UpgradeInfo;
import com.xukui.library.upgrade.builder.DownloadBuilder;
import com.xukui.library.upgrade.dialog.DownloadFailedDialog;
import com.xukui.library.upgrade.dialog.DownloadingDialog;
import com.xukui.library.upgrade.dialog.VersionDialog;
import com.xukui.library.upgrade.dialog.impl.DefaultDownloadFailedDialog;
import com.xukui.library.upgrade.dialog.impl.DefaultDownloadingDialog;
import com.xukui.library.upgrade.dialog.impl.DefaultVersionDialog;
import com.xukui.library.upgrade.event.DownloadingProgressEvent;
import com.xukui.library.upgrade.event.UpgradeEvent;
import com.xukui.library.upgrade.service.VersionService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MaskDialogActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {

    private static final String EXTRA_DIALOG_TYPE = "EXTRA_TYPE";
    private static final String TYPE_VERSION = "TYPE_VERSION";
    private static final String TYPE_DOWNLOADING = "TYPE_DOWNLOADING";
    private static final String TYPE_DOWNLOAD_FAILED = "TYPE_DOWNLOAD_FAILED";

    private VersionDialog mVersionDialog;
    private DownloadingDialog mDownloadingDialog;
    private DownloadFailedDialog mDownloadFailedDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        dismissVersionDialog();
        dismissDownloadingDialog();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadingProgressEvent(DownloadingProgressEvent event) {
        if (mDownloadingDialog != null && mDownloadingDialog.isShowing()) {
            mDownloadingDialog.showProgress(event.progress);
        }
    }

    private void handleIntent(Intent intent) {
        if (isFinishing()) {
            return;
        }

        DownloadBuilder downloadBuilder = VersionService.getDownloadBuilder();
        if (downloadBuilder == null) {
            UpgradeClient.getInstance().cancelAllMission();
            finish();
            return;
        }

        String dialogType = intent.getStringExtra(EXTRA_DIALOG_TYPE);
        if (dialogType == null) {
            dialogType = "";
        }

        switch (dialogType) {

            case TYPE_VERSION: {
                showVersionDialog(downloadBuilder);
                dismissDownloadingDialog();
                dismissDownloadFailedDialog();
            }
            break;

            case TYPE_DOWNLOADING: {
                dismissVersionDialog();
                showDownloadingDialog(downloadBuilder);
                dismissDownloadFailedDialog();
            }
            break;

            case TYPE_DOWNLOAD_FAILED: {
                dismissVersionDialog();
                dismissDownloadingDialog();
                showDownloadFailedDialog(downloadBuilder);
            }
            break;

            default:
                break;

        }
    }

    /**
     * 显示版本信息对话框
     */
    private void showVersionDialog(DownloadBuilder downloadBuilder) {
        if (mVersionDialog == null) {
            UpgradeInfo data = downloadBuilder.getUpgradeInfo();

            if (downloadBuilder.getOnCustomDialogListener() != null) {
                mVersionDialog = downloadBuilder.getOnCustomDialogListener().getVersionDialog(this, data);
            }
            if (mVersionDialog == null) {
                mVersionDialog = new DefaultVersionDialog.Builder(this)
                        .setTitle(data.getTitle())
                        .setMessage(data.getContent())
                        .force(data.isForce())
                        .create();
            }
            mVersionDialog.show();

            mVersionDialog.setOnDismissListener(this);
            mVersionDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    EventBus.getDefault().post(new UpgradeEvent(UpgradeEvent.CANCEL_UPGRADE));
                }

            });
            mVersionDialog.setOnConfirmListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new UpgradeEvent(UpgradeEvent.CONFIRM_UPGRADE));
                }

            });
        }

        if (!mVersionDialog.isShowing()) {
            mVersionDialog.show();
        }
    }

    /**
     * 隐藏版本信息对话框
     */
    private void dismissVersionDialog() {
        if (mVersionDialog != null) {
            if (mVersionDialog.isShowing()) {
                mVersionDialog.dismiss();
            }

            mVersionDialog = null;
        }
    }

    /**
     * 显示下载对话框
     */
    private void showDownloadingDialog(DownloadBuilder downloadBuilder) {
        if (mDownloadingDialog == null) {
            UpgradeInfo data = downloadBuilder.getUpgradeInfo();

            if (downloadBuilder.getOnCustomDialogListener() != null) {
                mDownloadingDialog = downloadBuilder.getOnCustomDialogListener().getDownloadingDialog(this, data);
            }
            if (mDownloadingDialog == null) {
                mDownloadingDialog = new DefaultDownloadingDialog.Builder(this)
                        .setForce(data.isForce())
                        .create();
            }
            mDownloadingDialog.show();

            mDownloadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    EventBus.getDefault().post(new UpgradeEvent(UpgradeEvent.CANCEL_DOWNLOADING));
                }

            });
            mDownloadingDialog.setOnInstallListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new UpgradeEvent(UpgradeEvent.DOWNLOAD_COMPLETE));
                }

            });
        }

        if (!mDownloadingDialog.isShowing()) {
            mDownloadingDialog.show();
        }
    }

    /**
     * 隐藏下载对话框
     */
    private void dismissDownloadingDialog() {
        if (mDownloadingDialog != null) {
            if (mDownloadingDialog.isShowing()) {
                mDownloadingDialog.dismiss();
            }

            mDownloadingDialog = null;
        }
    }

    /**
     * 显示下载失败对话框
     */
    private void showDownloadFailedDialog(DownloadBuilder downloadBuilder) {
        if (mDownloadFailedDialog == null) {
            UpgradeInfo data = downloadBuilder.getUpgradeInfo();

            if (downloadBuilder.getOnCustomDialogListener() != null) {
                mDownloadFailedDialog = downloadBuilder.getOnCustomDialogListener().getDownloadFailedDialog(this, data);
            }
            if (mDownloadFailedDialog == null) {
                mDownloadFailedDialog = new DefaultDownloadFailedDialog.Builder(this).create();
            }
            mDownloadFailedDialog.show();

            mDownloadFailedDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    EventBus.getDefault().post(new UpgradeEvent(UpgradeEvent.CANCEL_RETRY_DOWNLOAD));
                }

            });

            mDownloadFailedDialog.setOnConfirmListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new UpgradeEvent(UpgradeEvent.RETRY_DOWNLOAD));
                }

            });
        }

        if (!mDownloadFailedDialog.isShowing()) {
            mDownloadFailedDialog.show();
        }
    }

    /**
     * 隐藏下载失败对话框
     */
    private void dismissDownloadFailedDialog() {
        if (mDownloadFailedDialog != null) {
            if (mDownloadFailedDialog.isShowing()) {
                mDownloadFailedDialog.dismiss();
            }

            mDownloadFailedDialog = null;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if ((mVersionDialog == null || !mVersionDialog.isShowing()) && (mDownloadingDialog == null || !mDownloadingDialog.isShowing()) && (mDownloadFailedDialog == null || !mDownloadFailedDialog.isShowing())) {
            finish();
        }
    }

    public static class Builder {

        private Intent mIntent;

        public Builder(Context context) {
            mIntent = new Intent(context, MaskDialogActivity.class);
        }

        private Builder setDialogType(String dialogType) {
            mIntent.putExtra(EXTRA_DIALOG_TYPE, dialogType);
            return this;
        }

        public Builder setVersionType() {
            return setDialogType(TYPE_VERSION);
        }

        public Builder setDownloadingType() {
            return setDialogType(TYPE_DOWNLOADING);
        }

        public Builder setDownloadFailedType() {
            return setDialogType(TYPE_DOWNLOAD_FAILED);
        }

        public Intent create() {
            return mIntent;
        }

    }

}