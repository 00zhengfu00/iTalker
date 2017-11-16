package com.example.ggxiaozhi.common.app;

import android.os.SystemClock;
import android.support.annotation.StringRes;
import android.util.Log;
import android.widget.Toast;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.io.File;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.common.app
 * 作者名 ： 志先生_
 * 日期   ： 2017/11/10
 * 功能   ：全局的App
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
     * 获取单例的方法
     *
     * @return
     */
    public static Application getInstance() {
        return instance;
    }

    /**
     * 获取缓存文件地址
     *
     * @return 当前APP缓存文件夹的地址
     */
    public static File getCacheDirFile() {
        return instance.getCacheDir();
    }

    /**
     * 获取头像的临时存储文件地址
     *
     * @return
     */
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
        File path = new File(dir, SystemClock.uptimeMillis() + ".jpg");
        return path.getAbsoluteFile();
    }

    /**
     * 获取声音文件的本地地址
     *
     * @param isTmp 是否是缓存文件，True每次返回的文件地址是一样的
     * @return 录音文件地址
     */
    public static File getAudioTmpFile(boolean isTmp) {

        File dir = new File(getCacheDirFile(), "audio");
        dir.mkdirs();
        File[] files = dir.listFiles();
        if (files.length > 0 && files.length > 0) {
            for (File file : files) {
                file.delete();
            }
        }

        //aar
        File path = new File(dir, isTmp ? "tmp.mp3" : SystemClock.uptimeMillis() + ".mp3");
        Log.d(TAG, "getCacheDirFile: " + getCacheDirFile());
        Log.d(TAG, "path: " + path.getAbsolutePath());
        return path.getAbsoluteFile();
    }

    /**
     * 显示一个Toast
     *
     * @param msg 字符串
     */
    public static void showToast(final String msg) {
        // Toast 只能在主线程中显示，所有需要进行线程转换，
        // 保证一定是在主线程进行的show操作
//        Toast.makeText(instance, msg, Toast.LENGTH_SHORT).show();
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                // 这里进行回调的时候一定就是主线程状态了
                Toast.makeText(instance, msg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 显示一个Toast
     *
     * @param msgId 传递的是字符串的资源
     */
    public static void showToast(@StringRes int msgId) {
        showToast(instance.getString(msgId));
    }
}
