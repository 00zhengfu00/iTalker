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
 * 功能   ：用户关系Module，用于用户直接进行好友关系的实现(相当于中间表)
 */
@Entity//表明为实体类
@Table(name = "TB_USER_FOLLOW")//对应数据库中的表
public class UserFollow {

    @Id//标明字段含义
    @PrimaryKeyJoinColumn//这是一个主键
    @GeneratedValue(generator = "uuid")//主键生成存储类型为UUID
    //把uuid的生成器定义为uuid2,uuid2是将常规的uuid.toString，这样方便我们处理
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    //列的约束：不允许更改，不允许为null
    @Column(updatable = false, nullable = false)
    private String id;

    //定义一个发起人，你关注某人，这里就是你自己
    //多对1->你可以关注很多人，你的每一条关注都是一条记录
    //你可以创建很多关注的信息，所以是多对1
    //这里的多对1是：User 对应多个UserFollow
    //optional 不可选，必须存储，一条关注记录一定要有一个"你"
    //optional属性是定义该关联类是否必须存在，值为false 时，关联类双方都必须存在
    @ManyToOne(optional = false)
    //在多对一中使用@JoinColumn会存储关联表中的某一字段而不是保存整个User表的所有字段
    //@JoinColumn会默认引用User表中的主键作为自己的外键
    //定义关联表的字段为originId对应的就是User表的Id字段
    //定义的是数据库的存储字段(默认为origin_User表中的主键)
    @JoinColumn(name = "originId")
    private User origin;

    //那这个列提取到我们的Module中，不允许为null，不允许更新，插入
    @Column(nullable = false, updatable = false, insertable = false)
    private String originId;


    //定义被关注的目标，你关注的人
    //也是多对1，你可以被很多人关注，每次关注都是一条记录
    //所以就是，多个UserFollow对应 一个User的情况
    @ManyToOne(optional = false)
    @JoinColumn(name = "targetId")
    private User target;

    //那这个列提取到我们的Module中，不允许为null，不允许更新，插入
    @Column(nullable = false, updatable = false, insertable = false)
    private String targetId;

    //别名 也就是target的备注名
    private String alias;

    //定义为创建时间戳 在创建时就写入
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();

    //定义为更新时间戳 在创建时就写入
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getOrigin() {
        return origin;
    }

    public void setOrigin(User origin) {
        this.origin = origin;
    }

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

    public User getTarget() {
        return target;
    }

    public void setTarget(User target) {
        this.target = target;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
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
}
