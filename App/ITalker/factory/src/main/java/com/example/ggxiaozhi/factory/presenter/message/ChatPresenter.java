package com.example.ggxiaozhi.factory.presenter.message;

import android.support.v7.util.DiffUtil;

import com.example.ggxiaozhi.factory.data.helper.MessageHelper;
import com.example.ggxiaozhi.factory.data.message.MessageDataSource;
import com.example.ggxiaozhi.factory.model.api.message.MsgCreateModel;
import com.example.ggxiaozhi.factory.model.db.Message;
import com.example.ggxiaozhi.factory.presenter.BaseSourcePresenter;
import com.example.ggxiaozhi.factory.presistance.Account;
import com.example.ggxiaozhi.factory.utils.DiffUiDataCallback;

import java.util.List;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.presenter.message
 * 作者名 ： 志先生_
 * 日期   ： 2017/12
 * 功能   ：聊天基础的Presenter
 */

public class ChatPresenter<View extends ChatContract.View>
        extends BaseSourcePresenter<Message, Message, MessageDataSource, View>
        implements ChatContract.Presenter {

    //可能是群或是人的id
    protected String mReceiverId;
    protected int receiverType;

    public ChatPresenter(MessageDataSource source, View view, String receiverId, int receiverType) {
        super(source, view);
        this.mReceiverId = receiverId;
        this.receiverType = receiverType;
    }


    @Override
    public void pushText(String content) {
        //构建发送文本消息的model
        MsgCreateModel model = new MsgCreateModel.Builder()
                .receiver(mReceiverId, receiverType)
                .content(content, Message.TYPE_STR)
                .build();
        MessageHelper.push(model);
    }

    @Override
    public void pushAudio(String path) {
        //TODO
    }

    @Override
    public void pushImages(String[] paths) {
        //TODO
    }

    @Override
    public void pushAttach(String[] paths) {
        //TODO
    }

    @Override
    public boolean rePush(Message message) {
        //确认消息是可以重新发送的
        if (Account.getUserId().equalsIgnoreCase(message.getSender().getId()) &&
                message.getStatus() == Message.STATUS_FAILED) {
            //更新状态
            message.setStatus(Message.STATUS_CREATED);
            //构建重新发送的model
            MsgCreateModel model = MsgCreateModel.buildWithMessage(message);
            MessageHelper.push(model);
            return true;
        }
        return false;
    }

    @Override
    public void onDataLoaded(List<Message> messages) {
        View view = getView();
        if (view == null)
            return;
        //拿到旧数据
        List<Message> items = view.getAdapter().getItems();
        //差异计算
        DiffUiDataCallback<Message> callback = new DiffUiDataCallback<>(items, messages);
        final DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        //差异刷新
        refreshData(result, messages);
    }

}
