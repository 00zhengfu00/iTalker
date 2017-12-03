package com.example.ggxiaozhi.factory.data.message;

import android.support.annotation.NonNull;

import com.example.ggxiaozhi.factory.data.BaseDbRepository;
import com.example.ggxiaozhi.factory.data.DataSource;
import com.example.ggxiaozhi.factory.model.db.Session;
import com.example.ggxiaozhi.factory.model.db.Session_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.Collections;
import java.util.List;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.data.message
 * 作者名 ： 志先生_
 * 日期   ： 2017/12
 * 功能   ：
 */

public class SessionRepository extends BaseDbRepository<Session> implements SessionDataSource {

    @Override
    public void load(SucceedCallback<List<Session>> callback) {
        super.load(callback);
        //查询数据库
        SQLite.select()
                .from(Session.class)
                .orderBy(Session_Table.modifyAt, false)//倒序查询
                .limit(100)//查询100条对话的最新消息
                .async()
                .queryListResultCallback(this)
                .execute();
        //数据库查询回来后的数据顺序
        //10点
        //9点
        //8点

        //复写insert方法后 存在的问题 解决办法是复写queryListResultCallback的回调

        //8点
        //9点
        //10点
    }

    @Override
    protected boolean isRequired(Session session) {
        //所有的消息都是需要显示的 所以不需要过滤
        return true;
    }

    @Override
    protected void insert(Session session) {
        //让新的数据加载到头部  因为我们显示的数据要是最新的一条
        mDataList.addFirst(session);
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Session> tResult) {
        //将数据进行反转
        Collections.reverse(tResult);
        super.onListQueryResult(transaction, tResult);
    }
}
