package com.example.ggxiaozhi.factory.presenter.message;

import com.example.ggxiaozhi.factory.data.helper.GroupHelper;
import com.example.ggxiaozhi.factory.data.helper.UserHelper;
import com.example.ggxiaozhi.factory.data.message.MessgaeGroupRepository;
import com.example.ggxiaozhi.factory.data.message.MessgaeRepository;
import com.example.ggxiaozhi.factory.model.db.Group;
import com.example.ggxiaozhi.factory.model.db.Message;
import com.example.ggxiaozhi.factory.model.db.User;

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
        if (group==null){
            //初始化操作
        }

    }
}
