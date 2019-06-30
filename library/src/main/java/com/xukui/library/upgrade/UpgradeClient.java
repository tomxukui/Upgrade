package com.xukui.library.upgrade;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.xukui.library.upgrade.bean.UpgradeInfo;
import com.xukui.library.upgrade.http.HttpClient;
import com.xukui.library.upgrade.builder.DownloadBuilder;
import com.xukui.library.upgrade.builder.RequestVersionBuilder;
import com.xukui.library.upgrade.service.VersionService;

public class UpgradeClient {

    private Context mContext;

    private UpgradeClient() {
    }

    public static UpgradeClient getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        static final UpgradeClient INSTANCE = new UpgradeClient();
    }

    /**
     * 建议在application中调用
     */
    public void init(Application context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * 请求更新接口
     */
    public RequestVersionBuilder requestVersion() {
        return new RequestVersionBuilder();
    }

    /**
     * 只下载请求
     */
    public DownloadBuilder downloadOnly(@Nullable UpgradeInfo upgradeInfo) {
        return new DownloadBuilder(upgradeInfo);
    }

    /**
     * 取消所有请求
     */
    public void cancelAllMission() {
        try {
            HttpClient.getHttpClient().dispatcher().cancelAll();
            Intent intent = new Intent(mContext, VersionService.class);
            mContext.stopService(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}