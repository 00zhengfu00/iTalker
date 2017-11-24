package com.example.ggxiaozhi.factory.presenter;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：Presenter的公共方法实现的基类
 */

public class BasePresenter<T extends BaseContract.View> implements BaseContract.Presenter {

    private T mView;

    public BasePresenter(T view) {
        setView(view);
    }

    /**
     * 设置一个View子类可以复写
     */
    @SuppressWarnings("unchecked")
    protected void setView(T view) {
        mView = view;
        mView.setPresenter(this);
    }

    /**
     * 给子类提供过去View的操作
     * 不允许被复写
     *
     * @return View
     */
    protected final T getView() {
        return mView;
    }


    @Override
    public void start() {
        T view = mView;
        if (view != null)
            view.showLoading();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void detach() {
        T view = mView;
        if (view != null)
            view.setPresenter(null);
    }
}
