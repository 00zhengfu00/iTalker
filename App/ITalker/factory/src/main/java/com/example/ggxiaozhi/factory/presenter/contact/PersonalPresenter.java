package com.example.ggxiaozhi.factory.presenter.contact;

import com.example.ggxiaozhi.factory.Factory;
import com.example.ggxiaozhi.factory.data.helper.UserHelper;
import com.example.ggxiaozhi.factory.model.db.User;
import com.example.ggxiaozhi.factory.presenter.BasePresenter;
import com.example.ggxiaozhi.factory.presistance.Account;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.presenter.contact
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：个人信息页面的Presenter
 */

public class PersonalPresenter extends BasePresenter<PersonalContract.View> implements PersonalContract.Presenter {
    private User mUser;

    public PersonalPresenter(PersonalContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        super.start();
        //进行拉取个人信息 优先从网络拉取
        //网络请求是同步 所以我们在这里要异步执行
        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                PersonalContract.View view = getView();
                if (view != null) {
                    String id = getView().getUserId();
                    User user = UserHelper.searchUserFirstOfNet(id);
                    onLoaded(view, user);
                }
            }
        });
    }

    private void onLoaded(final PersonalContract.View view, final User user) {
        this.mUser = user;

        //拉取的人是否是我自己
        final boolean isSelf = user.getId().equalsIgnoreCase(Account.getUserId());
        //是否已经关注了
        final boolean isFollow = isSelf || user.isFollow();
        //是否可以聊天
        final boolean allowSayHello = isFollow &&  !isSelf;
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.onLoadDone(user);
                view.allowSayHello(allowSayHello);
                view.isFollowState(isFollow);
            }
        });
    }

    @Override
    public User getUserPersonal() {
        return mUser;
    }
}
