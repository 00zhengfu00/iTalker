package com.example.ggxiaozhi.factory.model.db;

import android.text.TextUtils;

import com.example.ggxiaozhi.factory.data.helper.GroupHelper;
import com.example.ggxiaozhi.factory.data.helper.MessageHelper;
import com.example.ggxiaozhi.factory.data.helper.UserHelper;
import com.example.ggxiaozhi.factory.model.db.base.BaseDbModel;
import com.example.ggxiaozhi.factory.utils.DiffUiDataCallback;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;


import java.util.Date;
import java.util.Objects;


/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.model.card
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：本地的会话表 显示"最近的"  '一条'数据
 */
@Table(database = AppDatabase.class)
public class Session extends BaseDbModel<Session> {
    @PrimaryKey
    private String id; // Id, 是Message中的接收者User的Id或者群的Id
    @Column
    private String picture; // 图片，接收者用户的头像，或者群的图片
    @Column
    private String title; // 标题，用户的名称，或者群的名称
    @Column
    private String content; // 显示在界面上的简单内容，是Message的一个描述
    @Column
    private int receiverType = Message.RECEIVER_TYPE_NONE; // 类型，对应人，或者群消息
    @Column
    private int unReadCount = 0; // 未读数量，当没有在当前界面时，应当增加未读数量
    @Column
    private Date modifyAt; // 最后更改时间

    @ForeignKey(tableClass = Message.class)
    private Message message; // 对应的消息，外键为Message的Id

    public Session() {

    }

    public Session(Identify identify) {
        this.id = identify.id;
        this.receiverType = identify.type;
    }

    public Session(Message message) {
        if (message.getGroup() == null) {
            receiverType = Message.RECEIVER_TYPE_NONE;
            User other = message.getOther();
            id = other.getId();
            picture = other.getPortrait();
            title = other.getName();
        } else {
            receiverType = Message.RECEIVER_TYPE_GROUP;
            id = message.getGroup().getId();
            picture = message.getGroup().getPicture();
            title = message.getGroup().getName();
        }
        this.message = message;
        this.content = message.getSampleContent();
        this.modifyAt = message.getCreateAt();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(int receiverType) {
        this.receiverType = receiverType;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public int getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(int unReadCount) {
        this.unReadCount = unReadCount;
    }

    public Date getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(Date modifyAt) {
        this.modifyAt = modifyAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Session session = (Session) o;

        return receiverType == session.receiverType
                && unReadCount == session.unReadCount
                && Objects.equals(id, session.id)
                && Objects.equals(picture, session.picture)
                && Objects.equals(title, session.title)
                && Objects.equals(content, session.content)
                && Objects.equals(modifyAt, session.modifyAt)
                && Objects.equals(message, session.message);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + receiverType;
        return result;
    }

    @Override
    public boolean isSame(Session oldT) {
        return Objects.equals(id, oldT.id)
                && Objects.equals(receiverType, oldT.receiverType);
    }

    @Override
    public boolean isUiContentsSame(Session oldT) {
        return this.content.equals(oldT.content)
                && Objects.equals(this.modifyAt, oldT.modifyAt);
    }


    /**
     * 对于一条消息，我们提取主要部分，用于和Session进行对应
     *
     * @param message 消息Model
     * @return 返回一个Session.Identify
     */
    public static Identify createSessionIdentify(Message message) {
        Identify identify = new Identify();
        if (message.getGroup() == null) {
            identify.type = Message.RECEIVER_TYPE_NONE;
            User other = message.getOther();
            identify.id = other.getId();
        } else {
            identify.type = Message.RECEIVER_TYPE_GROUP;
            identify.id = message.getGroup().getId();
        }
        return identify;
    }

    /**
     * 刷新当前Seesion 为最新的Message
     */
    public void refreshToNow() {
        Message message;


        if (receiverType == Message.RECEIVER_TYPE_GROUP) {//如果是群
            //1.刷新当前对应群的相关信息
            message = MessageHelper.findLastWithGroup(id);
            if (message == null) {
                //2.如果当前没有该群的基本信息
                if (TextUtils.isEmpty(this.picture) || TextUtils.isEmpty(this.title)) {
                    //那么我们就查找当前Session的该群是不是存在
                    Group group = GroupHelper.findFromLocal(id);
                    if (group != null) {
                        this.unReadCount = this.unReadCount + 1;
                        //3.如果找到了群的信息 那么我们就加载群的基本信息赋值给当前的session
                        this.picture = group.getPicture();
                        this.title = group.getName();

                    }
                }
                //4.如果没有找到 就赋值基本默认信息
                this.message = null;
                this.content = "";
                this.modifyAt = new Date(System.currentTimeMillis());//赋值当前时间
            } else {//如果当前message找到了 意味着本地有有最后一条记录
                if (TextUtils.isEmpty(this.picture) || TextUtils.isEmpty(this.title)) {
                    //如果没有基本信息 我们就用Message去load群的信息
                    Group group = message.getGroup();
                    group.load();//懒加载的原因
                    this.unReadCount = this.unReadCount + 1;
                    this.picture = group.getPicture();
                    this.title = group.getName();
                }
                //4.如果没有找到 就赋值基本默认信息
                this.message = message;
                this.content = message.getSampleContent();
                this.modifyAt = message.getCreateAt();

            }
        } else {//如果是人 和人聊天
            message = MessageHelper.findLastWithUser(id);
            if (message == null) {
                //如果当前没有该用户的基本信息
                if (TextUtils.isEmpty(this.picture) || TextUtils.isEmpty(this.title)) {
                    //查询人
                    User user = UserHelper.findFromLocal(id);
                    if (user != null) {
                        //如果找到了人的信息 那么我们就加载人的基本信息赋值给当前的session
                        this.unReadCount = this.unReadCount + 1;
                        this.picture = user.getPortrait();
                        this.title = user.getName();
                    }
                }
                //如果没有找到 就赋值基本默认信息
                this.message = null;
                this.content = "";
                this.modifyAt = new Date(System.currentTimeMillis());//赋值当前时间
            } else {//我和他之间有消息来往
                if (TextUtils.isEmpty(this.picture) || TextUtils.isEmpty(this.title)) {
                    //如果没有基本信息 我们就用Message去load群的信息
                    User other = message.getOther();
                    other.load();//懒加载的原因
                    this.unReadCount = this.unReadCount + 1;
                    this.picture = other.getPortrait();
                    this.title = other.getName();
                }
                //4.如果没有找到 就赋值基本默认信息
                this.message = message;
                this.content = message.getSampleContent();
                this.modifyAt = message.getCreateAt();
            }
        }
    }


    /**
     * 对于会话信息，最重要的部分进行提取
     * 其中我们主要关注两个点：
     * 一个会话最重要的是标示是和人聊天还是在群聊天；
     * 所以对于这点：Id存储的是人或者群的Id
     * 紧跟着Type：存储的是具体的类型（人、群）
     * equals 和 hashCode 也是对两个字段进行判断
     */
    public static class Identify {
        public String id;
        public int type;


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Identify identify = (Identify) o;
            return type == identify.type
                    && (id != null ? id.equals(identify.id) : identify.id == null);
        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + type;
            return result;
        }
    }
}