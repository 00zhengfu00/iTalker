package net.ggxiaozhi.web.italker.push.bean.factory;

import com.google.common.base.Strings;
import net.ggxiaozhi.web.italker.push.bean.api.base.PushModel;
import net.ggxiaozhi.web.italker.push.bean.card.*;
import net.ggxiaozhi.web.italker.push.bean.db.*;
import net.ggxiaozhi.web.italker.push.utils.Hib;
import net.ggxiaozhi.web.italker.push.utils.PushDispatcher;
import net.ggxiaozhi.web.italker.push.utils.TextUtil;

import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 工程名 ： iTalker
 * 包名   ： net.ggxiaozhi.web.italker.push.bean.factory
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：消息存储与处理的工具类
 */
public class PushFactory {
    //发送一条新的消息 并在当前发送历史记录中存储
    public static void pushNewMessage(User sender, Message message) {
        //如果为空 直接返回
        if (sender == null || message == null)
            return;

        //推送给客户端的消息实体
        MessageCard messageCard = new MessageCard(message);
        //转化成字符串
        String entity = TextUtil.toJson(messageCard);
        byte[] bytes = entity.getBytes();
        //个推推送工具类-->发送者
        PushDispatcher dispatcher = new PushDispatcher();

        if (message.getGroup() == null && Strings.isNullOrEmpty(message.getGroupId())) {
            //发消息给朋友
            User receiver = UserFactory.findById(message.getReceiverId());
            if (receiver == null)
                return;
            //构建推送历史消息Model
            PushHistory pushHistory = new PushHistory();
            pushHistory.setEntityType(PushModel.ENTITY_TYPE_MESSAGE);
            try {
                pushHistory.setEntity(new String(bytes,"UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            pushHistory.setReceiverPushId(receiver.getPushId());
            pushHistory.setReceiver(receiver);

            //推送的知识model
            PushModel pushModel = new PushModel();
            //每一条历史记录都是独立的  可以打单独发送
            pushModel.add(pushHistory.getEntityType(), pushHistory.getEntity());

            //要发送的数据丢给个推框架发送者 去发送
            dispatcher.add(receiver, pushModel);
            //历史推送消息保存到数据库
            Hib.queryOnOnly(session -> session.save(pushHistory));
        } else {
            //发消息给群
            Group group = message.getGroup();
            if (group == null)
                //因为存在延迟加载的情况可能为null 则需要通过id查询
                group = GroupFactory.findById(message.getGroupId());
            //如果还没有找到 那么就这返回
            if (group == null)
                return;

            Set<GroupMember> members = GroupFactory.getMembers(group);
            if (members == null || members.size() == 0)
                return;

            //历史记录集合
            List<PushHistory> histories = new ArrayList<>();
            members = members.stream()
                    //过滤我自己
                    .filter(groupMember -> !groupMember.getUserId().equalsIgnoreCase(sender.getId()))
                    .collect(Collectors.toSet());
            //过滤完成后再判断一下 因为可能群成员只有你自己 那么就会过滤掉了
            if (members.size() == 0)
                return;
            //发送并构建推送消息
            addGroupMembersPushModel(dispatcher, histories, members, entity, PushModel.ENTITY_TYPE_MESSAGE);

            Hib.queryOnOnly(session -> {
                //经过addGroupMembersPushModel()方法后histories已经有数据了
                for (PushHistory history : histories) {
                    session.saveOrUpdate(history);
                }
            });
        }

        //发送者真是的提交
        dispatcher.submit();
    }

    /**
     * 给群成员构建一个消息
     * 把消息的存储到数据库的历史记录中去 每个人每条消息都是一个记录
     *
     * @param dispatcher        推送的发送者
     * @param histories         数据库要存储的列表Model
     * @param members           要推送的人或群
     * @param entity            要发送的数据
     * @param entityTypeMessage 发送的类型
     */
    private static void addGroupMembersPushModel(PushDispatcher dispatcher,
                                                 List<PushHistory> histories,
                                                 Set<GroupMember> members,
                                                 String entity,
                                                 int entityTypeMessage) {

        for (GroupMember member : members) {
            //这里是加载 群存在 那么群成员一定存在 所以不需要利用ID去查询用户信息
            User receiver = member.getUser();
            if (receiver == null)
                return;

            //构建推送历史消息Model
            PushHistory pushHistory = new PushHistory();
            pushHistory.setEntityType(entityTypeMessage);
            byte[] bytes = entity.getBytes();
            try {
                pushHistory.setEntity(new String(bytes,"UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            pushHistory.setReceiverPushId(receiver.getPushId());
            pushHistory.setReceiver(receiver);
            //添加进集合
            histories.add(pushHistory);
            //构建一个推送的model
            PushModel pushModel = new PushModel();
            pushModel.add(pushHistory.getEntityType(), pushHistory.getEntity());

            //添加到发送者的数据集合中
            dispatcher.add(receiver, pushModel);
        }
    }

    /**
     * 给群成员推送一条添加进群的推送
     *
     * @param members 要通知的群成员集合
     */
    public static void pushJoinGroup(Set<GroupMember> members, Group group) {

        //个推推送工具类
        PushDispatcher dispatcher = new PushDispatcher();
        //历史记录集合
        List<PushHistory> histories = new ArrayList<>();
        for (GroupMember member : members) {
            //这里是加载 群存在 那么群成员一定存在 所以不需要利用ID去查询用户信息
            User receiver = member.getUser();
            if (receiver == null)
                return;
            String entity = TextUtil.toJson(new GroupCard(group, member));
            byte[] bytes = entity.getBytes();
            //构建推送历史消息Model
            PushHistory pushHistory = new PushHistory();
            pushHistory.setEntityType(PushModel.ENTITY_TYPE_ADD_GROUP);
            try {
                pushHistory.setEntity(new String(bytes,"UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            pushHistory.setReceiverPushId(receiver.getPushId());
            pushHistory.setReceiver(receiver);
            //添加进集合
            histories.add(pushHistory);
            //构建一个推送的model
            PushModel pushModel = new PushModel();
            pushModel.add(pushHistory.getEntityType(), pushHistory.getEntity());

            //添加到发送者的数据集合中
            dispatcher.add(receiver, pushModel);
        }
        //循环保存
        Hib.queryOnOnly(session -> {
            //经过addGroupMembersPushModel()方法后histories已经有数据了
            for (PushHistory history : histories) {
                session.saveOrUpdate(history);
            }
        });
        //提交发送
        dispatcher.submit();

    }

    /**
     * 通知老的成员 有一组新成员加入该群
     *
     * @param oldMembers    老的群成员
     * @param insertMembers 新的群成员
     */
    public static void pushGroupMembersAdd(Set<GroupMember> oldMembers, Set<GroupMember> insertMembers) {
        //个推推送工具类
        PushDispatcher dispatcher = new PushDispatcher();
        //历史记录集合
        List<PushHistory> histories = new ArrayList<>();

        //将新增成员的信息列表转换成Json
        String entity = TextUtil.toJson(insertMembers);
        //循环添加 给每一个老的oldMembers用户发送一条消息 消息的内容为 新增的用户的集合
        addGroupMembersPushModel(dispatcher, histories, oldMembers, entity, PushModel.ENTITY_TYPE_ADD_GROUP_MEMBERS);
        //循环保存
        Hib.queryOnOnly(session -> {
            //经过addGroupMembersPushModel()方法后histories已经有数据了
            for (PushHistory history : histories) {
                session.saveOrUpdate(history);
            }
        });
        //提交发送
        dispatcher.submit();

    }

    /**
     * 给群主发送一个某人神奇加群的推送通知
     *
     * @param applyCard 推送的消息
     * @param ownerId   群主Id
     */
    public static void pushGroupOwner(ApplyCard applyCard, String ownerId) {
        User receiver = UserFactory.findById(ownerId);

        String entity = TextUtil.toJson(applyCard);
        byte[] bytes = entity.getBytes();
        //个推推送工具类
        PushDispatcher dispatcher = new PushDispatcher();
        //构建推送历史消息Model
        PushHistory pushHistory = new PushHistory();
        pushHistory.setEntityType(PushModel.ENTITY_TYPE_ADD_GROUP_MEMBERS);
        try {
            pushHistory.setEntity(new String(bytes,"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        pushHistory.setReceiverPushId(receiver.getPushId());
        pushHistory.setReceiver(receiver);
        //保存推送历史
        Hib.queryOnOnly(session -> session.save(pushHistory));

        //构建一个推送的model
        PushModel pushModel = new PushModel();
        pushModel.add(pushHistory.getEntityType(), pushHistory.getEntity());

        //添加到发送者的数据集合中
        dispatcher.add(receiver, pushModel);
        //提交发送
        dispatcher.submit();
    }

    public static void pushLogout(User receiver, String pushId) {
        //个推推送工具类
        PushDispatcher dispatcher = new PushDispatcher();
        //构建推送历史消息Model
        PushHistory pushHistory = new PushHistory();
        pushHistory.setEntityType(PushModel.ENTITY_TYPE_LOGOUT);
        pushHistory.setEntity("Account logout!!!");
        pushHistory.setReceiverPushId(receiver.getPushId());
        pushHistory.setReceiver(receiver);
        //保存推送历史
        Hib.queryOnOnly(session -> session.save(pushHistory));

        //构建一个推送的model
        PushModel pushModel = new PushModel();
        pushModel.add(pushHistory.getEntityType(), pushHistory.getEntity());

        //添加到发送者的数据集合中
        dispatcher.add(receiver, pushModel);
        //提交发送
        dispatcher.submit();
    }

    /**
     * 给一个我要关注的人发一条我关注了你的推送消息
     *
     * @param receiver 我关注的人 目标
     * @param userCard 我的信息
     */
    public static void pushFollow(User receiver, UserCard userCard) {
        //一定是已经关注了
        userCard.setIsFollow(true);
        String entity = TextUtil.toJson(userCard);
        byte[] bytes = entity.getBytes();
        //构建推送历史消息Model
        PushHistory pushHistory = new PushHistory();
        pushHistory.setEntityType(PushModel.ENTITY_TYPE_ADD_FRIEND);
        try {
            pushHistory.setEntity(new String(bytes,"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        pushHistory.setReceiverPushId(receiver.getPushId());
        pushHistory.setReceiver(receiver);

        //构建一个推送的model
        PushModel pushModel = new PushModel();
        pushModel.add(pushHistory.getEntityType(), pushHistory.getEntity());
        //个推推送工具类
        PushDispatcher dispatcher = new PushDispatcher();
        //添加到发送者的数据集合中
        dispatcher.add(receiver, pushModel);
        dispatcher.submit();
    }

    public static void pushHistoryMsg(List<PushHistory>  histories, User user) {
        //个推推送工具类
        PushDispatcher dispatcher = new PushDispatcher();

        for (PushHistory history : histories) {
            //构建一个推送的model
            PushModel pushModel = new PushModel();
            pushModel.add(history.getEntityType(), history.getEntity());
            //添加到发送者的数据集合中
            dispatcher.add(user, pushModel);
        }
        dispatcher.submit();
    }
}
