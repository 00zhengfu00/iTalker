package com.example.ggxiaozhi.italker;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.WindowManager;

import com.example.ggxiaozhi.common.app.Application;
import com.example.ggxiaozhi.common.widget.CommomDialog;
import com.example.ggxiaozhi.factory.Factory;
import com.example.ggxiaozhi.italker.activity.AccountActivity;
import com.igexin.sdk.PushManager;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.italker
 * 作者名 ： 志先生_
 * 日期   ： 2017/11/10
 * 功能   ：
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //调用Factory初始化
        Factory.setup();
        //个推进行初始化
        PushManager.getInstance().initialize(this);
    }

    @Override
    public void showAccountView(Context context) {
        Intent intent = new Intent(context, AccountActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
