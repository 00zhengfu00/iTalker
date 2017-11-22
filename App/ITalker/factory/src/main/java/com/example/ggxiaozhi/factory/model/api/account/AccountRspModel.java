package com.example.ggxiaozhi.factory.model.api.account;

import com.example.ggxiaozhi.factory.model.db.User;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.model.api.account
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：登录和注册成功后返回的所有账户相关的信息的model
 */

public class AccountRspModel {


    //用户进本信息
    private User user;
    //当前登录的账号
    private String account;
    //当前登录成功后获取的Token
    //可以通过Token查询用户的所有信息
    private String token;
    //标识是否已经绑定了设配(PushId)
    private boolean isBind;




    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isBind() {
        return isBind;
    }

    public void setBind(boolean bind) {
        isBind = bind;
    }
}
