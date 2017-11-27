package com.example.ggxiaozhi.factory.data.user;

import android.text.TextUtils;

import com.example.ggxiaozhi.factory.data.helper.DbHelper;
import com.example.ggxiaozhi.factory.model.card.UserCard;
import com.example.ggxiaozhi.factory.model.db.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.data.user
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ： 用户管理中心唯一的实现类 实现具体的逻辑
 */

public class UserDispatcher implements UserCenter {
    private static UserCenter instance;

    //创建一个单线程池管理线程 用户的操作
    //只能在这一个线程中操作 只有一个完成后然后线程调度操作下一个
    private Executor mExecutor = Executors.newSingleThreadExecutor();


    private UserDispatcher() {
    }

    public static UserCenter instance() {
        if (instance == null) {
            synchronized (UserDispatcher.class) {
                if (instance == null) {
                    instance = new UserDispatcher();
                }
            }
        }
        return instance;
    }

    @Override
    public void dispatch(UserCard... cards) {
        if (cards == null || cards.length == 0)
            return;
        //丢到单线程中
        mExecutor.execute(new UserCardHandle(cards));
    }

    /**
     * 具体的数据库操作与分发在这里操作 当线程执行调度时走这个类的run方法
     */
    private class UserCardHandle implements Runnable {
        private final UserCard[] mUserCards;

        UserCardHandle(UserCard[] userCards) {
            mUserCards = userCards;
        }

        @Override
        public void run() {

            //单线程调度时触发
            List<User> users = new ArrayList<>();
            for (UserCard card : mUserCards) {
                //过滤
                if (card == null || TextUtils.isEmpty(card.getId()))
                    continue;
                //添加
                users.add(card.build());
            }
            //进行数据库存储 并分发通知 异步操作
            DbHelper.save(User.class, users.toArray(new User[0]));
        }
    }
}
