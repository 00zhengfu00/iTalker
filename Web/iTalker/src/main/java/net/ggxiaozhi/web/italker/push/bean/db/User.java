package net.ggxiaozhi.web.italker.push.bean.db;

import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 工程名 ： iTalker
 * 包名   ： net.ggxiaozhi.web.italker.push.bean.db
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：用户的Module，对应数据库TB_USER表
 */
@Entity//表明为实体类
@Table(name = "TB_USER")//对应数据库中的表
public class User {


    @Id//标明字段含义
    @PrimaryKeyJoinColumn//这是一个主键
    @GeneratedValue(generator = "uuid")//主键生成存储类型为UUID
    //把uuid的生成器定义为uuid2,uuid2是将常规的uuid.toString，这样方便我们处理
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    //列的约束：不允许更改，不允许为null
    @Column(updatable = false, nullable = false)
    private String id;

    //用户名必须唯一
    @Column(nullable = false, length = 128, unique = true)
    private String name;

    //电话号必须唯一
    @Column(nullable = false, length = 62, unique = true)
    private String phone;

    //密码
    @Column(nullable = false)
    private String password;

    //头像可以为空
    @Column
    private String portrait;

    //描述(相当于个性签名)
    @Column
    private String description;

    //性别有初始值 所以不为空
    @Column(nullable = false)
    private int sex;

    //token可以拉取用户信息，类似cookie的作用。所有token必须唯一，
    @Column(unique = true)
    private String token;

    //定义为创建时间戳 在创建时就写入
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();

    //定义为更新时间戳 在创建时就写入
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();

    //最后一次收到消息的时间
    @Column
    private LocalDateTime lastReceviedAt = LocalDateTime.now();

    //拉取我关注人的列表的方法
    //对应数据库表中的字段为UserFollow.originId
    @JoinColumn(name = "originId")
    //定义为懒加载，默认加载User信息的时候，并不查询这个集合
    @LazyCollection(LazyCollectionOption.EXTRA)
    //关于@OneToMany和@ManyToOne相关方法查询博客 http://www.cnblogs.com/mjsh/archive/2013/08/27/3284931.html
    //一对多，一个用户可以关注很多人，每一个关注都是一条记录
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<UserFollow> following = new HashSet<>();


    //拉取关注我的人列表的方法
    //对应数据库表中的字段为UserFollow.targetId
    @JoinColumn(name = "targetId")
    //定义为懒加载，默认加载User信息的时候，并不查询这个集合
    @LazyCollection(LazyCollectionOption.EXTRA)
    //关于@OneToMany和@ManyToOne相关方法查询博客 http://www.cnblogs.com/mjsh/archive/2013/08/27/3284931.html
    //一对多，一个用户可以被很多人关注，每一个关注都是一条记录
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<UserFollow> followers = new HashSet<>();


    //用户所有创建的群
    //对应的字段为：Group.ownerId
    @JoinColumn(name = "ownerId")
    //懒记载集合的方式：LazyCollectionOption.EXTRA 尽可能不加载具体的数据
    //当访问groups.size()时仅仅查询数量，不加载具体的Group的信息
    //只有在遍历集合的时候才记载具体的数据
    @LazyCollection(LazyCollectionOption.EXTRA)
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Group> groups = new HashSet<>();


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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public LocalDateTime getLastReceviedAt() {
        return lastReceviedAt;
    }

    public void setLastReceviedAt(LocalDateTime lastReceviedAt) {
        this.lastReceviedAt = lastReceviedAt;
    }

    public Set<UserFollow> getFollowing() {
        return following;
    }

    public void setFollowing(Set<UserFollow> following) {
        this.following = following;
    }

    public Set<UserFollow> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<UserFollow> followers) {
        this.followers = followers;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }
}


