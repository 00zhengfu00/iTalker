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
 * 跟群聊天时的聊天列表 关注的内容一定是我发送给群成员 或者是群成员发送给我的
 */

public class MessgaeGroupRepository extends BaseDbRepository<Message> implements MessageDataSource {

    //聊天对象的Id 可能是人 可能是群
    private String receiverId;

    public MessgaeGroupRepository(String receiverId) {
        super();
        this.receiverId = receiverId;
    }

    @Override
    public void load(SucceedCallback<List<Message>> callback) {
        super.load(callback);
        //无论是直接发送还是给别人发送 只要发到这个群
        //那么这个群group_id就是receiveId
        SQLite.select()
                .from(Message.class)
                .where(Message_Table.group_id.eq(receiverId))
                .orderBy(Message_Table.createAt, false)//降序 最近的时间在上面
                .limit(30)//30条
                .async()//异步执行
                .queryListResultCallback(this)
                .execute();
    }

    /**
     * @param message message
     * @return True 通过
     */
    @Override
    protected boolean isRequired(Message message) {
        //如果消息中的GroupId 不等于null 那么一定是发送给群的
        //如果接受者Id 就是我们消息中的群Id那么就通过拦截
        return message.getGroup() != null && message.getGroup().getId().equalsIgnoreCase(receiverId);
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Message> tResult) {
        //反转炒操作
        Collections.reverse(tResult);//最近的数据在下面
        super.onListQueryResult(transaction, tResult);
    }
}
