package com.example.ggxiaozhi.factory.model.api;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.model
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：用户注册请求的实体类
 */

public class RegisterModel {

    private String account;
    private String name;
    private String password;
    private String pushId;

    public RegisterModel(String account, String name, String password) {
        this(account, name, password, null);
    }

    public RegisterModel(String account, String name, String password, String pushId) {
        this.account = account;
        this.name = name;
        this.password = password;
        this.pushId = pushId;
    }

    public String getaccount() {
        return account;
    }

    public void setaccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return "RegisterModel{" +
                "account='" + account + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", pushId='" + pushId + '\'' +
                '}';
    }
}
