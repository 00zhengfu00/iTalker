package com.example.ggxiaozhi.factory.data.message;

import com.example.ggxiaozhi.factory.model.card.MessageCard;
import com.example.ggxiaozhi.factory.model.card.UserCard;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.data.message
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：消息管理中心 用户消息的数据库操作和消息的消费
 */

public interface MessageCenter {

    void dispatch(MessageCard... cards);
}
