package com.example.ggxiaozhi.factory.presenter;

import android.support.annotation.StringRes;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：MVP中公共的契约(P与V)
 */

public interface BaseContract {
     interface View<T extends Presenter> {

        //注册失败
        void showError(@StringRes int str);

        //显示进度条
        void showLoading();

        //支持设置一个Presenter
        void setPresenter(T presenter);
    }

     interface Presenter {

        //公用的开始触发
        void start();

        //公用的销毁触发
        void destory();
    }

}
