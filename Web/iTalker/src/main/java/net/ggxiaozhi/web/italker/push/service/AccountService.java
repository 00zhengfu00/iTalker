package net.ggxiaozhi.web.italker.push.service;

import net.ggxiaozhi.web.italker.push.bean.api.RegisterModule;
import net.ggxiaozhi.web.italker.push.bean.db.User;

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
    public RegisterModule register(RegisterModule module) {
        return module;
       /* User user = new User();
        user.setName(module.getName());
        user.setSex(2);
        return user;*/
    }
}
