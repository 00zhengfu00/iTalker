package com.example.ggxiaozhi.factory.presenter.contact;

import com.example.ggxiaozhi.factory.model.card.UserCard;
import com.example.ggxiaozhi.factory.presenter.BaseContract;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.presenter.contract
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：关注人的契约
 */

public interface FollowContract {

    interface Presenter extends BaseContract.Presenter {
        /**
         * 关注某人
         *
         * @param id 要关注人的Id
         */
        void follow(String id);
    }

    interface View extends BaseContract.View<Presenter> {
        void followUserSuccessed(UserCard userCard);
    }
}
