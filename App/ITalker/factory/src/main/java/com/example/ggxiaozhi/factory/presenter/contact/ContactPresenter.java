package com.example.ggxiaozhi.factory.presenter.contact;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

import com.example.ggxiaozhi.common.widget.recycler.RecyclerAdapter;
import com.example.ggxiaozhi.factory.data.DataSource;
import com.example.ggxiaozhi.factory.data.helper.UserHelper;
import com.example.ggxiaozhi.factory.data.user.ContactDataSource;
import com.example.ggxiaozhi.factory.data.user.ContactRepository;
import com.example.ggxiaozhi.factory.model.db.User;
import com.example.ggxiaozhi.factory.presenter.BaseRecyclerPresenter;
import com.example.ggxiaozhi.factory.presenter.BaseSourcePresenter;
import com.example.ggxiaozhi.factory.utils.DiffUiDataCallback;

import java.util.List;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.presenter.contact
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ： 联系人的Presenter
 */

public class ContactPresenter extends BaseSourcePresenter<User, User, ContactDataSource, ContactContract.View>
        implements ContactContract.Presenter, DataSource.SucceedCallback<List<User>> {


    public ContactPresenter(ContactContract.View view) {
        //初始化数据仓库
        super(new ContactRepository(), view);
    }

    @Override
    public void start() {
        super.start();
        //服务器拉取联系人
        UserHelper.refreshContacts();
    }

    public void refreshContacts() {
        //服务器拉取联系人
        UserHelper.refreshContacts();
    }

    @Override
    public void onDataLoaded(List<User> users) {
        //无论是网络请求 还是数据库查询数据 最终都会走到这个方法中
        ContactContract.View view = getView();
        RecyclerAdapter<User> adapter = view.getAdapter();
        //得到旧的数据
        List<User> items = adapter.getItems();
        //进行数据对比
        DiffUiDataCallback callback = new DiffUiDataCallback<>(items, users);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        //在子线程更新数据
        refreshData(result, users);
    }

}
 /* public void diff(List<User> newList, List<User> oldList) {
        //进行数据对比
        DiffUiDataCallback callback = new DiffUiDataCallback<>(oldList, newList);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        //在数据对比完成后进行数据的赋值
        getView().getAdapter().replace(newList);
        //尝试刷新
        result.dispatchUpdatesTo(getView().getAdapter());
        getView().onAdapterDataChanged();
    }*/