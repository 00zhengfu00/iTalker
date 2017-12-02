package com.example.ggxiaozhi.factory.data.message;

import android.text.TextUtils;

import com.example.ggxiaozhi.factory.data.helper.DbHelper;
import com.example.ggxiaozhi.factory.data.helper.GroupHelper;
import com.example.ggxiaozhi.factory.data.helper.MessageHelper;
import com.example.ggxiaozhi.factory.data.helper.UserHelper;
import com.example.ggxiaozhi.factory.data.user.UserDispatcher;
import com.example.ggxiaozhi.factory.model.card.MessageCard;
import com.example.ggxiaozhi.factory.model.db.Group;
import com.example.ggxiaozhi.factory.model.db.Message;
import com.example.ggxiaozhi.factory.model.db.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.data.message
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：消息管理中心实现类 实现具体逻辑
 */

public class MessageDispatcher implements MessageCenter {

    private static MessageCenter instance;

    //创建一个单线程池管理线程 用户的操作
    //只能在这一个线程中操作 只有一个完成后然后线程调度操作下一个
    private Executor mExecutor = Executors.newSingleThreadExecutor();


    private MessageDispatcher() {
    }

    public static MessageCenter instance() {
        if (instance == null) {
            synchronized (UserDispatcher.class) {
                if (instance == null) {
                    instance = new MessageDispatcher();
                }
            }
        }
        return instance;
    }

    @Override
    public void dispatch(MessageCard... cards) {
        if (cards == null || cards.length == 0)
            return;
        //丢到单线程中
        mExecutor.execute(new MessageCardHandle(cards));
    }

    /**
     * 具体的数据库操作与分发在这里操作 当线程执行调度时走这个类的run方法
     */
    private class MessageCardHandle implements Runnable {
        private final MessageCard[] cards;

        MessageCardHandle(MessageCard[] userCards) {
            cards = userCards;
        }

        @Override
        public void run() {
            List<Message> messages = new ArrayList<>();
            for (MessageCard card : cards) {
                if (card == null || TextUtils.isEmpty(card.getSenderId())
                        || TextUtils.isEmpty(card.getId())
                        || (TextUtils.isEmpty(card.getReceiverId())
                        && TextUtils.isEmpty(card.getGroupId())))
                    continue;

                //消息卡片有可能是推送过来的 也有可能是自己造的
                //推送过来的代表服务器一定有这条消息 我们可以查到(本地可能有可能没有)
                //如果是自己发送的 先存储到本地数据库再发送网络
                //发送消息流程：写消息->存储本地->发送网络->网络返回->刷新本地状态
                Message message = MessageHelper.findFromLocal(card.getId());
                if (message != null) {//找到了本地消息
                    // 如果已经完成则不做处理
                    if (message.getStatus() == Message.STATUS_DONE)
                        continue;
                    //消息发送后就不可能在修改了 如果收到的消息
                    //本地数据库已经存在了 同时本地显示状态为已经完成了 则不必处理
                    //因为此时服务器返回的消息与本地是一样的
                    // 新状态为完成才更新服务器时间，不然不做更新
                    if (card.getStatus() == Message.STATUS_DONE) {
                        //代表消息发送成功 此时需要修改时间为服务器时间
                        message.setCreateAt(card.getCreateAt());

                        //如果没有进入判断 则代表这条信息发送失败
                        //重新进行数据库更新而已
                    }
                    // 更新一些会变化的内容
                    message.setContent(card.getContent());
                    message.setAttach(card.getAttach());
                    //更新状态
                    message.setStatus(card.getStatus());
                } else {//没有找到本地消息 初次在数据库存储
                    User sender = UserHelper.searchUser(card.getSenderId());
                    User receiver = null;
                    Group group = null;

                    if (!TextUtils.isEmpty(card.getReceiverId())) {
                        receiver = UserHelper.searchUser(card.getReceiverId());
                    } else if (!TextUtils.isEmpty(card.getGroupId())) {
                        group = GroupHelper.findFromLocal(card.getGroupId());
                    }
                    //接收者总要有一个 可以是人 或是群
                    if (receiver == null && group == null && sender != null)
                        continue;

                    message = card.build(sender, receiver, group);
                }
                messages.add(message);
            }
            if (messages.size() > 0)
                DbHelper.save(Message.class, messages.toArray(new Message[0]));
        }
    }
}
