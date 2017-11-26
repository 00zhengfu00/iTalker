package com.example.ggxiaozhi.factory.net;

import com.example.ggxiaozhi.factory.model.RspModel;
import com.example.ggxiaozhi.factory.model.api.account.RegisterModel;
import com.example.ggxiaozhi.factory.model.api.account.AccountRspModel;
import com.example.ggxiaozhi.factory.model.api.account.LoginModel;
import com.example.ggxiaozhi.factory.model.api.user.UserUpdateModel;
import com.example.ggxiaozhi.factory.model.card.UserCard;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

    /**
     * 用户更新用户信息的接口
     *
     * @param model 用户更新的参数Model
     * @return UserCard (都是根据数据库的返回类型所决定)
     */
    @PUT("user")
    Call<RspModel<UserCard>> userUpdate(@Body UserUpdateModel model);

    /**
     * 搜索用户的接口
     *
     * @param name 输入的搜索的用户名
     * @return 返回模糊匹配符合条件的List<UserCard>
     */
    @GET("user/search/{name}")
    Call<RspModel<List<UserCard>>> userSearch(@Path("name") String name);

    @PUT("user/follow/{userId}")
    Call<RspModel<UserCard>> userFollow(@Path("userId") String userId);

    /**
     * 查询联系人的接口
     *
     * @return 查询到的用户信息集合
     */
    @GET("user/contact")
    Call<RspModel<List<UserCard>>> userContacts();

    /**
     * 查询某人信息的接口
     *
     * @return 查询到的用户信息
     */
    @GET("user/{userId}")
    Call<RspModel<UserCard>> userFind(@Path("userId") String userId);
}
