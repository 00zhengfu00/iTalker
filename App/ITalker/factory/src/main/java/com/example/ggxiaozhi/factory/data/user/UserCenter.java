package com.example.ggxiaozhi.factory.data.user;

import com.example.ggxiaozhi.factory.model.card.UserCard;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.data.user
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：用户管理中心 用于数据转化 数据库变更与通知分发
 */

public interface UserCenter {

    void dispatch(UserCard... cards);
}
