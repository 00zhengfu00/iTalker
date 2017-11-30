package net.ggxiaozhi.web.italker.push.bean.api.message;

import com.google.common.base.Strings;
import com.google.gson.annotations.Expose;
import net.ggxiaozhi.web.italker.push.bean.api.user.UpdateInfoModle;
import net.ggxiaozhi.web.italker.push.bean.db.Group;
import net.ggxiaozhi.web.italker.push.bean.db.Message;
import net.ggxiaozhi.web.italker.push.bean.db.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 工程名 ： iTalker
 * 包名   ： net.ggxiaozhi.web.italker.push.bean.api.message
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：Api请求的Model
 */
public class MessageCreateModel {
    //id由代码写入，由客户端负责生成
    @Expose
    private String id;

    //输入消息的内容，不允许为空
    @Expose
    private String content;

    //附件
    @Expose
    private String attach;

    //消息的类型
    @Expose
    private int type = Message.TYPE_STR;

    //接受者 可为空
    @Expose
    private String receiverId;


    //接收者类型->群/人
    @Expose
    private int receiverType = Message.RECEIVER_TYPR_NONE;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public int getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(int receiverType) {
        this.receiverType = receiverType;
    }


    //校验
    public static boolean check(MessageCreateModel model) {
        return
                model != null && !(Strings.isNullOrEmpty(model.id)
                        || Strings.isNullOrEmpty(model.content)
                        || Strings.isNullOrEmpty(model.receiverId))
                        && (model.receiverType == Message.RECEIVER_TYPR_NONE
                        || model.receiverType == Message.RECEIVER_TYPR_GROUP)
                        && (model.type == Message.TYPE_STR
                        || model.type == Message.TYPE_PIC
                        || model.type == Message.TYPE_FILE
                        || model.type == Message.TYPE_AUDIO);
    }

}
