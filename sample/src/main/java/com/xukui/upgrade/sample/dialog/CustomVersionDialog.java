package com.xukui.upgrade.sample.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xukui.upgrade.sample.R;
import com.xukui.library.upgrade.bean.UpgradeInfo;
import com.xukui.library.upgrade.dialog.VersionDialog;

public class CustomVersionDialog extends Dialog implements VersionDialog {

    private TextView tv_title;
    private TextView tv_msg;
    private Button btn_commit;

    private UpgradeInfo mUpgradeInfo;

    public CustomVersionDialog(@NonNull Context context, UpgradeInfo upgradeInfo) {
        super(context, R.style.BaseDialog);
        mUpgradeInfo = upgradeInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_custom_version);

        tv_title = findViewById(R.id.tv_title);
        tv_msg = findViewById(R.id.tv_msg);
        btn_commit = findViewById(R.id.btn_commit);

        tv_title.setText(mUpgradeInfo.getTitle());
        tv_msg.setText(mUpgradeInfo.getContent());
    }

    @Override
    public void setOnConfirmListener(View.OnClickListener listener) {
        btn_commit.setOnClickListener(listener);
    }

}