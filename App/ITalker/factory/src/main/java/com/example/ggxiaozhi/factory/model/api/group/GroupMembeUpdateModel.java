package com.example.ggxiaozhi.factory.model.api.group;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.model.api.group
 * 作者名 ： 志先生_
 * 日期   ： 2017/12
 * 功能   ：群成员修改信息的Model
 */

public class GroupMembeUpdateModel {
    private String alias;// 别名／备注
    private boolean isAdmin;// 是否是管理员
    private String groupId;// 对应的群Id

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
