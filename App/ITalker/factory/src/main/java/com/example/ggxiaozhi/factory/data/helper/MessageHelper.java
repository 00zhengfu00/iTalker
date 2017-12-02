package com.example.ggxiaozhi.factory.data.helper;

import com.example.ggxiaozhi.factory.Factory;
import com.example.ggxiaozhi.factory.model.RspModel;
import com.example.ggxiaozhi.factory.model.api.message.MsgCreateModel;
import com.example.ggxiaozhi.factory.model.card.MessageCard;
import com.example.ggxiaozhi.factory.model.db.Message;
import com.example.ggxiaozhi.factory.model.db.Message_Table;
import com.example.ggxiaozhi.factory.net.Network;
import com.example.ggxiaozhi.factory.net.RemoteService;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.data.helper
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：消息相关的网络请求 数据库存储
 */

public class MessageHelper {
    /**
     * 从本地数据库查找消息
     *
     * @param id 消息id
     * @return Message
     */
    public static Message findFromLocal(String id) {
        //从本地查询一天消息
        return SQLite.select()
                .from(Message.class)
                .where(Message_Table.id.eq(id))
                .querySingle();
    }

    /**
     * 发起网络数据请求 发送消息
     *
     * @param model 请求参数
     */
    public static void push(final MsgCreateModel model) {
        //保证在子线程中进行
        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                //成功状态：如果是是一条已经发送的消息 不能重新发送
                //正在发送状态：如果是是一条正在发送的消息 不能重新发送
                Message message = MessageHelper.findFromLocal(model.getId());
                if (message != null && message.getStatus() != Message.STATUS_FAILED)
                    return;
                // TODO 如果是文件文件类型(语音 图片 文件) 需要先上传后才能发送

                //我们在发送的时候需要通知界面更新状态
                final MessageCard card = model.buildCard(); //这是利用传入的model构建卡片
                //保存数据库与通知
                Factory.getMessageCenter().dispatch(card);

                RemoteService service = Network.remote();
                Call<RspModel<MessageCard>> rspModelCall = service.msgPush(model);
                rspModelCall.enqueue(new Callback<RspModel<MessageCard>>() {
                    @Override
                    public void onResponse(Call<RspModel<MessageCard>> call, Response<RspModel<MessageCard>> response) {
                        RspModel<MessageCard> rspModel = response.body();
                        if (rspModel != null && rspModel.success()) {//成功
                            MessageCard messageCard = rspModel.getResult();
                            if (messageCard != null) {
                                Factory.getMessageCenter().dispatch(messageCard);
                            }
                        } else {
                            //解析失败的原因是否是登录异常
                            Factory.decodeRspCode(rspModel, null);
                            onFailure(call, null);
                        }
                    }

                    @Override
                    public void onFailure(Call<RspModel<MessageCard>> call, Throwable t) {
                        card.setStatus(Message.STATUS_FAILED);
                        //更新消息状态
                        Factory.getMessageCenter().dispatch(card);
                    }
                });


            }
        });
    }
}
