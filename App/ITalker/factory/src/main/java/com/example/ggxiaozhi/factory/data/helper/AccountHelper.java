package com.example.ggxiaozhi.factory.data.helper;

import android.text.TextUtils;

import com.example.ggxiaozhi.factory.Factory;
import com.example.ggxiaozhi.factory.R;
import com.example.ggxiaozhi.factory.data.DataSource;
import com.example.ggxiaozhi.factory.model.RspModel;
import com.example.ggxiaozhi.factory.model.api.account.RegisterModel;
import com.example.ggxiaozhi.factory.model.api.account.AccountRspModel;
import com.example.ggxiaozhi.factory.model.api.account.LoginModel;
import com.example.ggxiaozhi.factory.model.db.User;
import com.example.ggxiaozhi.factory.net.Network;
import com.example.ggxiaozhi.factory.net.RemoteService;
import com.example.ggxiaozhi.factory.presistance.Account;

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
        RemoteService service = Network.remote();
        //等到一个返回结果的Call
        Call<RspModel<AccountRspModel>> rspModelCall = service.accountRegister(model);
        //异步请求
        rspModelCall.enqueue(new AccountRspCallback(callback));

    }

    /**
     * 发起一个登录的请求 异步调用
     *
     * @param model    申请登录的model
     * @param callback 成功与失败的接口回送
     */
    public static void login(LoginModel model, final DataSource.Callback<User> callback) {
        //调用Retrofit2对我们的网络请求接口做代理
        RemoteService service = Network.remote();
        //等到一个返回结果的Call
        Call<RspModel<AccountRspModel>> rspModelCall = service.accountLogin(model);
        //异步请求
        rspModelCall.enqueue(new AccountRspCallback(callback));

    }

    /**
     * 对设备ID的绑定
     *
     * @param callback
     */
    @SuppressWarnings("JavaDoc")
    public static void bindPush(final DataSource.Callback<User> callback) {

        //检查是否为空
        String pushId = Account.getPushId();
        if (TextUtils.isEmpty(pushId)) {
            return;
        }
        //调用Retrofit2对我们的网络请求接口做代理
        RemoteService service = Network.remote();
        Call<RspModel<AccountRspModel>> call = service.accountBindId(pushId);
        call.enqueue(new AccountRspCallback(callback));
    }

    /**
     * 请求回调的封装
     */
    private static class AccountRspCallback implements Callback<RspModel<AccountRspModel>> {
        final DataSource.Callback<User> callback;

        AccountRspCallback(DataSource.Callback<User> callback) {
            this.callback = callback;
        }

        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public void onResponse(Call<RspModel<AccountRspModel>> call, Response<RspModel<AccountRspModel>> response) {
            //请求成功
            //从返回中得到我们的全局model 内部是使用Gson进行解析
            RspModel<AccountRspModel> rspModel = response.body();

            if (rspModel.success()) {//返回成功
                //拿到我们的实体
                AccountRspModel accountRspModel = rspModel.getResult();
                final User user = accountRspModel.getUser();
                //  进行数据库的  写入与缓存绑定
                //第一种 直接保存
                user.save();
                    /* //第二种 通过ModelAdapter
                        FlowManager
                                .getModelAdapter(User.class)
                                .save(user);
                        //第三种 事务中
                        DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
                        definition.beginTransactionAsync(new ITransaction() {
                            @Override
                            public void execute(DatabaseWrapper databaseWrapper) {
                                FlowManager
                                        .getModelAdapter(User.class)
                                        .save(user);
                            }
                    }).build().execute();*/
                //保存我自己的信息到XML持久化中
                Account.login(accountRspModel);
                if (accountRspModel.isBind()) {//绑定状态的判断 是否绑定了设备
                    // 设置绑定状态为True
                    Account.setBind(true);
                    if (callback != null)
                        callback.onDataLoaded(user);
                } else {//没有进行设备绑定 那由我们自己手动绑定
                    bindPush(callback);
                }
            } else {
                // 对返回的RspModel中的失败Code进行解析 ，解析到我们对应string资源中
                Factory.decodeRspCode(rspModel, callback);
            }
        }

        @Override
        public void onFailure(Call<RspModel<AccountRspModel>> call, Throwable t) {
            //请求失败
            if (callback != null)
                callback.onDataNotAvailable(R.string.data_network_error);
        }
    }
}
