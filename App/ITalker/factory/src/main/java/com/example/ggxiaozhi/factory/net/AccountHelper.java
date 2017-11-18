package com.example.ggxiaozhi.factory.net;

import com.example.ggxiaozhi.factory.R;
import com.example.ggxiaozhi.factory.data.DataSource;
import com.example.ggxiaozhi.factory.model.RspModel;
import com.example.ggxiaozhi.factory.model.api.RegisterModel;
import com.example.ggxiaozhi.factory.model.api.account.AccountModel;
import com.example.ggxiaozhi.factory.model.db.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.net
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：账户相关的网络请求
 */

public class AccountHelper {

    /**
     * 发起一个注册的请求 异步调用
     *
     * @param model    申请注册的model
     * @param callback 成功与失败的接口回送
     */
    public static void register(RegisterModel model, final DataSource.Callback<User> callback) {
        //调用Retrofit2对我们的网络请求接口做代理
        RemoteService service = Network.getRetrofit().create(RemoteService.class);
        //等到一个返回结果的Call
        Call<RspModel<AccountModel>> rspModelCall = service.accountRegister(model);
        //异步请求
        rspModelCall.enqueue(new Callback<RspModel<AccountModel>>() {
            @Override
            public void onResponse(Call<RspModel<AccountModel>> call, Response<RspModel<AccountModel>> response) {
                //请求成功
                //从返回中得到我们的全局model 内部是使用Gson进行解析
                RspModel<AccountModel> rspModel = response.body();
                if (rspModel.success()) {//返回成功
                    //拿到我们的实体
                    AccountModel accountModel = rspModel.getResult();
                    if (accountModel.isBind()) {//绑定状态的判断 是否绑定了设备
                        User user = accountModel.getUser();
                        //  进行数据库的  写入与缓存绑定
                        callback.onDataLoaded(user);
                    } else {//没有进行设备绑定 那由我们自己手动绑定

                    }

                } else {
                    // TODO 对返回的RspModel中的失败Code进行解析 ，解析到我们对应string资源中
                    //callback.onDataAvailable();
                }
            }

            @Override
            public void onFailure(Call<RspModel<AccountModel>> call, Throwable t) {
                //请求失败
                callback.onDataAvailable(R.string.data_network_error);
            }
        });

    }

    /**
     * 对设备ID的绑定
     *
     * @param callback
     */
    public static void bindPush(final DataSource.Callback<User> callback) {

    }
}
