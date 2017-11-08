package com.example.ggxiaozhi.common.widget;

import android.content.Context;
import android.util.AttributeSet;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 工程名 ： ITalker
 * 包名   ： com.example.ggxiaozhi.common.widget
 * 作者名 ： 志先生_
 * 日期   ： 2017/11/7
 * 功能   ：圆形图片的封装 方便以后在头像上添加装饰或是红点等
 */

public class PortraitView extends CircleImageView {
    public PortraitView(Context context) {
        super(context);
    }

    public PortraitView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PortraitView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
