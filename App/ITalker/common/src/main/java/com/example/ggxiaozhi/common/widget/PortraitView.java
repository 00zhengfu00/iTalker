package com.example.ggxiaozhi.common.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.bumptech.glide.RequestManager;
import com.example.ggxiaozhi.common.R;
import com.example.ggxiaozhi.factory.model.Author;

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

    public void setup(RequestManager manager, Author author) {
        setup(manager, author.getPortrait());
    }

    public void setup(RequestManager manager, String url) {
        setup(manager, R.drawable.default_portrait, url);
    }

    /**
     * 封装加载圆形图片的方法
     *
     * @param manager    Gilde.with()返回参数
     * @param resourceId 默认显示的图片资源Id
     * @param url        加载的地址
     */
    public void setup(RequestManager manager, int resourceId, String url) {
        manager.load(url)
                .placeholder(resourceId)//默认显示的占位图片
                .dontAnimate()//CircleImageView 控件中不能使用渐变动画 会导致显示延迟
                .centerCrop()
                .into(this);
    }
}
