package com.example.ggxiaozhi.factory.presenter.user;

import com.example.ggxiaozhi.factory.presenter.BaseContract;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.presenter.user
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：用户更新的基本的契约
 */

public interface UpdateInfoContract {

    interface Presenter extends BaseContract.Presenter {
        //更新
        void update(String photoFilePath, String desc, boolean isMan);
    }

    interface View extends BaseContract.View<Presenter> {
        //回调成功
        void updateSuccessed();
    }
}
