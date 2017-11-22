package com.example.ggxiaozhi.factory.model.api.account;

import com.example.ggxiaozhi.factory.model.RspModel;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.model.api.account
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：登录请求提交的Model参数
 */

public class LoginModel {
    private String account;
    private String password;
    private String pushId;

    public LoginModel(String account, String password) {
        this.account = account;
        this.password = password;
    }

    public LoginModel(String account, String password, String pushId) {
        this.account = account;
        this.password = password;
        this.pushId = pushId;
    }

    public String getaccount() {
        return account;
    }

    public void setaccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }
}
