package net.ggxiaozhi.web.italker.push.service;

import com.google.common.base.Strings;
import net.ggxiaozhi.web.italker.push.bean.api.base.ResponseModel;
import net.ggxiaozhi.web.italker.push.bean.api.group.GroupApplyModel;
import net.ggxiaozhi.web.italker.push.bean.api.group.GroupCreateModel;
import net.ggxiaozhi.web.italker.push.bean.api.group.GroupMembeUpdateModel;
import net.ggxiaozhi.web.italker.push.bean.api.group.GroupMemberAddModel;
import net.ggxiaozhi.web.italker.push.bean.card.ApplyCard;
import net.ggxiaozhi.web.italker.push.bean.card.GroupCard;
import net.ggxiaozhi.web.italker.push.bean.card.GroupMemberCard;
import net.ggxiaozhi.web.italker.push.bean.db.Apply;
import net.ggxiaozhi.web.italker.push.bean.db.Group;
import net.ggxiaozhi.web.italker.push.bean.db.GroupMember;
import net.ggxiaozhi.web.italker.push.bean.db.User;
import net.ggxiaozhi.web.italker.push.bean.factory.GroupFactory;
import net.ggxiaozhi.web.italker.push.bean.factory.PushFactory;
import net.ggxiaozhi.web.italker.push.bean.factory.UserFactory;
import net.ggxiaozhi.web.italker.push.provider.LocalDateTimeConverter;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 工程名 ： iTalker
 * 包名   ： net.ggxiaozhi.web.italker.push.service
 * 作者名 ： 志先生_
 * 日期   ： 2017/12
 * 功能   ：群相关操作的接口
 */
@Path("/group")
public class GroupService extends BaseService {

    /**
     * 创建群
     *
     * @param model 创建群的信息
     * @return 创建成功的群的信息
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)//指定传入的格式
    @Produces(MediaType.APPLICATION_JSON)//指定返回的格式
    public ResponseModel<GroupCard> create(GroupCreateModel model) {
        //检查不通过 返回参数错误 (此处也可以创建一个检查参数model的拦截器 所有请求参数model继承基础的model 然后基础的model有一个检查的方法)
        if (!GroupCreateModel.check(model)) {
            return ResponseModel.buildParameterError();
        }

        //获取创建者
        User createUser = getSelf();
        //创建者不应该在群列表中
        model.getUsers().remove(createUser.getId());
        if (model.getUsers().size() == 0) {
            return ResponseModel.buildParameterError();
        }

        //检查群名 是否已经存在
        if (GroupFactory.findByName(model.getName()) != null) {
            return ResponseModel.buildHaveNameError();
        }

        List<User> users = new ArrayList<>();
        for (String userId : model.getUsers()) {
            User user = UserFactory.findById(userId);
            if (user == null)
                continue;
            users.add(user);
        }
        //如果还是没有一个成员 返回参数错误
        if (users.size() == 0)
            return ResponseModel.buildParameterError();
        Group group = GroupFactory.create(createUser, model, users);
        if (group == null) {
            //服务器异常
            return ResponseModel.buildServiceError();
        }

        //拿管理员的信息(自己的信息)
        GroupMember createMember = GroupFactory.getMember(createUser.getId(), group.getId());
        if (createMember == null) {
            //服务器异常
            return ResponseModel.buildServiceError();
        }
        //获取当前群下所有成员的信息
        Set<GroupMember> members = GroupFactory.getMembers(group);
        if (members == null)
            //服务器异常
            return ResponseModel.buildServiceError();
        members.stream().filter(groupMember -> !groupMember.getId().equalsIgnoreCase(createMember.getId())).collect(Collectors.toSet());

        //给群成员推送一条加入群的通知
        PushFactory.pushJoinGroup(members);
        return ResponseModel.buildOk(new GroupCard(createMember));
    }

    /**
     * 查找群 没有参数则搜索最近创建的列表群
     *
     * @param name 参数
     * @return 返回一个群的列表
     */
    @GET//name:(.*)? 任意长度 任意字符的正则
    @Path("/search/{name:(.*)?}")
    @Consumes(MediaType.APPLICATION_JSON)//指定传入的格式
    @Produces(MediaType.APPLICATION_JSON)//指定返回的格式
    public ResponseModel<List<GroupCard>> search(@PathParam("name") @DefaultValue("") String name) {
        User self = getSelf();
        List<Group> groups = GroupFactory.search(name);
        if (groups != null && groups.size() > 0) {
            List<GroupCard> groupCards = groups.stream().map(group -> {
                //判断在搜索到的群中 我是不是这个群成员
                GroupMember member = GroupFactory.getMember(self.getId(), group.getId());
                return new GroupCard(group, member);
            }).collect(Collectors.toList());
            //成功找到群 如果是群成员那么就会存在通知级别和加入时间 不是这两个字段就是默认值 客户端可以自行判断
            return ResponseModel.buildOk(groupCards);
        }
        //没有找到 就返回一个空的
        return ResponseModel.buildOk();

    }

    /**
     * 拉取自己的群列表信息
     *
     * @param date 时间字段 不传递 则返回当前全部群的列表 传递参数 则返回传入时间之后加入的群
     * @return 群的列表
     */
    @GET//date指定某一时间段的数据
    @Path("/list/{date:(.*)?}")
    @Consumes(MediaType.APPLICATION_JSON)//指定传入的格式
    @Produces(MediaType.APPLICATION_JSON)//指定返回的格式
    public ResponseModel<List<GroupCard>> list(@PathParam("date") @DefaultValue("") String date) {
        User self = getSelf();

        LocalDateTime dateTime = null;
        if (!Strings.isNullOrEmpty(date)) {
            try {
                //格式转换
                dateTime = LocalDateTime.parse(date, LocalDateTimeConverter.FORMATTER);
            } catch (Exception e) {
                //异常则设置为空
                dateTime = null;
            }
        }

        Set<GroupMember> members = GroupFactory.getMembers(self);
        if (members == null || members.size() == 0) {
            return ResponseModel.buildOk();
        }
        final LocalDateTime finalLocalTime = dateTime;
        List<GroupCard> groupCards = members.stream().filter(groupMember -> finalLocalTime == null//如果时间为null 那么则不做过滤 全部返回
                || groupMember.getUpdateAt().isAfter(finalLocalTime))//如果时间不为null 那么 过滤 现在在传入时间之后的数据
                .map(GroupCard::new)// 转换操作
                .collect(Collectors.toList());
        return ResponseModel.buildOk(groupCards);

    }

    /**
     * 获取某一个群的信息 你必须是这个群的群成员
     *
     * @param id 群的Id
     * @return 返回群的信息
     */
    @GET//
    @Path("/{groupId}")//http://.../api/group/0000-0000-0000-0000
    @Consumes(MediaType.APPLICATION_JSON)//指定传入的格式
    @Produces(MediaType.APPLICATION_JSON)//指定返回的格式
    public ResponseModel<GroupCard> getGroup(@PathParam("groupId") String id) {
        User self = getSelf();
        if (Strings.isNullOrEmpty(id))
            return ResponseModel.buildParameterError();
        GroupMember member = GroupFactory.getMember(self.getId(), id);
        if (member == null)
            return ResponseModel.buildNotFoundGroupError(null);
        return ResponseModel.buildOk(new GroupCard(member));
    }

    /**
     * 拉群一个群的所有成员 你必须是群的成员才行
     *
     * @param groupId 群Id
     * @return 成员列表
     */
    @GET
    @Path("/{groupId}/members")//http://.../api/group/0000-0000-0000-0000/member
    @Consumes(MediaType.APPLICATION_JSON)//指定传入的格式
    @Produces(MediaType.APPLICATION_JSON)//指定返回的格式
    public ResponseModel<List<GroupMemberCard>> members(@PathParam("groupId") String groupId) {
        User self = getSelf();
        Group group = GroupFactory.findById(groupId);
        if (group == null) {
            return ResponseModel.buildNotFoundGroupError(null);
        }
        //权限检查
        GroupMember selfMember = GroupFactory.getMember(self.getId(), groupId);
        if (selfMember == null) {
            return ResponseModel.buildNoPermissionError();
        }
        Set<GroupMember> members = GroupFactory.getMembers(group);
        if (members == null) {
            return ResponseModel.buildServiceError();
        }
        //转换
        List<GroupMemberCard> groupMemberCards = members.stream()
                .map(GroupMemberCard::new)
                .collect(Collectors.toList());
        return ResponseModel.buildOk(groupMemberCards);
    }

    /**
     * 给群添加成员的接口
     *
     * @param groupId 群Id 你必须是这个群成员之一
     * @param model   添加成员
     * @return 添加的成员列表
     */
    @POST
    @Path("/{groupId}/member")//http://.../api/group/0000-0000-0000-0000/member
    @Consumes(MediaType.APPLICATION_JSON)//指定传入的格式
    @Produces(MediaType.APPLICATION_JSON)//指定返回的格式
    public ResponseModel<List<GroupMemberCard>> MembersAdd(@PathParam("groupId") String groupId, GroupMemberAddModel model) {
        if (Strings.isNullOrEmpty(groupId) || !GroupMemberAddModel.check(model))
            return ResponseModel.buildParameterError();
        User self = getSelf();
        //移除我自己
        //noinspection SuspiciousMethodCalls
        model.getUsers().remove(self);
        //除了我自己没有其他要
        if (model.getUsers().size() == 0)
            return ResponseModel.buildParameterError();
        //没有这个群
        Group group = GroupFactory.findById(groupId);
        if (group == null)
            return ResponseModel.buildNotFoundGroupError(null);
        GroupMember member = GroupFactory.getMember(self.getId(), groupId);
        //如果你不是群成员 或是你是普通成员 那么则没有权限
        if (member == null || member.getPermissionType() == GroupMember.PERMISSION_TYPE_NONE)
            return ResponseModel.buildNoPermissionError();
        //得到该群中已有的成员
        Set<GroupMember> oldMembers = GroupFactory.getMembers(group);
        //得到该群中已有的成员的Id
        Set<String> oldMembersIds = oldMembers
                .stream()
                .map(GroupMember::getId)
                .collect(Collectors.toSet());
        //得到要添加用户的集合
        Set<String> users = model.getUsers();
        List<User> insertGroupMembers = new ArrayList<>();
        for (String id : model.getUsers()) {
            User user = UserFactory.findById(id);
            //如果没有找到要添加的用户
            if (user == null)
                continue;
            //如果要添加的用户以及是群成员了
            if (oldMembersIds.contains(id))
                continue;
            insertGroupMembers.add(user);
        }

        //如果还是没有一个成员 返回参数错误
        if (users.size() == 0)
            return ResponseModel.buildParameterError();
        //如果有新增的成员则进行添加的操作
        Set<GroupMember> alreadyInsert = GroupFactory.addMembers(group, insertGroupMembers);
        if (alreadyInsert == null)
            return ResponseModel.buildServiceError();
        //转换
        List<GroupMemberCard> insertCards = alreadyInsert.stream()
                .map(GroupMemberCard::new)
                .collect(Collectors.toList());

        //转换完成后 我们进行通知
        //1.通知新增的成员-->你被加入了XXX群
        PushFactory.pushJoinGroup(alreadyInsert);
        //2.通知群中老的成员，有XXX,XXX加入群
        PushFactory.pushGroupMembersAdd(oldMembers, alreadyInsert);
        return ResponseModel.buildOk(insertCards);

    }

    /**
     * 更改成员信息，请求的人要么是管理员，要么就是成员本人
     * 管理员可以修改成员的别名和权限 成员只能修改别名
     *
     * @param memberId 成员Id，可以查询对应的群，和人
     * @param model    修改的Model
     * @return 当前成员的信息
     */
    @PUT
    @Path("/member/{memberId}")//http://.../api/group/member/0000-0000-0000-0000
    @Consumes(MediaType.APPLICATION_JSON)//指定传入的格式
    @Produces(MediaType.APPLICATION_JSON)//指定返回的格式
    public ResponseModel<GroupMemberCard> modifyMember(@PathParam("memberId") String memberId, GroupMembeUpdateModel model) {

        /*1.判断参数*/

        if (!GroupMembeUpdateModel.check(model)) {
            return ResponseModel.buildParameterError();
        }
        //获取当前请求的用户信息
        User self = getSelf();
        //获取要修改群成员在哪个群的信息要修改
        Group group = GroupFactory.findById(model.getGroupId());
        if (group == null)
            return ResponseModel.buildParameterError();
        /*2.校验参数*/

        //得到当前群下所有群信息
        Set<GroupMember> members = GroupFactory.getMembers(group);
        //得到self 在群众的群成员卡片
        GroupMember selfMember = GroupFactory.getMember(self.getId(), model.getGroupId());
        if (selfMember == null)
            return ResponseModel.buildParameterError();
        List<String> menberIds = members.stream().map(GroupMember::getId).collect(Collectors.toList());
        boolean isAlready = false;
        for (String id : menberIds) {
            if (id.equalsIgnoreCase(memberId))
                isAlready = true;
        }
        if (!isAlready)
            return ResponseModel.buildParameterError();

        //1.自己只能修改自己的信息 只能修改自己在群中的别名
        //2.如果是管理员或是创建者 都能修改他人的权限和别名
        //权限错误 如果你是普通权限 同时 传入的memberId不是你自己的群成员id 那么就证明你想改别人的  那么返回权限不足
        if (selfMember.getPermissionType() == GroupMember.PERMISSION_TYPE_NONE && !selfMember.getId().equalsIgnoreCase(memberId))
            return ResponseModel.buildNoPermissionError();
        /*3.修改信息*/
        boolean isAmind = false;
        if (selfMember.getPermissionType() != GroupMember.NOTIFY_LEVEL_NONE)
            isAmind = true;
        //获取存储到数据库返回的member
        GroupMember member = GroupFactory.updateMember(memberId, model, isAmind);
        if (member == null) {
            return ResponseModel.buildServiceError();
        }
        //返回最新的GroupMemberCard
        return ResponseModel.buildOk(new GroupMemberCard(member));
    }

    /**
     * 申请加入一个群，
     * 此时会创建一个加入的申请，并写入表；然后会给管理员发送消息
     * 管理员同意，其实就是调用添加成员的接口把对应的用户添加进去
     *
     * @param groupId 群Id
     * @return 申请的信息
     */
    @POST
    @Path("/applyJoin/{groupId}")//http://.../api/group/applyJoin/0000-0000-0000-0000
    @Consumes(MediaType.APPLICATION_JSON)//指定传入的格式
    @Produces(MediaType.APPLICATION_JSON)//指定返回的格式
    public ResponseModel<ApplyCard> join(@PathParam("groupId") String groupId, GroupApplyModel model) {
        //得到自己(申请人)的信息
        User self = getSelf();
        //要加入的群
        Group group = GroupFactory.findById(groupId);
        Set<GroupMember> members = GroupFactory.getMembers(group);
        //判断已经是群成员了
        boolean isAlready = false;
        Set<String> userIds = members.stream()
                .map(GroupMember::getUserId)
                .collect(Collectors.toSet());
        for (String userId : userIds) {
            if (userId.equalsIgnoreCase(self.getId()))
                isAlready = true;
        }
        //如果是 那么就返回一条消息
        if (isAlready)
            return ResponseModel.buildNotFoundUserError("You already join this group");
        Apply apply = GroupFactory.joinApply(groupId, self, model);
        //创建返回Model
        ApplyCard applyCard = new ApplyCard(apply);
        //推送给群主一条消息
        PushFactory.pushGroupOwner(applyCard, group.getOwnerId());
        //返回
        return ResponseModel.buildOk(applyCard);
    }
}
