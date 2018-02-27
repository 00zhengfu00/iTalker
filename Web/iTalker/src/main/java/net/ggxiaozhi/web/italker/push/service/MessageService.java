package net.ggxiaozhi.web.italker.push.service;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import net.ggxiaozhi.web.italker.push.bean.api.base.ResponseModel;
import net.ggxiaozhi.web.italker.push.bean.api.message.MessageCreateModel;
import net.ggxiaozhi.web.italker.push.bean.card.MessageCard;
import net.ggxiaozhi.web.italker.push.bean.card.UserCard;
import net.ggxiaozhi.web.italker.push.bean.db.Group;
import net.ggxiaozhi.web.italker.push.bean.db.Message;
import net.ggxiaozhi.web.italker.push.bean.db.PushHistory;
import net.ggxiaozhi.web.italker.push.bean.db.User;
import net.ggxiaozhi.web.italker.push.bean.factory.GroupFactory;
import net.ggxiaozhi.web.italker.push.bean.factory.MessageFactory;
import net.ggxiaozhi.web.italker.push.bean.factory.PushFactory;
import net.ggxiaozhi.web.italker.push.bean.factory.UserFactory;
import net.ggxiaozhi.web.italker.push.provider.GsonProvider;
import net.ggxiaozhi.web.italker.push.utils.TextUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 工程名 ： iTalker
 * 包名   ： net.ggxiaozhi.web.italker.push.service
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：消息发送相关的接口
 */
@Path("/msg")
public class MessageService extends BaseService {

    //用户发送一条消息
    @POST
    //指定请求与响应体为json
    @Consumes(MediaType.APPLICATION_JSON)//指定传入的格式
    @Produces(MediaType.APPLICATION_JSON)//指定返回的格式
    public ResponseModel<MessageCard> pushMessage(MessageCreateModel model) {

        //参数不符合校验 直接返回参数异常
        if (!MessageCreateModel.check(model))
            return ResponseModel.buildParameterError();

        //新传入的消息在数据库中查询
        Message message = MessageFactory.findById(model.getId());
        //如果找到了则已经在数据库中存储了 那么就表示已经发送完成了
        if (message != null)
            return ResponseModel.buildOk(new MessageCard(message));

        /**
         * 下面开始是这条消息第一次发送消息请求的处理逻辑
         */
        User self = getSelf();
        //返回消息的处理
        if (model.getReceiverType() == Message.RECEIVER_TYPR_NONE) {//发送给人
            return pushToUser(self, model);
        } else {//发送给群
            return pushToGroup(self, model);
        }
    }

    //获取历史消息
    @POST
    @Path("/history")
    //指定请求与响应体为json
    @Consumes(MediaType.APPLICATION_JSON)//指定传入的格式
    @Produces(MediaType.APPLICATION_JSON)//指定返回的格式
    public ResponseModel<List<MessageCard>> pushHistoryMessage() {
        User self = getSelf();
        String pushId = self.getPushId();
        if (Strings.isNullOrEmpty(pushId))
            return ResponseModel.buildNotFoundUserError("No found pushId");
        List<PushHistory> pullMsg = MessageFactory.findHistory(self);
        if (pullMsg == null)
            return ResponseModel.buildNotFoundUserError("No found miss Messages");
        //发起推送
        PushFactory.pushHistoryMsg(pullMsg, self);
        //返回全部数据
        List<MessageCard> messageCards = new ArrayList<>();
        for (PushHistory history : pullMsg) {
            MessageCard messageCard = GsonProvider.getGson().fromJson(history.getEntity(), MessageCard.class);
            messageCards.add(messageCard);
        }
        return ResponseModel.buildOk(messageCards);
    }


    /**
     * 发送消息给人的业务逻辑处理
     *
     * @param sender 发送者
     * @param model  发送消息的model
     * @return 返回服务器处理是否成功的信息card
     */
    private ResponseModel<MessageCard> pushToUser(User sender, MessageCreateModel model) {
        //找到消息接受者
        User receiver = UserFactory.findById(model.getReceiverId());
        if (receiver == null)
            //返回没有找到接受者
            return ResponseModel.buildNotFoundUserError("Can't find receiver user");
        //接受者不能是自己
        if (receiver.getId().equalsIgnoreCase(sender.getId()))
            return ResponseModel.buildCreateError(ResponseModel.ERROR_CREATE_MESSAGE);

        //向数据库中保存并得到最新保存的消息
        Message message = MessageFactory.add(sender, receiver, model);
        //推送并构建一个返回消息
        return buildAndPushResponse(sender, message);
    }

    /**
     * 发送消息给群的业务逻辑处理
     *
     * @param sender 发送者
     * @param model  发送消息的model
     * @return 返回服务器处理是否成功的信息card
     */
    private ResponseModel<MessageCard> pushToGroup(User sender, MessageCreateModel model) {
        //找群是有权限限制性的
        Group group = GroupFactory.findById(sender, model.getReceiverId());
        if (group == null)
            return ResponseModel.buildNotFoundGroupError("Can't find receiver group");
        //将message存在数据库中
        Message message = MessageFactory.add(sender, group, model);
        //走通用的推送逻辑
        return buildAndPushResponse(sender, message);
    }

    /**
     * 推送并构建一个返回消息
     *
     * @param sender  发送者
     * @param message 发送消息
     * @return MessageCard
     */
    private ResponseModel<MessageCard> buildAndPushResponse(User sender, Message message) {
        if (message == null)
            //数据库保存失败
            return ResponseModel.buildCreateError(ResponseModel.ERROR_CREATE_MESSAGE);

        //进行一条新的消息推送
        PushFactory.pushNewMessage(sender, message);
        return ResponseModel.buildOk(new MessageCard(message));
    }


}
