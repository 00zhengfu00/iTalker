package com.example.ggxiaozhi.factory.model.api.group;

import com.example.ggxiaozhi.factory.model.db.User;
import com.raizlabs.android.dbflow.annotation.Column;

import java.util.HashSet;
import java.util.Set;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.model.api.group
 * 作者名 ： 志先生_
 * 日期   ： 2017/12
 * 功能   ：群创建的请求model
 */

public class GroupCreateModel {
    private String name;// 群名称
    private String desc;// 群描述
    private String picture;// 群图片
    private Set<String> users = new HashSet<>();

    public GroupCreateModel(String name, String desc, String picture, Set<String> users) {
        this.name = name;
        this.desc = desc;
        this.picture = picture;
        this.users = users;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Set<String> getUsers() {
        return users;
    }

    public void setUsers(Set<String> users) {
        this.users = users;
    }
}
