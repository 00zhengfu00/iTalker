package com.example.ggxiaozhi.factory.data.group;


import android.text.TextUtils;

import com.example.ggxiaozhi.factory.data.BaseDbRepository;
import com.example.ggxiaozhi.factory.data.helper.GroupHelper;
import com.example.ggxiaozhi.factory.data.user.ContactDataSource;
import com.example.ggxiaozhi.factory.model.db.Group;
import com.example.ggxiaozhi.factory.model.db.Group_Table;
import com.example.ggxiaozhi.factory.model.db.User;
import com.example.ggxiaozhi.factory.model.db.User_Table;
import com.example.ggxiaozhi.factory.model.db.view.MemberUserModel;
import com.example.ggxiaozhi.factory.presistance.Account;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.data.user
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：群列表仓库
 */

public class GroupRepository extends BaseDbRepository<Group> implements GroupDataSource {

    @Override
    public void load(SucceedCallback<List<Group>> callback) {
        super.load(callback);
        //查询操作 这里是查询本地数据库
        SQLite.select()
                .from(Group.class)
                .orderBy(Group_Table.name, true)//根据名字排序 正序
                .limit(100)
                .async()
                .queryListResultCallback(this)
                .execute();
    }

    /**
     * 判断当期群是否是我想要的数据
     *
     * @param group User
     * @return True 表示是我们想要的数据
     */
    @Override
    protected boolean isRequired(Group group) {
        //这里我们不进行过滤 直接获取所有
        //因为每条数据都要走此方法  同时我们需要给group.holder赋值 那么就可以在这里赋值

        //一个群的信息 只有可能2中情况出现在数据库
        //一种是你被拉入进群 另一种是你创建一个群
        //无论哪种情况 你拿到的都是群的信息 并没有等到群成员信息
        //所以你需要在刷新群信息的同时进行群信息初始化
        if (group.getGroupMembersCount() > 0) {
            //构建
            group.holder = buildGroupHolder(group);
        } else {
            //待初始化的群信息
            group.holder = null;
            GroupHelper.refreshGroupMembers(group);
        }
        return true;
    }

    /**
     * 初始化界面显示成员信息
     *
     * @param group 当期群
     * @return 返回组装的字符串
     */
    private String buildGroupHolder(Group group) {
        //懒加载无法直接获取用户信息 只有用户Id 那么创建一个简单model 用来获取需要的用户信息 用来显示界面
        List<MemberUserModel> latelyGroupMembers = group.getGroupLatelyGroupMembers();
        StringBuilder builder = new StringBuilder();
        for (MemberUserModel model : latelyGroupMembers) {
            builder.append(TextUtils.isEmpty(model.getAlias()) ? model.getName() : model.getAlias());
            builder.append(", ");
        }
        builder.delete(builder.lastIndexOf(", "), builder.length());
        return builder.toString();
    }


}
