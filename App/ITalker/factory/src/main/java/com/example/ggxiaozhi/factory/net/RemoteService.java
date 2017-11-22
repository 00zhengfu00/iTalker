package com.example.ggxiaozhi.factory.net;

import com.example.ggxiaozhi.factory.model.RspModel;
import com.example.ggxiaozhi.factory.model.api.RegisterModel;
import com.example.ggxiaozhi.factory.model.api.account.AccountRspModel;
import com.example.ggxiaozhi.factory.model.api.account.LoginModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.net
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：Retrofit2所有Service
 */

public interface RemoteService {

    /**
     * 注册请求的接口
     *
     * @param model 用户的注册信息
     * @return 返回注册RspModel<AccountModel>
     */
    @POST("account/register")
    Call<RspModel<AccountRspModel>> accountRegister(@Body RegisterModel model);

    /**
     * 登录请求的接口
     *
     * @param model 用户的登录请求信息
     * @return 返回注册RspModel<AccountModel>
     */
    @POST("account/login")
    Call<RspModel<AccountRspModel>> accountLogin(@Body LoginModel model);

    /**
     * 绑定设备Id的接口
     *
     * @param pushId 设备Id
     * @return 返回注册RspModel<AccountModel>
     */
    @POST("account/bind/{pushId}")
    Call<RspModel<AccountRspModel>> accountBindId(@Path(encoded = true, value = "pushId") String pushId);
}
