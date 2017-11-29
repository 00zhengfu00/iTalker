package com.example.ggxiaozhi.factory.data.group;

import com.example.ggxiaozhi.factory.data.helper.DbHelper;
import com.example.ggxiaozhi.factory.data.helper.GroupHelper;
import com.example.ggxiaozhi.factory.data.helper.UserHelper;
import com.example.ggxiaozhi.factory.data.message.MessageCenter;
import com.example.ggxiaozhi.factory.data.message.MessageDispatcher;
import com.example.ggxiaozhi.factory.data.user.UserDispatcher;
import com.example.ggxiaozhi.factory.model.card.GroupCard;
import com.example.ggxiaozhi.factory.model.card.GroupMemberCard;
import com.example.ggxiaozhi.factory.model.db.Group;
import com.example.ggxiaozhi.factory.model.db.GroupMember;
import com.example.ggxiaozhi.factory.model.db.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.data.group
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：群／群成员卡片中心的实现类
 */

public class GroupDispatcher implements GroupCenter {
    private static GroupCenter instance;

    //创建一个单线程池管理线程 用户的操作
    //只能在这一个线程中操作 只有一个完成后然后线程调度操作下一个
    private Executor mExecutor = Executors.newSingleThreadExecutor();


    private GroupDispatcher() {
    }

    public static GroupCenter instance() {
        if (instance == null) {
            synchronized (UserDispatcher.class) {
                if (instance == null) {
                    instance = new GroupDispatcher();
                }
            }
        }
        return instance;
    }

    @Override
    public void groupDispatch(GroupCard... cards) {
        if (cards == null || cards.length == 0)
            return;
        //丢到单线程中
        mExecutor.execute(new GroupHandle(cards));
    }

    @Override
    public void groupMemberDispatch(GroupMemberCard... cards) {
        if (cards == null || cards.length == 0)
            return;
        //丢到单线程中
        mExecutor.execute(new GroupMemberHandle(cards));
    }

    class GroupHandle implements Runnable {

        private GroupCard[] mGroupCards;

        GroupHandle(GroupCard[] groupCards) {
            mGroupCards = groupCards;
        }

        @Override
        public void run() {

            List<Group> groups = new ArrayList<>();
            for (GroupCard card : mGroupCards) {
                User owner = UserHelper.searchUser(card.getOwnerId());
                if (owner != null) {
                    Group group = card.build(owner);
                    groups.add(group);
                }
            }
            if (groups.size() > 0)
                DbHelper.save(Group.class, groups.toArray(new Group[0]));
        }
    }

    class GroupMemberHandle implements Runnable {

        private GroupMemberCard[] mMemberCards;

        GroupMemberHandle(GroupMemberCard[] groupCards) {
            mMemberCards = groupCards;
        }

        @Override
        public void run() {

            List<GroupMember> groupMembers = new ArrayList<>();
            for (GroupMemberCard card : mMemberCards) {
                User user = UserHelper.searchUser(card.getUserId());
                Group group = GroupHelper.find(card.getGroupId());
                if (user!=null&&group!=null){
                    GroupMember groupMember = card.build(group, user);
                    groupMembers.add(groupMember);
                }
            }
            if (groupMembers.size() > 0)
                DbHelper.save(GroupMember.class, groupMembers.toArray(new GroupMember[0]));
        }
    }
}
