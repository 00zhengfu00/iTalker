package com.example.ggxiaozhi.factory.presenter.search;

import com.example.ggxiaozhi.factory.model.card.GroupCard;
import com.example.ggxiaozhi.factory.model.card.UserCard;
import com.example.ggxiaozhi.factory.presenter.BaseContract;

import java.util.List;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.presenter.search
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：搜索Fragment的契约
 */

public interface SearchContract {

    interface SearchPresenter extends BaseContract.Presenter {
        /**
         * 实际进行搜索的方法
         *
         * @param content 搜索的内容
         */
        void search(String content);
    }

    interface SearchUserView extends BaseContract.View<SearchPresenter> {
        /**
         * 搜索人请求成功的返回
         *
         * @param userCards 用户的信息
         */
        void searchUserDone(List<UserCard> userCards);
    }

    interface SearchGroupView extends BaseContract.View<SearchPresenter> {
        /**
         * 搜索群请求成功的返回
         *
         * @param userGroupCards 群的信息
         */
        void searchGroupDone(List<GroupCard> userGroupCards);
    }
}
