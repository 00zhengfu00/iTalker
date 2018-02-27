package net.ggxiaozhi.web.italker.push.bean.factory;

import com.google.gson.Gson;
import net.ggxiaozhi.web.italker.push.bean.api.base.PushModel;
import net.ggxiaozhi.web.italker.push.bean.api.message.MessageCreateModel;
import net.ggxiaozhi.web.italker.push.bean.card.MessageCard;
import net.ggxiaozhi.web.italker.push.bean.db.*;
import net.ggxiaozhi.web.italker.push.provider.GsonProvider;
import net.ggxiaozhi.web.italker.push.utils.Hib;
import net.ggxiaozhi.web.italker.push.utils.TextUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 工程名 ： iTalker
 * 包名   ： net.ggxiaozhi.web.italker.push.bean.factory
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：Message相关的业务逻辑处理
 */
public class MessageFactory {

    /**
     * 通过Id查询数据库中的消息
     *
     * @param id 消息id
     * @return Message
     */
    public static Message findById(String id) {
        return Hib.query(session -> session.get(Message.class, id));
    }

    /**
     * 向数据库中添加一条消息
     *
     * @param sender   发送者
     * @param receiver 接受者
     * @param model    客户端传入的model
     * @return 存储到数据库中再取出的最新的Message
     */
    public static Message add(User sender, User receiver, MessageCreateModel model) {
        Message message = new Message(sender, receiver, model);
        return save(message);
    }

    /**
     * 向数据库中添加一条消息
     *
     * @param sender 发送者
     * @param group  接受群
     * @param model  客户端传入的model
     * @return 存储到数据库中再取出的最新的Message
     */
    public static Message add(User sender, Group group, MessageCreateModel model) {
        Message message = new Message(sender, group, model);
        return save(message);
    }

    /**
     * 向数据库中保存一条消息
     *
     * @param message
     * @return
     */
    private static Message save(Message message) {
        return Hib.query(session -> {
            //保存 并刷新
            session.save(message);
            session.flush();
            //接着从数据库中查询出来
            session.refresh(message);
            //返回最新存储的消息
            return message;
        });
    }

    public static List<PushHistory> findHistory(User self) {
        @SuppressWarnings("unchecked")
        List<PushHistory> histories = Hib.query(session -> session.createQuery("from PushHistory where receiverId=:receiverId and entityType=:entityType")
                .setParameter("receiverId", self.getId())
                .setParameter("entityType", PushModel.ENTITY_TYPE_MESSAGE)
                .list());

        if (histories.size() == 0)
            return null;
        else
            return histories;
    }
}
