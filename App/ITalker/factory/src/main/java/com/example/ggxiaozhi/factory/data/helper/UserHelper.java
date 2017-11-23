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

    //更新用户的操作  异步的
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
}
