package com.example.ggxiaozhi.factory.presenter;

import com.example.ggxiaozhi.factory.data.DataSource;
import com.example.ggxiaozhi.factory.data.DbDataSource;

import java.util.List;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.presenter
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：基础仓库源
 */

/**
 * @param <ViewModel> 是RecyclerView 的Adapter中的数据模型
 * @param <Data> 是具体的数据
 * @param <Source> 数据源加载接口
 * @param <View> 具体的界面View
 */
public abstract class BaseSourcePresenter<ViewModel, Data,
        Source extends DbDataSource<Data>,
        View extends BaseContract.RecyclerView>
        extends BaseRecyclerPresenter<ViewModel, View>
        implements DataSource.SucceedCallback<List<Data>> {

    protected Source mSource;

    public BaseSourcePresenter(Source source, View view) {
        super(view);
        this.mSource = source;
    }

    @Override
    public void start() {
        super.start();
        if (mSource != null)
            mSource.load(this);
    }

    @Override
    public void detach() {
        super.detach();
        mSource.dispose();
        this.mSource = null;
    }
}
