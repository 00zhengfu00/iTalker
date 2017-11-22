package com.example.ggxiaozhi.italker;

import com.example.ggxiaozhi.common.app.Application;
import com.example.ggxiaozhi.factory.Factory;
import com.igexin.sdk.PushManager;

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
}
