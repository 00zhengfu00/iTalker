package net.ggxiaozhi.web.italker.push.bean.db;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 工程名 ： iTalker
 * 包名   ： net.ggxiaozhi.web.italker.push.bean.db
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：发送消息的Module 这里用户(User)发送给群或是一个用户(User)
 */
@Entity//表明为实体类
@Table(name = "TB_MESSAGE")//对应数据库中的表
public class Message {

    /**
     * 发送消息的类型
     */
    public static final int TYPE_STR = 1;//文本类型
    public static final int TYPE_PIC = 2;//图片类型
    public static final int TYPE_FILE = 3;//文件类型
    public static final int TYPE_AUDIO = 4;//语音类型

    @Id//标明字段含义
    @PrimaryKeyJoinColumn//这是一个主键
    //这里我们不自动生成UUID，id由代码写入，由客户端负责生成
    //这样可以避免复杂的服务器和客户端的映射关系
    //@GeneratedValue(generator = "uuid")//主键生成存储类型为UUID
    //把uuid的生成器定义为uuid2,uuid2是将常规的uuid.toString，这样方便我们处理
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    //列的约束：不允许更改，不允许为null
    @Column(updatable = false, nullable = false)
    private String id;

    //输入消息的内容，不允许为空，类型为文本
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    //附件
    @Column
    private String attach;

    //消息的类型
    @Column(nullable = false)
    private int Type;

    //定义为创建时间戳 在创建时就写入
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();
    //定义为更新时间戳 在创建时就写入
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();

    //发送者 不可为空
    //多个消息对应一个发送者
    @JoinColumn(name = "senderId")
    @ManyToOne(optional = false)
    private User sender;
    //这个字段仅仅是只是为了对应sender的数据库字段中的senderId
    //不允许手动更新或插入
    @Column(nullable = false, updatable = false, insertable = false)
    private String senderId;

    //接受者 可为空
    //多个消息对应一个User
    @JoinColumn(name = "receiverId")
    @ManyToOne//这里不设置optional 因为接受者可能是用户也可能是群组
    private User receiver;
    //这个字段仅仅是只是为了对应receiver的数据库字段中的senderId
    //不允许手动更新或插入
    @Column(updatable = false, insertable = false)
    private String receiverId;


    //一个群可以接受多个消息
    @JoinColumn(name = "groupId")
    @ManyToOne//这里不设置optional 因为接受者可能是用户也可能是群组
    private Group group;
    //这个字段仅仅是只是为了对应group的数据库字段中的groupId
    //不允许手动更新或插入
    @Column(updatable = false, insertable = false)
    private String groupId;


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
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }


}
