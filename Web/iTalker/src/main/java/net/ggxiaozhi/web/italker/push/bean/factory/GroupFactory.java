package net.ggxiaozhi.web.italker.push.bean.factory;

import com.google.common.base.Strings;
import net.ggxiaozhi.web.italker.push.bean.api.group.GroupApplyModel;
import net.ggxiaozhi.web.italker.push.bean.api.group.GroupCreateModel;
import net.ggxiaozhi.web.italker.push.bean.api.group.GroupMembeUpdateModel;
import net.ggxiaozhi.web.italker.push.bean.db.Apply;
import net.ggxiaozhi.web.italker.push.bean.db.Group;
import net.ggxiaozhi.web.italker.push.bean.db.GroupMember;
import net.ggxiaozhi.web.italker.push.bean.db.User;
import net.ggxiaozhi.web.italker.push.utils.Hib;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
     * @return group 返回查找的群
     */
    public static Group findById(String groupId) {
        return Hib.query(session -> session.get(Group.class, groupId));
    }

    /**
     * 根据id查找一个群 同时发送者 user 一定是群成员 否则返回Null
     * 否者返回一个 null
     *
     * @param user    发送者
     * @param groupId Id
     * @return user不在该群众返回一个null
     */
    public static Group findById(User user, String groupId) {
        GroupMember member = getMember(user.getId(), groupId);
        if (member != null)
            return member.getGroup();
        return null;
    }

    /**
     * 通过名字查找群
     *
     * @param name 群名
     * @return 群
     */
    public static Group findByName(String name) {
        return Hib.query(session -> (Group) session.createQuery("from Group where lower(name)=:name ")
                .setParameter("name", name.toLowerCase())
                .uniqueResult());
    }

    /**
     * 根据当前的群的所有成员
     *
     * @param group 当前的群
     * @return 群中的成员 (不能重复)
     */
    public static Set<GroupMember> getMembers(Group group) {
        return Hib.query(session -> {
            @SuppressWarnings("unchecked")
            List<GroupMember> members = session.createQuery("from GroupMember where group=:group")
                    .setParameter("group", group)
                    .list();
            return new HashSet<>(members);
        });
    }

    /**
     * 查询当前用户下是那些群的群成员
     *
     * @param user 当前用户
     * @return 返回用户在所有群的群成员
     */
    public static Set<GroupMember> getMembers(User user) {
        return Hib.query(session -> {
            @SuppressWarnings("unchecked")
            List<GroupMember> members = session.createQuery("from GroupMember where userId=:userId")
                    .setParameter("userId", user.getId())
                    .list();
            return new HashSet<>(members);
        });
    }

    /**
     * 创建群的方法
     *
     * @param createUser 创建者
     * @param model      创建群的请求model
     * @param users      群成员(包括创建者"我自己")
     * @return 创建完成的群
     */
    public static Group create(User createUser, GroupCreateModel model, List<User> users) {
        return Hib.query(session -> {
            Group group = new Group(createUser, model);
            //在事务中 调用session.save()并没有真正的保存到数据库 而是在缓存中
            //只有在所有操作完成后才会提交
            session.save(group);

            //通关创建者信息创建创建者群成员信息
            GroupMember ownerMember = new GroupMember(createUser, group);
            //设置创建者权限超级权限-->创建者权限
            ownerMember.setPermissionType(GroupMember.PERMISSION_TYPE_ADMIN_SU);
            //保存 并没有保存到数据库
            session.save(ownerMember);

            for (User user : users) {
                //创建普通成员
                GroupMember normalMember = new GroupMember(user, group);
                //保存 并没有保存到数据库
                session.save(normalMember);
            }

            /*//刷新缓冲区
            session.flush();
            //重新加载  如果不是非常急 可以不这么写
            session.load(group, group.getId());*/
            return group;
        });

    }

    /**
     * 得到群成员中指定成员的信息
     *
     * @param userId  指定要得到成员的Id
     * @param groupId 当前群的Id
     * @return 返回群成员信息
     */
    public static GroupMember getMember(String userId, String groupId) {
        return Hib.query(session -> (GroupMember) session.createQuery("from GroupMember where userId=:userId and groupId=:groupId")
                .setParameter("userId", userId)
                .setParameter("groupId", groupId)
                .setMaxResults(1)
                .uniqueResult());
    }

    /**
     * 得到群成员中指定成员的信息
     *
     * @param memberId 指定要得到成员的Id
     * @return 返回群成员信息
     */
    public static GroupMember getMember(String memberId) {
        return Hib.query(session -> session.get(GroupMember.class, memberId));
    }

    @SuppressWarnings("unchecked")
    public static List<Group> search(String name) {
        if (Strings.isNullOrEmpty(name))
            name = "";//保证不能为空的情况 减少后面的判断和额外一些错误
        final String searchName = "%" + name + "%";//模糊匹配

        return Hib.query(session -> {
            //查询条件：name忽略大小写 并且使用like(模糊)查询
            return (List<Group>) session.createQuery("from Group where lower(name) like :name")
                    .setParameter("name", searchName)
                    .setMaxResults(20)//返回的数据最多20条
                    .list();
        });
    }

    /**
     * 给指定的群添加一组群成员
     *
     * @param group   指定的群
     * @param members 一组要添加的群成员
     * @return 返回添加成功的群成员信息列表
     */
    public static Set<GroupMember> addMembers(Group group, List<User> members) {
        return Hib.query(session -> {
            Set<GroupMember> memberSet = new HashSet<>();
            for (User user : members) {
                GroupMember member = new GroupMember(user, group);
                session.save(member);
                memberSet.add(member);
            }
            //此时会出现一个问题：在返回的GroupMember中userId和groupId是空的 因为我们是引用关联 外键
            //在没有 查询的情况下 默认外键是空的 所以我们要查询或刷新一次 如下
            //进行数据刷新
           /* for (GroupMember member : memberSet) {
                //进行刷新 进行关联查询  但此方法消耗较高 不建议使用
                session.refresh(member);
            }*/
            //我们可以在初始化群卡片的时候直接获取Id
            return memberSet;
        });
    }

    /**
     * 修改群成员的信息
     * <p>
     * group    要修改信息的群成员所在的group
     *
     * @param memberId 被修改群成员id
     * @param model    修改的参数
     * @param isAdmin  修改人是否有对应的权限   True 表示有
     * @return 修改后的成员信息
     */
    public static GroupMember updateMember(String memberId, GroupMembeUpdateModel model, boolean isAdmin) {
        GroupMember member = getMember(memberId);
        return Hib.query(session -> {
            member.setAlias(model.getAlias());
            //1.在参数中修改了权限 同时你是普通权限 同时申请接口的用户有对应的权限 满足三者才能修改权限
            if (model.isAdmin() && member.getPermissionType() == GroupMember.NOTIFY_LEVEL_NONE && isAdmin)
                member.setPermissionType(GroupMember.PERMISSION_TYPE_ADMIN);
            session.saveOrUpdate(member);
            return member;
        });
    }

    /**
     * 创建一个添加群的推送记录model
     *
     * @param groupId id
     * @param self    申请人
     * @param model   申请描述
     * @return Apply
     */
    public static Apply joinApply(String groupId, User self, GroupApplyModel model) {
        Apply apply = new Apply();
        apply.setApplicant(self);
        apply.setDesciption(Strings.isNullOrEmpty(model.getDesciption()) ? "我先加入群聊!!!" : model.getDesciption());
        apply.setAttach(Strings.isNullOrEmpty(model.getAttach()) ? "" : model.getAttach());
        apply.setType(Apply.TYPE_ADD_GROUP);
        apply.setTargetId(groupId);
        return Hib.query(session -> {
            session.save(apply);
            return apply;
        });
    }
}
