package com.example.ggxiaozhi.factory.data;

import android.support.annotation.StringRes;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.data
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：数据源接口定义
 */

public interface DataSource {

    /**
     * 只关注成功的接口
     *
     * @param <T> 成功返回的实体对象
     */
    interface SucceedCallback<T> {
        //网络数据加载成功 请求成功
        void onDataLoaded(T t);
    }

    /**
     * 只关注失败的接口
     */
    interface FailedCallback {
        //网络数据加载失败 请求失败
        void onDataNotAvailable(@StringRes int str);
    }

    /**
     * 通知白喊成功与失败的接口
     *
     * @param <T> 成功返回的实体对象
     */
    interface Callback<T> extends SucceedCallback<T>, FailedCallback {

    }

    /**
     * 销毁仓库操作
     */
    void dispose();
}
