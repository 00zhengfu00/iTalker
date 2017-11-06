package net.ggxiaozhi.web.italker.push.service;

import net.ggxiaozhi.web.italker.push.bean.api.RegisterModule;
import net.ggxiaozhi.web.italker.push.bean.card.UserCard;
import net.ggxiaozhi.web.italker.push.bean.db.User;
import net.ggxiaozhi.web.italker.push.bean.factory.UserFactory;
import net.ggxiaozhi.web.italker.push.utils.TextUtil;

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
    public UserCard register(RegisterModule module) {

        User user = UserFactory.findByPhone(module.getAccount().trim());

        if (user != null) {
            UserCard card = new UserCard();
            card.setName("已有了Phone");
            return card;
        }

        user = UserFactory.findByName(module.getName().trim());

        if (user != null) {
            UserCard card = new UserCard();
            card.setName("已有了Name");
            return card;
        }

        user = UserFactory.register(module.getAccount(),
                module.getPassword(), module.getName());


        if (user != null) {
            UserCard card = new UserCard();
            card.setName(user.getName());
            card.setPhone(user.getPhone());
            card.setSex(user.getSex());
            card.setIsFollow(true);
            card.setModifyAt(user.getUpdateAt());
            return card;
        }
        return null;
       /* User user = new User();
        user.setName(module.getName());
        user.setSex(2);
        return user;*/
    }

}
