package com.example.ggxiaozhi.factory.presenter.account;

import android.text.TextUtils;

import com.example.ggxiaozhi.common.Common;
import com.example.ggxiaozhi.factory.BasePresenter;
import com.example.ggxiaozhi.factory.data.AccountHelper;
import com.example.ggxiaozhi.factory.model.RegisterModel;

import java.util.regex.Pattern;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.presenter.account
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：注册相关逻辑Presenter
 */

public class RegisterPresenter extends BasePresenter<RegisterContract.View> implements RegisterContract.Presenter {

    public RegisterPresenter(RegisterContract.View view) {
        super(view);
    }

    @Override
    public void register(String phone, String name, String password) {

        if (!checkMobile(phone)) {
            //提示
        } else if (name.length() < 2) {
            //用户名要大于2位
        } else if (password.length() > 6) {
            //密码要大于6位
        } else {
            //网络请求

            //构建Model 进行请求调用
            RegisterModel model = new RegisterModel(phone, name, password);
            AccountHelper.Register(model);
        }
    }

    /**
     * 检查手机号是否合法
     *
     * @param phone 手机号
     * @return True 表示符合要求
     */
    @Override
    public boolean checkMobile(String phone) {
        return !TextUtils.isEmpty(phone)
                && Pattern.matches(Common.Constance.REGEX_MOBILE, phone);
    }
}
