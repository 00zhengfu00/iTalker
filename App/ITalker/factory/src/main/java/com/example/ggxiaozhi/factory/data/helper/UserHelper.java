package com.example.ggxiaozhi.factory.data.helper;

import com.example.ggxiaozhi.factory.Factory;
import com.example.ggxiaozhi.factory.R;
import com.example.ggxiaozhi.factory.data.DataSource;
import com.example.ggxiaozhi.factory.model.RspModel;
import com.example.ggxiaozhi.factory.model.api.account.AccountRspModel;
import com.example.ggxiaozhi.factory.model.api.user.UserUpdateModel;
import com.example.ggxiaozhi.factory.model.card.UserCard;
import com.example.ggxiaozhi.factory.model.db.User;
import com.example.ggxiaozhi.factory.model.db.User_Table;
import com.example.ggxiaozhi.factory.net.Network;
import com.example.ggxiaozhi.factory.net.RemoteService;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.data.helper
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：用户相关的网络请求
 */

public class UserHelper {

    /**
     * 更新用户的操作  异步的
     *
     * @param model    包含用户修改信息的请求model
     * @param callback 回调监听
     */
    public static void update(UserUpdateModel model, final DataSource.Callback<UserCard> callback) {
        //调用Retrofit2对我们的网络请求接口做代理
        RemoteService service = Network.remote();
        //等到一个返回结果的Call
        Call<RspModel<UserCard>> rspModelCall = service.userUpdate(model);
        //异步请求
        rspModelCall.enqueue(new Callback<RspModel<UserCard>>() {
            @Override
            public void onResponse(Call<RspModel<UserCard>> call, Response<RspModel<UserCard>> response) {
                RspModel<UserCard> rspModel = response.body();
                if (rspModel.success()) {
                    UserCard userCard = rspModel.getResult();
                    //数据路的存储 需要把UserCard转化成User
                    User user = userCard.build();
                    //保存数据库
                    user.save();
                    //返回成功
                    callback.onDataLoaded(userCard);
                } else {
                    // 对返回的RspModel中的失败Code进行解析 ，解析到我们对应string资源中
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<UserCard>> call, Throwable t) {
                //请求失败
                if (callback != null)
                    callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }

    /**
     * 搜索用户的操作
     *
     * @param name     传入的搜索条件
     * @param callback
     */
    public static Call search(String name, final DataSource.Callback<List<UserCard>> callback) {
        //调用Retrofit2对我们的网络请求接口做代理
        RemoteService service = Network.remote();
        //等到一个返回结果的Call
        Call<RspModel<List<UserCard>>> rspModelCall = service.userSearch(name);
        //异步请求
        rspModelCall.enqueue(new Callback<RspModel<List<UserCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<UserCard>>> call, Response<RspModel<List<UserCard>>> response) {
                RspModel<List<UserCard>> rspModel = response.body();
                if (rspModel.success()) {
                    //不用存入数据库 直接返回请求成功数据
                    callback.onDataLoaded(rspModel.getResult());
                } else {
                    // 对返回的RspModel中的失败Code进行解析 ，解析到我们对应string资源中
                    Factory.decodeRspCode(rspModel, callback);
                }

            }

            @Override
            public void onFailure(Call<RspModel<List<UserCard>>> call, Throwable t) {
                //请求失败
                if (callback != null)
                    callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
        return rspModelCall;
    }

    /**
     * 关注某人的操作
     *
     * @param id       关注人的Id
     * @param callback
     */
    public static void follow(String id, final DataSource.Callback<UserCard> callback) {
        //调用Retrofit2对我们的网络请求接口做代理
        RemoteService service = Network.remote();
        //等到一个返回结果的Call
        Call<RspModel<UserCard>> rspModelCall = service.userFollow(id);
        //异步请求
        rspModelCall.enqueue(new Callback<RspModel<UserCard>>() {
            @Override
            public void onResponse(Call<RspModel<UserCard>> call, Response<RspModel<UserCard>> response) {
                RspModel<UserCard> rspModel = response.body();
                if (rspModel.success()) {
                    //将添加的联系人存入数据库
                    UserCard userCard = rspModel.getResult();
                    User user = userCard.build();
                    user.save();
                    // TODO: 同时联系人列表刷新界面
                    callback.onDataLoaded(rspModel.getResult());
                } else {
                    // 对返回的RspModel中的失败Code进行解析 ，解析到我们对应string资源中
                    Factory.decodeRspCode(rspModel, callback);
                }

            }

            @Override
            public void onFailure(Call<RspModel<UserCard>> call, Throwable t) {
                //请求失败
                if (callback != null)
                    callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }

    public static void refreshContacts(final DataSource.Callback<List<UserCard>> callback) {
        //调用Retrofit2对我们的网络请求接口做代理
        RemoteService service = Network.remote();
        //等到一个返回结果的Call
        Call<RspModel<List<UserCard>>> rspModelCall = service.userContacts();
        //异步请求
        rspModelCall.enqueue(new Callback<RspModel<List<UserCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<UserCard>>> call, Response<RspModel<List<UserCard>>> response) {
                RspModel<List<UserCard>> rspModel = response.body();
                if (rspModel.success()) {
                    //返回成功
                    callback.onDataLoaded(rspModel.getResult());
                } else {
                    // 对返回的RspModel中的失败Code进行解析 ，解析到我们对应string资源中
                    Factory.decodeRspCode(rspModel, callback);
                }

            }

            @Override
            public void onFailure(Call<RspModel<List<UserCard>>> call, Throwable t) {
                //请求失败
                if (callback != null)
                    callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }


    /**
     * 从本地数据库查询用户信息
     *
     * @param id
     * @return
     */
    public static User findFromLocal(String id) {

        return SQLite.select()
                .from(User.class)
                .where(User_Table.id.eq(id))
                .querySingle();
    }

    /**
     * 从网络同步查询用户信息
     *
     * @param id
     * @return
     */

    public static User findFromNet(String id) {
        RemoteService service = Network.remote();
        try {
            //同步执行
            Response<RspModel<UserCard>> response = service.userFind(id).execute();
            UserCard userCard = response.body().getResult();
            if (userCard != null) {
                //TODO 数据库刷新 但是还没有通知
                User user = userCard.build();
                user.save();
                return user;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 优先从本地查询用户
     * @param id
     * @return
     */
    public static User searchUser(String id) {
        User user = findFromLocal(id);
        if (user == null) {
            return findFromNet(id);
        }
        return user;
    }
    /**
     * 优先从网络查询用户
     * @param id
     * @return
     */
    public static User searchUserFirstOfNet(String id) {
        User user = findFromNet(id);
        if (user == null) {
            return findFromLocal(id);
        }
        return user;
    }
}
