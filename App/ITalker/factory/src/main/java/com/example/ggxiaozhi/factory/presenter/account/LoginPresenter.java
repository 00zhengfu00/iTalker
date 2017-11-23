package com.example.ggxiaozhi.factory.presenter.account;

import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.example.ggxiaozhi.factory.R;
import com.example.ggxiaozhi.factory.data.DataSource;
import com.example.ggxiaozhi.factory.data.helper.AccountHelper;
import com.example.ggxiaozhi.factory.model.api.account.LoginModel;
import com.example.ggxiaozhi.factory.model.db.User;
import com.example.ggxiaozhi.factory.presenter.BasePresenter;
import com.example.ggxiaozhi.factory.presistance.Account;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.presenter.account
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：登录相关逻辑Presenter
 */

public class LoginPresenter extends BasePresenter<LoginContract.View>
        implements LoginContract.Presenter, DataSource.Callback<User> {
    public LoginPresenter(LoginContract.View view) {
        super(view);
    }

    @Override
    public void login(String phone, String password) {
        start();
        LoginContract.View view = getView();
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
            view.showError(R.string.data_account_login_invalid_parameter);
        } else {
            //尝试传递PushId
            LoginModel model = new LoginModel(phone, password, Account.getPushId());
            AccountHelper.login(model, this);
        }
    }

    @Override
    public void onDataLoaded(User user) {
        //当网络请求成功 登录好了 回送一个用户信息回来
        //告知界面 登录成功
        final LoginContract.View view = getView();
        if (view == null)
            return;
        //强制切换主线程 更新UI
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                //调用登录成功的回调
                view.loginSuccess();
            }
        });
    }

    @Override
    public void onDataNotAvailable(@StringRes final int str) {
        //告知界面 登录失败
        final LoginContract.View view = getView();
        if (view == null)
            return;
        //强制切换主线程 更新UI
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                //调用登录失败的回调
                view.showError(str);
            }
        });
    }
}
