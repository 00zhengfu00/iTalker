package com.example.ggxiaozhi.italker.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.example.ggxiaozhi.common.app.Activity;
import com.example.ggxiaozhi.common.app.Fragment;
import com.example.ggxiaozhi.italker.R;
import com.example.ggxiaozhi.italker.fragment.account.AccountTrigger;
import com.example.ggxiaozhi.italker.fragment.account.LoginFragment;
import com.example.ggxiaozhi.italker.fragment.account.RegisterFragment;

import net.qiujuer.genius.ui.compat.UiCompat;

import butterknife.BindView;

/**
 * 账户登录Activity
 */
public class AccountActivity extends Activity implements AccountTrigger {

    private Fragment mCurFragment;
    private Fragment mLoginFragment;
    private Fragment mRegisterFragment;
    @BindView(R.id.im_bg)
    ImageView mBg;//背景

    /**
     * 当前Activity入口
     *
     * @param context
     */
    public static void show(Context context) {
        context.startActivity(new Intent(context, AccountActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_account;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mCurFragment = mLoginFragment = new LoginFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.lay_container, mCurFragment);
        transaction.commit();

        //初始化背景
        Glide.with(this)
                .load(R.drawable.bg_src_tianjin)
                .centerCrop()
                .into(new ViewTarget<ImageView, GlideDrawable>(mBg) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        //拿到glide的Drawable
                        Drawable drawable = resource.getCurrent();
                        //使用适配类进行包装
                        drawable = DrawableCompat.wrap(drawable);
                        drawable.setColorFilter(UiCompat.getColor(getResources(), R.color.colorAccent),//设置着色效果和颜色
                                PorterDuff.Mode.SCREEN);//蒙板模式
                        //设置给ImageView
                        this.view.setImageDrawable(drawable);
                    }
                });
    }


    @Override//调用此方法后会切换注册界面
    public void triggerView() {
        Fragment fragment;//将要现实的额Fragment
        if (mCurFragment == mLoginFragment) {
            if (mRegisterFragment == null) {
                //默认情况下为空
                //第一次之后就不为空了
                mRegisterFragment = new RegisterFragment();
            }
            fragment = mRegisterFragment;
        } else {
            //因为默认请求下mLoginFragment已经赋值，无需判断是否为空
            fragment = mLoginFragment;
        }

        //重新赋值将要显示的Fragment
        mCurFragment = fragment;
        //切换显示
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.lay_container, mCurFragment)
                .commit();


    }
}
