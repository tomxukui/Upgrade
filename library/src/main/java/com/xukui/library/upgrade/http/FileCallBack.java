package com.xukui.library.upgrade.http;

import android.os.Handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public abstract class FileCallBack implements Callback {

    private String mPath;
    private String mName;

    private Handler mHandler;

    public FileCallBack(Handler handler, String path, String name) {
        mHandler = handler;
        mPath = path;
        mName = name;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                onDownloadFailed();
            }

        });
    }

    @Override
    public void onResponse(final Call call, final Response response) throws IOException {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        // 储存下载文件的目录
        File pathFile = new File(mPath);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }

        try {
            is = response.body().byteStream();

            final File file = new File(mPath, mName);
            if (file.exists()) {
                file.delete();

            } else {
                file.createNewFile();
            }

            fos = new FileOutputStream(file);
            long sum = 0;
            while ((len = is.read(buf)) != -1) {
                long total = response.body().contentLength();
                fos.write(buf, 0, len);
                sum += len;
                final int progress = (int) (((double) sum / total) * 100);

                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        onDownloading(progress);
                    }

                });
            }

            fos.flush();

            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    onSuccess(file, call, response);
                }

            });

        } catch (Exception e) {
            e.printStackTrace();

            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    onDownloadFailed();
                }

            });

        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Handler getHandle() {
        return mHandler;
    }

    public abstract void onSuccess(File file, Call call, Response response);

    public abstract void onDownloading(int progress);

    public abstract void onDownloadFailed();

}