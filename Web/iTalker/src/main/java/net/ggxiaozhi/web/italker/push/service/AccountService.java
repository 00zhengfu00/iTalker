package net.ggxiaozhi.web.italker.push.service;

import com.google.common.base.Strings;
import net.ggxiaozhi.web.italker.push.bean.api.account.AccountRspModule;
import net.ggxiaozhi.web.italker.push.bean.api.account.LoginModule;
import net.ggxiaozhi.web.italker.push.bean.api.account.RegisterModule;
import net.ggxiaozhi.web.italker.push.bean.api.base.ResponseModel;
import net.ggxiaozhi.web.italker.push.bean.db.User;
import net.ggxiaozhi.web.italker.push.bean.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * 工程名 ： iTalker
 * 包名   ： net.ggxiaozhi.web.italker.push.service
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：所有账号相关请求的入口
 */
//127.0.0.1/api/account/...
@Path("/account")
public class AccountService extends BaseService {
    //127.0.0.1/api/account/register
    //用户注册的接口
    @POST
    @Path("/register")
    //指定请求与响应体为json
    @Consumes(MediaType.APPLICATION_JSON)//指定传入的格式
    @Produces(MediaType.APPLICATION_JSON)//指定返回的格式
    public ResponseModel<AccountRspModule> register(RegisterModule module) {

        //表示当前注册缺少必要参数
        if (!RegisterModule.check(module)) {
            //参数异常
            return ResponseModel.buildParameterError();
        }

        User user = UserFactory.findByPhone(module.getAccount().trim());

        if (user != null) {
            //已有账户
            return ResponseModel.buildHaveAccountError();
        }

        user = UserFactory.findByName(module.getName().trim());

        if (user != null) {
            //已有名字
            return ResponseModel.buildHaveNameError();
        }

        user = UserFactory.register(module.getAccount(),
                module.getPassword(), module.getName());


        if (user != null) {//注册并登录成功
            //如果注册并登录成功的用户有携带PushId
            if (!Strings.isNullOrEmpty(module.getPushId())) {
                return bindPushId(user, module.getPushId());
            }

            //返回当前的用户
            AccountRspModule accountRspModule = new AccountRspModule(user);
            return ResponseModel.buildOk(accountRspModule);
        } else {//注册失败
            //注册异常
            return ResponseModel.buildRegisterError();
        }
    }


    //127.0.0.1/api/account/login
    //用户登录的接口
    @POST
    @Path("/login")
    //指定请求与响应体为json
    @Consumes(MediaType.APPLICATION_JSON)//指定传入的格式
    @Produces(MediaType.APPLICATION_JSON)//指定返回的格式
    public ResponseModel<AccountRspModule> login(LoginModule module) {

        //表示当前登录缺少必要参数
        if (!LoginModule.check(module)) {
            //参数异常
            return ResponseModel.buildParameterError();
        }

        User user = UserFactory.login(module.getAccount(), module.getPassword());
        if (user != null) {//登录成功

            //如果登录的用户有携带PushId
            if (!Strings.isNullOrEmpty(module.getPushId())) {
                return bindPushId(user, module.getPushId());
            }

            //返回当前的用户
            AccountRspModule accountRspModule = new AccountRspModule(user);
            return ResponseModel.buildOk(accountRspModule);
        } else {//登录失败
            return ResponseModel.buildLoginError();
        }
    }

    //127.0.0.1/api/account/bind/{pushId}
    //用户绑定设备ID的接口
    @POST
    @Path("/bind/{pushId}")
    //指定请求与响应体为json
    @Consumes(MediaType.APPLICATION_JSON)//指定传入的格式
    @Produces(MediaType.APPLICATION_JSON)//指定返回的格式
    //从请求头中获取token字段
    //pushId从url地址中获取
    public ResponseModel<AccountRspModule> bind(
            //不需要token 拦截器已经帮我们做了处理
            // @HeaderParam("token") String token,
            @PathParam("pushId") String pushId) {

        //表示当前登录缺少必要参数
        if (/*Strings.isNullOrEmpty(token) ||*/
                Strings.isNullOrEmpty(pushId)) {
            //参数异常
            return ResponseModel.buildParameterError();
        }
        //拿到自己的个人信息
        User self = getSelf();
        return bindPushId(self, pushId);

    }

    /**
     * 当前登录用户绑定PushId
     *
     * @param self   当前在线的user
     * @param pushId
     * @return 返回绑定了设备Id的user
     */
    public ResponseModel<AccountRspModule> bindPushId(User self, String pushId) {

        //绑定pushId
        User user = UserFactory.bindPushId(self, pushId);
        if (user == null) {
            //绑定失败这是服务器的异常
            return ResponseModel.buildServiceError();
        }
        //返回当前的用户 并且已经绑定了
        AccountRspModule accountRspModule = new AccountRspModule(user, true);
        return ResponseModel.buildOk(accountRspModule);
    }
}