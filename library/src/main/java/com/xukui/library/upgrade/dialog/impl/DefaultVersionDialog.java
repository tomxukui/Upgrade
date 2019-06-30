package com.xukui.library.upgrade.dialog.impl;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.xukui.library.upgrade.R;
import com.xukui.library.upgrade.dialog.VersionDialog;

public class DefaultVersionDialog extends Dialog implements VersionDialog {

    private TextView tv_title;
    private TextView tv_message;
    private TextView tv_cancel;
    private TextView tv_confirm;

    private String mTitle;
    private String mMessage;
    private boolean mForce;

    public DefaultVersionDialog(@NonNull Context context) {
        super(context, R.style.versionCheckLib_BaseDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_default_version);
        initView();
        setView();
    }

    private void initView() {
        tv_title = findViewById(R.id.tv_title);
        tv_message = findViewById(R.id.tv_message);
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_confirm = findViewById(R.id.tv_confirm);
    }

    private void setView() {
        setCanceledOnTouchOutside(false);
        setCancelable(!mForce);

        tv_title.setText(mTitle);

        tv_message.setText(mMessage);
        tv_message.setMovementMethod(ScrollingMovementMethod.getInstance());

        tv_cancel.setText(mForce ? R.string.upgrade_cancel : R.string.upgrade_ignore);
        tv_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cancel();
            }

        });
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public void setForce(boolean force) {
        mForce = force;
    }

    @Override
    public void setOnConfirmListener(View.OnClickListener listener) {
        tv_confirm.setOnClickListener(listener);
    }

    public static class Builder {

        private DefaultVersionDialog mDialog;

        public Builder(Context context) {
            mDialog = new DefaultVersionDialog(context);
        }

        public Builder setTitle(String title) {
            mDialog.setTitle(title);
            return this;
        }

        public Builder setMessage(String message) {
            mDialog.setMessage(message);
            return this;
        }

        public Builder force(boolean force) {
            mDialog.setForce(force);
            return this;
        }

        public DefaultVersionDialog create() {
            return mDialog;
        }

    }

}