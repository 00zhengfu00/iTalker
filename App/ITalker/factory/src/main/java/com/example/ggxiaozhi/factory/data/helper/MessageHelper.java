package com.example.ggxiaozhi.factory.data.helper;

import android.os.SystemClock;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.ggxiaozhi.common.Common;
import com.example.ggxiaozhi.common.app.Application;
import com.example.ggxiaozhi.factory.Factory;
import com.example.ggxiaozhi.factory.model.RspModel;
import com.example.ggxiaozhi.factory.model.api.message.MsgCreateModel;
import com.example.ggxiaozhi.factory.model.card.MessageCard;
import com.example.ggxiaozhi.factory.model.db.Message;
import com.example.ggxiaozhi.factory.model.db.Message_Table;
import com.example.ggxiaozhi.factory.net.Network;
import com.example.ggxiaozhi.factory.net.RemoteService;
import com.example.ggxiaozhi.factory.net.UploadHelper;
import com.example.ggxiaozhi.utils.PicturesCompressor;
import com.example.ggxiaozhi.utils.StreamUtil;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.File;
import java.util.concurrent.ExecutionException;

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

                //我们在发送的时候需要通知界面更新状态
                final MessageCard card = model.buildCard(); //这是利用传入的model构建卡片
                //保存数据库与通知
                Factory.getMessageCenter().dispatch(card);
                //  如果是文件文件类型(语音 图片 文件) 需要先上传后才能发送
                if (card.getType() != Message.TYPE_STR) {//发送的消息类型不是语音类型
                    String content;
                    switch (card.getType()) {
                        case Message.TYPE_PIC:
                            content = uploadPicture(card.getContent());
                            break;
                        case Message.TYPE_AUDIO:
                            content = uploadAudio(card.getContent());
                            break;
                        case Message.TYPE_FILE:
                            content = uploadFile(card.getContent());
                            break;
                        default:
                            content = "";
                            break;
                    }

                    if (TextUtils.isEmpty(content)) {
                        //上传失败的情况下  设置失败的状态 并进行一次调度 通知界面
                        card.setStatus(Message.STATUS_FAILED);
                        Factory.getMessageCenter().dispatch(card);
                        //直接返回
                        return;
                    }

                    //重新设置内容
                    card.setContent(content);
                    //重新刷新界面
                    Factory.getMessageCenter().dispatch(card);
                    //重新构建发送的model 这个时候发送的内容就是外网的地址了
                    model.refreshByCard();
                }
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


    /**
     * 构建发送消息之 上传图片
     *
     * @param path 上传的路径
     * @return 外网地址
     */
    private static String uploadPicture(String path) {
        File file = null;//根据图片的本地路径创建对应的托片文件
        try {
            file = Glide.with(Factory.app())
                    .load(path)
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)//按照图片的原比例进行下载
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (file != null) {
            String cacheDir = Application.getCacheDirFile().getAbsolutePath();//获取整个应用的文件缓存路径
            //临时的压缩处理图片 将这个压缩的处理图片上传到云服务器 这样可以节省云服务器空间和减少加载的流量
            String fileFormat = path.substring(path.lastIndexOf("."));//获取图片的后缀格式
            String tempFile = String.format("%s/image/Cache_%s.%s", cacheDir, SystemClock.uptimeMillis(), fileFormat);
            try {
                if (PicturesCompressor.compressImage(file.getAbsolutePath(), tempFile, Common.Constance.MAX_UPLOAD_IMAGE_LENGTH)) {
                    //上传压缩图片到云服务器
                    String ossPath = UploadHelper.uploadIamge(tempFile);
                    //清楚缓存
                    StreamUtil.delete(tempFile);
                    return ossPath;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 构建发送消息之 上传语音
     *
     * @param path 上传的路径
     * @return 外网地址
     */
    private static String uploadAudio(String path) {
        File file = new File(path);
        if (!file.exists() || file.length() <= 0)
            return null;
        return UploadHelper.uploadAudio(path);
    }

    /**
     * 构建发送消息之 上传文件
     *
     * @param content 上传的路径
     * @return 外网地址
     */
    private static String uploadFile(String content) {
        return null;
    }

    /**
     * 查询与群聊天的最后一条消息
     *
     * @param groupId 群ID
     * @return 最后一条message
     */
    public static Message findLastWithGroup(String groupId) {
        return SQLite.select()
                .from(Message.class)
                .where(Message_Table.group_id.eq(groupId))
                .orderBy(Message_Table.createAt, false)//倒叙查询
                .querySingle();
    }

    /**
     * 查询与人聊天的最后一条消息
     *
     * @param userId
     * @return
     */
    public static Message findLastWithUser(String userId) {
        return SQLite.select()
                .from(Message.class)
                .where(OperatorGroup.clause().and(Message_Table.sender_id.eq(userId))
                        .and(Message_Table.group_id.isNull()))
                .or(Message_Table.receiver_id.eq(userId))
                .orderBy(Message_Table.createAt, false)//倒叙查询
                .querySingle();
    }
}
