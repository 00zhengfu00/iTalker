package net.ggxiaozhi.web.italker.push.bean.api.account;

import com.google.common.base.Strings;
import com.google.gson.annotations.Expose;

/**
 * 工程名 ： iTalker
 * 包名   ： net.ggxiaozhi.web.italker.push.bean.api
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：客户端注册请求的实体
 */
public class RegisterModule {

    /*@Expose 是用于将字段转换成Json*/
    @Expose
    private String account;//注册的账户
    @Expose
    private String password;//密码
    @Expose
    private String name;//用户名称
    @Expose
    private String pushId;//绑定设备Id可以为空 不进行校验

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

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    /**
     * 校验用户注册信息 内容不允许为空
     *
     * @param module
     * @return
     */
    public static boolean check(RegisterModule module) {
        return module != null &&
                !Strings.isNullOrEmpty(module.account) &&
                !Strings.isNullOrEmpty(module.password) &&
                !Strings.isNullOrEmpty(module.name);
    }
}
