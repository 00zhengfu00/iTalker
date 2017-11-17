package com.example.ggxiaozhi.factory.presenter.account;


import com.example.ggxiaozhi.factory.BaseContract;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.presenter
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：注册MVP 中的P与V
 */

public interface RegisterContract {

     interface View extends BaseContract.View<Presenter> {
        //注册成功
        void registerSuccess();

    }

     interface Presenter extends BaseContract.Presenter {

        //发起注册请求
        void register(String phone, String name, String password);

        //检查手机号是否正确
        boolean checkMobile(String phone);

    }

}
