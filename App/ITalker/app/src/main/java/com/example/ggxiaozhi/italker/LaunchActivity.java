package com.example.ggxiaozhi.italker;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Property;
import android.view.View;

import com.example.ggxiaozhi.common.app.Activity;
import com.example.ggxiaozhi.common.app.BaseActivity;
import com.example.ggxiaozhi.factory.presistance.Account;
import com.example.ggxiaozhi.italker.activity.AccountActivity;
import com.example.ggxiaozhi.italker.activity.MainActivity;
import com.example.ggxiaozhi.italker.fragment.assist.PermissionsFragment;

import net.qiujuer.genius.res.Resource;
import net.qiujuer.genius.ui.compat.UiCompat;

public class LaunchActivity extends BaseActivity {

    private ColorDrawable mBgDrawable;//背景颜色的Drawable


    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_launch;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        //拿到根布局
        View root = findViewById(R.id.activity_launch);
        //获取颜色
        int color = UiCompat.getColor(getResources(), R.color.colorPrimary);
        //创建一个ColorDrawable
        ColorDrawable colorDrawable = new ColorDrawable(color);
        //设置给背景
        root.setBackground(colorDrawable);
        mBgDrawable = colorDrawable;

    }


    @Override
    protected void initData() {
        super.initData();
        //当动画进行到50%的时候获取PushId
        startAnimator(0.5f, new Runnable() {
            @Override
            public void run() {
                //获取绑定的Id
                waitPushIdRecevierId();
            }
        });
    }


    /**
     * 等待个推框架给我们的PushId设置好值
     */
    private void waitPushIdRecevierId() {

        if (Account.isLogin()) {//已经登录

            //已经登录的情况下 判断是否绑定
            //如果没有绑定则等待广播接收器进行绑定
            if (Account.isBind()) {
                skip();
                return;
            }
        } else {//没有登录
            //如果拿到PushId 没有登录是不能绑定PushId的
            if (!TextUtils.isEmpty(Account.getPushId())) {
                skip();
                return;
            }
        }


        //循环等待 每隔0.5s执行一次本方法 直到获取成功
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                waitPushIdRecevierId();
            }
        }, 500);
    }

    /**
     * 进行剩下的50%动画 完成后跳转
     */
    private void skip() {
        //进行剩下的50%动画
        startAnimator(1f, new Runnable() {
            @Override
            public void run() {
                realSkip();
            }
        });
    }

    /**
     * 真正的跳转页面
     */
    public void realSkip() {
        //检测权限
        if (PermissionsFragment.haveAllPerms(this,  getSupportFragmentManager())) {
            if (Account.isLogin()) {
                MainActivity.show(this);
            } else {
                AccountActivity.show(this);
            }
            finish();
        }
    }

    private void startAnimator(float endProgress, final Runnable callback) {

        //获取一个最终的颜色
        int finalColor = Resource.Color.WHITE;//等价于UiCompat.getColor(getResources(), R.color.colorPrimary);
        //运算当前进度的颜色(颜色变换计算器)
        ArgbEvaluator evaluator = new ArgbEvaluator();
        int endColor = (int) evaluator.evaluate(endProgress, mBgDrawable.getColor(), finalColor);
        ValueAnimator animator = ObjectAnimator.ofObject(this, mProperty, evaluator, endColor);
        animator.setDuration(1500);
        animator.setIntValues(mBgDrawable.getColor(), endColor);//开始结束的颜色值
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //结束时的触发
                callback.run();
            }
        });
        animator.start();

    }

    Property<LaunchActivity, Object> mProperty = new Property<LaunchActivity, Object>(Object.class, "color") {
        @Override
        public Object get(LaunchActivity object) {
            return object.mBgDrawable.getColor();
        }

        @Override
        public void set(LaunchActivity object, Object value) {
            object.mBgDrawable.setColor((Integer) value);
        }
    };


}
