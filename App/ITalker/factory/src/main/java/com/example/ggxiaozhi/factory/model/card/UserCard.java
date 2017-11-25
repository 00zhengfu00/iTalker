package com.example.ggxiaozhi.factory.model.card;

import com.example.ggxiaozhi.factory.model.Author;
import com.example.ggxiaozhi.factory.model.db.User;

import java.util.Date;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.model.card
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：账户想修改完成后服务器返回的User信息的Model
 */

public class UserCard implements Author{
    private String id;

    //用户名
    private String name;

    //电话号
    private String phone;

    //头像
    private String portrait;

    //描述(相当于个性签名)
    private String desc;

    //性别
    private int sex = 0;

    //用户关注人的数量
    private int follows;

    //用户粉丝的数量
    private int following;

    //我与当前User的关系状态，是否关注了这个人
    private boolean isFollow;

    //用户信息最后的更新时间
    private Date modifyAt;

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

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public Date getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(Date modifyAt) {
        this.modifyAt = modifyAt;
    }

    //缓存一个对应的User 不能被GSON框架解析使用-->transient
    private transient User mUser;

    public User build() {
        if (mUser == null) {
            User user = new User();
            user.setName(name);
            user.setDesc(desc);
            user.setFollow(isFollow);
            user.setFollowing(following);
            user.setFollows(follows);
            user.setId(id);
            user.setPhone(phone);
            user.setPortrait(portrait);
            user.setSex(sex);
            user.setModifyAt(modifyAt);
            this.mUser = user;
        }
        return mUser;
    }

    @Override
    public String toString() {
        return "UserCard{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", portrait='" + portrait + '\'' +
                ", desc='" + desc + '\'' +
                ", sex=" + sex +
                ", follows=" + follows +
                ", following=" + following +
                ", isFollow=" + isFollow +
                ", modifyAt=" + modifyAt +
                '}';
    }
}
