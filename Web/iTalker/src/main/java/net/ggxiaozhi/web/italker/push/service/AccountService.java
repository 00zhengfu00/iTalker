package net.ggxiaozhi.web.italker.push.service;

import net.ggxiaozhi.web.italker.push.bean.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * 工程名 ： iTalker
 * 包名   ： net.ggxiaozhi.web.italker.push.service
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：测试(使用restful框架)
 */
//127.0.0.1/api/account/...
@Path("/account")
public class AccountService {

    //127.0.0.1/api/account/login
    @GET
    @Path("/login")
    public String get() {
        return "You get the login.";
    }

    //127.0.0.1/api/account/login
    @POST
    @Path("/login")
    //指定请求与响应体为json
    @Consumes(MediaType.APPLICATION_JSON)//指定传入的格式
    @Produces(MediaType.APPLICATION_JSON)//指定返回的格式
    public User post() {
        User user = new User();
        user.setName("美女");
        user.setSex("2");
        return user;
    }
}
