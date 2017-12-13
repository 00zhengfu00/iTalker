package com.example.ggxiaozhi.common.app;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.ggxiaozhi.common.R;
import com.example.ggxiaozhi.common.widget.CommomDialog;
import com.example.ggxiaozhi.common.widget.convention.PlaceHolderView;

import java.util.List;

import butterknife.ButterKnife;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.common.app
 * 作者名 ： 志先生_
 * 日期   ： 2017/11/4
 * 功能   ：所有Activity的父类
 */

public abstract class Activity extends BaseActivity {
    protected ForceOfflineReceiver mReceiver;

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.broadcastbestpracrice.FORCE_OFFLINE");
        mReceiver = new ForceOfflineReceiver();
        registerReceiver(mReceiver, intentFilter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    public class ForceOfflineReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, Intent intent) {
            CommomDialog dialog = new CommomDialog(context, R.style.dialog, "是否重新登录?", new CommomDialog.OnCloseListener() {
                @Override
                public void onClick(Dialog dialog, boolean confirm) {
                    if (confirm) {
                        //登录界面显示
                        Application.getInstance().showAccountView(context);
                    }
                    Application.getInstance().finishAll();
                    dialog.dismiss();
                }
            });
            dialog.setTitle("您的账户已在其他设备上登录");
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    Application.getInstance().finishAll();
                }
            });
            dialog.setPositiveButton("登录").setNegativeButton("退出").show();
        }
    }
}
