package com.example.ggxiaozhi.factory.presenter.search;

import android.support.annotation.StringRes;

import com.example.ggxiaozhi.factory.data.DataSource;
import com.example.ggxiaozhi.factory.data.helper.UserHelper;
import com.example.ggxiaozhi.factory.model.card.UserCard;
import com.example.ggxiaozhi.factory.presenter.BasePresenter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.List;

import retrofit2.Call;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.presenter.search
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：搜索人的Presenter
 */

public class SearchUserPresenter extends BasePresenter<SearchContract.SearchUserView>
        implements SearchContract.SearchPresenter, DataSource.Callback<List<UserCard>> {
    private Call searchCall;

    public SearchUserPresenter(SearchContract.SearchUserView view) {
        super(view);
    }

    @Override
    public void search(String content) {
        start();

        //这样承接一下的目的是避免线程冲突
        Call call = searchCall;
        if (call != null && call.isCanceled()) {//上次请求的call以及赋值或是没有取消(请求未完成)
            //那么取消上次的请求 重新发起请求
            //避免重复点击造成冲突与浪费流量
            call.cancel();
        }
        searchCall = UserHelper.search(content, this);
    }

    @Override
    public void onDataLoaded(final List<UserCard> userCards) {
        final SearchContract.SearchUserView view = getView();
        if (view != null) {
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.searchUserDone(userCards);
                }
            });
        }
    }

    @Override
    public void onDataNotAvailable(@StringRes final int str) {
        final SearchContract.SearchUserView view = getView();
        if (view != null) {
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.showError(str);
                }
            });
        }
    }
}
