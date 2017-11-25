package com.example.ggxiaozhi.factory.utils;

import android.support.v7.util.DiffUtil.Callback;

import java.util.List;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.factory.utils
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：比较2个数据源是否相同
 */

public class DiffUiDataCallback<T extends DiffUiDataCallback.UiDataDiff<T>> extends Callback {
    private List<T> oldList, newList;

    @Override
    public int getOldListSize() {
        //旧的数据大小
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        //新的数据大小
        return newList.size();
    }

    //两个类是否是同一个数据的依据条件 比如Id相同的User
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        T beanOld = oldList.get(oldItemPosition);
        T beanNew = oldList.get(newItemPosition);
        //里面是判断逻辑让调用者去实现
        return beanNew.isSame(beanOld);
    }

    //经过相等判断后 进一步判断是否有数据更改
    //比如用一个用户两个不同的实例 其中的name字段不同
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        T beanOld = oldList.get(oldItemPosition);
        T beanNew = oldList.get(newItemPosition);
        //里面是判断逻辑让调用者去实现
        return beanNew.isUiContentsSame(beanOld);
    }


    /**
     * 进行比较的数据类型
     *
     * @param <T> 比较的数据类型
     */
    public interface UiDataDiff<T> {
        //传一个旧的数据给你 判断是否和你标示的是同一个数据
        boolean isSame(T old);

        //和你旧的数据对比 内容是否相同
        boolean isUiContentsSame(T old);
    }
}
