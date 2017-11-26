package com.example.ggxiaozhi.factory.presenter.contact;

import com.example.ggxiaozhi.factory.model.db.User;
import com.example.ggxiaozhi.factory.presenter.BaseContract;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.presenter.contact
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：个人信息界面的契约
 */

public interface PersonalContract {

    interface Presenter extends BaseContract.Presenter {
        //获取用户信息
        User getUserPersonal();
    }

    interface View extends BaseContract.View<Presenter> {
        //获取用户的Id
        String getUserId();

        //加载数据成功
        void onLoadDone(User user);

        //是否发起聊天
        void allowSayHello(boolean isAllow);

        //设置关注人状态
        void isFollowState(boolean isFollow);
    }
}
