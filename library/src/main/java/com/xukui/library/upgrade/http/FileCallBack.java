package com.xukui.library.upgrade.http;

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

    public FileCallBack(String path, String name) {
        mPath = path;
        mName = name;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        onDownloadFailed();
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
                int progress = (total < 0 ? -1 : ((int) (((double) sum / total) * 100)));

                onDownloading(progress, sum);
            }

            fos.flush();

            onSuccess(file, call, response);

        } catch (Exception e) {
            e.printStackTrace();

            onDownloadFailed();

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

    public abstract void onSuccess(File file, Call call, Response response);

    public abstract void onDownloading(int progress, long currentLength);

    public abstract void onDownloadFailed();

}