package com.example.ggxiaozhi.factory.presenter.account;

import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.example.ggxiaozhi.common.Common;
import com.example.ggxiaozhi.factory.R;
import com.example.ggxiaozhi.factory.data.DataSource;
import com.example.ggxiaozhi.factory.model.db.User;
import com.example.ggxiaozhi.factory.presenter.BasePresenter;
import com.example.ggxiaozhi.factory.data.AccountHelper;
import com.example.ggxiaozhi.factory.model.api.RegisterModel;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.regex.Pattern;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.presenter.account
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：注册相关逻辑Presenter
 */

public class RegisterPresenter extends BasePresenter<RegisterContract.View>
        implements RegisterContract.Presenter, DataSource.Callback<User> {

    public RegisterPresenter(RegisterContract.View view) {
        super(view);
    }

    @Override
    public void register(String phone, String name, String password) {
        //启动loading
        start();
        //得到View接口
        RegisterContract.View view = getView();

        if (!checkMobile(phone)) {
            //提示
            view.showError(R.string.data_account_register_invalid_parameter_mobile);
        } else if (name.length() < 2) {
            //用户名要大于2位
            view.showError(R.string.data_account_register_invalid_parameter_name);
        } else if (password.length() < 6) {
            //密码要大于6位
            view.showError(R.string.data_account_register_invalid_parameter_password);
        } else {
            /*网络请求*/
            //构建Model 进行请求调用
            RegisterModel model = new RegisterModel(phone, name, password);
            AccountHelper.Register(model, this);
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

    @Override
    public void onDataLoaded(User user) {

        //当网络请求成功 注册好了 回送一个用户信息回来
        //告知界面 注册成功
        final RegisterContract.View view = getView();
        if (view == null)
            return;
        //强制切换主线程 更新UI
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                //调用注册成功的回调
                view.registerSuccess();
            }
        });
    }

    @Override
    public void onDataAvailable(@StringRes final int str) {
        //告知界面 注册失败
        final RegisterContract.View view = getView();
        if (view == null)
            return;
        //强制切换主线程 更新UI
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                //调用注册失败的回调
                view.showError(str);
            }
        });
    }
}
