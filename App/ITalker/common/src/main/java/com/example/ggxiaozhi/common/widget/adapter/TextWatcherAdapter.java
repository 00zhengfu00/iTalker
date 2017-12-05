package com.example.ggxiaozhi.common.widget.adapter;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.common.widget.adapter
 * 作者名 ： 志先生_
 * 日期   ： 2017/12
 * 功能   ：重写EditText监听文字变换的接口目的是简化 外界只需要实现想要的接口
 */

public abstract class TextWatcherAdapter implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
