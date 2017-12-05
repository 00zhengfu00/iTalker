package net.ggxiaozhi.web.italker.push.bean.api.group;

import com.google.common.base.Strings;
import com.google.gson.annotations.Expose;

/**
 * 工程名 ： iTalker
 * 包名   ： net.ggxiaozhi.web.italker.push.bean.api.group
 * 作者名 ： 志先生_
 * 日期   ： 2017/12
 * 功能   ：修改群成员信息的请求Model(例如管理员修改普通用户的别名)
 */
public class GroupMembeUpdateModel {
    @Expose
    private String alias;// 别名／备注
    @Expose
    private boolean isAdmin;// 是否是管理员
    @Expose
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

    public static boolean check(GroupMembeUpdateModel model) {
        return ((!Strings.isNullOrEmpty(model.alias) || model.isAdmin == true)
                && !Strings.isNullOrEmpty(model.groupId));
    }
}
