package com.xukui.library.upgrade.http;

import android.os.Handler;
import android.os.Looper;

import com.xukui.library.upgrade.UpgradeClient;
import com.xukui.library.upgrade.bean.UpgradeInfo;
import com.xukui.library.upgrade.builder.DownloadBuilder;
import com.xukui.library.upgrade.builder.RequestVersionBuilder;
import com.xukui.library.upgrade.callback.OnRequestVersionListener;

import java.io.IOException;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RequestVersionManager {

    private RequestVersionManager() {
    }

    public static RequestVersionManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public static class SingletonHolder {
        static final RequestVersionManager INSTANCE = new RequestVersionManager();
    }

    /**
     * 请求版本接口
     */
    public void requestVersion(final DownloadBuilder builder) {
        Executors.newSingleThreadExecutor().submit(new Runnable() {

            @Override
            public void run() {
                RequestVersionBuilder requestVersionBuilder = builder.getRequestVersionBuilder();
                OkHttpClient client = HttpClient.getHttpClient();
                HttpRequestMethod requestMethod = requestVersionBuilder.getRequestMethod();
                Request request = null;

                switch (requestMethod) {

                    case GET: {
                        request = HttpClient.get(requestVersionBuilder).build();
                    }
                    break;

                    case POST: {
                        request = HttpClient.post(requestVersionBuilder).build();
                    }
                    break;

                    case POSTJSON: {
                        request = HttpClient.postJson(requestVersionBuilder).build();
                    }
                    break;

                    default:
                        break;

                }

                final OnRequestVersionListener onRequestVersionListener = requestVersionBuilder.getOnRequestVersionListener();
                Handler handler = new Handler(Looper.getMainLooper());
                if (onRequestVersionListener != null) {
                    try {
                        final Response response = client.newCall(request).execute();
                        if (response.isSuccessful()) {
                            final String result = response.body() != null ? response.body().string() : null;

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    UpgradeInfo upgradeInfo = onRequestVersionListener.onRequestVersionSuccess(result);
                                    if (upgradeInfo != null) {
                                        builder.setUpgradeInfo(upgradeInfo);
                                        builder.download();
                                    }
                                }
                            });

                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onRequestVersionListener.onRequestVersionFailure(response.message());
                                    UpgradeClient.getInstance().cancelAllMission();
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                onRequestVersionListener.onRequestVersionFailure(e.getMessage());
                                UpgradeClient.getInstance().cancelAllMission();
                            }

                        });
                    }

                } else {
                    throw new RuntimeException("using request version function,you must set a requestVersionListener");
                }
            }
        });
    }

}