package com.example.ggxiaozhi.italker.fragment.account;


import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.EditText;

import com.example.ggxiaozhi.common.app.Fragment;
import com.example.ggxiaozhi.common.app.PresenterFragment;
import com.example.ggxiaozhi.factory.presenter.BaseContract;
import com.example.ggxiaozhi.factory.presenter.account.LoginContract;
import com.example.ggxiaozhi.factory.presenter.account.LoginPresenter;
import com.example.ggxiaozhi.italker.R;
import com.example.ggxiaozhi.italker.activity.MainActivity;

import net.qiujuer.genius.ui.widget.Button;
import net.qiujuer.genius.ui.widget.Loading;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 登录页面的Fragment
 */
public class LoginFragment extends PresenterFragment<LoginContract.Presenter>
        implements LoginContract.View {

    private AccountTrigger mAccountTrigger;
    /**
     * UI
     */
    @BindView(R.id.edit_phone)
    EditText mPhone;
    @BindView(R.id.edit_password)
    EditText mPassword;
    @BindView(R.id.loading)
    Loading mLoading;
    @BindView(R.id.btn_submit)
    Button mSubmit;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAccountTrigger = (AccountTrigger) context;
    }

    @Override
    protected LoginContract.Presenter initPresenter() {
        return new LoginPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    public void loginSuccess() {
        //我们需要跳转到MainActivity界面
        MainActivity.show(getContext());
        //关闭当前页面
        getActivity().finish();
    }

    @Override
    public void showError(@StringRes int str) {
        super.showError(str);
        //当显示错误的时候触发 一定是结束了
        //停止loading
        mLoading.stop();
        //设置输入框可以点击
        mPhone.setEnabled(true);
        mPassword.setEnabled(true);
        //提交按钮可以点击
        mSubmit.setEnabled(true);
    }

    @Override
    public void showLoading() {
        super.showLoading();
        //开始loading
        mLoading.start();
        //设置输入框不可以点击
        mPhone.setEnabled(false);
        mPassword.setEnabled(false);
        //提交按钮不可以点击
        mSubmit.setEnabled(false);
    }

    @OnClick(R.id.btn_submit)
    void onSubmitClick() {
        String phone = mPhone.getText().toString();
        String password = mPassword.getText().toString();
        mPresenter.login(phone, password);
    }

    @OnClick(R.id.txt_go_register)
    void onShowReginsterClick() {
        mAccountTrigger.triggerView();
    }
}
