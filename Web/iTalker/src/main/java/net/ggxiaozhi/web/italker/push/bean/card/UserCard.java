package net.ggxiaozhi.web.italker.push.bean.card;

import com.google.gson.annotations.Expose;
import net.ggxiaozhi.web.italker.push.bean.db.User;
import net.ggxiaozhi.web.italker.push.utils.Hib;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;

/**
 * 工程名 ： iTalker
 * 包名   ： net.ggxiaozhi.web.italker.push.bean.card
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：用于简化数据库返回给客户端的User对象，去除一些不必要或是敏感的字段
 */
public class UserCard {
    /*@Expose 是用于将字段转换成Json*/
    @Expose
    private String id;

    //用户名
    @Expose
    private String name;

    //电话号
    @Expose
    private String phone;

    //头像
    @Expose
    private String portrait;

    //描述(相当于个性签名)
    @Expose
    private String desc;

    //性别
    @Expose
    private int sex = 0;

    //用户关注人的数量
    @Expose
    private int follows;

    //用户粉丝的数量
    @Expose
    private int following;

    //我与当前User的关系状态，是否关注了这个人
    @Expose
    private boolean isFollow;

    //用户信息最后的更新时间
    @Expose
    private LocalDateTime modifyAt;

    public UserCard(User user) {
        this(user, false);
    }

    public UserCard(User user, boolean isFollow) {
        this.isFollow = isFollow;
        this.id = user.getId();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.portrait = user.getPortrait();
        this.desc = user.getDescription();
        this.sex = user.getSex();
        this.modifyAt = user.getUpdateAt();
        //得到关注和粉丝的数量
        //user.getFollowers().size();
        //懒加载会报错 因为没有Session
        Hib.queryOnOnly(session -> {
            //重新加载一次用户信息
            session.load(user, user.getId());
            //这个时候仅仅只是进行了数量查询 并没有查询整个集合
            //要查询集合必须在session存在的情况下 进行遍历
            //或者使用Hibernate.initialize(user.getFollowers());
            //这里只是想获取size 不是遍历 所以不需要
            follows = user.getFollowers().size();
            following = user.getFollowing().size();
        });
    }

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

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getFollows() {
        return follows;
    }

    public void setFollows(int follows) {
        this.follows = follows;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public boolean getIsFollow() {
        return isFollow;
    }

    public void setIsFollow(boolean isFollow) {
        this.isFollow = isFollow;
    }

    public LocalDateTime getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(LocalDateTime modifyAt) {
        this.modifyAt = modifyAt;
    }
}
