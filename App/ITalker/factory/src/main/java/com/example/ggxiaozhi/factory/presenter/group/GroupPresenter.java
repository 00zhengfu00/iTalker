package com.example.ggxiaozhi.factory.presenter.group;

import android.support.v7.util.DiffUtil;
import android.util.Log;

import com.example.ggxiaozhi.common.widget.recycler.RecyclerAdapter;
import com.example.ggxiaozhi.factory.data.DataSource;
import com.example.ggxiaozhi.factory.data.group.GroupDataSource;
import com.example.ggxiaozhi.factory.data.group.GroupRepository;
import com.example.ggxiaozhi.factory.data.helper.GroupHelper;
import com.example.ggxiaozhi.factory.data.helper.UserHelper;
import com.example.ggxiaozhi.factory.data.user.ContactDataSource;
import com.example.ggxiaozhi.factory.data.user.ContactRepository;
import com.example.ggxiaozhi.factory.model.db.Group;
import com.example.ggxiaozhi.factory.model.db.User;
import com.example.ggxiaozhi.factory.presenter.BaseSourcePresenter;
import com.example.ggxiaozhi.factory.presenter.contact.ContactContract;
import com.example.ggxiaozhi.factory.utils.DiffUiDataCallback;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.presenter.contact
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ： 联系人的Presenter
 */

public class GroupPresenter extends BaseSourcePresenter<Group, Group, GroupDataSource, GroupContract.View>
        implements GroupContract.Presenter, DataSource.SucceedCallback<List<Group>> {


    public GroupPresenter(GroupContract.View view) {
        //初始化数据仓库
        super(new GroupRepository(), view);
    }

    @Override
    public void start() {
        super.start();

    }

    public void refreshGroups(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -3);
        Date calendarTime = calendar.getTime();
        String string = calendarTime.toString();
        Log.d("TAG", "refreshGroups: " + string);
        //服务器拉取群
        //这里可以进行优化 只有用户下拉时刷新
        GroupHelper.refreshGroups(string);
    }

    @Override
    public void onDataLoaded(List<Group> groups) {
        //无论是网络请求 还是数据库查询数据 最终都会走到这个方法中
        GroupContract.View view = getView();
        if (view == null)
            return;
        RecyclerAdapter<Group> adapter = view.getAdapter();
        //得到旧的数据
        List<Group> items = adapter.getItems();
        //进行数据对比
        DiffUiDataCallback callback = new DiffUiDataCallback<>(items, groups);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        //在子线程更新数据
        refreshData(result, groups);
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