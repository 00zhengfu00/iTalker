package com.example.ggxiaozhi.factory;

import com.example.ggxiaozhi.common.app.Application;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
    //全局的线程池
    private final Executor mExecutor;
    private final Gson mGson;

    static {
        instance = new Factory();
    }

    private Factory() {
        //创建一个4个线程的线程池
        mExecutor = Executors.newFixedThreadPool(4);
        mGson = new GsonBuilder()
                //设置时间格式
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                // TODO 设置一个过滤器，数据库级别的Model不进行Json转换
//                .setExclusionStrategies()
                .create();

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

    /**
     * 返回一个全局的Gson 在这里可以进行Gson的一些全局的初始化
     *
     * @return
     */
    public static Gson getGson() {
        return instance.mGson;
    }
}
