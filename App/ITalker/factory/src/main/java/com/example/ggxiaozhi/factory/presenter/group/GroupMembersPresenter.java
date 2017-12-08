package com.example.ggxiaozhi.factory.presenter.group;

import com.example.ggxiaozhi.factory.Factory;
import com.example.ggxiaozhi.factory.data.helper.GroupHelper;
import com.example.ggxiaozhi.factory.model.db.view.MemberUserModel;
import com.example.ggxiaozhi.factory.presenter.BaseRecyclerPresenter;

import java.util.List;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.presenter.group
 * 作者名 ： 志先生_
 * 日期   ： 2017/12
 * 功能   ：
 */

public class GroupMembersPresenter extends BaseRecyclerPresenter<MemberUserModel, GroupMembersContract.View>
        implements GroupMembersContract.Presenter {
    public GroupMembersPresenter(GroupMembersContract.View view) {
        super(view);
    }

    @Override
    public void refresh() {
        //显示loading
        start();
        //异步初始化联系人
        Factory.runOnAsync(loader);

    }

    private Runnable loader = new Runnable() {
        @Override
        public void run() {
            GroupMembersContract.View view = getView();
            if (view == null)
                return;
            //获取简单的群成员信息 传入-1 表示查询所有
            List<MemberUserModel> viewModels = GroupHelper.getMemberUsers(view.getGroupId(), -1);
            //刷新界面并显示
            refreshData(viewModels);
        }
    };
}
