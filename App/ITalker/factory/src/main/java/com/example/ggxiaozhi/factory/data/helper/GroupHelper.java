package com.example.ggxiaozhi.factory.data.helper;

import com.example.ggxiaozhi.factory.Factory;
import com.example.ggxiaozhi.factory.R;
import com.example.ggxiaozhi.factory.data.DataSource;
import com.example.ggxiaozhi.factory.model.RspModel;
import com.example.ggxiaozhi.factory.model.api.group.GroupCreateModel;
import com.example.ggxiaozhi.factory.model.api.group.GroupMemberAddModel;
import com.example.ggxiaozhi.factory.model.card.GroupCard;
import com.example.ggxiaozhi.factory.model.card.GroupMemberCard;
import com.example.ggxiaozhi.factory.model.card.UserCard;
import com.example.ggxiaozhi.factory.model.db.Group;
import com.example.ggxiaozhi.factory.model.db.GroupMember;
import com.example.ggxiaozhi.factory.model.db.GroupMember_Table;
import com.example.ggxiaozhi.factory.model.db.Group_Table;
import com.example.ggxiaozhi.factory.model.db.User;
import com.example.ggxiaozhi.factory.model.db.User_Table;
import com.example.ggxiaozhi.factory.model.db.view.MemberUserModel;
import com.example.ggxiaozhi.factory.net.Network;
import com.example.ggxiaozhi.factory.net.RemoteService;
import com.example.ggxiaozhi.factory.presenter.group.GroupMemberAddPresenter;
import com.example.ggxiaozhi.factory.presenter.search.SearchGroupPresenter;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.SQLite;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.data.helper
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：群相关的网络请求 数据库存储
 */

public class GroupHelper {
    /**
     * 查询一个群信息 优先从本地 本地不存在网络查找
     *
     * @param groupId 群id
     * @return 返回查找的群信息
     */
    public static Group find(String groupId) {
        Group group = findFromLocal(groupId);
        if (group == null)
            return findFromNet(groupId);
        return group;
    }

    /**
     * 从本地数据库查询群信息
     *
     * @param groupId 群id
     * @return 返回查找的群信息
     */
    public static Group findFromLocal(String groupId) {
        return SQLite.select()
                .from(Group.class)
                .where(Group_Table.id.eq(groupId))
                .querySingle();
    }

    /**
     * 从网络同步拉取一个群的信息
     *
     * @param groupId id
     * @return group
     */
    private static Group findFromNet(String groupId) {
        RemoteService service = Network.remote();
        try {
            //同步执行
            Response<RspModel<GroupCard>> response = service.groupFind(groupId).execute();
            GroupCard groupCard = response.body().getResult();
            if (groupCard != null) {
                //数据库刷新 但是并通知
                User ownerUser = UserHelper.searchUser(groupCard.getOwnerId());
                Group group = groupCard.build(ownerUser);
                //将存储与转化统一交给用户中心去管理分发
                Factory.getGroupCenter().groupDispatch(groupCard);
                return group;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void create(GroupCreateModel model, final DataSource.Callback<GroupCard> callback) {
        RemoteService service = Network.remote();
        Call<RspModel<GroupCard>> rspModelCall = service.groupCreate(model);
        rspModelCall.enqueue(new Callback<RspModel<GroupCard>>() {
            @Override
            public void onResponse(Call<RspModel<GroupCard>> call, Response<RspModel<GroupCard>> response) {
                RspModel<GroupCard> rspModel = response.body();
                if (rspModel.success()) {
                    GroupCard card = rspModel.getResult();
                    if (card == null)
                        return;
                    //返回成功
                    //将存储与转化统一交给用户中心去管理分发
                    Factory.getGroupCenter().groupDispatch(card);
                    callback.onDataLoaded(card);
                } else {
                    // 对返回的RspModel中的失败Code进行解析 ，解析到我们对应string资源中
                    Factory.decodeRspCode(rspModel, null);
                }
            }

            @Override
            public void onFailure(Call<RspModel<GroupCard>> call, Throwable t) {
                //请求失败
                if (callback != null)
                    callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }

    /**
     * 搜索群的操作
     *
     * @param name     传入的搜索条件
     * @param callback 界面显示数据的接口回调
     */
    public static Call search(String name, final DataSource.Callback<List<GroupCard>> callback) {
        //调用Retrofit2对我们的网络请求接口做代理
        RemoteService service = Network.remote();
        //等到一个返回结果的Call
        Call<RspModel<List<GroupCard>>> rspModelCall = service.groupSearch(name);
        //异步请求
        rspModelCall.enqueue(new Callback<RspModel<List<GroupCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<GroupCard>>> call, Response<RspModel<List<GroupCard>>> response) {
                RspModel<List<GroupCard>> rspModel = response.body();
                if (rspModel.success()) {
                    //不用存入数据库 直接返回请求成功数据
                    callback.onDataLoaded(rspModel.getResult());
                } else {
                    // 对返回的RspModel中的失败Code进行解析 ，解析到我们对应string资源中
                    Factory.decodeRspCode(rspModel, callback);
                }

            }

            @Override
            public void onFailure(Call<RspModel<List<GroupCard>>> call, Throwable t) {
                //请求失败
                if (callback != null)
                    callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
        return rspModelCall;
    }

    /**
     * 刷新群的请求
     * @param date 指定一个日期
     */
    public static void refreshGroups(String date) {
        RemoteService service = Network.remote();
        Call<RspModel<List<GroupCard>>> rspModelCall = service.groups(date);
        //异步请求
        rspModelCall.enqueue(new Callback<RspModel<List<GroupCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<GroupCard>>> call, Response<RspModel<List<GroupCard>>> response) {
                RspModel<List<GroupCard>> rspModel = response.body();
                if (rspModel.success()) {
                    List<GroupCard> cards = rspModel.getResult();
                    if (cards == null || cards.size() == 0)
                        return;
                    //返回成功
                    //将存储与转化统一交给用户中心去管理分发
                    Factory.getGroupCenter().groupDispatch(cards.toArray(new GroupCard[0]));
                } else {
                    // 对返回的RspModel中的失败Code进行解析 ，解析到我们对应string资源中
                    Factory.decodeRspCode(rspModel, null);
                }

            }

            @Override
            public void onFailure(Call<RspModel<List<GroupCard>>> call, Throwable t) {
                //不做任何处理
            }
        });
    }

    public static long GroupMembersCount(String id) {
        return SQLite.selectCountOf()
                .from(GroupMember.class)
                .where(GroupMember_Table.group_id.eq(id))
                .count();
    }

    /**
     * 刷新群成员信息
     * @param group 当前所在群
     */
    public static void refreshGroupMembers(Group group) {
        RemoteService service = Network.remote();
        Call<RspModel<List<GroupMemberCard>>> rspModelCall = service.groupMembers(group.getId());
        //异步请求
        rspModelCall.enqueue(new Callback<RspModel<List<GroupMemberCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<GroupMemberCard>>> call, Response<RspModel<List<GroupMemberCard>>> response) {
                RspModel<List<GroupMemberCard>> rspModel = response.body();
                if (rspModel.success()) {
                    List<GroupMemberCard> cards = rspModel.getResult();
                    if (cards == null || cards.size() == 0)
                        return;
                    //返回成功
                    //将存储与转化统一交给用户中心去管理分发
                    Factory.getGroupCenter().groupMemberDispatch(cards.toArray(new GroupMemberCard[0]));
                } else {
                    // 对返回的RspModel中的失败Code进行解析 ，解析到我们对应string资源中
                    Factory.decodeRspCode(rspModel, null);
                }

            }

            @Override
            public void onFailure(Call<RspModel<List<GroupMemberCard>>> call, Throwable t) {
                //不做任何处理
            }
        });
    }

    /**
     * 创建自定义映射表 MemberUserModel
     * 先创建 在返回
     * @param groupId 指定群的Id
     * @param size 返回集合的数据个数
     * @return 集合
     */
    public static List<MemberUserModel> getMemberUsers(String groupId, int size) {
        return SQLite.select(GroupMember_Table.alias.withTable().as("alias")//群成员中的alias数据作为新表的数据 并将新表的字段定义为alias
                , User_Table.name.withTable().as("name")//群成员中的name数据作为新表的数据 并将新表的字段定义为name
                , User_Table.portrait.withTable().as("portrait")//群成员中的portrait数据作为新表的数据 并将新表的字段定义为portrait
                , User_Table.id.withTable().as("userId"))//群成员中的Id数据作为新表的数据 并将新表的字段定义为userId
                .from(GroupMember.class)//从成员表查询
                .join(User.class, Join.JoinType.INNER)//将成员表与User表联合(内连接) 只要两个表的公共字段有匹配值，就将这两个表中的记录组合起来。
                .on(GroupMember_Table.user_id.withTable().eq(User_Table.id.withTable()))//前提条件 需要时匹配同一个人
                .where(GroupMember_Table.group_id.withTable().eq(groupId))//查询条件
                .orderBy(GroupMember_Table.user_id, true)//排序
                .limit(size)//查询个数
                .queryCustomList(MemberUserModel.class);//返回自定义参数集合 将字段映射到新建表中

    }

    /**
     * 网络请求进行成员添加
     * @param groupId
     * @param model
     * @param callback
     */
    public static void addMembers(String groupId, GroupMemberAddModel model, final DataSource.Callback<List<GroupMemberCard>> callback) {
        RemoteService service = Network.remote();
        service.groupMemberAdd(groupId, model)
                .enqueue(new Callback<RspModel<List<GroupMemberCard>>>() {
                    @Override
                    public void onResponse(Call<RspModel<List<GroupMemberCard>>> call, Response<RspModel<List<GroupMemberCard>>> response) {
                        RspModel<List<GroupMemberCard>> rspModel = response.body();
                        if (rspModel.success()) {
                            List<GroupMemberCard> memberCards = rspModel.getResult();
                            if (memberCards != null && memberCards.size() > 0) {
                                // 进行调度显示
                                Factory.getGroupCenter().groupMemberDispatch(memberCards.toArray(new GroupMemberCard[0]));
                                callback.onDataLoaded(memberCards);
                            }
                        } else {
                            Factory.decodeRspCode(rspModel, null);
                        }
                    }

                    @Override
                    public void onFailure(Call<RspModel<List<GroupMemberCard>>> call, Throwable t) {
                        callback.onDataNotAvailable(R.string.data_network_error);
                    }
                });
    }
}
