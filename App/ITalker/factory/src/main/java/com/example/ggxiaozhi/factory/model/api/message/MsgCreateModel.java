package com.example.ggxiaozhi.factory.model.api.message;

import com.example.ggxiaozhi.factory.model.card.MessageCard;
import com.example.ggxiaozhi.factory.model.db.Message;
import com.example.ggxiaozhi.factory.presistance.Account;

import java.util.Date;
import java.util.UUID;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.model.api.message
 * 作者名 ： 志先生_
 * 日期   ： 2017/12
 * 功能   ：发送消息请求的Api
 */

public class MsgCreateModel {
    //id由代码写入，由客户端负责生成
    private String id;

    //输入消息的内容，不允许为空
    private String content;

    //附件
    private String attach;

    //消息的类型
    private int type = Message.TYPE_STR;

    //接受者 可为空
    private String receiverId;

    //接收者类型->群/人
    private int receiverType = Message.RECEIVER_TYPE_NONE;

    private MsgCreateModel() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public String getAttach() {
        return attach;
    }

    public int getType() {
        return type;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public int getReceiverType() {
        return receiverType;
    }


    //TODO 当我们需要发送一个文件的时候 content刷新的问题
    //将这个卡片缓存起来
    private MessageCard mCard;

    public MessageCard buildCard() {
        if (mCard == null) {
            MessageCard card = new MessageCard();
            card.setId(id);
            card.setAttach(attach);
            card.setContent(content);
            card.setType(type);
            card.setSenderId(Account.getUserId());
            // 如果是群
            if (receiverType == Message.RECEIVER_TYPE_GROUP) {
                card.setGroupId(receiverId);
            } else {
                card.setReceiverId(receiverId);
            }
            //通过当前Model创建的card 就是初步创建状态
            card.setStatus(Message.STATUS_CREATED);
            card.setCreateAt(new Date());
            this.mCard = card;
        }
        return mCard;
    }

    /**
     * 把一个Message消息转成一个创建状态的CreateModel
     *
     * @param message Message
     * @return CreateModel
     */
    public static MsgCreateModel buildWithMessage(Message message) {
        MsgCreateModel model = new MsgCreateModel();
        model.id = message.getId();
        model.content = message.getContent();
        model.attach = message.getAttach();
        model.type = message.getType();
        if (message.getReceiver() != null) {
            model.receiverId = message.getReceiver().getId();
            model.receiverType = Message.RECEIVER_TYPE_NONE;
        } else {
            model.receiverId = message.getGroup().getId();
            model.receiverType = Message.RECEIVER_TYPE_GROUP;
        }
        return model;
    }

    //刷新创建的发送消息
    public void refreshByCard() {
        if (mCard == null)
            return;
        //刷新内容和附件内容
        this.content = mCard.getContent();
        this.attach = mCard.getAttach();
    }

    /**
     * 创建创建者模式 快速创建一个model
     * 创建者的其中的一个特点就是不向外界提供setter方法 不让外界修改
     */
    public static class Builder {
        private MsgCreateModel model;

        public Builder() {
            this.model = new MsgCreateModel();
        }

        //设置接受者
        public Builder receiver(String receiverId, int receiverType) {
            this.model.receiverId = receiverId;
            this.model.receiverType = receiverType;
            return this;

        }

        //设置内容和消息类型
        public Builder content(String content, int type) {
            this.model.content = content;
            this.model.type = type;
            return this;

        }

        //设置附件
        public Builder attach(String attach) {
            this.model.attach = attach;
            return this;
        }

        //返回创建
        public MsgCreateModel build() {
            return this.model;
        }

    }
}
