package com.example.ggxiaozhi.common.app;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ggxiaozhi.common.widget.convention.PlaceHolderView;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.common.app
 * 作者名 ： 志先生_
 * 日期   ： 2017/11
 * 功能   ：所有Fragment的父类
 */

public abstract class Fragment extends android.support.v4.app.Fragment {

    protected View rootView;
    protected Unbinder rootUnbinder;

    protected PlaceHolderView mPlaceHolderView;

    protected boolean mIsFirstDataInit = true;//由于我的Fragment切换 会重复加载Fragment与View 所以我写一个标志位 标志第一次进入

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //初始化参数
        initArgs(getArguments());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (rootView == null) {
            //初始化当前根布局，但是不在创建时就添加到container里边
            int layoutId = getContentLayoutId();
            View root = inflater.inflate(layoutId, container, false);
            initWidget(root);
            rootView = root;
        } else {
            if (rootView.getParent() != null) {
                ((ViewGroup) rootView.getParent()).removeView(rootView);
            }
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mIsFirstDataInit) {
            mIsFirstDataInit = false;
            //第一次进入时调用
            initFirstData();
        }
        //View创建完成后初始化数据
        initData();
    }

    /**
     * 得到哦当前资源文件ID
     */
    protected abstract int getContentLayoutId();

    /**
     * 初始化控件
     */
    protected void initWidget(View root) {
        rootUnbinder = ButterKnife.bind(this, root);
    }

    /**
     * 初始化数据
     */
    protected void initData() {

    }

    /**
     * 第一次进入Fragment初始化数据 只被调用一次
     */
    protected void initFirstData() {

    }

    /**
     * 初始化参数
     */
    protected void initArgs(Bundle bundle) {
    }

    /**
     * 返回按键触发时调用
     *
     * @return 返回True代表我已处理返回逻辑，Activity不用自己finish。
     * 返回False代表我没有处理逻辑，Activity自己走自己的逻辑
     */
    public boolean onBackPressed() {
        return false;
    }

    /**
     * 设置占位布局
     *
     * @param placeHolderView 继承占位布局规范的View
     */
    public void setPlaceHolderView(PlaceHolderView placeHolderView) {
        this.mPlaceHolderView = placeHolderView;
    }
}
