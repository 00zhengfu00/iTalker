package com.example.ggxiaozhi.factory.presenter.search;

import com.example.ggxiaozhi.factory.presenter.BasePresenter;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.presenter.search
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：搜索群的Presenter
 */

public class SearchGroupPresenter extends BasePresenter<SearchContract.SearchGroupView> implements SearchContract.SearchPresenter{

    public SearchGroupPresenter(SearchContract.SearchGroupView view) {
        super(view);
    }

    @Override
    public void search(String content) {

    }
}
