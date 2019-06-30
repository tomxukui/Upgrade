package com.xukui.library.upgrade.dialog.impl;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.xukui.library.upgrade.R;
import com.xukui.library.upgrade.dialog.DownloadFailedDialog;

public class DefaultDownloadFailedDialog extends Dialog implements DownloadFailedDialog {

    private TextView tv_cancel;
    private TextView tv_confirm;

    public DefaultDownloadFailedDialog(@NonNull Context context) {
        super(context, R.style.versionCheckLib_BaseDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_default_download_failed);
        initView();
        setView();
    }

    private void initView() {
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_confirm = findViewById(R.id.tv_confirm);
    }

    private void setView() {
        setCanceledOnTouchOutside(false);

        tv_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cancel();
            }

        });
    }

    @Override
    public void setOnConfirmListener(View.OnClickListener listener) {
        tv_confirm.setOnClickListener(listener);
    }

    public static class Builder {

        private DefaultDownloadFailedDialog mDialog;

        public Builder(Context context) {
            mDialog = new DefaultDownloadFailedDialog(context);
        }

        public DefaultDownloadFailedDialog create() {
            return mDialog;
        }

    }

}