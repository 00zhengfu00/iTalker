package net.ggxiaozhi.web.italker.push.bean.factory;

import net.ggxiaozhi.web.italker.push.bean.db.Group;
import net.ggxiaozhi.web.italker.push.bean.db.GroupMember;
import net.ggxiaozhi.web.italker.push.bean.db.User;

import java.util.Set;

/**
 * 工程名 ： iTalker
 * 包名   ： net.ggxiaozhi.web.italker.push.bean.factory
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：群信息存储与处理的工具类
 */
public class GroupFactory {
    /**
     * 根据id查找一个群
     *
     * @param groupId Id
     * @return group
     */
    public static Group findById(String groupId) {
        //TODO
        return null;
    }

    /**
     * 根据id查找一个群 同时发送者 user 一定是群成员
     * 否者返回一个 null
     * @param user 发送者
     * @param groupId Id
     * @return user不在该群众返回一个null
     */
    public static Group findById(User user,String groupId) {
        //TODO
        return null;
    }
    /**
     * 根据当前的群 查询群中的成员
     *
     * @param group 当前的群
     * @return 群中的成员 (不能重复)
     */
    public static Set<GroupMember> getMembers(Group group) {
        //TODO
        return null;
    }
}
