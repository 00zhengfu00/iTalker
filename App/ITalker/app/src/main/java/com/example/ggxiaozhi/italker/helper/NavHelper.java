package com.example.ggxiaozhi.italker.helper;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;


/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.italker.helper
 * 作者名 ： 志先生_
 * 日期   ： 2017/11/8
 * 功能   ：解决对Fragment的调度与重用问题  达到最优的Fragment的切换
 */

public class NavHelper<T> {

    //所有Tab的集合
    private final SparseArray<Tab<T>> tabs = new SparseArray();
    //一些初始化必要的参数
    private Context mContext;
    private final int containerId;
    private final FragmentManager mFragmentManager;
    private onTabChangedListener<T> mListener;

    //当前选中的Tab
    private Tab<T> currentTab;

    public NavHelper(Context context, int containerId,
                     FragmentManager fragmentManager,
                     onTabChangedListener<T> listener) {
        this.mContext = context;
        this.containerId = containerId;
        this.mFragmentManager = fragmentManager;
        this.mListener = listener;
    }

    /**
     * 添加一个Tab
     *
     * @param menuId menuId
     * @param tab    Tab
     * @return 当前类 链式使用
     */
    public NavHelper<T> add(int menuId, Tab<T> tab) {
        tabs.put(menuId, tab);
        return this;
    }

    /**
     * 获取当前Tab
     *
     * @return
     */
    public Tab<T> getCurrentTab() {
        return this.currentTab;
    }

    /**
     * 执行点击菜单的操作
     *
     * @param menuId 菜单Id
     * @return 是否能够处理这个点击
     */
    public boolean perfromClickMenu(int menuId) {
        Tab<T> tab = tabs.get(menuId);
        if (tab != null) {
            doSelect(tab);
            return true;
        }
        return false;
    }

    /**
     * 进行真实Tab选择操作
     *
     * @param tab
     */
    private void doSelect(Tab<T> tab) {
        Tab<T> oldTab = null;
        if (currentTab != null) {
            oldTab = currentTab;
            if (oldTab == tab) {
                //如果当前的Tab就是传入点击的Tab
                //那么我们不做处理
                notifyTabReselect(tab);
                return;
            }
        }
        //赋值并调用切换方法
        currentTab = tab;
        doTabChanged(currentTab, oldTab);
    }

    /**
     * doSelect()中调用 切换Fragment的逻辑处理
     *
     * @param newTab 新的
     * @param oldTab 旧的
     */
    private void doTabChanged(Tab<T> newTab, Tab<T> oldTab) {

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (oldTab != null) {
            if (oldTab.fragment != null) {
                //从界面移除，但是还在Fragment的缓存空间中
                transaction.detach(oldTab.fragment);
            }
        }
        if (newTab != null) {
            if (newTab.fragment == null) {
                //首次创建
                Fragment fragment = Fragment.instantiate(mContext, newTab.clx.getName(), null);
                //缓存起来
                newTab.fragment = fragment;
                //提交到transaction中
                transaction.add(containerId, fragment, newTab.clx.getName());
            } else {
                //从Fragment的缓存空间中重新加载到界面中
                transaction.attach(newTab.fragment);
            }
        }
        //提交事务
        transaction.commit();
        //通知回调
        notifyTabSelect(newTab, oldTab);

    }


    /**
     * 切换完成后通知主界面的回调
     *
     * @param newTab 新的Tab
     * @param oldTab 旧的Tab
     */
    private void notifyTabSelect(Tab<T> newTab, Tab<T> oldTab) {

        if (mListener != null) {
            mListener.onTabChanged(newTab, oldTab);
        }
    }

    /**
     * 重复点击相同的Tab
     *
     * @param tab
     */
    private void notifyTabReselect(Tab<T> tab) {

        //TODO 二次点击Tab的操作 可以实现刷新等逻辑
    }


    /**
     * 所有Tab的基础属性
     *
     * @param <T> 范型的额外参数
     */
    public static class Tab<T> {

        public Tab(Class<? extends Fragment> clx, T extra) {
            this.clx = clx;
            this.extra = extra;
        }

        //Fragment对应的Class信息
        public Class<? extends Fragment> clx;
        //额外的字段，用户自己设定是否需要(除了点击切换菜单，Title等其他跟随变换)
        public T extra;
        //内部缓存的对应的Fragment
        //Package 外界无法调用
        Fragment fragment;
    }

    /**
     * 定义事件处理完成后的回调接口
     *
     * @param <T> Tab<T>中指的范型的额外参数
     */
    public interface onTabChangedListener<T> {
        void onTabChanged(Tab<T> newTab, Tab<T> oldTab);
    }
}
