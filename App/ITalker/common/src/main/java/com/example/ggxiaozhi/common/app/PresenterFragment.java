package com.example.ggxiaozhi.common.app;

import android.content.Context;
import android.support.annotation.StringRes;

import com.example.ggxiaozhi.factory.presenter.BaseContract;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.common.app
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：Fragment基类的封装 显示一些公用的Presenter方法的实现
 */

public abstract class PresenterFragment<Presenter extends BaseContract.Presenter> extends Fragment implements BaseContract.View<Presenter> {


    protected Presenter mPresenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //在界面onAttach后初始化Presenter
        initPresenter();//给当前View接口和Presenter赋值
    }

    protected abstract Presenter initPresenter();

    @Override
    public void showError(@StringRes int str) {
        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerError(str);
        } else {
            Application.showToast(str);
        }
    }

    @Override
    public void showLoading() {
        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerLoading();
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.detach();
    }
}
