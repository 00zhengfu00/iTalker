package com.example.ggxiaozhi.factory.presenter.message;

import android.support.v7.util.DiffUtil;

import com.example.ggxiaozhi.factory.data.message.SessionDataSource;
import com.example.ggxiaozhi.factory.data.message.SessionRepository;
import com.example.ggxiaozhi.factory.model.db.Session;
import com.example.ggxiaozhi.factory.presenter.BaseSourcePresenter;
import com.example.ggxiaozhi.factory.utils.DiffUiDataCallback;

import java.util.List;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.presenter.message
 * 作者名 ： 志先生_
 * 日期   ： 2017/12
 * 功能   ：最近聊天列表的Presenter
 */

public class SessionPresenter extends
        BaseSourcePresenter<Session, Session, SessionDataSource, SessionContract.View>
        implements SessionContract.Presenter {
    public SessionPresenter(SessionContract.View view) {
        super(new SessionRepository(), view);
    }

    @Override
    public void onDataLoaded(List<Session> sessions) {
        SessionContract.View view = getView();
        if (view == null)
            return;
        //差异对比
        List<Session> old = view.getAdapter().getItems();
        DiffUiDataCallback<Session> callback = new DiffUiDataCallback<>(old, sessions);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        //刷新界面
        refreshData(result, sessions);
    }
}
