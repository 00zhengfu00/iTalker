package com.example.ggxiaozhi.factory.presenter.message;

import com.example.ggxiaozhi.factory.data.helper.GroupHelper;
import com.example.ggxiaozhi.factory.data.message.MessgaeGroupRepository;
import com.example.ggxiaozhi.factory.model.db.Group;
import com.example.ggxiaozhi.factory.model.db.Message;
import com.example.ggxiaozhi.factory.model.db.view.MemberUserModel;
import com.example.ggxiaozhi.factory.presistance.Account;

import java.util.List;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.presenter.message
 * 作者名 ： 志先生_
 * 日期   ： 2017/12
 * 功能   ：群聊天的Presenter
 */

public class ChatGroupPresenter extends ChatPresenter<ChatContract.GroupView> implements ChatContract.Presenter {
    public ChatGroupPresenter(ChatContract.GroupView view, String receiverId) {
        //数据源 view 接受者 类型
        super(new MessgaeGroupRepository(receiverId), view, receiverId, Message.RECEIVER_TYPE_GROUP);
    }

    @Override
    public void start() {
        super.start();
        Group group = GroupHelper.findFromLocal(mReceiverId);
        if (group != null) {
            //初始化操作
            ChatContract.GroupView view = getView();
            //判断是否有管理员权限
            boolean isAdmin = Account.getUserId().equalsIgnoreCase(group.getOwner().getId());
            view.showAdminOption(isAdmin);
            //基础信息初始化
            view.onInit(group);
            //加载4条简单的成员信息
            List<MemberUserModel> latelyGroupMembers = group.getGroupLatelyGroupMembers();
            //总的成员数量
            long membersCount = group.getGroupMembersCount();
            //没有显示的成员数量
            long count = membersCount - latelyGroupMembers.size();
            //群成员信息初始化
            view.onInitGroupMembers(latelyGroupMembers, count);
        }
    }
}
