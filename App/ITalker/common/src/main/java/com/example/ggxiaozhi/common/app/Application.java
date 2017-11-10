package com.example.ggxiaozhi.common.app;

import android.os.SystemClock;
import android.util.Log;

import java.io.File;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.common.app
 * 作者名 ： 志先生_
 * 日期   ： 2017/11/10
 * 功能   ：
 */

public class Application extends android.app.Application {

    private static final String TAG = "Application";
    private static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    /**
     * 获取缓存文件地址
     *
     * @return 当前APP缓存文件夹的地址
     */
    public static File getCacheDirFile() {
        return instance.getCacheDir();
    }

    public static File getPortraitTmpFile() {
        File dir = new File(getCacheDirFile(), "portrait");
        dir.mkdirs();

        File[] files = dir.listFiles();
        //删除一些旧的缓存文件
        if (files.length > 0 && files != null) {
            for (File file : files) {
                file.delete();
            }
        }
        //返回当前时间戳的目录文件地址
        File path = new File(dir, SystemClock.currentThreadTimeMillis() + ".jpg");
        return path.getAbsoluteFile();
    }
}
