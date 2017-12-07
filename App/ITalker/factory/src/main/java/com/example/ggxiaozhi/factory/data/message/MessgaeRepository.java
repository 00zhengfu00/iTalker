package com.example.ggxiaozhi.factory.data.message;


import android.support.annotation.NonNull;

import com.example.ggxiaozhi.factory.data.BaseDbRepository;
import com.example.ggxiaozhi.factory.model.db.Message;
import com.example.ggxiaozhi.factory.model.db.Message_Table;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.Collections;
import java.util.List;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.data.message
 * 作者名 ： 志先生_
 * 日期   ： 2017/12
 * 功能   ：消息数据源的实现类
 * 跟某人聊天时的聊天列表 关注的内容一定是我发送给这个人 或者是他发送给我的
 */

public class MessgaeRepository extends BaseDbRepository<Message> implements MessageDataSource {

    //聊天对象的Id 可能是人 可能是群
    private String receiverId;

    public MessgaeRepository(String receiverId) {
        super();
        this.receiverId = receiverId;
    }

    @Override
    public void load(SucceedCallback<List<Message>> callback) {
        super.load(callback);

        SQLite.select()
                .from(Message.class)
                .where(OperatorGroup.clause().and(Message_Table.sender_id.eq(receiverId))
                        .and(Message_Table.group_id.isNull()))
                .or(Message_Table.receiver_id.eq(receiverId))
                .orderBy(Message_Table.createAt, false)//降序 最近的时间在上面
                .limit(30)//30条
                .async()//异步执行
                .queryListResultCallback(this)
                .execute();
    }

    /**
     * @param message message
     * @return True 与人聊天
     */
    @Override
    protected boolean isRequired(Message message) {
        //receiverId 如果是发送者id 那么Group==null的情况下 那么一定是发送给"我"的消息
        //如果这个接受者者不为空，那么一定是发送给"某个人"的 那么这个人一定是我或是"某个人"
        //如果"某个人的"的receiverId就是当前id 那么就是我需要关注的信息
        return (receiverId.equalsIgnoreCase(message.getSender().getId()) && message.getGroup() == null)
                || (message.getReceiver() != null && receiverId.equalsIgnoreCase(message.getReceiver().getId()));
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Message> tResult) {
        //反转炒操作
        Collections.reverse(tResult);//最近的数据在下面

        super.onListQueryResult(transaction, tResult);
    }
}
