package com.example.ggxiaozhi.common.widget;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.common.widget
 * 作者名 ： 志先生_
 * 日期   ： 2017/11/8
 * 功能   ：矩形布局 用于矩形图片选择器
 */

public class SquareLayout extends FrameLayout {
    public SquareLayout(@NonNull Context context) {
        super(context);
    }

    public SquareLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //给父类的测量值都为宽度
        //那么就是基于宽度的正方形控件了
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
