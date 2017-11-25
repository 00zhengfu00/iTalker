package com.example.ggxiaozhi.factory.presenter;

import android.support.annotation.StringRes;

import com.example.ggxiaozhi.common.widget.recycler.RecyclerAdapter;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：MVP中公共的契约(P与V)
 */

public interface BaseContract {
    interface View<T extends Presenter> {

        //请求失败
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
        void detach();
    }

    interface RecyclerView<T extends Presenter,ViewModel> extends View<T>{
        //不能这样做 这样做会将界面全部刷新 那么就会显示效果很不好 我们想做的局部刷新
        //void onDone(List<User> users);

        //拿到一个适配器 然后我们自己决定如何刷新
        RecyclerAdapter<ViewModel> getAdapter();

        //当适配器数据更改了的时候触发
        void onAdapterDataChanged();
    }
}
