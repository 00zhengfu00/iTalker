package net.ggxiaozhi.web.italker.push.bean.api.account;

import com.google.gson.annotations.Expose;
import net.ggxiaozhi.web.italker.push.bean.card.UserCard;
import net.ggxiaozhi.web.italker.push.bean.db.User;

/**
 * 工程名 ： iTalker
 * 包名   ： net.ggxiaozhi.web.italker.push.bean.api.account
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：账户部分返回的module(与UserCard区分 差别在于token这是属于一个隐私的字段)
 */
public class AccountRspModule {

    //用户进本信息
    @Expose
    private UserCard user;
    //当前登录的账号
    @Expose
    private String account;
    //当前登录成功后获取的Token
    //可以通过Token查询用户的所有信息
    @Expose
    private String token;
    //标识是否已经绑定了设配(PushId)
    @Expose
    private boolean isBind;

    public AccountRspModule(User user) {
        //默认无绑定
        this(user, false);
    }

    public AccountRspModule(User user, boolean isBind) {
        this.user = new UserCard(user);
        this.account = user.getPhone();
        this.token = user.getToken();
        this.isBind = isBind;
    }

    public UserCard getUser() {
        return user;
    }

    public void setUser(UserCard user) {
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

    public boolean getIsBind() {
        return isBind;
    }

    public void setIsBind(boolean isBind) {
        this.isBind = isBind;
    }
}
