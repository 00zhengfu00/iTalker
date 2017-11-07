package net.ggxiaozhi.web.italker.push.service;

import com.google.common.base.Strings;
import net.ggxiaozhi.web.italker.push.bean.api.base.ResponseModel;
import net.ggxiaozhi.web.italker.push.bean.api.user.UpdateInfoModle;
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
 * 功能   ：操作用户的相关接口
 */
//127.0.0.1/api/user/...
@Path("/user")
public class UserService extends BaseService {

    //用户信息修改接口
    //返回自己的个人信息
    @PUT
    //@Path("update")////127.0.0.1/api/user/update 不需要写  就是当前目录
    //指定请求与响应体为json
    @Consumes(MediaType.APPLICATION_JSON)//指定传入的格式
    @Produces(MediaType.APPLICATION_JSON)//指定返回的格式
    public ResponseModel<UserCard> update(
            //不需要token 拦截器已经帮我们做了处理
            // @HeaderParam("token") String token,
            UpdateInfoModle modle) {
        //表示当前登录缺少必要参数
        if (/*Strings.isNullOrEmpty(token) ||*/ !UpdateInfoModle.check(modle)) {
            //参数异常
            return ResponseModel.buildParameterError();
        }

        //拿到自己的个人信息
        User self = getSelf();
        //更新用户信息
        self = modle.updateToUser(self);
        //更新用户信息到数据库
        self = UserFactory.update(self);
        //构建自己的用户信息
        UserCard card = new UserCard(self, true);
        //将修改后的用户信息返回
        return ResponseModel.buildOk(card);

    }
}
