package net.ggxiaozhi.web.italker.push.service;

import com.google.common.base.Strings;
import net.ggxiaozhi.web.italker.push.bean.api.base.PushModel;
import net.ggxiaozhi.web.italker.push.bean.api.base.ResponseModel;
import net.ggxiaozhi.web.italker.push.bean.api.user.UpdateInfoModle;
import net.ggxiaozhi.web.italker.push.bean.card.UserCard;
import net.ggxiaozhi.web.italker.push.bean.db.User;
import net.ggxiaozhi.web.italker.push.bean.db.UserFollow;
import net.ggxiaozhi.web.italker.push.bean.factory.PushFactory;
import net.ggxiaozhi.web.italker.push.bean.factory.UserFactory;
import net.ggxiaozhi.web.italker.push.utils.PushDispatcher;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
    //@Path("update")//127.0.0.1/api/user/update 不需要写  就是当前目录
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

    //拉去联系人列表
    @GET
    @Path("/contact")//127.0.0.1/api/user/update
    //指定请求与响应体为json
    @Consumes(MediaType.APPLICATION_JSON)//指定传入的格式
    @Produces(MediaType.APPLICATION_JSON)//指定返回的格式
    public ResponseModel<List<UserCard>> contact() {

        User self = getSelf();

        //拿到我的联系人
        List<User> users = UserFactory.contacts(self);
        //转换UserCard
        List<UserCard> userCards = users.stream()
                //map操作 相当于转置操作 User->UserCard
                .map(user -> new UserCard(user, true))
                .collect(Collectors.toList());
        //返回
        return ResponseModel.buildOk(userCards);
    }

    //关注某人
    // 简化:关注人的操作  其实是双方同时关注 不需要对方同意
    @PUT//修改类型的请求用PUT
    @Path("/follow/{followId}")//127.0.0.1/api/user/update
    //指定请求与响应体为json
    @Consumes(MediaType.APPLICATION_JSON)//指定传入的格式
    @Produces(MediaType.APPLICATION_JSON)//指定返回的格式
    public ResponseModel<UserCard> follow(@PathParam("followId") String followId) {

        User self = getSelf();
        //不能关注自己
        if (self.getId().equalsIgnoreCase(followId) || Strings.isNullOrEmpty(followId)) {
            //返回参数异常
            return ResponseModel.buildParameterError();
        }

        //找到我要关注的人
        User userFollow = UserFactory.findById(followId);
        if (userFollow == null) {
            //未找到人
            return ResponseModel.buildNotFoundUserError(null);
        }

        //备注默认没有  后面可以扩展
        User follow = UserFactory.follow(self, userFollow, null);
        if (follow == null) {
            //关注失败 返回服务器异常
            return ResponseModel.buildServiceError();
        }

        //通知我关注的人  我关注了他的消息
        //给他发送一个我的信息过去
        PushFactory.pushFollow(userFollow,new UserCard(self));
        //返回关注人的信息
        return ResponseModel.buildOk(new UserCard(userFollow, true));
    }

    //拉取某人的信息
    @GET
    @Path("{id}")//127.0.0.1/api/user/{id}
    //指定请求与响应体为json
    @Consumes(MediaType.APPLICATION_JSON)//指定传入的格式
    @Produces(MediaType.APPLICATION_JSON)//指定返回的格式
    public ResponseModel<UserCard> getUser(@PathParam("id") String id) {

        //校验参数
        if (Strings.isNullOrEmpty(id)) {
            return ResponseModel.buildParameterError();
        }

        User self = getSelf();
        if (self.getId().equalsIgnoreCase(id)) {
            //如果查询的用户是我自己 那么直接返回我自己 不用数据库查询操作
            return ResponseModel.buildOk(new UserCard(self, true));
        }

        User user = UserFactory.findById(id);
        if (user == null) {
            //没有找到匹配的用户
            return ResponseModel.buildNotFoundUserError(null);
        }

        //判断两个人是否已经关注了对方
        boolean isFollow = UserFactory.getUserFollow(self, user) != null;

        return ResponseModel.buildOk(new UserCard(user, isFollow));

    }

    //用户搜索
    //为了简化分页：只返回20条数据
    @GET//搜索人不涉及数据更改 只是查询 所以用GET
    //127.0.0.1/api/user/search/{name}
    @Path("/search/{name:(.*)?}")//名字为任意字符  可以为空
    //指定请求与响应体为json
    @Consumes(MediaType.APPLICATION_JSON)//指定传入的格式
    @Produces(MediaType.APPLICATION_JSON)//指定返回的格式
    public ResponseModel<List<UserCard>> search(@DefaultValue("") @PathParam("name") String name) {
        User self = getSelf();

        //先查询数据 searchUsers查询到的模糊匹配的用户集合
        List<User> searchUsers = UserFactory.search(name);
        //把查询到人封装成UserCard
        //判断这些人是否存在我已经关注的
        //如果有 则返回的关注状态中应该已经设置好了状态

        //拿到当前用户的联系人
        final List<User> contacts = UserFactory.contacts(self);
        List<UserCard> userCards = searchUsers.stream()
                .map(user -> {
                    //判断这个user是否在我的联系人中
                    boolean isFollow = user.getId().equalsIgnoreCase(self.getId())//搜索中的人中是否有自己 因为自己对自己肯定是已经关注了
                            //进行联系人的任意匹配 匹配其中的Id字段
                            || contacts.stream().anyMatch(contactUser -> contactUser.getId().equalsIgnoreCase(user.getId()));
                    return new UserCard(user, isFollow);
                }).collect(Collectors.toList());
        //返回
        return ResponseModel.buildOk(userCards);
    }
}
