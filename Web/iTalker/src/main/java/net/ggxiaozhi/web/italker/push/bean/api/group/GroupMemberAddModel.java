package net.ggxiaozhi.web.italker.push.bean.api.group;

import com.google.gson.annotations.Expose;

import java.util.HashSet;
import java.util.Set;

/**
 * 工程名 ： iTalker
 * 包名   ： net.ggxiaozhi.web.italker.push.bean.api.group
 * 作者名 ： 志先生_
 * 日期   ： 2017/12
 * 功能   ：拉取好友加入群的Model
 */
public class GroupMemberAddModel {
    //群成员
    @Expose
    private Set<String> users = new HashSet<>();

    public Set<String> getUsers() {
        return users;
    }

    public void setUsers(Set<String> users) {
        this.users = users;
    }

    public static boolean check(GroupMemberAddModel model) {
        return !(model.users == null || model.users.size() == 0);
    }
}
