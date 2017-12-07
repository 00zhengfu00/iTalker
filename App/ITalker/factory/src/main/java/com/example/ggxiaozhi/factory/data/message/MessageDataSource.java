package com.example.ggxiaozhi.factory.data.message;

import com.example.ggxiaozhi.factory.data.DbDataSource;
import com.example.ggxiaozhi.factory.model.db.Message;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.data.message
 * 作者名 ： 志先生_
 * 日期   ： 2017/12
 * 功能   ：消息数据源的定义 实现类是MessgaeRepository, MessgaeGroupRepository关注的对象是Message
 */

public interface MessageDataSource extends DbDataSource<Message> {
}
