package com.example.ggxiaozhi.factory.net;

import com.example.ggxiaozhi.factory.model.RspModel;
import com.example.ggxiaozhi.factory.model.api.RegisterModel;
import com.example.ggxiaozhi.factory.model.api.account.AccountModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

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
    @POST
    Call<RspModel<AccountModel>> accountRegister(@Body RegisterModel model);
}
