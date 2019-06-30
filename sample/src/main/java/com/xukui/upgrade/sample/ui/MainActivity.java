package com.xukui.upgrade.sample.ui;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.xukui.upgrade.sample.R;
import com.xukui.upgrade.sample.dialog.CustomDownloadFailedDialog;
import com.xukui.upgrade.sample.dialog.CustomDownloadingDialog;
import com.xukui.upgrade.sample.dialog.CustomVersionDialog;
import com.xukui.library.upgrade.UpgradeClient;
import com.xukui.library.upgrade.bean.UpgradeInfo;
import com.xukui.library.upgrade.builder.DownloadBuilder;
import com.xukui.library.upgrade.builder.NotificationBuilder;
import com.xukui.library.upgrade.callback.OnCustomDialogListener;
import com.xukui.library.upgrade.callback.OnRequestVersionListener;
import com.xukui.library.upgrade.dialog.DownloadFailedDialog;
import com.xukui.library.upgrade.dialog.DownloadingDialog;
import com.xukui.library.upgrade.dialog.VersionDialog;

public class MainActivity extends AppCompatActivity {

    private RadioGroup rg_version;
    private RadioGroup rg_downloading;
    private RadioGroup rg_download_failed;
    private CheckBox cb_silent_upgrade;
    private CheckBox cb_force_redownload;
    private CheckBox cb_force;
    private CheckBox cb_only_download;
    private CheckBox cb_show_Notification;
    private CheckBox cb_custom_Notification;
    private EditText et_apkDir;
    private Button btn_send;
    private Button btn_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        rg_version = findViewById(R.id.rg_version);
        rg_downloading = findViewById(R.id.rg_downloading);
        rg_download_failed = findViewById(R.id.rg_download_failed);
        cb_silent_upgrade = findViewById(R.id.cb_silent_upgrade);
        cb_force_redownload = findViewById(R.id.cb_force_redownload);
        cb_force = findViewById(R.id.cb_force);
        cb_only_download = findViewById(R.id.cb_only_download);
        cb_show_Notification = findViewById(R.id.cb_show_Notification);
        cb_custom_Notification = findViewById(R.id.cb_custom_Notification);
        et_apkDir = findViewById(R.id.et_apkDir);
        btn_send = findViewById(R.id.btn_send);
        btn_cancel = findViewById(R.id.btn_cancel);

        btn_send.setOnClickListener(v -> sendRequest());
        btn_cancel.setOnClickListener(v -> UpgradeClient.getInstance().cancelAllMission());
    }

    private void sendRequest() {
        UpgradeInfo data = new UpgradeInfo();
        data.setTitle("更新提示");
        data.setDownloadUrl("http://test-1251233192.coscd.myqcloud.com/1_1.apk");
        data.setContent(getString(R.string.updatecontent));
        data.setForce(cb_force.isChecked());

        DownloadBuilder downloadBuilder;
        if (cb_only_download.isChecked()) {
            downloadBuilder = UpgradeClient
                    .getInstance()
                    .downloadOnly(data);

        } else {
            downloadBuilder = UpgradeClient
                    .getInstance()
                    .requestVersion()
                    .setRequestUrl("https://www.baidu.com")
                    .request(new OnRequestVersionListener() {

                        @Override
                        public UpgradeInfo onRequestVersionSuccess(String result) {
                            Toast.makeText(MainActivity.this, "request successful", Toast.LENGTH_SHORT).show();
                            return data;
                        }

                        @Override
                        public void onRequestVersionFailure(String message) {
                            Toast.makeText(MainActivity.this, "request failed", Toast.LENGTH_SHORT).show();
                        }

                    });
        }

        downloadBuilder.setSilentDownload(cb_silent_upgrade.isChecked());
        downloadBuilder.setShowNotification(cb_show_Notification.isChecked());
        downloadBuilder.setForceRedownload(cb_force_redownload.isChecked());

        String address = et_apkDir.getText().toString();
        if (!TextUtils.isEmpty(address)) {
            downloadBuilder.setApkDir(address);
        }

        if (cb_custom_Notification.isChecked()) {
            NotificationBuilder notificationBuilder = NotificationBuilder.create()
                    .setRingtone(true)
                    .setIcon(R.mipmap.dialog4)
                    .setTicker("custom_ticker")
                    .setContentTitle("custom title")
                    .setContentText("自定义通知栏进度:%d%%/100%%");

            downloadBuilder.setNotificationBuilder(notificationBuilder);
        }

        downloadBuilder.setOnCustomDialogListener(new OnCustomDialogListener() {

            //更新界面选择
            @Override
            public VersionDialog getVersionDialog(Context context, UpgradeInfo upgradeInfo) {
                switch (rg_version.getCheckedRadioButtonId()) {

                    case R.id.rb_default_version:
                        return null;

                    case R.id.rb_custom_version:
                        return new CustomVersionDialog(context, upgradeInfo);

                    default:
                        return null;

                }
            }

            //下载进度界面选择
            @Override
            public DownloadingDialog getDownloadingDialog(Context context, UpgradeInfo upgradeInfo) {
                switch (rg_downloading.getCheckedRadioButtonId()) {

                    case R.id.rb_default_downloading:
                        return null;

                    case R.id.rb_custom_downloading:
                        return new CustomDownloadingDialog(context, upgradeInfo);

                    default:
                        return null;

                }
            }

            @Override
            public DownloadFailedDialog getDownloadFailedDialog(Context context, UpgradeInfo upgradeInfo) {
                switch (rg_download_failed.getCheckedRadioButtonId()) {

                    case R.id.rb_default_download_failed:
                        return null;

                    case R.id.rb_custom_download_failed:
                        return new CustomDownloadFailedDialog(context, upgradeInfo);

                    default:
                        return null;

                }
            }

        });

        downloadBuilder.setOnCancelListener(info -> {
            Toast.makeText(MainActivity.this, "已取消更新", Toast.LENGTH_SHORT).show();

            if (info.isForce()) {
                finish();
            }
        });

        downloadBuilder.executeMission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UpgradeClient.getInstance().cancelAllMission();
    }

}