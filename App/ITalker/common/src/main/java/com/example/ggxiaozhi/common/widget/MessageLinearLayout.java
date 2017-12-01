package com.example.ggxiaozhi.common.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.common.widget
 * 作者名 ： 志先生_
 * 日期   ： 2017/12
 * 功能   ：解决消息聊天布局无法在软键盘弹出时无法设置沉浸式状态栏的问题
 */

public class MessageLinearLayout extends LinearLayout {
    public MessageLinearLayout(Context context) {
        super(context);
    }

    public MessageLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MessageLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected boolean fitSystemWindows(Rect insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//API=19
            insets.left = 0;
            insets.right = 0;
            insets.top = 0;
        }
        return super.fitSystemWindows(insets);
    }
}
