package net.ggxiaozhi.web.italker.push.bean.api;

import com.google.gson.annotations.Expose;

/**
 * 工程名 ： iTalker
 * 包名   ： net.ggxiaozhi.web.italker.push.bean.api
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：注册请求的实体
 */
public class RegisterModule {

    @Expose
    private String account;//注册的账户
    @Expose
    private String password;//密码
    @Expose
    private String name;//用户名称

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
