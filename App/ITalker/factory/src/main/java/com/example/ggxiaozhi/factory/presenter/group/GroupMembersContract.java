package com.example.ggxiaozhi.factory.presenter.group;

import com.example.ggxiaozhi.factory.model.db.view.MemberUserModel;
import com.example.ggxiaozhi.factory.presenter.BaseContract;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.presenter.group
 * 作者名 ： 志先生_
 * 日期   ： 2017/12
 * 功能   ：群联系人契约
 */

public interface GroupMembersContract {

    interface Presenter extends BaseContract.Presenter {
        //刷新群成员
        void refresh();
    }

    interface View extends BaseContract.RecyclerView<Presenter, MemberUserModel> {
        String getGroupId();
    }
}
