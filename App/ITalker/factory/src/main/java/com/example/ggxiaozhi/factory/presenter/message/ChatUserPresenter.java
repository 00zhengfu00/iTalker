package com.example.ggxiaozhi.factory.presenter.message;

import com.example.ggxiaozhi.factory.data.helper.UserHelper;
import com.example.ggxiaozhi.factory.data.message.MessageDataSource;
import com.example.ggxiaozhi.factory.data.message.MessgaeRepository;
import com.example.ggxiaozhi.factory.model.api.message.MsgCreateModel;
import com.example.ggxiaozhi.factory.model.db.Message;
import com.example.ggxiaozhi.factory.model.db.User;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.presenter.message
 * 作者名 ： 志先生_
 * 日期   ： 2017/12
 * 功能   ：用户聊天的Presenter
 */

public class ChatUserPresenter extends ChatPresenter<ChatContract.UserView> implements ChatContract.Presenter {
    public ChatUserPresenter(ChatContract.UserView view, String receiverId) {
        //数据源 view 接受者 类型
        super(new MessgaeRepository(receiverId), view, receiverId, Message.RECEIVER_TYPE_NONE);
    }

    @Override
    public void start() {
        super.start();
        User user = UserHelper.findFromLocal(mReceiverId);
        getView().onInit(user);

    }
}
