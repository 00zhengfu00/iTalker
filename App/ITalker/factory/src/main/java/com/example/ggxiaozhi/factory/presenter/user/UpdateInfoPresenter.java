package com.example.ggxiaozhi.factory.presenter.user;

import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.example.ggxiaozhi.factory.Factory;
import com.example.ggxiaozhi.factory.R;
import com.example.ggxiaozhi.factory.data.DataSource;
import com.example.ggxiaozhi.factory.data.helper.UserHelper;
import com.example.ggxiaozhi.factory.model.api.user.UserUpdateModel;
import com.example.ggxiaozhi.factory.model.card.UserCard;
import com.example.ggxiaozhi.factory.model.db.User;
import com.example.ggxiaozhi.factory.net.UploadHelper;
import com.example.ggxiaozhi.factory.presenter.BasePresenter;
import com.example.ggxiaozhi.factory.presenter.account.RegisterContract;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;


/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.italker.fragment.user
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：用户相关逻辑Presenter
 */

public class UpdateInfoPresenter extends BasePresenter<UpdateInfoContract.View>
        implements UpdateInfoContract.Presenter, DataSource.Callback<UserCard> {

    public UpdateInfoPresenter(UpdateInfoContract.View view) {
        super(view);
    }

    @Override
    public void update(final String photoFilePath, final String desc, final boolean isMan) {
        start();
        final UpdateInfoContract.View view = getView();
        if (TextUtils.isEmpty(photoFilePath) || TextUtils.isEmpty(desc)) {
            view.showError(R.string.data_account_update_invalid_parameter);
        } else {
            Factory.runOnAsync(new Runnable() {
                @Override
                public void run() {
                    //获取本地的图片路径
                    String url = UploadHelper.uploadPortrait(photoFilePath);
                    if (TextUtils.isEmpty(url)) {
                        view.showError(R.string.data_upload_error);
                    } else {
                        //构建Model
                        UserUpdateModel model = new UserUpdateModel("", photoFilePath, desc, isMan ? User.SEX_MAN : User.SEX_WOMAN);
                        //进行网络请求 上传
                        UserHelper.update(model, UpdateInfoPresenter.this);

                    }
                }
            });
        }
    }

    @Override
    public void onDataLoaded(UserCard userCard) {
        //当网络请求成功 注册好了 回送一个用户信息回来
        //告知界面 注册成功
        final UpdateInfoContract.View view = getView();
        if (view == null)
            return;
        //强制切换主线程 更新UI
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                //调用注册成功的回调
                view.updateSuccessed();
            }
        });
    }

    @Override
    public void onDataNotAvailable(@StringRes final int str) {
        //告知界面 注册失败
        final UpdateInfoContract.View view = getView();
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
