package com.example.ggxiaozhi.italker.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.example.ggxiaozhi.common.app.Activity;
import com.example.ggxiaozhi.common.widget.PortraitView;
import com.example.ggxiaozhi.factory.presistance.Account;
import com.example.ggxiaozhi.italker.R;
import com.example.ggxiaozhi.italker.activity.AccountActivity;
import com.example.ggxiaozhi.italker.fragment.assist.PermissionsFragment;
import com.example.ggxiaozhi.italker.fragment.main.ActiveFragment;
import com.example.ggxiaozhi.italker.fragment.main.ContactFragment;
import com.example.ggxiaozhi.italker.fragment.main.GroupFragment;
import com.example.ggxiaozhi.italker.helper.NavHelper;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.widget.FloatActionButton;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;


public class MainActivity extends Activity implements
        BottomNavigationView.OnNavigationItemSelectedListener,
        NavHelper.onTabChangedListener<Integer> {

    /**
     * UI
     */
    @BindView(R.id.appbar)
    View mLayAppbar;
    @BindView(R.id.im_portrait)
    PortraitView mPortrait;
    @BindView(R.id.txt_title)
    TextView mTitle;
    @BindView(R.id.lay_container)
    FrameLayout mContainer;
    @BindView(R.id.navigation)
    BottomNavigationView mNavigation;
    @BindView(R.id.btn_action)
    FloatActionButton mAction;

    private NavHelper<Integer> mHelper;//Fragment切换工具类

    /**
     * MainActivity的入口
     *
     * @param context
     */
    public static void show(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        //判断用户信息是否填写完全
        if (Account.isComplete()) {//完全
            //走正正常的逻辑
            return super.initArgs(bundle);
        } else {
            UserActivity.show(this);
        }
        return false;
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        //初始化Fragment工具类
        mHelper = new NavHelper<>(this, R.id.lay_container, getSupportFragmentManager(), this);
        mHelper.add(R.id.action_home, new NavHelper.Tab<>(ActiveFragment.class, R.string.title_home))
                .add(R.id.action_group, new NavHelper.Tab<>(GroupFragment.class, R.string.title_group))
                .add(R.id.action_contact, new NavHelper.Tab<>(ContactFragment.class, R.string.title_contact));

        mNavigation.setOnNavigationItemSelectedListener(this);//设置底部Navigation的监听事件
        Glide.with(this).//设置ActionBar与通知栏背景颜色
                load(R.drawable.bg_src_morning).
                centerCrop().
                into(new ViewTarget<View, GlideDrawable>(mLayAppbar) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        //防止图片被拉伸
                        this.view.setBackground(resource.getCurrent());
                    }
                });
        PermissionsFragment.haveAllPerms(this, getSupportFragmentManager());
    }

    @Override
    protected void initData() {
        super.initData();
        //从底部按钮中接管我们的menu，然户进行手动的触发第一次点击
        Menu menu = mNavigation.getMenu();
        //触发首次选中Home 调用这个方法后会走到-->onNavigationItemSelected()方法中
        menu.performIdentifierAction(R.id.action_home, 0);
    }

    @OnClick(R.id.im_search)
    void onSearchMenuClick() {
        //如果是群 则打开创建群界面
        //其他都打开用户添加界面
        int type = Objects.equals(mHelper.getCurrentTab().extra, R.string.title_group) ? SearchActivity.TYPE_GROUP : SearchActivity.TYPE_USER;
        SearchActivity.show(this, type);
    }

    @OnClick(R.id.btn_action)
    void onActionClick() {
        //浮动按钮点击时 判断当前页面是群界面还是联系人界面
        //如果是群 则打开创建群界面

        if (Objects.equals(mHelper.getCurrentTab().extra, R.string.title_group)) {
            //TODO 打开添加群界面
            SearchActivity.show(this, SearchActivity.TYPE_GROUP);
        } else {
            //如果是其他都打开用户添加界面
            SearchActivity.show(this, SearchActivity.TYPE_USER);
        }
    }

    /**
     * 底部按钮导航按钮被点击的时候触发
     *
     * @param item MenuItem
     * @return True 代表我们能处理这个点击 false点击事件响应但是动画不响应
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //转接事件流到我们的工具类中
        return mHelper.perfromClickMenu(item.getItemId());
    }

    /**
     * NaHalper处理Fragemnt完成后的回调
     *
     * @param newTab
     * @param oldTab
     */
    @Override
    public void onTabChanged(NavHelper.Tab<Integer> newTab, NavHelper.Tab<Integer> oldTab) {
        // 从额外字段中取出我们的Title资源Id
        mTitle.setText(newTab.extra);

        //对浮动按钮进行显示和隐藏的动画
        float transY = 0;
        float rotation = 0;
        if (Objects.equals(newTab.extra, R.string.title_home)) {
            //主界面隐藏
            transY = Ui.dipToPx(getResources(), 76);
        } else {
            //transY默认为0 则显示
            if (Objects.equals(newTab.extra, R.string.title_group)) {
                mAction.setImageResource(R.drawable.ic_group_add);
                rotation = -360;
            } else {
                mAction.setImageResource(R.drawable.ic_contact_add);
                rotation = 360;
            }
        }

        //开始动画
        //旋转 Y轴位移 弹性插值器 时间
        mAction.animate()
                .rotation(rotation)
                .translationY(transY)
                .setInterpolator(new AnticipateOvershootInterpolator(1))
                .setDuration(480)
                .start();
    }
}
