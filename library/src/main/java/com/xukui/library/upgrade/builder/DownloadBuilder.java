package com.xukui.library.upgrade.builder;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xukui.library.upgrade.bean.UpgradeInfo;
import com.xukui.library.upgrade.callback.OnCancelListener;
import com.xukui.library.upgrade.callback.OnCustomDialogListener;
import com.xukui.library.upgrade.callback.OnDownloadListener;
import com.xukui.library.upgrade.UpgradeClient;
import com.xukui.library.upgrade.http.RequestVersionManager;
import com.xukui.library.upgrade.service.VersionService;
import com.xukui.library.upgrade.utils.UpgradeUtil;

import java.io.File;

public class DownloadBuilder {

    private RequestVersionBuilder mRequestVersionBuilder;
    private NotificationBuilder mNotificationBuilder;

    private OnCustomDialogListener mOnCustomDialogListener;
    private OnCancelListener mOnCancelListener;
    private OnDownloadListener mOnDownloadListener;

    private UpgradeInfo mUpgradeInfo;//版本更新信息
    private String mApkDir;//存储apk的文件目录
    private String mApkName;//apk的文件名称

    private boolean mIsForceRedownload;//是否强制下载(不使用缓存文件)
    private boolean mIsSilentDownload;//是否静默下载
    private boolean mIsShowNotification;//是否显示通知栏

    private DownloadBuilder(@Nullable RequestVersionBuilder requestVersionBuilder, @Nullable UpgradeInfo upgradeInfo) {
        mRequestVersionBuilder = requestVersionBuilder;
        mUpgradeInfo = upgradeInfo;

        initialize();
    }

    public DownloadBuilder(@NonNull RequestVersionBuilder requestVersionBuilder) {
        this(requestVersionBuilder, null);
    }

    public DownloadBuilder(@NonNull UpgradeInfo upgradeInfo) {
        this(null, upgradeInfo);
    }

    private void initialize() {
        mIsForceRedownload = true;
        mIsSilentDownload = false;
        mIsShowNotification = true;
    }

    //设置自定义对话框的回调
    public DownloadBuilder setOnCustomDialogListener(@Nullable OnCustomDialogListener listener) {
        mOnCustomDialogListener = listener;
        return this;
    }

    //设置取消更新的回调
    public DownloadBuilder setOnCancelListener(OnCancelListener cancelListener) {
        mOnCancelListener = cancelListener;
        return this;
    }

    //设置下载apk的回调
    public DownloadBuilder setOnDownloadListener(@Nullable OnDownloadListener listener) {
        mOnDownloadListener = listener;
        return this;
    }

    //设置版本更新信息
    public DownloadBuilder setUpgradeInfo(@NonNull UpgradeInfo upgradeInfo) {
        mUpgradeInfo = upgradeInfo;
        return this;
    }

    //设置存储apk的文件目录
    public DownloadBuilder setApkDir(String apkDir) {
        mApkDir = apkDir;
        return this;
    }

    //设置是否强制下载
    public DownloadBuilder setForceRedownload(boolean forceRedownload) {
        mIsForceRedownload = forceRedownload;
        return this;
    }

    //设置是否静默下载
    public DownloadBuilder setSilentDownload(boolean silentDownload) {
        mIsSilentDownload = silentDownload;
        return this;
    }

    //设置是否显示通知栏
    public DownloadBuilder setShowNotification(boolean showNotification) {
        mIsShowNotification = showNotification;
        return this;
    }

    public DownloadBuilder setNotificationBuilder(@NonNull NotificationBuilder notificationBuilder) {
        mNotificationBuilder = notificationBuilder;
        return this;
    }

    //获取自定义对话框的回调
    public OnCustomDialogListener getOnCustomDialogListener() {
        return mOnCustomDialogListener;
    }

    //获取取消更新的回调
    public OnCancelListener getOnCancelListener() {
        return mOnCancelListener;
    }

    //获取下载apk的回调
    public OnDownloadListener getOnDownloadListener() {
        return mOnDownloadListener;
    }

    //获取版本更新信息
    public UpgradeInfo getUpgradeInfo() {
        return mUpgradeInfo;
    }

    //获取存储apk的文件目录
    public String getApkDir() {
        return mApkDir;
    }

    //获取apk的文件名称
    public String getApkName() {
        return mApkName;
    }

    //获取apk的文件地址
    public File getApkFile() {
        return new File(mApkDir, mApkName);
    }

    //获取是否强制下载
    public boolean isForceRedownload() {
        return mIsForceRedownload;
    }

    //获取是否静默下载
    public boolean isSilentDownload() {
        return mIsSilentDownload && (mUpgradeInfo != null && mUpgradeInfo.isForce());
    }

    //获取是否显示通知栏
    public boolean isShowNotification() {
        return mIsShowNotification;
    }

    public RequestVersionBuilder getRequestVersionBuilder() {
        return mRequestVersionBuilder;
    }

    public NotificationBuilder getNotificationBuilder() {
        return mNotificationBuilder;
    }

    public void executeMission() {
        Context context = UpgradeClient.getInstance().getContext();

        if (TextUtils.isEmpty(mApkDir)) {
            mApkDir = UpgradeUtil.getDefaultApkDir();
        }

        if (mNotificationBuilder == null) {
            mNotificationBuilder = NotificationBuilder.create();
        }
        if (mNotificationBuilder.getIcon() == 0) {
            final PackageManager pm = context.getPackageManager();
            final ApplicationInfo applicationInfo;
            try {
                applicationInfo = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);

                final int appIconResId = applicationInfo.icon;
                mNotificationBuilder.setIcon(appIconResId);

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (mRequestVersionBuilder != null) {
            RequestVersionManager.getInstance().requestVersion(this);

        } else {
            download();
        }
    }

    public void download() {
        if (mUpgradeInfo == null) {
            return;
        }

        String downloadUrl = mUpgradeInfo.getDownloadUrl();

        if (TextUtils.isEmpty(downloadUrl)) {
            return;
        }

        mApkName = UpgradeUtil.getApkName(downloadUrl);

        if (mApkName == null) {
            return;
        }

        VersionService.enqueueWork(this);
    }

}