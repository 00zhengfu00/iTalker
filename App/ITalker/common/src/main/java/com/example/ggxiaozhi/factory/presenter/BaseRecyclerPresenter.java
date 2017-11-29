package com.example.ggxiaozhi.factory.presenter;


import android.support.v7.util.DiffUtil;

import com.example.ggxiaozhi.common.widget.recycler.RecyclerAdapter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.List;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.presenter
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：对RecyclerView进行的一个简单的Presenter封装
 */

public class BaseRecyclerPresenter<ViewModel, View extends BaseContract.RecyclerView> extends BasePresenter<View> {
    public BaseRecyclerPresenter(View view) {
        super(view);
    }

    /**
     * 刷新单一集合到界面
     *
     * @param modelList 最新的数据集合
     */
    protected void refreshData(final List<ViewModel> modelList) {

        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                //得到当前View
                View view = getView();
                if (view == null)
                    return;
                //显示数据到界面
                RecyclerAdapter adapter = view.getAdapter();
                adapter.replace(modelList);
                view.onAdapterDataChanged();
            }
        });
    }

    /**
     * 刷新界面操作，该操作可以保证执行方法在主线程进行
     *
     * @param result 一个差异的结果集
     * @param modelList   具体的新数据
     */
    protected void refreshData(final DiffUtil.DiffResult result, final List<ViewModel> modelList) {
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                // 这里是主线程运行时
                refreshDataOnUiThread(result, modelList);
            }
        });
    }

    protected void refreshDataOnUiThread(DiffUtil.DiffResult result, List<ViewModel> modelList) {
        //得到当前View
        View view = getView();
        if (view == null)
            return;
        //清空原来的数据
        RecyclerAdapter adapter = view.getAdapter();
        //替换新的数据集合 但是不更新界面
        adapter.getItems().clear();
        adapter.getItems().addAll(modelList);

        // 通知界面刷新占位布局
        view.onAdapterDataChanged();
        // 进行增量更新
        result.dispatchUpdatesTo(adapter);
    }
}
