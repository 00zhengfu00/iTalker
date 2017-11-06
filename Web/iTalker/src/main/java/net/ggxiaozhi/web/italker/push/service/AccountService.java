package net.ggxiaozhi.web.italker.push.service;

import net.ggxiaozhi.web.italker.push.bean.api.account.AccountRspModule;
import net.ggxiaozhi.web.italker.push.bean.api.account.RegisterModule;
import net.ggxiaozhi.web.italker.push.bean.api.base.ResponseModel;
import net.ggxiaozhi.web.italker.push.bean.card.UserCard;
import net.ggxiaozhi.web.italker.push.bean.db.User;
import net.ggxiaozhi.web.italker.push.bean.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * 工程名 ： iTalker
 * 包名   ： net.ggxiaozhi.web.italker.push.service
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：所有请求的入口
 */
//127.0.0.1/api/account/...
@Path("/account")
public class AccountService {
    //127.0.0.1/api/account/register
    @POST
    @Path("/register")
    //指定请求与响应体为json
    @Consumes(MediaType.APPLICATION_JSON)//指定传入的格式
    @Produces(MediaType.APPLICATION_JSON)//指定返回的格式
    public ResponseModel<AccountRspModule> register(RegisterModule module) {

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


        if (user != null) {//注册成功
            //返回当前的用户
            AccountRspModule accountRspModule = new AccountRspModule(user);
            return ResponseModel.buildOk(accountRspModule);
        } else {//注册失败
            //注册异常
            return ResponseModel.buildRegisterError();
        }
    }
}
