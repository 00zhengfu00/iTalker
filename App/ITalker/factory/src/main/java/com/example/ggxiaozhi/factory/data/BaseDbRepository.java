package com.example.ggxiaozhi.factory.data;

import android.support.annotation.NonNull;

import com.example.ggxiaozhi.factory.data.helper.DbHelper;
import com.example.ggxiaozhi.factory.model.db.base.BaseDbModel;
import com.example.ggxiaozhi.utils.CollectionUtil;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import net.qiujuer.genius.kit.reflect.Reflector;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.data
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：提取公用仓库方法 封装成基类
 */

public abstract class BaseDbRepository<Data extends BaseDbModel<Data>> implements DbDataSource<Data>
        , DbHelper.ChangedListener<Data>, QueryTransaction.QueryResultListCallback<Data> {
    private SucceedCallback<List<Data>> callback;
    protected final LinkedList<Data> mDataList = new LinkedList<>();//缓存数据的集合
    private Class<Data> mClass;//数据对应的真实的Class信息

    @SuppressWarnings("unchecked")
    public BaseDbRepository() {
        //拿到当前类当前数组信息
        Type[] types = Reflector.getActualTypeArguments(BaseDbRepository.class, this.getClass());
        mClass = (Class<Data>) types[0];
    }

    @Override
    public void load(SucceedCallback<List<Data>> callback) {
        this.callback = callback;
        //添加数据库回调监听
        registerDbChangedListener();
    }

    @Override
    public void dispose() {
        //取消监听 销毁数据
        if (this.callback != null) {
            this.callback = null;
        }
        DbHelper.removeChangedListener(mClass, this);
        mDataList.clear();
    }

    //数据库统一通知的地方：更新/保存

    /**
     * 数据库数据更改与保存的通知
     *
     * @param datas 数据库中最新的数据
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onDataSave(Data... datas) {
        boolean isChanged = false;
        //
        for (Data data : datas) {
            if (isRequired(data)) {
                //此时可能为数据更新 也可能为数据插入
                insertOrUpDate(data);
                isChanged = true;
            }
        }
        if (isChanged)
            //如果有更改 通知界面更新
            notifyChangedDate();
    }

    /**
     * 判断 当前数据是更新还是插入
     *
     * @param data 要显示的一条User数据
     */
    private void insertOrUpDate(Data data) {
        int index = indexof(data);
        if (index >= 0) {//更新
            replace(index, data);
        } else {//插入
            insert(data);
        }

    }

    /**
     * 插入一条数据
     *
     * @param data 新的数据
     */
    protected void insert(Data data) {
        mDataList.add(data);
    }

    /**
     * 更新一条数据
     *
     * @param data  新的数据
     * @param index 更新数据的坐标
     */
    protected void replace(int index, Data data) {
        mDataList.remove(index);
        mDataList.add(index, data);
    }

    /**
     * 判断当前用户是否存在 ->id相同就表示存在
     *
     * @param newData 新的数据
     * @return -1表示不存在
     */
    protected int indexof(Data newData) {
        int index = -1;
        for (Data data : mDataList) {
            index++;
            if (newData.isSame(data)) {//有数据变更 进行界面刷新
                return index;
            }
        }
        return -1;
    }

    //数据库统一通知的地方：删除

    /**
     * 数据库数据删除的通知
     *
     * @param datas 要删除的数据
     */
    @Override
    public void onDataDelete(Data... datas) {
        //这里我们不需要注意得到的数据是否是我们所关心的
        //因为能删除成功就表示缓存集合中存在 就可以删除 刷新界面
        boolean isChanged = false;
        for (Data data : datas) {
            if (mDataList.remove(data)) {
                isChanged = true;
            }
        }
        if (isChanged) {//有数据变更 进行界面刷新
            notifyChangedDate();
        }
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Data> tResult) {
        if (tResult.size() == 0) {//返回的数据为空
            //清空数据
            mDataList.clear();
            //界面通知
            notifyChangedDate();
            return;
        }

        Data[] datas = CollectionUtil.toArray(tResult, mClass);
        //触发
        onDataSave(datas);
    }

    /**
     * 判断当期用户是否是我想要的数据
     *
     * @param data User
     * @return True 表示是我们想要的数据
     */
    protected abstract boolean isRequired(Data data);

    protected void registerDbChangedListener() {
        DbHelper.addChangedListener(mClass, this);
    }

    /**
     * 通知界面刷新
     */
    private void notifyChangedDate() {
        SucceedCallback<List<Data>> callback = this.callback;
        if (callback != null)
            callback.onDataLoaded(mDataList);
    }
}
