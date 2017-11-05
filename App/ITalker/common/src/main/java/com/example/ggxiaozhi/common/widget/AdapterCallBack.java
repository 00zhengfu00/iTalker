package com.example.ggxiaozhi.common.widget;

import android.view.View;

import com.example.ggxiaozhi.common.widget.recycler.RecyclerAdapter;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.common.widget
 * 作者名 ： 志先生_
 * 日期   ： 2017/11/4
 * 功能   ：适配器的回调
 */

public interface AdapterCallBack<Data> {
    void updata(Data data, RecyclerAdapter.ViewHolder<Data> holder);
}
