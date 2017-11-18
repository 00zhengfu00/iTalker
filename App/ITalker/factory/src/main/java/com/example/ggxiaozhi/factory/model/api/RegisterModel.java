package com.example.ggxiaozhi.factory.model.api;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.model
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：用户注册请求的实体类
 */

public class RegisterModel {

    private String phone;
    private String name;
    private String password;
    private String pushId;

    public RegisterModel(String phone, String name, String password) {
        this(phone, name, password, null);
    }

    public RegisterModel(String phone, String name, String password, String pushId) {
        this.phone = phone;
        this.name = name;
        this.password = password;
        this.pushId = pushId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
                "phone='" + phone + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", pushId='" + pushId + '\'' +
                '}';
    }
}
