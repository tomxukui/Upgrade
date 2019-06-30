package com.xukui.library.upgrade.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.xukui.library.upgrade.R;
import com.xukui.library.upgrade.builder.NotificationBuilder;
import com.xukui.library.upgrade.ui.MaskDialogActivity;
import com.xukui.library.upgrade.UpgradeClient;

import java.io.File;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationHelper {

    public static final int NOTIFICATION_ID = 1;

    private static final String CHANNEL_ID = "version_service_id";
    private static final String CHANNEL_NAME = "version_service_name";

    private NotificationManager mManager;
    private NotificationCompat.Builder mBuilder;

    private int mCurrentProgress;
    private int mSmallIcon;
    private boolean mIsRingtone;
    private String mContentTitle;
    private String mTicker;
    private String mContentText;

    public NotificationHelper(NotificationBuilder notificationBuilder) {
        mManager = (NotificationManager) UpgradeClient.getInstance().getContext().getSystemService(NOTIFICATION_SERVICE);
        mCurrentProgress = 0;

        mSmallIcon = notificationBuilder.getIcon();
        mIsRingtone = notificationBuilder.isRingtone();

        if (notificationBuilder.getContentTitle() != null) {
            mContentTitle = notificationBuilder.getContentTitle();
        }
        if (mContentTitle == null) {
            mContentTitle = UpgradeUtil.getString(R.string.app_name);
        }

        if (notificationBuilder.getTicker() != null) {
            mTicker = notificationBuilder.getTicker();
        }
        if (mTicker == null) {
            mTicker = UpgradeUtil.getString(R.string.upgrade_downloading);
        }

        if (notificationBuilder.getContentText() != null) {
            mContentText = notificationBuilder.getContentText();
        }
        if (mContentText == null) {
            mContentText = UpgradeUtil.getString(R.string.upgrade_download_progress);
        }
    }

    /**
     * 创建通知
     */
    public Notification createServiceNotification() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            mManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notifcationBuilder = new NotificationCompat.Builder(UpgradeClient.getInstance().getContext(), CHANNEL_ID)
                .setContentTitle(UpgradeUtil.getString(R.string.app_name))
                .setContentText(UpgradeUtil.getString(R.string.upgrade_service_running))
                .setSmallIcon(mSmallIcon)
                .setAutoCancel(false);

        if (mIsRingtone) {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone ringtone = RingtoneManager.getRingtone(UpgradeClient.getInstance().getContext(), notification);
            ringtone.play();
        }

        mBuilder = notifcationBuilder;
        return notifcationBuilder.build();
    }

    /**
     * 显示服务通知栏
     */
    public void showServiceNotification() {
        mBuilder.setContentTitle(UpgradeUtil.getString(R.string.app_name));
        mBuilder.setTicker(UpgradeUtil.getString(R.string.upgrade_service_running));
        mBuilder.setContentText(UpgradeUtil.getString(R.string.upgrade_service_running));
        mBuilder.setProgress(100, 0, false);
        mBuilder.setContentIntent(null);

        mManager.cancelAll();
        mManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    /**
     * 显示下载的通知栏
     */
    public void showDownloadingNotification() {
        mCurrentProgress = 0;

        mBuilder.setContentTitle(mContentTitle);
        mBuilder.setTicker(mTicker);
        mBuilder.setContentText(String.format(mContentText, 0));
        mBuilder.setProgress(100, mCurrentProgress, false);
        mBuilder.setContentIntent(null);

        mManager.cancelAll();
        mManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    /**
     * 更新下载进度的通知
     */
    public void updateDownloadProgressNotification(int progress) {
        if ((progress - mCurrentProgress) > 5) {
            mBuilder.setContentText(String.format(mContentText, progress));
            mBuilder.setProgress(100, progress, false);
            mBuilder.setContentIntent(null);

            mManager.notify(NOTIFICATION_ID, mBuilder.build());
            mCurrentProgress = progress;
        }
    }

    /**
     * 显示下载完成的通知
     */
    public void showDownloadCompleteNotifcation(File file) {
        mCurrentProgress = 100;

        Intent intent = UpgradeUtil.buildInstallApkIntent(file);
        PendingIntent pendingIntent = PendingIntent.getActivity(UpgradeClient.getInstance().getContext(), 0, intent, 0);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setContentText(UpgradeUtil.getString(R.string.upgrade_download_install));
        mBuilder.setProgress(100, mCurrentProgress, false);

        mManager.cancelAll();
        mManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    /**
     * 显示下载失败的通知
     */
    public void showDownloadFailedNotification() {
        mCurrentProgress = 0;

        Intent intent = new MaskDialogActivity.Builder(UpgradeClient.getInstance().getContext())
                .setDownloadFailedType()
                .create()
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(UpgradeClient.getInstance().getContext(), 0, intent, FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setContentText(UpgradeUtil.getString(R.string.upgrade_download_fail_retry));
        mBuilder.setProgress(100, mCurrentProgress, false);

        mManager.cancelAll();
        mManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    public void onDestroy() {
        if (mManager != null) {
            mManager.cancel(NOTIFICATION_ID);
        }
    }

}