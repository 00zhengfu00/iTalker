package com.example.ggxiaozhi.factory.data.user;

import android.support.annotation.NonNull;

import com.example.ggxiaozhi.factory.data.BaseDbRepository;
import com.example.ggxiaozhi.factory.data.DataSource;
import com.example.ggxiaozhi.factory.data.helper.DbHelper;
import com.example.ggxiaozhi.factory.model.db.User;
import com.example.ggxiaozhi.factory.model.db.User_Table;
import com.example.ggxiaozhi.factory.presistance.Account;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.data.user
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：联系人仓库
 */

public class ContactRepository extends BaseDbRepository<User> implements ContactDataSource {

    @Override
    public void load(DataSource.SucceedCallback<List<User>> callback) {
        super.load(callback);
        //查询操作 这里是查询本地数据库
        SQLite.select()
                .from(User.class)
                .where(User_Table.isFollow.eq(true))
                .and(User_Table.id.notEq(Account.getUserId()))
                .orderBy(User_Table.name, true)
                .limit(100)
                .async()
                .queryListResultCallback(this)
                .execute();
    }

    /**
     * 判断当期用户是否是我想要的数据
     *
     * @param user User
     * @return True 表示是我们想要的数据
     */
    @Override
    protected boolean isRequired(User user) {
        return user.isFollow() && !user.getId().equals(Account.getUserId());
    }


}
