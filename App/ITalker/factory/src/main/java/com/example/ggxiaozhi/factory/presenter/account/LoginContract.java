package com.example.ggxiaozhi.factory.presenter.account;

import com.example.ggxiaozhi.factory.presenter.BaseContract;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.presenter
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：登录MVP 中的P与V
 */

public interface LoginContract {

    interface View extends BaseContract.View<Presenter> {
        //登录成功
        void loginSuccess();
    }

    interface Presenter extends BaseContract.Presenter {

        //发起登录请求
        void login(String phone, String password);

    }

}
