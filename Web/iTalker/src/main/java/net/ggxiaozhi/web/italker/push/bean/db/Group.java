package net.ggxiaozhi.web.italker.push.bean.db;

import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * 工程名 ： iTalker
 * 包名   ： net.ggxiaozhi.web.italker.push.bean.db
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ： 群信息的Moduel
 */
@Entity
@Table(name = "TB_GROUP")
public class Group {

    @Id//标明字段含义
    @PrimaryKeyJoinColumn//这是一个主键
    @GeneratedValue(generator = "uuid")//主键生成存储类型为UUID
    //把uuid的生成器定义为uuid2,uuid2是将常规的uuid.toString，这样方便我们处理
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    //列的约束：不允许更改，不允许为null
    @Column(updatable = false, nullable = false)
    private String id;

    //群名称
    @Column(nullable = false)
    private String name;

    //群描述
    @Column(nullable = false)
    private String description;

    //群图片
    @Column(nullable = false)
    private String picture;

    //定义为创建时间戳 在创建时就写入
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();
    //定义为更新时间戳 在创建时就写入
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();


    //群的创建者
    //optional：可选为false，必须有一个创建者
    //fetch：加载方式 FetchType.EAGER为急加载，意味着加载群信息的时候必须加载owner的信息
    //cascade:联级级别为ALL，所有的更改(更新，删除等)都将进行关系更新
    @JoinColumn(name = "ownerId")
    @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private User owner;
    @Column(nullable = false, updatable = false, insertable = false)
    private String ownerId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}
