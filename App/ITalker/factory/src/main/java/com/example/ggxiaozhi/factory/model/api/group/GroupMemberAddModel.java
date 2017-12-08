package com.example.ggxiaozhi.factory.model.api.group;

import java.util.HashSet;
import java.util.Set;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.model.api.group
 * 作者名 ： 志先生_
 * 日期   ： 2017/12
 * 功能   ：添加群成员请求的Model
 */

public class GroupMemberAddModel {

    private Set<String> users = new HashSet<>();

    public GroupMemberAddModel(Set<String> users) {
        this.users = users;
    }

    public Set<String> getUsers() {
        return users;
    }

    public void setUsers(Set<String> users) {
        this.users = users;
    }
}
