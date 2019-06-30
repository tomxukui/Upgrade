package com.xukui.library.upgrade.callback;

import com.xukui.library.upgrade.bean.UpgradeInfo;

public interface OnRequestVersionListener {

    UpgradeInfo onRequestVersionSuccess(String result);

    void onRequestVersionFailure(String message);

}