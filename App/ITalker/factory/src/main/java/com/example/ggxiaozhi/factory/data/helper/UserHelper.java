package com.example.ggxiaozhi.factory.data.helper;

import com.example.ggxiaozhi.factory.Factory;
import com.example.ggxiaozhi.factory.R;
import com.example.ggxiaozhi.factory.data.DataSource;
import com.example.ggxiaozhi.factory.model.RspModel;
import com.example.ggxiaozhi.factory.model.api.account.AccountRspModel;
import com.example.ggxiaozhi.factory.model.api.user.UserUpdateModel;
import com.example.ggxiaozhi.factory.model.card.UserCard;
import com.example.ggxiaozhi.factory.model.db.User;
import com.example.ggxiaozhi.factory.net.Network;
import com.example.ggxiaozhi.factory.net.RemoteService;

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
}
