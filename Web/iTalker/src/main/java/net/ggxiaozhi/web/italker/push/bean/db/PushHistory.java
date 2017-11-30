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
 * 功能   ： 消息推送历史记录表
 */

@Entity//表明为实体类
@Table(name = "TB_PUSH_HISTORY")//对应数据库中的表
public class PushHistory {

    @Id//标明字段含义
    @PrimaryKeyJoinColumn//这是一个主键
    @GeneratedValue(generator = "uuid")//主键生成存储类型为UUID
    //把uuid的生成器定义为uuid2,uuid2是将常规的uuid.toString，这样方便我们处理
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    //列的约束：不允许更改，不允许为null
    @Column(updatable = false, nullable = false)
    private String id;

    //推送的具体实体储存都是JSON字符串
    //BLOB 是比TEXT更多的一个大字段类型
    @Lob//@Lob注解表示属性将被持久化为Blob或者Clob类型
    @Column(nullable = false, columnDefinition = "BLOB")
    private String entity;

    //推送的实体类型(区分推送的目标是群还是用户)
    @Column(nullable = false)
    private int entityType;


    //接收者
    //接收者不允许为空
    //一个接受者可以接收很多推送消息
    @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "receiverId")
    private User receiver;
    @Column(nullable = false, updatable = false, insertable = false)
    private String receiverId;


    //发送者
    //发送者可为空,因为可能是系统消息
    //一个发送者可以发送很多推送消息
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "senderId")
    private User sender;
    @Column(updatable = false, insertable = false)
    private String senderId;

    //接收者当前状态下的设备ID
    //对应User.pushId 可以为null
    @Column
    private String receiverPushId;

    //定义为创建时间戳 在创建时就写入
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();

    //定义为更新时间戳 在创建时就写入
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();

    //消息送达的时间，可为null
    @Column
    private LocalDateTime arrivalAt;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
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

    public String getReceiverPushId() {
        return receiverPushId;
    }

    public void setReceiverPushId(String receiverPushId) {
        this.receiverPushId = receiverPushId;
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

    public LocalDateTime getArrivalAt() {
        return arrivalAt;
    }

    public void setArrivalAt(LocalDateTime arrivalAt) {
        this.arrivalAt = arrivalAt;
    }
}
