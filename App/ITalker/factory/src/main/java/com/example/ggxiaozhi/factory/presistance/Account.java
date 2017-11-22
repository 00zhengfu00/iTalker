package com.example.ggxiaozhi.factory.presistance;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.ggxiaozhi.factory.Factory;
import com.example.ggxiaozhi.factory.model.api.account.AccountRspModel;
import com.example.ggxiaozhi.factory.model.db.User;
import com.example.ggxiaozhi.factory.model.db.User_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.w3c.dom.Text;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.presistance
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：推送相关常量参数 以及数据持久化的操作
 */

public class Account {

    private static final String KET_PUSH_ID = "KET_PUSH_ID";
    private static final String KEY_IS_BIND = "KEY_IS_BIND";
    private static final String KEY_TOKEN = "KEY_TOKEN";
    private static final String KEY_USER_ID = "KEY_PUSH_ID";
    private static final String KEY_ACCOUNT = "KEY_ACCOUNT";
    //设备Id
    public static String pushId;
    //是否绑定了设备ID到服务器
    public static boolean isBind;
    //登录状态的Token 用于接口请求
    public static String token;
    //登录用户的Id
    public static String userId;
    //登录用户的帐号
    public static String account;


    /**
     * 储存数据到XML文件 持久化
     *
     * @param context
     */
    public static void save(Context context) {
        //获取数据持久化 SP
        context.getSharedPreferences(Account.class.getName(),
                Context.MODE_PRIVATE).edit()
                .putString(KET_PUSH_ID, pushId)
                .putBoolean(KEY_IS_BIND, isBind)
                .putString(KEY_TOKEN, token)
                .putString(KEY_USER_ID, userId)
                .putString(KEY_ACCOUNT, account)
                .apply();
    }


    /**
     * 进行数据加载
     *
     * @param context
     */
    public static void load(Context context) {
        //获取数据持久化 SP
        SharedPreferences sharedPreferences = context.getSharedPreferences(Account.class.getName(),
                Context.MODE_PRIVATE);
        pushId = sharedPreferences.getString(KET_PUSH_ID, "");
        isBind = sharedPreferences.getBoolean(KEY_IS_BIND, false);
        token = sharedPreferences.getString(KEY_TOKEN, "");
        userId = sharedPreferences.getString(KEY_USER_ID, "");
        account = sharedPreferences.getString(KEY_ACCOUNT, "");


    }

    public static String getPushId() {
        return pushId;
    }

    /**
     * 设置并存储设备Id
     *
     * @param pushId 设备的推送Id
     */
    public static void setPushId(String pushId) {
        Account.pushId = pushId;
        Account.save(Factory.app());
    }

    /**
     * 获取推送Id
     *
     * @return 推动的Id
     */
    public static boolean isLogin() {
        //用户的ID和TOKEN不为空
        return !TextUtils.isEmpty(userId)
                && !TextUtils.isEmpty(token);
    }

    /**
     * 是否完善了用户信息
     *
     * @return True 是完成了
     */
    public static boolean isComplete() {
        //TODO
        return isLogin();
    }

    /**
     * 是否已经绑定了PushId
     */
    public static boolean isBind() {
        return isBind;
    }

    /**
     * 设置并储存BindId
     *
     * @param isBind
     */
    public static void setBind(boolean isBind) {
        Account.isBind = isBind;
        Account.save(Factory.app());
    }

    /**
     * 保存我自己的信息到XML持久化中
     *
     * @param model
     */
    public static void login(AccountRspModel model) {
        //存储当前用户的token 用户id 方便从数据库中查询我的信息
        Account.token = model.getToken();
        Account.userId = model.getUser().getId();
        Account.account = model.getAccount();
        save(Factory.app());
    }

    public static User getUser() {
        return TextUtils.isEmpty(userId) ? new User() :
                SQLite.select()
                        .from(User.class)
                        .where(User_Table.id.eq(userId))
                        .querySingle();
    }

    /**
     * 得到当期登录的Token
     *
     * @return Token
     */
    public static String getToken() {
        return token;
    }
}
