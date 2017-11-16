package com.example.ggxiaozhi.factory;

import com.example.ggxiaozhi.common.app.Application;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：提供上传逻辑需要的参数
 */

public class Factory {
    //单例模式
    private static final Factory instance;
    private final Executor mExecutor;

    static {
        instance = new Factory();
    }

    private Factory() {
        //创建一个4个线程的线程池
        mExecutor = Executors.newFixedThreadPool(4);

    }

    public static Application app() {
        return Application.getInstance();
    }

    /**
     * 异步执行的方法
     *
     * @param runnable
     */
    public static void runOnAsync(Runnable runnable) {
        //拿到单例 拿到线程池 然后异步执行
        instance.mExecutor.execute(runnable);
    }
}
