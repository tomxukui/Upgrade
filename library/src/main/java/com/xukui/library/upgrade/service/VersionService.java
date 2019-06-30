package com.xukui.library.upgrade.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.xukui.library.upgrade.callback.OnCheckDownloadListener;
import com.xukui.library.upgrade.http.HttpClient;
import com.xukui.library.upgrade.event.DownloadingProgressEvent;
import com.xukui.library.upgrade.event.UpgradeEvent;
import com.xukui.library.upgrade.ui.MaskDialogActivity;
import com.xukui.library.upgrade.utils.UpgradeUtil;
import com.xukui.library.upgrade.UpgradeClient;
import com.xukui.library.upgrade.builder.DownloadBuilder;
import com.xukui.library.upgrade.utils.NotificationHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VersionService extends Service {

    private static DownloadBuilder mDownloadBuilder;
    private ExecutorService mExecutorService;
    private NotificationHelper mNotificationHelper;

    private boolean mIsServiceAlive = false;//服务是否存在
    private boolean mIsDownloadComplete = false;//下载是否已完成

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        if (!mIsServiceAlive) {
            init();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        mIsServiceAlive = false;
        HttpClient.getHttpClient().dispatcher().cancelAll();

        if (mExecutorService != null) {
            mExecutorService.shutdown();
        }

        if (mNotificationHelper != null) {
            try {
                mNotificationHelper.onDestroy();
                stopForeground(true);

            } catch (Exception e) {
                e.printStackTrace();
            }

            mNotificationHelper = null;
        }

        if (mDownloadBuilder != null) {
            mDownloadBuilder = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 初始化
     */
    private void init() {
        if (mDownloadBuilder == null) {
            UpgradeClient.getInstance().cancelAllMission();
            return;
        }

        mIsServiceAlive = true;

        if (!mDownloadBuilder.isSilentDownload()) {
            if (mDownloadBuilder.isShowNotification()) {
                mNotificationHelper = new NotificationHelper(mDownloadBuilder.getNotificationBuilder());

                startForeground(NotificationHelper.NOTIFICATION_ID, mNotificationHelper.createServiceNotification());
            }
        }

        mExecutorService = Executors.newSingleThreadExecutor();
        mExecutorService.submit(new Runnable() {

            @Override
            public void run() {
                downloadAPK();
            }

        });
    }

    /**
     * 显示版本对话框
     */
    private void showVersionDialog() {
        Intent intent = new MaskDialogActivity.Builder(this)
                .setVersionType()
                .create()
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }

    /**
     * 显示下载对话框
     */
    private void showDownloadingDialog() {
        Intent intent = new MaskDialogActivity.Builder(this)
                .setDownloadingType()
                .create()
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }

    /**
     * 更新下载进度
     */
    private void updateDownloadingDialogProgress(int progress) {
        EventBus.getDefault().post(new DownloadingProgressEvent(progress));
    }

    /**
     * 显示下载失败对话框
     */
    private void showDownloadFailedDialog() {
        Intent intent = new MaskDialogActivity.Builder(this)
                .setDownloadFailedType()
                .create()
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }

    /**
     * 下载apk
     */
    private void downloadAPK() {
        if (mDownloadBuilder.isSilentDownload()) {
            startDownloadApk();

        } else {
            showVersionDialog();
        }
    }

    /**
     * 安装apk
     */
    private void install() {
        UpgradeUtil.installApk(mDownloadBuilder.getApkFile());
    }

    @WorkerThread
    private void startDownloadApk() {
        //判断是否缓存并且是否强制重新下载
        if (!mDownloadBuilder.isForceRedownload()) {
            if (UpgradeUtil.checkApkExist(mDownloadBuilder.getApkFile())) {
                install();
                return;
            }
        }

        //如果存在该文件, 则先删除
        UpgradeUtil.deleteFile(mDownloadBuilder.getApkFile());

        //准备下载apk
        mIsDownloadComplete = false;
        UpgradeUtil.download(mDownloadBuilder.getUpgradeInfo().getDownloadUrl(), mDownloadBuilder.getApkDir(), mDownloadBuilder.getApkName(), new OnCheckDownloadListener() {

            @Override
            public void onCheckerStartDownload() {
                if (!mIsServiceAlive) {
                    return;
                }

                if (mNotificationHelper != null) {
                    mNotificationHelper.showDownloadingNotification();
                }

                if (!mDownloadBuilder.isSilentDownload()) {
                    showDownloadingDialog();
                }
            }

            @Override
            public void onCheckerDownloading(int progress) {
                if (!mIsServiceAlive) {
                    return;
                }

                if (mNotificationHelper != null) {
                    mNotificationHelper.updateDownloadProgressNotification(progress);
                }

                if (!mDownloadBuilder.isSilentDownload()) {
                    updateDownloadingDialogProgress(progress);
                }

                if (mDownloadBuilder.getOnDownloadListener() != null) {
                    mDownloadBuilder.getOnDownloadListener().onDownloading(progress);
                }
            }

            @Override
            public void onCheckerDownloadSuccess(File file) {
                if (!mIsServiceAlive) {
                    return;
                }

                mIsDownloadComplete = true;

                if (mNotificationHelper != null) {
                    mNotificationHelper.showDownloadCompleteNotifcation(file);
                }

                if (mDownloadBuilder.getOnDownloadListener() != null) {
                    mDownloadBuilder.getOnDownloadListener().onDownloadSuccess(file);
                }

                install();
            }

            @Override
            public void onCheckerDownloadFail() {
                if (!mIsServiceAlive) {
                    return;
                }

                if (mDownloadBuilder.getOnDownloadListener() != null) {
                    mDownloadBuilder.getOnDownloadListener().onDownloadFail();
                }

                if (mNotificationHelper != null) {
                    mNotificationHelper.showDownloadFailedNotification();
                }

                if (!mDownloadBuilder.isSilentDownload()) {
                    showDownloadFailedDialog();

                } else {
                    UpgradeClient.getInstance().cancelAllMission();
                }
            }

        });
    }

    private void cancelUpgrade() {
        UpgradeClient.getInstance().cancelAllMission();

        if (mDownloadBuilder.getOnCancelListener() != null) {
            mDownloadBuilder.getOnCancelListener().onCancel(mDownloadBuilder.getUpgradeInfo());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpgradeEvent(UpgradeEvent event) {
        switch (event.type) {

            case UpgradeEvent.CONFIRM_UPGRADE: {//用户同意更新
                startDownloadApk();
            }
            break;

            case UpgradeEvent.DOWNLOAD_COMPLETE: {//下载已完成
                install();
            }
            break;

            case UpgradeEvent.RETRY_DOWNLOAD: {//重新下载
                startDownloadApk();
            }
            break;

            case UpgradeEvent.CANCEL_UPGRADE: {//用户取消更新
                cancelUpgrade();
            }
            break;

            case UpgradeEvent.CANCEL_DOWNLOADING: {//用户取消下载
                HttpClient.getHttpClient().dispatcher().cancelAll();

                if (mIsDownloadComplete) {
                    if (mNotificationHelper != null) {
                        mNotificationHelper.showServiceNotification();
                    }

                    showVersionDialog();
                }
            }
            break;

            case UpgradeEvent.CANCEL_RETRY_DOWNLOAD: {//用户取消重试
                if (mNotificationHelper != null) {
                    mNotificationHelper.showServiceNotification();
                }

                showVersionDialog();
            }
            break;

            default:
                break;

        }
    }

    public static DownloadBuilder getDownloadBuilder() {
        return mDownloadBuilder;
    }

    public static void enqueueWork(DownloadBuilder downloadBuilder) {
        if (downloadBuilder == null) {
            return;
        }

        mDownloadBuilder = downloadBuilder;

        Context context = UpgradeClient.getInstance().getContext();

        Intent intent = new Intent(context, VersionService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);

        } else {
            context.startService(intent);
        }
    }

}