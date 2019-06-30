package com.xukui.upgrade.sample;

import android.app.Application;

import com.xukui.library.upgrade.UpgradeClient;

public class Mapp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        UpgradeClient.getInstance().init(this);
    }

}